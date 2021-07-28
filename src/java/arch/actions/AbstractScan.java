package arch.actions;

import org.ros.message.Duration;

import arch.agarch.LAASAgArch;
import dt_head_gestures.HeadScanActionFeedback;
import dt_head_gestures.HeadScanActionGoal;
import dt_head_gestures.HeadScanActionResult;
import dt_head_gestures.HeadScanGoal;
import geometry_msgs.Point;
import geometry_msgs.PointStamped;
import jason.asSemantics.ActionExec;
import rjs.arch.actions.ros.RjsActionClient;

public abstract class AbstractScan extends AbstractClientPhysicalAction<HeadScanActionGoal, HeadScanActionFeedback, HeadScanActionResult> {
	
	protected Point point;

	public AbstractScan(ActionExec actionExec, LAASAgArch rosAgArch,
			RjsActionClient<HeadScanActionGoal, HeadScanActionFeedback, HeadScanActionResult> actionClient) {
		super(actionExec, rosAgArch, actionClient);
	}

	@Override
	public HeadScanActionGoal computeGoal() {
		HeadScanActionGoal goal = rosAgArch.createMessage(HeadScanActionGoal._TYPE);
		HeadScanGoal scanGoal = goal.getGoal();
		PointStamped ps = getRosNode().buildPointStamped("base_footprint");
		point = rosAgArch.createMessage(Point._TYPE);
		setPoint();
		ps.setPoint(point);
		scanGoal.setCentralPoint(ps);
		std_msgs.Duration duration = rosAgArch.createMessage(std_msgs.Duration._TYPE);
		Duration durationData = new Duration();
		durationData.secs = 2;
//		durationData.nsecs = 000000000;
		duration.setData(durationData);
		scanGoal.setDurationPerPoint(duration);
		scanGoal.setHeight(0.2);
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
	
	protected abstract void setPoint();

}
