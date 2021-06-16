package arch.actions.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.ros.message.MessageListener;

import jason.asSemantics.ActionExec;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.MapTermImpl;
import jason.asSyntax.Term;
import planner_msgs.AgentTasksRequest;
import planner_msgs.Plan;
import planner_msgs.PlanRequest;
import planner_msgs.Task;
import planner_msgs.TaskRequest;
import rjs.arch.actions.AbstractAction;
import rjs.arch.agarch.AbstractROSAgArch;
import rjs.utils.Tools;

public class GetMAHTNPlan extends AbstractAction {
	
	public GetMAHTNPlan(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		super(actionExec, rosAgArch);
		
	}
	
	// request asl : getMAHTNPlan([[name_t1,param1_t1,param2_t1],[name_t2, param1_t2]],[Human1,Human2]);

	@SuppressWarnings("unchecked")
	@Override
	public void execute() {
		List<List<Term>> tasks = (List<List<Term>>) actionTerms.get(0);
		List<String> humanAgentNames = Tools.removeQuotes((List<Term>) actionTerms.get(1));
		
		rosAgArch.setSubListener("plan",new MessageListener<Plan>() {

			@Override
			public void onNewMessage(Plan plan) {
				for(Task task : plan.getTasks()) {
					if(task.getType() == Task.PRIMITIVE_TASK) {
						// put parameters in alphabetical order to match planned and executed
						List<String> parameters = task.getParameters();
//						parameters.sort(String::compareToIgnoreCase);
						
						ListTerm preds = new ListTermImpl();
						preds = ListTermImpl.parseList(Tools.arrayToStringArray(task.getPredecessors()));
						
						rosAgArch.addBelief("action", Arrays.asList(task.getId(), "planned", task.getName(), 
								task.getAgent(), Tools.arrayToListTerm(parameters), preds, task.getDecompositionOf()));
					}else {
						rosAgArch.addBelief("abstractTask", Arrays.asList(task.getId(), "planned", task.getName(), task.getDecompositionOf()));
					}
				}
				setActionExecuted(true);
			}
		});
		
		AgentTasksRequest robotTaskReq = rosAgArch.createMessage(AgentTasksRequest._TYPE);
		robotTaskReq.setAgentName(getRosNode().getParameters().getString("/supervisor/robot_name"));
		Iterator<List<Term>> iteTasks = tasks.iterator();
		List<TaskRequest> tasksList = new ArrayList<TaskRequest>();
		while(iteTasks.hasNext()) {
			List<Term> task = iteTasks.next();
			Iterator<Term> iteTask = task.iterator();
			TaskRequest taskRequest = rosAgArch.createMessage(TaskRequest._TYPE);
			taskRequest.setName(Tools.removeQuotes(iteTask.next().toString()));
			List<String> listParameters = new ArrayList<String>();
			while(iteTask.hasNext()) {
				MapTermImpl taskInfos = (MapTermImpl) iteTask.next();
				listParameters.add(taskInfos.getAsJSON("  "));
			}
			taskRequest.setParameters(listParameters);
			tasksList.add(taskRequest);
		}
		robotTaskReq.setTasks(tasksList);
		
		List<AgentTasksRequest> agentTaskRequests = new ArrayList<AgentTasksRequest>();
		for(String humanAgentName : humanAgentNames) {
			AgentTasksRequest humanTaskReq = rosAgArch.createMessage(AgentTasksRequest._TYPE);
			humanTaskReq.setAgentName(humanAgentName);
			agentTaskRequests.add(humanTaskReq);
		}
		
		PlanRequest planReq = rosAgArch.createMessage(PlanRequest._TYPE);
		planReq.setUncontrollableAgentTasks(agentTaskRequests);
		planReq.setControllableAgentTasks(Arrays.asList(robotTaskReq));
		getRosNode().publish("plan_request", planReq); 
	}

}
