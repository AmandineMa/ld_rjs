package arch.actions.robot;

import actionlib_msgs.GoalStatusArray;
import arch.actions.AbstractClientPhysicalAction;
import arch.agarch.LAASAgArch;
import jason.asSemantics.ActionExec;
import pr2_motion_tasks_msgs.executeActionFeedback;
import pr2_motion_tasks_msgs.executeActionGoal;
import pr2_motion_tasks_msgs.executeActionResult;
import rjs.arch.actions.ros.RjsActionClient;
import rjs.arch.agarch.AbstractROSAgArch;
import rjs.utils.Tools;

public class PR2MotionExecute extends AbstractClientPhysicalAction<executeActionGoal, executeActionFeedback, executeActionResult> {

	public PR2MotionExecute(ActionExec actionExec, AbstractROSAgArch rosAgArch,
			RjsActionClient<executeActionGoal, executeActionFeedback, executeActionResult> actionClient) {
		super(actionExec, (LAASAgArch) rosAgArch, actionClient);
		actionName =  Tools.removeQuotes(actionTerms.get(0).toString());
	}

	@Override
	public void feedbackReceived(executeActionFeedback feedback) {
	}

	@Override
	public void statusReceived(GoalStatusArray status) {
	}

	@Override
	public executeActionGoal computeGoal() {
		executeActionGoal goal = (executeActionGoal) newGoalMessage();
		return goal;
	}

	@Override
	protected void setResultSucceeded(executeActionResult result) {}

	@Override
	protected void setResultAborted(executeActionResult result) {}

	@Override
	protected void endFeedbackReceived(executeActionFeedback fb) {
	}

}
