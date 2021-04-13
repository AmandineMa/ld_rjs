package arch.agarch;

import org.ros.message.Time;

import mementar.MementarAction;
import ontologenius.OntologeniusService;
import ontologenius.OntologeniusServiceRequest;
import ontologenius.OntologeniusServiceResponse;
import rjs.arch.agarch.AbstractROSAgArch;

public class LAASAgArch extends AbstractROSAgArch {

	public LAASAgArch() {
		super();
	}
	
	public OntologeniusServiceResponse callOnto(String action, String param) {
		OntologeniusServiceRequest req = rosnode.newServiceRequestFromType(OntologeniusService._TYPE);
		req.setAction(action);
		req.setParam(param);
		return  rosnode.callSyncService("onto_individual", req);
	}
	
	public enum ActionIndicator {
		START,
		END
	}
	
	public void callInsertAction(String action, Time time, ActionIndicator actionIndicator) {
		MementarAction memAction = createMessage(MementarAction._TYPE);
		memAction.setName(action);
		switch(actionIndicator) {
		case START:
			memAction.setStartStamp(time);
			break;
		case END:
			memAction.setEndStamp(time);
			break;
		}
		rosnode.publish("insert_action", memAction); 
	}
	

}
