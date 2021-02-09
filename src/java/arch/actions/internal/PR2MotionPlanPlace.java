package arch.actions.internal;

import jason.asSemantics.ActionExec;
import pr2_motion_tasks_msgs.planActionFeedback;
import pr2_motion_tasks_msgs.planActionGoal;
import pr2_motion_tasks_msgs.planActionResult;
import rjs.arch.actions.ros.RjsActionClient;
import rjs.arch.agarch.AbstractROSAgArch;
import rjs.utils.Tools;

public class PR2MotionPlanPlace extends AbstractPR2MotionPlan {

	public PR2MotionPlanPlace(ActionExec actionExec, AbstractROSAgArch rosAgArch,
			RjsActionClient<planActionGoal, planActionFeedback, planActionResult> ac) {
		super(actionExec, rosAgArch, ac);
	}

	@Override
	protected void setGoalFields() {
		goal.setAction("place");
		goal.setBoxId(Tools.removeQuotes(actionTerms.get(0).toString()));
		goal.setPlanGroup(Tools.removeQuotes(actionTerms.get(1).toString()));
	}
	
}
