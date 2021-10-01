package arch.actions.internal;

import java.util.Arrays;

import org.ros.exception.RemoteException;
import org.ros.node.service.ServiceResponseListener;

import arch.agarch.LAASAgArch;
import jason.asSemantics.ActionExec;
import mementar.MementarOcassionUnsubscription;
import mementar.MementarOcassionUnsubscriptionRequest;
import mementar.MementarOcassionUnsubscriptionResponse;
import rjs.arch.actions.AbstractAction;
import rjs.arch.agarch.AbstractROSAgArch;
import rjs.utils.Tools;

public class MementarUnsubscribe extends AbstractAction {

	public MementarUnsubscribe(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		super(actionExec, rosAgArch);
	}

	@Override
	public void execute() {
		int id = Integer.parseInt(actionTerms.get(0).toString());
//		String action = Tools.removeQuotes(actionTerms.get(1).toString());
		
		ServiceResponseListener<MementarOcassionUnsubscriptionResponse> respListener = new ServiceResponseListener<MementarOcassionUnsubscriptionResponse>() {

			@Override
			public void onFailure(RemoteException arg0) {
				setActionExecuted(false);
			}

			@Override
			public void onSuccess(MementarOcassionUnsubscriptionResponse resp) {
				rosAgArch.removeBelief("monitoring", Arrays.asList(resp.getId(),"_","_","_"));
				((LAASAgArch) rosAgArch).addMonitoringID(resp.getId());
				setActionExecuted(true);
			}
		};
		
		MementarOcassionUnsubscriptionRequest req = getRosNode().newServiceRequestFromType(MementarOcassionUnsubscription._TYPE);
		req.setId(id);
		getRosNode().callAsyncService("mementar_unsub", respListener, req);
	}

}
