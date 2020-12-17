package arch.actions;

import org.ros.node.topic.Publisher;

import arch.actions.internal.Disambiguate;
import arch.actions.internal.GetMAHTNPlan;
import arch.actions.internal.GetSparqlVerba;
import arch.actions.internal.MementarSubscribe;
import arch.actions.internal.MementarUnsubscribe;
import arch.actions.internal.PR2MotionPlanDrop;
import arch.actions.internal.PR2MotionPlanMove;
import arch.actions.internal.PR2MotionPlanPick;
import arch.actions.internal.PR2MotionPlanPlace;
import arch.actions.robot.Listen;
import arch.actions.robot.PR2MotionExecute;
import arch.actions.robot.Say;
import arch.actions.ros.StartROSNode;
import dialogue_as.dialogue_actionActionFeedback;
import dialogue_as.dialogue_actionActionGoal;
import dialogue_as.dialogue_actionActionResult;
import jason.asSemantics.ActionExec;
import pr2_motion_tasks_msgs.executeActionFeedback;
import pr2_motion_tasks_msgs.executeActionGoal;
import pr2_motion_tasks_msgs.executeActionResult;
import pr2_motion_tasks_msgs.planActionFeedback;
import pr2_motion_tasks_msgs.planActionGoal;
import pr2_motion_tasks_msgs.planActionResult;
import rjs.arch.actions.AbstractActionFactory;
import rjs.arch.actions.Action;
import rjs.arch.actions.GetHATPPlan;
import rjs.arch.actions.ros.ConfigureNode;
import rjs.arch.actions.ros.InitServices;
import rjs.arch.actions.ros.RetryInitServices;
import rjs.arch.actions.ros.RjsActionClient;
import rjs.arch.actions.ros.StartParameterLoaderNode;
import rjs.arch.agarch.AbstractROSAgArch;

public class ActionFactoryImpl extends AbstractActionFactory {
	
	private Publisher<std_msgs.String> sayPub;
	private RjsActionClient<dialogue_actionActionGoal, dialogue_actionActionFeedback, dialogue_actionActionResult> dialogueActionClient;
	private RjsActionClient<planActionGoal, planActionFeedback, planActionResult> pr2MotionPlanActionClient;
	private RjsActionClient<executeActionGoal, executeActionFeedback, executeActionResult> pr2MotionExecuteActionClient;
	
	public void setRosVariables() {
		super.setRosVariables();
		sayPub = createPublisher("supervisor/topic_to_change/say");
		dialogueActionClient = new RjsActionClient<dialogue_actionActionGoal, dialogue_actionActionFeedback, dialogue_actionActionResult>(rosnode.getConnectedNode(), rosnode.getParameters().getString("/supervisor/action_servers/dialogue"), 
				dialogue_actionActionGoal._TYPE, dialogue_actionActionFeedback._TYPE, dialogue_actionActionResult._TYPE);
		
		pr2MotionPlanActionClient = new RjsActionClient<planActionGoal, planActionFeedback, planActionResult>(rosnode.getConnectedNode(), 
				rosnode.getParameters().getString("/supervisor/action_servers/plan_motion"), planActionGoal._TYPE, planActionFeedback._TYPE, planActionResult._TYPE);
		pr2MotionExecuteActionClient = new RjsActionClient<executeActionGoal, executeActionFeedback, executeActionResult>(rosnode.getConnectedNode(), 
				rosnode.getParameters().getString("/supervisor/action_servers/execute_motion"), executeActionGoal._TYPE, executeActionFeedback._TYPE, executeActionResult._TYPE);
	}
	
	public Action createAction(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		String actionName = actionExec.getActionTerm().getFunctor();
		Action action = null;
		switch(actionName) {
			case "disambiguate":
				action = new Disambiguate(actionExec, rosAgArch);
				break;
			case "sparqlVerbalization":
				action = new GetSparqlVerba(actionExec, rosAgArch);
				break;
			case "listen":
				action = new Listen(actionExec, rosAgArch, dialogueActionClient);
				break;
			case "say":
				action = new Say(actionExec, rosAgArch, sayPub);
				break;
			case "getPlan":
				action = new GetMAHTNPlan(actionExec, rosAgArch);
				break;
			case "planPick":
				action = new PR2MotionPlanPick(actionExec, rosAgArch, pr2MotionPlanActionClient);
				break;
			case "planPlace":
				action = new PR2MotionPlanPlace(actionExec, rosAgArch, pr2MotionPlanActionClient);
				break;
			case "planDrop":
				action = new PR2MotionPlanDrop(actionExec, rosAgArch, pr2MotionPlanActionClient);
				break;
			case "planMove":
				action = new PR2MotionPlanMove(actionExec, rosAgArch, pr2MotionPlanActionClient);
				break;
			case "execute":
				action = new PR2MotionExecute(actionExec, rosAgArch, pr2MotionExecuteActionClient);
				break;
			case "getHatpPlan":
				action = new GetHATPPlan(actionExec, rosAgArch);
				break;
			case "mementarSubscribe":
				action = new MementarSubscribe(actionExec, rosAgArch);
				break;
			case "mementarUnsubscribe":
				action = new MementarUnsubscribe(actionExec, rosAgArch);
				break;
			case "configureNode":
				action = new ConfigureNode(actionExec, rosAgArch);
				break;
			case "startParameterLoaderNode":
				action = new StartParameterLoaderNode(actionExec, rosAgArch);
				break;
			case "startROSNode":
				action = new StartROSNode(actionExec, rosAgArch);
				break;
			case "initServices":
				action = new InitServices(actionExec, rosAgArch);
				break;
			case "retryInitServices":
				action = new RetryInitServices(actionExec, rosAgArch);
				break;
			default:
				break;
		}
			
		return action;
	}
	
}
