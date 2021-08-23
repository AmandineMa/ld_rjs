package arch.actions.internal;

import java.util.Arrays;
import java.util.List;

import arch.actions.AbstractPR2MotionPlan;
import arch.agarch.LAASAgArch;
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
		String object = Tools.removeQuotes(actionTerms.get(0).toString());
		goal.setObjId(object);
		List<String> ontoClass = ((LAASAgArch)rosAgArch).callOntoIndiv("getUp", object,"robot").getValues();
		if(ontoClass.contains("DtCube")) {
			goal.setAction("pick_dt");
		}else if(ontoClass.contains("Cube")) {
			goal.setAction("pick_didine");
		}
		goal.setPlanGroup("right_arm");
	}

	@Override
	public void setResultSucceeded(planActionResult result) {
		rosAgArch.addBelief(actionName, Arrays.asList("armUsed",result.getResult().getArmUsed()));
	}
	
}
