package arch.actions.internal;

import java.util.Arrays;

import org.ros.exception.RemoteException;
import org.ros.node.service.ServiceResponseListener;

import jason.asSemantics.ActionExec;
import mementar.MementarOcassionUnsubscription;
import mementar.MementarOcassionUnsubscriptionRequest;
import mementar.MementarOcassionUnsubscriptionResponse;
import rjs.arch.actions.AbstractAction;
import rjs.arch.agarch.AbstractROSAgArch;

public class MementarUnsubscribe extends AbstractAction {

	public MementarUnsubscribe(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		super(actionExec, rosAgArch);
	}

	@Override
	public void execute() {
		String action = removeQuotes(actionTerms.get(0).toString());
		int id = Integer.parseInt(actionTerms.get(1).toString());
		
		ServiceResponseListener<MementarOcassionUnsubscriptionResponse> respListener = new ServiceResponseListener<MementarOcassionUnsubscriptionResponse>() {

			@Override
			public void onFailure(RemoteException arg0) {
				setResult(false);
			}

			@Override
			public void onSuccess(MementarOcassionUnsubscriptionResponse resp) {
				rosAgArch.removeBelief(action+"Monitoring", Arrays.asList(resp.getId()));
				setResult(true);
			}
		};
		
		MementarOcassionUnsubscriptionRequest req = rosnode.newServiceRequestFromType(MementarOcassionUnsubscription._TYPE);
		req.setId(id);
		rosnode.callAsyncService("mementar_unsub", respListener, req);
	}

}
