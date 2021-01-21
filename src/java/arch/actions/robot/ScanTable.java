package arch.actions.robot;

import org.ros.message.Duration;

import dt_head_gestures.HeadScan;
import dt_head_gestures.HeadScanRequest;
import dt_head_gestures.HeadScanResponse;
import geometry_msgs.Point;
import geometry_msgs.PointStamped;
import jason.asSemantics.ActionExec;
import rjs.arch.actions.AbstractAction;
import rjs.arch.agarch.AbstractROSAgArch;

public class ScanTable extends AbstractAction {

	public ScanTable(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		super(actionExec, rosAgArch);
		setSync(true);
	}

	@Override
	public void execute() {
		HeadScanRequest req = getRosNode().newServiceRequestFromType(HeadScan._TYPE);
		PointStamped ps = getRosNode().buildPointStamped("base_footprint");
		Point point = rosAgArch.createMessage(Point._TYPE);
		point.setX(1.2);
		point.setZ(0.4);
		ps.setPoint(point);
		req.setCentralPoint(ps);
		std_msgs.Duration duration = rosAgArch.createMessage(std_msgs.Duration._TYPE);
		Duration durationData = new Duration();
		durationData.secs = 1;
		durationData.nsecs = 500000000;
		duration.setData(durationData);
		req.setDurationPerPoint(duration);
		req.setHeight(0.3);
		req.setWidth(1.5);
		req.setStepLength(0.2);
		HeadScanResponse resp = getRosNode().callSyncService("head_scan", req);
		if(resp != null) {
			setActionExecuted(resp.getSuccess());
		} else {
			setActionExecuted(false);
		}
		
	}

}
