package arch.actions.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import hatp_msgs.Plan;
import hatp_msgs.PlanningRequest;
import hatp_msgs.PlanningRequestRequest;
import hatp_msgs.PlanningRequestResponse;
import hatp_msgs.Request;
import hatp_msgs.StreamNode;
import hatp_msgs.TreeNode;
import jason.asSemantics.ActionExec;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTermImpl;
import rjs.arch.actions.AbstractAction;
import rjs.arch.agarch.AbstractROSAgArch;
import rjs.utils.Tools;

public class GetHATPPlan extends AbstractAction {

	public GetHATPPlan(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		super(actionExec, rosAgArch);
		setSync(true);
	}
	
	// request asl : getHATPPlan(taskName, paramList)

	@Override
	public void execute() {
		String taskName = Tools.removeQuotes(actionExec.getActionTerm().getTerm(0).toString());
		Request request = rosAgArch.createMessage(Request._TYPE);
		
		if(actionExec.getActionTerm().getArity() > 1) {
			List<String> planParameters = Tools.listTermStringTolist((ListTermImpl) actionExec.getActionTerm().getTerm(1));
			request.setParameters(planParameters);
		}
		request.setTask(taskName);
		request.setType("plan");
		
		PlanningRequestRequest planreq = (PlanningRequestRequest) getRosNode().newServiceRequestFromType(PlanningRequest._TYPE);
		planreq.setRequest(request);
		PlanningRequestResponse hatpPlannerResp = getRosNode().callSyncService("hatp_planner", planreq);
		
		if(hatpPlannerResp != null) {
			Plan plan = hatpPlannerResp.getSolution();
			if(plan.getReport().equals("OK")) {
				actionExec.setResult(true);
				for(hatp_msgs.Task task : plan.getTasks()) {
					
					Integer decompoOf = -1;
					for(TreeNode tree : plan.getTree()) {
						if( Arrays.stream(tree.getSubnodes()).boxed().collect(Collectors.toList()).contains(task.getId())) {
							decompoOf = tree.getTaskId();
							break;
						}
					}
					if(task.getType()) {
						// name of the actor is removed
						task.getParameters().remove(0);
						List<String> taskParameters = task.getParameters();
						if(taskParameters.stream().anyMatch(s -> s.contains("FLAG"))) {
							taskParameters = replaceParams(taskParameters);
						}
						// remove the agents that are in the parameters from the agent list
						task.getAgents().removeAll(taskParameters);
						Collections.replaceAll(task.getAgents(), "AGENTX2", "AGENTX");
						String agent = task.getAgents().get(0);
						for(StreamNode stream : plan.getStreams()) {
							if(stream.getTaskId() == task.getId()) {
								List<Integer>  preds = Arrays.stream(stream.getPredecessors()).boxed().collect(Collectors.toList());
								
								rosAgArch.addBelief("action", new ArrayList<Object>(Arrays.asList(
										task.getId(), "planned", task.getName(), agent,  Tools.arrayToListTerm(taskParameters), preds,decompoOf)));
								break;
							}
						}
						
					} else {
						// abstractTask does not have any sense as it is because of the recursivity
//						rosAgArch.addBelief("abstractTask", Arrays.asList(task.getId(), "planned", task.getName(), decompoOf));
					}
				}
			} 
		} else {
			actionExec.setResult(false);
			actionExec.setFailureReason(new Atom("no_plan_found"), "hatp planner could not find any feasible plan");
		}
	}
	
	private List<String> replaceParams(List<String> taskParameters){
		taskParameters = simulateSparqlFromHATP(taskParameters);
		List<String> newList = new ArrayList<String>();
		boolean flag = false;
		for(int i = 0; i < taskParameters.size(); i++) {
			if(!flag) {
				flag = true;
			}else {
				flag = false;
				if(!taskParameters.get(i).isEmpty()) {
					newList.add(taskParameters.get(i));
				}else {
					newList.add(taskParameters.get(i-1));
				}
			}
		}
		return newList;
	}
	
	// ?0 is the param, ?1 an agent
	private List<String> simulateSparqlFromHATP(List<String> taskParameters){
		for(int i = 0; i < taskParameters.size(); i++) {
			switch(taskParameters.get(i)) {
			case "REDFLAG":
				taskParameters.set(i,"?0 isA Cube. ?0 isReachableBy ?1 NOT EXISTS { ?0 isOnTopOf ?2. ?2 isA Cube }");
				break;
			case "GREENFLAG":
				taskParameters.set(i,"?0 isA Cube. ?0 hasColor green. ?0 isReachableBy ?1 NOT EXISTS { ?0 isOnTopOf ?2. ?2 isA Cube }");
				break;
			case "BLUEFLAG":
				taskParameters.set(i,"?0 isA Cube. ?0 hasColor blue. ?0 isReachableBy ?1 NOT EXISTS { ?0 isOnTopOf ?2. ?2 isA Cube }");
				break;	
			case "STICKFLAG":
				taskParameters.set(i,"?0 isA Stick. ?0 isReachableBy ?1 NOT EXISTS { ?0 isOnTopOf ?2. ?2 isA Cube }");
				break;
			case "PLACEFLAG":
				taskParameters.set(i,"?0 isA Spot NOT EXISTS { ?0 isUnder ?2. ?2 isA Cube }");
				break;
			case "EMPTYFLAG":
				taskParameters.set(i,"");
				break;
			}
		}
		return taskParameters;
	}


}
