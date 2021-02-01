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

public class PlanManagerAgArch extends LAASAgArch {
	
	private String actName;
	private List<String> actParams;
	private String actAgent;
	private Collection<Literal> perceive = new ArrayList<Literal>();
	private List<MementarOccasion> perceptions = Collections.synchronizedList(new ArrayList<MementarOccasion>());
	
	public PlanManagerAgArch() {
		super();
		setMementarListener();
	}
	

	public void setMementarListener() {
		rosnode.setSubListener("mementar_occasions", new MessageListener<MementarOccasion>() {
			
			@Override
			public void onNewMessage(MementarOccasion occasion) {
				synchronized (perceptions) {
					perceptions.add(occasion);
				}
				
			}
		});
	}

	@Override
	public Collection<Literal> perceive() {
		perceive = new ArrayList<Literal>();
		if(rosnode != null) {
			Iterator<Literal> monitorBel = get_beliefs_iterator("monitoring(_,_,_,_)");
			
			if(monitorBel != null) {
				synchronized (perceptions) {
					for(Iterator<MementarOccasion> iterator = perceptions.iterator(); iterator.hasNext();) {
						List<Integer> ids = new ArrayList<>();
						monitorBel = get_beliefs_iterator("monitoring(_,_,_,_)");
						while(monitorBel.hasNext()) {
							ids.add(Integer.parseInt(monitorBel.next().getTerm(0).toString()));
						}
						
						MementarOccasion occas = iterator.next();
						if(ids.contains(occas.getId())) {
							
							String fact = occas.getData();
							if(fact.endsWith("start")) {
								Pattern p = Pattern.compile("(?<=\\[add\\])(.*)(?=\\|\\_\\|start)");
								Matcher m = p.matcher(fact);
								if(m.find()) {
									setAction(m.group());
									addPercept("ongoing");
								}
								
							}else if(fact.endsWith("end")){
								
								Pattern p = Pattern.compile("(?<=\\[add\\])(.*)(?=\\|\\_\\|end)");
								Matcher m = p.matcher(fact);
								if(m.find()) {
									setAction(m.group());
									addPercept("executed");
								}
								
							}
							iterator.remove();
						}
					}
				}
			}
		}
		return perceive;
	}
	
	
	private void setAction(String action) {
		actName = callOnto("getUp", action).getValues().get(0);
		actParams = callOnto("getRelationWith", action).getValues();
		actParams.sort(String::compareToIgnoreCase);
		actAgent = callOnto("getOn", action+":hasParameterAgent").getValues().get(0);
		actParams.remove(actAgent);
	}
	
	private void addPercept(String state) {
		perceive.add(Tools.stringFunctorAndTermsToBelLiteral("action",Arrays.asList("_", state, actName, actAgent, Tools.arrayToListTerm(actParams))));
	}

}
