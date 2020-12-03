package arch.actions.internal;

import java.util.Arrays;

import actionlib_msgs.GoalStatusArray;
import jason.asSemantics.ActionExec;
import pr2_motion_tasks_msgs.planActionFeedback;
import pr2_motion_tasks_msgs.planActionGoal;
import pr2_motion_tasks_msgs.planActionResult;
import pr2_motion_tasks_msgs.planGoal;
import rjs.arch.actions.AbstractClientAction;
import rjs.arch.actions.ros.RjsActionClient;
import rjs.arch.agarch.AbstractROSAgArch;

public abstract class AbstractPR2MotionPlan extends AbstractClientAction<planActionGoal, planActionFeedback, planActionResult> {
	planGoal goal;
	planActionGoal actionGoal;

	public AbstractPR2MotionPlan(ActionExec actionExec, AbstractROSAgArch rosAgArch, RjsActionClient<planActionGoal, planActionFeedback, planActionResult> ac) {
		super(actionExec, rosAgArch, ac);
	}

	@Override
	public void feedbackReceived(planActionFeedback feedback) {
		rosAgArch.addBelief(actionName, Arrays.asList("actionFeedback",feedback.getFeedback().getStatus()));
	}

	@Override
	public void statusReceived(GoalStatusArray status) {
	}

	@Override
	public planActionGoal computeGoal() {
		actionGoal = (planActionGoal) newGoalMessage();
		goal = actionGoal.getGoal();
		setGoalFields();
		return actionGoal;
	}

	protected abstract void setGoalFields();

	@Override
	public void setResultSucceeded(planActionResult result) {}

	@Override
	protected void setResultAborted(planActionResult result) {}
	
	

}

