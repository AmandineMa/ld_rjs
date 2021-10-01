package arch.actions.robot;

import java.util.Arrays;

import arch.actions.AbstractClientPhysicalAction;
import arch.agarch.LAASAgArch;
import jason.asSemantics.ActionExec;
import jason.asSyntax.Literal;
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
	public executeActionGoal computeGoal() {
		executeActionGoal goal = (executeActionGoal) newGoalMessage();
		return goal;
	}

	@Override
	protected void setResultSucceeded(executeActionResult result) {}

	@Override
	public void setResultAborted(executeActionResult result) {
		String error = "";
		int errorCode = result.getResult().getErrorCode();
		if(errorCode == -2)
			error = "execution of "+ actionName +" failed";
		else
			error = "the planning had failed";
		actionExec.setFailureReason(Tools.stringFunctorAndTermsToBelLiteral("actionFailed",Arrays.asList(Literal.parseLiteral("execute"+ actionName),errorCode)), error);
	}

	@Override
	protected void endFeedbackReceived(executeActionFeedback fb) {
	}

}
