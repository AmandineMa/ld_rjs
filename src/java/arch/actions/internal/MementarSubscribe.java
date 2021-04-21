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
import rjs.utils.Tools;

public class MementarSubscribe extends AbstractAction {

	public MementarSubscribe(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		super(actionExec, rosAgArch);
	}

	@Override
	public void execute() {
		String function = Tools.removeQuotes(actionTerms.get(0).toString());
		String subject = Tools.removeQuotes(actionTerms.get(1).toString());
		String predicate = Tools.removeQuotes(actionTerms.get(2).toString());
		String object = Tools.removeQuotes(actionTerms.get(3).toString());
		int count = Integer.parseInt(actionTerms.get(4).toString());
		
		ServiceResponseListener<MementarOccasionSubscriptionResponse> respListener = new ServiceResponseListener<MementarOccasionSubscriptionResponse>() {

			@Override
			public void onFailure(RemoteException arg0) {
				setActionExecuted(false);
			}

			@Override
			public void onSuccess(MementarOccasionSubscriptionResponse resp) {
				rosAgArch.addBelief("monitoring", Arrays.asList(resp.getId(),function,subject,predicate,object,count));
				setActionExecuted(true);
			}
		};
		
		MementarOccasionSubscriptionRequest req = getRosNode().newServiceRequestFromType(MementarOccasionSubscription._TYPE);
		String data = "["+function+"]"+subject+"|"+predicate+"|"+object;
		req.setData(data);
		req.setCount(count);
		getRosNode().callAsyncService("mementar_sub", respListener, req);
	}

}
