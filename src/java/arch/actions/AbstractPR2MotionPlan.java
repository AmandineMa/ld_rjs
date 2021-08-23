package arch.actions;

import java.util.Arrays;

import actionlib_msgs.GoalStatusArray;
import jason.asSemantics.ActionExec;
import jason.asSyntax.Literal;
import pr2_motion_tasks_msgs.planActionFeedback;
import pr2_motion_tasks_msgs.planActionGoal;
import pr2_motion_tasks_msgs.planActionResult;
import pr2_motion_tasks_msgs.planGoal;
import rjs.arch.actions.AbstractClientAction;
import rjs.arch.actions.ros.RjsActionClient;
import rjs.arch.agarch.AbstractROSAgArch;
import rjs.utils.Tools;

public abstract class AbstractPR2MotionPlan extends AbstractClientAction<planActionGoal, planActionFeedback, planActionResult> {
	protected planGoal goal;
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
	public void setResultAborted(planActionResult result) {
		String error = "";
		int errorCode = result.getResult().getErrorCode();
		if(errorCode == -1)
			error = "planning of the "+ actionGoal.getGoal().getAction() +" action failed";
		else
			error = "update of the world failed";
		actionExec.setFailureReason(Tools.stringFunctorAndTermsToBelLiteral("actionFailed",Arrays.asList(Literal.parseLiteral("plan"+ actionGoal.getGoal().getAction()),errorCode)), error);
	}
	
	

}

