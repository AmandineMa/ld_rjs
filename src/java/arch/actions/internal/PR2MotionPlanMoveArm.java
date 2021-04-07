package arch.actions.internal;

import jason.asSemantics.ActionExec;
import pr2_motion_tasks_msgs.planActionFeedback;
import pr2_motion_tasks_msgs.planActionGoal;
import pr2_motion_tasks_msgs.planActionResult;
import rjs.arch.actions.ros.RjsActionClient;
import rjs.arch.agarch.AbstractROSAgArch;
import rjs.utils.Tools;

public class PR2MotionPlanMoveArm extends AbstractPR2MotionPlan {

	public PR2MotionPlanMoveArm(ActionExec actionExec, AbstractROSAgArch rosAgArch,
			RjsActionClient<planActionGoal, planActionFeedback, planActionResult> ac) {
		super(actionExec, rosAgArch, ac);
	}

	@Override
	protected void setGoalFields() {
		goal.setAction("move");
		goal.setPlanGroup(Tools.removeQuotes(actionTerms.get(0).toString()));
		goal.setPredefinedPoseId(Tools.removeQuotes(actionTerms.get(1).toString()));
	}
	
}

