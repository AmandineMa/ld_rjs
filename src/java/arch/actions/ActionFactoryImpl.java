package arch.actions;

import arch.actions.internal.AnalyzeSentence;
import arch.actions.internal.DisambiguateEntity;
import arch.actions.internal.DisambiguateSentence;
import arch.actions.internal.GetMAHTNPlan;
import arch.actions.internal.GetSparqlVerba;
import arch.actions.internal.MementarSubscribe;
import arch.actions.internal.MementarUnsubscribe;
import arch.actions.internal.PR2MotionPlanDrop;
import arch.actions.internal.PR2MotionPlanMoveArm;
import arch.actions.internal.PR2MotionPlanPick;
import arch.actions.internal.PR2MotionPlanPlace;
import arch.actions.robot.LookAt;
import arch.actions.robot.PR2MotionExecute;
import arch.actions.robot.Say;
import arch.actions.robot.ScanTable;
import arch.actions.robot.Strafe;
import arch.actions.ros.StartROSNode;
import arch.agarch.LAASAgArch;
import dt_head_gestures.HeadScanActionFeedback;
import dt_head_gestures.HeadScanActionGoal;
import dt_head_gestures.HeadScanActionResult;
import dt_navigation.MoveActionFeedback;
import dt_navigation.MoveActionGoal;
import dt_navigation.MoveActionResult;
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
import rjs.arch.actions.ros.InitSub;
import rjs.arch.actions.ros.RjsActionClient;
import rjs.arch.actions.ros.StartParameterLoaderNode;
import rjs.arch.agarch.AbstractROSAgArch;

public class ActionFactoryImpl extends AbstractActionFactory {
	
	private RjsActionClient<MoveActionGoal, MoveActionFeedback, MoveActionResult> strafeActionClient;
	private RjsActionClient<planActionGoal, planActionFeedback, planActionResult> pr2MotionPlanActionClient;
	private RjsActionClient<executeActionGoal, executeActionFeedback, executeActionResult> pr2MotionExecuteActionClient;
	private RjsActionClient<HeadScanActionGoal, HeadScanActionFeedback, HeadScanActionResult> headScanActionClient;
	
	public void setRosVariables() {
		super.setRosVariables();
		strafeActionClient = new RjsActionClient<MoveActionGoal, MoveActionFeedback, MoveActionResult>(
				rosnode.getConnectedNode(), 
				rosnode.getParameters().getString("/supervisor/action_servers/strafe"), 
				MoveActionGoal._TYPE, MoveActionFeedback._TYPE, MoveActionResult._TYPE);
		
		pr2MotionPlanActionClient = new RjsActionClient<planActionGoal, planActionFeedback, planActionResult>(
				rosnode.getConnectedNode(), 
				rosnode.getParameters().getString("/supervisor/action_servers/plan_motion"), 
				planActionGoal._TYPE, planActionFeedback._TYPE, planActionResult._TYPE);
		
		pr2MotionExecuteActionClient = new RjsActionClient<executeActionGoal, executeActionFeedback, executeActionResult>(
				rosnode.getConnectedNode(), 
				rosnode.getParameters().getString("/supervisor/action_servers/execute_motion"), 
				executeActionGoal._TYPE, executeActionFeedback._TYPE, executeActionResult._TYPE);
		
		headScanActionClient = new RjsActionClient<HeadScanActionGoal, HeadScanActionFeedback, HeadScanActionResult>(
				rosnode.getConnectedNode(), 
				rosnode.getParameters().getString("/supervisor/action_servers/head_scan"), 
				HeadScanActionGoal._TYPE, HeadScanActionFeedback._TYPE, HeadScanActionResult._TYPE);
	}
	
	public Action createAction(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		String actionName = actionExec.getActionTerm().getFunctor();
		Action action = null;
		switch(actionName) {
			case "disambiguate":
				action = new DisambiguateEntity(actionExec, rosAgArch);
				break;
			case "sparqlVerbalization":
				action = new GetSparqlVerba(actionExec, rosAgArch);
				break;
			case "analyzeSentence":
				action = new AnalyzeSentence(actionExec, rosAgArch);
				break;
			case "disambiSentence":
				action = new DisambiguateSentence(actionExec, rosAgArch);
				break;
			case "say":
				action = new Say(actionExec, rosAgArch);
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
			case "planMoveArm":
				action = new PR2MotionPlanMoveArm(actionExec, rosAgArch, pr2MotionPlanActionClient);
				break;
			case "execute":
				action = new PR2MotionExecute(actionExec, rosAgArch, pr2MotionExecuteActionClient);
				break;
			case "strafe":
				action = new Strafe(actionExec, (LAASAgArch) rosAgArch, strafeActionClient);
				break;
			case "getHatpPlan":
				action = new GetHATPPlan(actionExec, rosAgArch);
				break;
			case "mementarSubscribe":
				action = new MementarSubscribe(actionExec, (LAASAgArch) rosAgArch);
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
			case "initSub":
				action = new InitSub(actionExec, rosAgArch);
				break;
			case "scanTable":
				action = new ScanTable(actionExec, (LAASAgArch) rosAgArch, headScanActionClient);
				break;
			case "lookAt":
				action = new LookAt(actionExec, rosAgArch);
				break;
			default:
				break;
		}
			
		return action;
	}
	
}
