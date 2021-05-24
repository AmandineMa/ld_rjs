package arch.actions.internal;

import java.util.Arrays;

import arch.actions.AbstractPR2MotionPlan;
import jason.asSemantics.ActionExec;
import pr2_motion_tasks_msgs.planActionFeedback;
import pr2_motion_tasks_msgs.planActionGoal;
import pr2_motion_tasks_msgs.planActionResult;
import rjs.arch.actions.ros.RjsActionClient;
import rjs.arch.agarch.AbstractROSAgArch;
import rjs.utils.Tools;

public class PR2MotionPlanPick extends AbstractPR2MotionPlan {
	

	public PR2MotionPlanPick(ActionExec actionExec, AbstractROSAgArch rosAgArch, RjsActionClient<planActionGoal, planActionFeedback, planActionResult> ac) {
		super(actionExec, rosAgArch, ac);
	}

	@Override
	protected void setGoalFields() {
		goal.setObjId(Tools.removeQuotes(actionTerms.get(0).toString()));
		goal.setAction("pick");
	}

	@Override
	public void setResultSucceeded(planActionResult result) {
		rosAgArch.addBelief(actionName, Arrays.asList("armUsed",result.getResult().getArmUsed()));
	}
	
	

}
