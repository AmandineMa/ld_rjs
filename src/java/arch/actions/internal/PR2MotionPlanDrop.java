package arch.actions.internal;

import jason.asSemantics.ActionExec;
import pr2_motion_tasks_msgs.planActionFeedback;
import pr2_motion_tasks_msgs.planActionGoal;
import pr2_motion_tasks_msgs.planActionResult;
import rjs.arch.actions.ros.RjsActionClient;
import rjs.arch.agarch.AbstractROSAgArch;
import rjs.utils.Tools;

public class PR2MotionPlanDrop extends AbstractPR2MotionPlan {

	public PR2MotionPlanDrop(ActionExec actionExec, AbstractROSAgArch rosAgArch,
			RjsActionClient<planActionGoal, planActionFeedback, planActionResult> ac) {
		super(actionExec, rosAgArch, ac);
	}

	@Override
	protected void setGoalFields() {
		goal.setAction("drop");
		//TODO à ne pas laisser en dur
		goal.setBoxId("throw_box_left");
		goal.setPlanGroup(Tools.removeQuotes(actionTerms.get(0).toString()));
	}
	
}
