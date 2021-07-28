package arch.actions.robot;

import arch.actions.AbstractScan;
import arch.agarch.LAASAgArch;
import dt_head_gestures.HeadScanActionFeedback;
import dt_head_gestures.HeadScanActionGoal;
import dt_head_gestures.HeadScanActionResult;
import jason.asSemantics.ActionExec;
import rjs.arch.actions.ros.RjsActionClient;

public class ScanTable extends AbstractScan{

	public ScanTable(ActionExec actionExec, LAASAgArch rosAgArch,
			RjsActionClient<HeadScanActionGoal, HeadScanActionFeedback, HeadScanActionResult> actionClient) {
		super(actionExec, rosAgArch, actionClient);
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

	@Override
	protected void setPoint() {
		point.setX(1.2);
		point.setZ(0.4);
	}

}
