package arch.agarch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ros.message.MessageListener;

import jason.asSyntax.Literal;
import mementar.MementarOccasion;
import rjs.utils.Tools;

public class ActionMonitoringAgArch extends LAASAgArch {
	
	private Collection<Literal> perceive = new ArrayList<Literal>();
	private List<MementarOccasion> perceptions = Collections.synchronizedList(new ArrayList<MementarOccasion>());
	
	public ActionMonitoringAgArch() {
		super();
	}
	
	@Override
	public void init() {
		super.init();
	}
	
	public void initListeners() {
		rosnode.setSubListener("mementar_occasions", new MessageListener<MementarOccasion>() {
			
			@Override
			public void onNewMessage(MementarOccasion occasion) {
				synchronized (perceptions) {
					if(monitoringIDs.contains(occasion.getId())) {
						perceptions.add(occasion);
					}
				}
				
			}
		});
	}

	@Override
	public Collection<Literal> perceive() {
		perceive = new ArrayList<Literal>();
		synchronized (perceptions) {
			for(Iterator<MementarOccasion> iterator = perceptions.iterator(); iterator.hasNext();) {

				MementarOccasion occas = iterator.next();

				String fact = occas.getData();
				Pattern p = Pattern.compile("\\[(?<function>add|del)\\](?<subject>\\w+)\\|(?<property>\\w+)\\|(?<object>\\w+)");
				Matcher m = p.matcher(fact);
				if(m.find()) {
					String negation = m.group("function").equals("del")?"~":"";
					perceive.add(Tools.stringFunctorAndTermsToBelLiteral(negation+m.group("property"),Arrays.asList(m.group("subject"), m.group("object"))));
					logger.info("perceive add "+Tools.stringFunctorAndTermsToBelLiteral(negation+m.group("property"),Arrays.asList(m.group("subject"), m.group("object"))).toString());
				}
			}
			perceptions.clear();
		}
		return perceive;
	}
	
	

}
