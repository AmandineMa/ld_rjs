package arch.actions.robot;

import java.util.Arrays;

import actionlib_msgs.GoalStatusArray;
import arch.actions.AbstractClientPhysicalAction;
import arch.agarch.ExecutorAgArch;
import dt_navigation.MoveActionFeedback;
import dt_navigation.MoveActionGoal;
import dt_navigation.MoveActionResult;
import dt_navigation.MoveGoal;
import jason.asSemantics.ActionExec;
import rjs.arch.actions.ros.RjsActionClient;
import rjs.utils.Tools;

public class Strafe extends AbstractClientPhysicalAction<MoveActionGoal, MoveActionFeedback, MoveActionResult> {

	public Strafe(ActionExec actionExec, ExecutorAgArch rosAgArch,
			RjsActionClient<MoveActionGoal, MoveActionFeedback, MoveActionResult> actionClient) {
		super(actionExec, rosAgArch, actionClient);
	}


	@Override
	public void statusReceived(GoalStatusArray arg0) {}

	@Override
	public MoveActionGoal computeGoal() {
		MoveActionGoal goal = (MoveActionGoal) newGoalMessage();
		MoveGoal moveGoal = goal.getGoal();
		Integer i = Integer.parseInt(Tools.removeQuotes(actionTerms.get(0).toString()));
		moveGoal.setMoveType(i.byteValue());
		moveGoal.setFrameId(Tools.removeQuotes(actionTerms.get(1).toString()));
		return goal;
	}

	@Override
	protected void setResultSucceeded(MoveActionResult result) {
	}

	@Override
	protected void setResultAborted(MoveActionResult result) {
		
	}


	@Override
	protected void endFeedbackReceived(MoveActionFeedback fb) {
		rosAgArch.addBelief(actionName, Arrays.asList("distToGoal",fb.getFeedback().getDistanceToGoal()));
	}


}
