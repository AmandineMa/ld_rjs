package arch.actions.internal;

import java.util.List;

import org.ros.exception.RemoteException;
import org.ros.node.service.ServiceResponseListener;

import jason.asSemantics.ActionExec;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.StringTermImpl;
import jason.asSyntax.Term;
import knowledge_sharing_planner_msgs.Verbalization;
import knowledge_sharing_planner_msgs.VerbalizationRequest;
import knowledge_sharing_planner_msgs.VerbalizationResponse;
import rjs.arch.actions.AbstractAction;
import rjs.arch.agarch.AbstractROSAgArch;

public class GetSparqlVerba extends AbstractAction {

	public GetSparqlVerba(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		super(actionExec, rosAgArch);
	}

	@Override
	public void execute() {
		ListTermImpl listTerm = (ListTermImpl) actionExec.getActionTerm().getTerm(0);
		@SuppressWarnings("unchecked")
		List<String> sparql = removeQuotes((List<Term>) actionTerms.get(0));
		String receiverID = removeQuotes(actionTerms.get(1).toString());
		
		ServiceResponseListener<VerbalizationResponse> respListener = new ServiceResponseListener<VerbalizationResponse>() {

			@Override
			public void onFailure(RemoteException e) {
				handleFailure(actionExec, actionName, e);
			}

			@Override
			public void onSuccess(VerbalizationResponse resp) {
				String object = rosAgArch.findBel("sparql_result(_,"+listTerm+")").getTerm(0).toString();
				StringTermImpl sentence = new StringTermImpl(resp.getVerbalization());
				rosAgArch.addBelief("verba("+object+","+sentence+")");
				actionExec.setResult(true);
				rosAgArch.actionExecuted(actionExec);
			}
		}; 
		VerbalizationRequest verbaReq = getRosNode().newServiceRequestFromType(Verbalization._TYPE);
		verbaReq.setSparqlQuery(sparql);
		verbaReq.setReceiverId(receiverID);
		getRosNode().callAsyncService("verbalize", respListener, verbaReq);

	}

}
