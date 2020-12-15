package arch.actions.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ros.message.MessageListener;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import jason.asSemantics.ActionExec;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Term;
import mementar.MementarOccasion;
import planner_msgs.AgentTasksRequest;
import planner_msgs.Plan;
import planner_msgs.PlanRequest;
import planner_msgs.TaskRequest;
import rjs.arch.actions.AbstractAction;
import rjs.arch.agarch.AbstractROSAgArch;

public class GetMAHTNPlan extends AbstractAction {
	
	public GetMAHTNPlan(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		super(actionExec, rosAgArch);
		
	}

	@Override
	public void execute() {
		List<String> humanAgentNames = removeQuotes((List<Term>) actionTerms.get(1));
		ListTerm tasks = (ListTerm) actionTerms.get(0);
		
			
		rosAgArch.setSubListener("plan",new MessageListener<Plan>() {

			@Override
			public void onNewMessage(Plan arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		AgentTasksRequest robotTaskReq = rosAgArch.createMessage(AgentTasksRequest._TYPE);
		robotTaskReq.setAgentName(rosnode.getParameters().getString("/supervisor/robot_name"));
		Iterator<Term> ite = tasks.iterator();
		List<TaskRequest> tasksList = new ArrayList<TaskRequest>();
		while(ite.hasNext()) {
			ListTerm taskInfos = (ListTerm) ite.next();
			TaskRequest taskRequest = rosAgArch.createMessage(TaskRequest._TYPE);
			taskRequest.setName(taskInfos.get(0).toString());
			taskRequest.setParameters(removeQuotes((List<Term>) taskInfos.get(1)));
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
		planReq.setAgentTasks(agentTaskRequests);
		rosnode.publish("plan_request", planReq); 
	}

}
