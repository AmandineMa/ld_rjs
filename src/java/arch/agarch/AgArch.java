package arch.agarch;

import ontologenius.OntologeniusService;
import ontologenius.OntologeniusServiceRequest;
import ontologenius.OntologeniusServiceResponse;
import rjs.arch.agarch.AbstractROSAgArch;

public class AgArch extends AbstractROSAgArch {

	public AgArch() {
		super();
	}

	public OntologeniusServiceResponse callOnto(String action, String param) {
		OntologeniusServiceRequest req = rosnode.newServiceRequestFromType(OntologeniusService._TYPE);
		req.setAction(action);
		req.setParam(param);
		return  rosnode.callSyncService("onto_individual", req);
	}
	

}
