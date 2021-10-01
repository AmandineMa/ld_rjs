package arch.actions.internal;

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

public class PR2MotionPlanPlace extends AbstractPR2MotionPlan {

	public PR2MotionPlanPlace(ActionExec actionExec, AbstractROSAgArch rosAgArch,
			RjsActionClient<planActionGoal, planActionFeedback, planActionResult> ac) {
		super(actionExec, rosAgArch, ac);
	}

	@Override
	protected void setGoalFields() {
		String object = Tools.removeQuotes(actionTerms.get(0).toString());
		List<String> ontoClass = ((LAASAgArch)rosAgArch).callOntoIndiv("getUp", object,"robot").getValues();
//		if(ontoClass.contains("Spot")) {
//			goal.setAction("placeOnFrame");
//		}else if(ontoClass.contains("Cube")) {
			goal.setAction("placeOnTopCube");
//		}
//		if(ontoClass.contains("DtCube")) {
//			goal.setAction("placeStick");
//		}else {
//			goal.setAction("placeOnTopCube");
//		}
		goal.setBoxId(object);
		goal.setPlanGroup(Tools.removeQuotes(actionTerms.get(1).toString()));
	}
	
}
