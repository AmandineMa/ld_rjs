package arch.actions.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jason.asSemantics.ActionExec;
import jason.asSyntax.Atom;
import knowledge_sharing_planner_msgs.Merge;
import knowledge_sharing_planner_msgs.MergeRequest;
import knowledge_sharing_planner_msgs.MergeResponse;
import knowledge_sharing_planner_msgs.Understand;
import knowledge_sharing_planner_msgs.UnderstandRequest;
import knowledge_sharing_planner_msgs.UnderstandResponse;
import ontologenius.OntologeniusSparqlResponse;
import ontologenius.OntologeniusSparqlService;
import ontologenius.OntologeniusSparqlServiceRequest;
import ontologenius.OntologeniusSparqlServiceResponse;
import rjs.arch.actions.AbstractAction;
import rjs.arch.agarch.AbstractROSAgArch;
import rjs.utils.Tools;

public class AnalyzeSentence extends AbstractAction {

	public AnalyzeSentence(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		super(actionExec, rosAgArch);
		setSync(true);
	}

	@Override
	public void execute() {
		
		UnderstandRequest understandReq = getRosNode().newServiceRequestFromType(Understand._TYPE);	
		understandReq.setVerbalization(Tools.removeQuotes(actionTerms.get(0).toString()));
		UnderstandResponse understandResp = getRosNode().callSyncService("understand", understandReq);
		List<String> sparqlInput = understandResp.getSparqlQuery();
		
		if(understandResp == null || sparqlInput.isEmpty()) {
			actionExec.setResult(false);
			actionExec.setFailureReason(new Atom("not_understood"), "sentence could not be understood");
			return;
		}
		
		MergeRequest mergeReq = getRosNode().newServiceRequestFromType(Merge._TYPE);
		mergeReq.setContextQuery(Arrays.asList("?0 isAbove table_1", "?0 isInContainer ?1", "?1 isA VisibleDtBox"));
		mergeReq.setBaseQuery(sparqlInput);
		mergeReq.setPartial(false);
		MergeResponse mergeResp = getRosNode().callSyncService("ksp_merge", mergeReq);
		
		if(mergeResp == null || mergeResp.getMergedQuery().isEmpty()) {
			actionExec.setResult(false);
			actionExec.setFailureReason(new Atom("unpossible_merged"), "could not merge");
			return;
		}
		
		OntologeniusSparqlServiceRequest ontoSparqlReq = getRosNode().newServiceRequestFromType(OntologeniusSparqlService._TYPE);
		ontoSparqlReq.setQuery(mergedQueryToString(mergeResp));
		OntologeniusSparqlServiceResponse ontoSparqlResp = getRosNode().callSyncService("sparql", ontoSparqlReq);
		
		if(ontoSparqlResp == null || ontoSparqlResp.getResults().isEmpty()) {
			actionExec.setResult(false);
			actionExec.setFailureReason(new Atom("no_sparql_match"), "could not find any match");
			return;
		}
		
		for(OntologeniusSparqlResponse resp : ontoSparqlResp.getResults()) {
			rosAgArch.addBelief("match", new ArrayList<Object>(resp.getValues()));
		}
		
		if(ontoSparqlResp.getResults().size() > 1) {
			rosAgArch.addBelief("mergedQuery(" + Tools.arrayToListTerm(mergeResp.getMergedQuery())+")");
		}
		
		actionExec.setResult(true);
	}
	
	private String mergedQueryToString(MergeResponse mergeResp) {
		String query = new String();
		for(String element : mergeResp.getMergedQuery()) {
			query += ((query.isEmpty()) ? "" : ", ") + element;
		}
		return query;
		
	}

}
