package arch.actions.internal;

import java.util.Arrays;

import org.ros.exception.RemoteException;
import org.ros.node.service.ServiceResponseListener;

import jason.asSemantics.ActionExec;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.StringTermImpl;
import knowledge_sharing_planner_msgs.Disambiguation;
import knowledge_sharing_planner_msgs.DisambiguationRequest;
import knowledge_sharing_planner_msgs.DisambiguationResponse;
import knowledge_sharing_planner_msgs.Triplet;
import rjs.arch.actions.AbstractAction;
import rjs.arch.agarch.AbstractROSAgArch;

public class Disambiguate extends AbstractAction {

	public Disambiguate(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		super(actionExec, rosAgArch);
	}

	@Override
	public void execute() {
		String individual = removeQuotes(actionTerms.get(0).toString());
		String ontology = removeQuotes(actionTerms.get(1).toString());
		boolean replan = Boolean.parseBoolean(actionTerms.get(2).toString());
		
		ServiceResponseListener<DisambiguationResponse> respListener = new ServiceResponseListener<DisambiguationResponse>() {

			@Override
			public void onFailure(RemoteException e) {
				handleFailure(actionExec, actionName, e);
			}

			@Override
			public void onSuccess(DisambiguationResponse resp) {
				ListTermImpl list = new ListTermImpl();
				for(String e : resp.getSparqlResult()) {
					list.add(new StringTermImpl(e));
				}
				rosAgArch.addBelief("sparql_result("+new StringTermImpl(individual)+","+list.toString()+")");
				actionExec.setResult(true);
				rosAgArch.actionExecuted(actionExec);
			}
		};
		DisambiguationRequest disambiReq = rosnode.newServiceRequestFromType(Disambiguation._TYPE);
		disambiReq.setIndividual(individual);
		disambiReq.setOntology(rosnode.getParameters().getString("supervisor/ontologies/"+ontology));
		Triplet ctx = rosAgArch.createMessage(Triplet._TYPE);
		ctx.setFrom(individual);
		ctx.setRelation("isOnTopOf");
		ctx.setOn("table_l_0");
		disambiReq.setBaseFacts(Arrays.asList(ctx));
		disambiReq.setReplan(replan);
		rosnode.callAsyncService("disambiguate", respListener, disambiReq);

	}

}
