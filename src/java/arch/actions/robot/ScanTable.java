package arch.actions.robot;

import org.ros.message.Duration;

import arch.actions.AbstractClientPhysicalAction;
import arch.agarch.LAASAgArch;
import dt_head_gestures.HeadScanActionFeedback;
import dt_head_gestures.HeadScanActionGoal;
import dt_head_gestures.HeadScanActionResult;
import dt_head_gestures.HeadScanGoal;
import geometry_msgs.Point;
import geometry_msgs.PointStamped;
import jason.asSemantics.ActionExec;
import rjs.arch.actions.ros.RjsActionClient;

public class ScanTable extends AbstractClientPhysicalAction<HeadScanActionGoal, HeadScanActionFeedback, HeadScanActionResult> {

	public ScanTable(ActionExec actionExec, LAASAgArch rosAgArch,
			RjsActionClient<HeadScanActionGoal, HeadScanActionFeedback, HeadScanActionResult> actionClient) {
		super(actionExec, rosAgArch, actionClient);
	}

	@Override
	public HeadScanActionGoal computeGoal() {
		HeadScanActionGoal goal = rosAgArch.createMessage(HeadScanActionGoal._TYPE);
		HeadScanGoal scanGoal = goal.getGoal();
		PointStamped ps = getRosNode().buildPointStamped("base_footprint");
		Point point = rosAgArch.createMessage(Point._TYPE);
		point.setX(1.2);
		point.setZ(0.4);
		ps.setPoint(point);
		scanGoal.setCentralPoint(ps);
		std_msgs.Duration duration = rosAgArch.createMessage(std_msgs.Duration._TYPE);
		Duration durationData = new Duration();
		durationData.secs = 1;
		durationData.nsecs = 500000000;
		duration.setData(durationData);
		scanGoal.setDurationPerPoint(duration);
		scanGoal.setHeight(0.3);
		scanGoal.setWidth(1.5);
		scanGoal.setStepLength(0.2);
		return goal;
	}

	@Override
	protected void setResultSucceeded(HeadScanActionResult result) {
	}

	@Override
	protected void setResultAborted(HeadScanActionResult result) {
	}

	@Override
	protected void endFeedbackReceived(HeadScanActionFeedback fb) {
	}

}
