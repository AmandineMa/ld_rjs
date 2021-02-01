package arch.actions.internal;

import java.util.Arrays;
import java.util.List;

import org.ros.exception.RemoteException;
import org.ros.node.service.ServiceResponseListener;

import arch.agarch.LAASAgArch;
import jason.asSemantics.ActionExec;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.StringTermImpl;
import knowledge_sharing_planner_msgs.Disambiguation;
import knowledge_sharing_planner_msgs.DisambiguationRequest;
import knowledge_sharing_planner_msgs.DisambiguationResponse;
import knowledge_sharing_planner_msgs.SymbolTable;
import knowledge_sharing_planner_msgs.Triplet;
import rjs.arch.actions.AbstractAction;
import rjs.arch.agarch.AbstractROSAgArch;
import rjs.utils.Tools;

public class Disambiguate extends AbstractAction {

	public Disambiguate(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		super(actionExec, rosAgArch);
	}

	@Override
	public void execute() {
		String individual = Tools.removeQuotes(actionTerms.get(0).toString());
		String ontology = Tools.removeQuotes(actionTerms.get(1).toString());
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
		DisambiguationRequest disambiReq = getRosNode().newServiceRequestFromType(Disambiguation._TYPE);
		disambiReq.setIndividual(individual);
		disambiReq.setOntology(getRosNode().getParameters().getString("supervisor/ontologies/"+ontology));
		List<String> ontoRep = ((LAASAgArch) rosAgArch).callOnto("getOn", individual+":isAbove").getValues();
		if(!ontoRep.isEmpty()) {
			Triplet ctx = rosAgArch.createMessage(Triplet._TYPE);
			ctx.setFrom("?0");
			ctx.setRelation("isAbove");
			ctx.setOn(getRosNode().getParameters().getString("supervisor/table_name"));
			disambiReq.setBaseFacts(Arrays.asList(ctx));
		}
		disambiReq.setReplan(replan);
		SymbolTable symbT = rosAgArch.createMessage(SymbolTable._TYPE);
		symbT.setIndividuals(Arrays.asList(individual));
		symbT.setSymbols(Arrays.asList("?0"));
		disambiReq.setSymbolTable(symbT);
		getRosNode().callAsyncService("disambiguate", respListener, disambiReq);

	}

}
