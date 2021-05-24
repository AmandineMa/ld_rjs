package arch.actions.internal;

import java.util.Arrays;

import org.ros.exception.RemoteException;
import org.ros.node.service.ServiceResponseListener;

import arch.agarch.LAASAgArch;
import jason.asSemantics.ActionExec;
import mementar.MementarOccasionSubscription;
import mementar.MementarOccasionSubscriptionRequest;
import mementar.MementarOccasionSubscriptionResponse;
import rjs.arch.actions.AbstractAction;
import rjs.utils.Tools;

public class MementarSubscribe extends AbstractAction {

	public MementarSubscribe(ActionExec actionExec, LAASAgArch rosAgArch) {
		super(actionExec, rosAgArch);
	}

	@Override
	public void execute() {
		String function = Tools.removeQuotes(actionTerms.get(0).toString());
		String subject = Tools.removeQuotes(actionTerms.get(1).toString());
		String property = Tools.removeQuotes(actionTerms.get(2).toString());
		String object = Tools.removeQuotes(actionTerms.get(3).toString());
		int count = Integer.parseInt(actionTerms.get(4).toString());
		
		if(rosAgArch.findBel(Tools.stringFunctorAndTermsToBelLiteral("monitoring", Arrays.asList("_",function,subject,property,object,count))) == null) {
			ServiceResponseListener<MementarOccasionSubscriptionResponse> respListener = new ServiceResponseListener<MementarOccasionSubscriptionResponse>() {
	
				@Override
				public void onFailure(RemoteException arg0) {
					setActionExecuted(false);
				}
	
				@Override
				public void onSuccess(MementarOccasionSubscriptionResponse resp) {
					rosAgArch.addBelief("monitoring", Arrays.asList(resp.getId(),function,subject,property,object,count));
					((LAASAgArch) rosAgArch).addMonitoringID(resp.getId());
					setActionExecuted(true);
				}
			};
			
			MementarOccasionSubscriptionRequest req = getRosNode().newServiceRequestFromType(MementarOccasionSubscription._TYPE);
			String data = "["+function+"]"+subject+"|"+property+"|"+object;
			req.setData(data);
			req.setCount(count);
			getRosNode().callAsyncService("mementar_sub", respListener, req);
		} else {
			setActionExecuted(true);
		}
		
	}

}
