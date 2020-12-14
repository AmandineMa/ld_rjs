package arch.agarch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jason.asSyntax.Literal;
import mementar.MementarOccasion;
import ontologenius.OntologeniusService;
import ontologenius.OntologeniusServiceRequest;
import ontologenius.OntologeniusServiceResponse;
import rjs.utils.Tools;
import ros.RosNode;

public class PlanManagerAgArch extends AgArch {
	
	String actName;
	List<String> actParams;
	Collection<Literal> perceive = new ArrayList<Literal>();
	
	public PlanManagerAgArch() {
		super();
	}

	@Override
	public Collection<Literal> perceive() {
		perceive = new ArrayList<Literal>();
		if(rosnode != null) {
			List<MementarOccasion> perceptions =((RosNode) rosnode).getPerceptions();
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
	
	
	private OntologeniusServiceResponse callOnto(String action, String param) {
		OntologeniusServiceRequest req = rosnode.newServiceRequestFromType(OntologeniusService._TYPE);
		req.setAction(action);
		req.setParam(param);
		return  rosnode.callSyncService("onto_individual", req);
	}
	
	private void setAction(String action) {
		actName = callOnto("getUp", action).getValues().get(0);
		actParams = callOnto("getRelationWith", action).getValues();
	}
	
	private void addPercept(String state) {
		perceive.add(Tools.stringFunctorAndTermsToBelLiteral("action",Arrays.asList("_", state, actName, Tools.arrayToListTerm(Arrays.asList("human_0")), Tools.arrayToListTerm(actParams))));
	}

}
