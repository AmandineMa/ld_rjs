package arch.actions.internal;

import java.util.Arrays;

import org.ros.exception.RemoteException;
import org.ros.node.service.ServiceResponseListener;

import jason.asSemantics.ActionExec;
import mementar.MementarOccasionSubscription;
import mementar.MementarOccasionSubscriptionRequest;
import mementar.MementarOccasionSubscriptionResponse;
import rjs.arch.actions.AbstractAction;
import rjs.arch.agarch.AbstractROSAgArch;

public class MementarSubscribe extends AbstractAction {

	public MementarSubscribe(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		super(actionExec, rosAgArch);
	}

	@Override
	public void execute() {
		String action = removeQuotes(actionTerms.get(0).toString());
		String type = removeQuotes(actionTerms.get(1).toString());
		int count = Integer.parseInt(actionTerms.get(2).toString());
		
		ServiceResponseListener<MementarOccasionSubscriptionResponse> respListener = new ServiceResponseListener<MementarOccasionSubscriptionResponse>() {

			@Override
			public void onFailure(RemoteException arg0) {
				setActionExecuted(false);
			}

			@Override
			public void onSuccess(MementarOccasionSubscriptionResponse resp) {
				rosAgArch.addBelief("monitoring", Arrays.asList(resp.getId(),action,type,count));
				setActionExecuted(true);
			}
		};
		
		MementarOccasionSubscriptionRequest req = getRosNode().newServiceRequestFromType(MementarOccasionSubscription._TYPE);
		req.setData("[add]"+action+"|_|"+type);
		req.setCount(count);
		getRosNode().callAsyncService("mementar_sub", respListener, req);
	}

}
