package arch.actions.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jason.asSemantics.ActionExec;
import jason.asSyntax.Atom;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.Term;
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
	
	private String sentence;
	private String robotName;

	public AnalyzeSentence(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		super(actionExec, rosAgArch);
		setSync(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute() {
		robotName = getRosNode().getParameters().getString("supervisor/robot_name");
		sentence = actionTerms.get(0).toString();
		UnderstandRequest understandReq = getRosNode().newServiceRequestFromType(Understand._TYPE);	
		understandReq.setVerbalization(Tools.removeQuotes(sentence));
		UnderstandResponse understandResp = getRosNode().callSyncService("understand", understandReq);
		
		if(understandResp.getComprehensionScore() > 0.6 && understandResp.getAnalyseScore() > 0.5) {
			
			List<String> sparqlInput = understandResp.getSparqlQuery();

			if(understandResp == null || sparqlInput.isEmpty()) {
				handleFailure("not_understood");
				return;
			}
			
			String action = understandResp.getAction().toLowerCase();
			handleAction(action);

			rosAgArch.addBelief("sparql_input("+Tools.arrayToStringArray(sparqlInput)+")");

			MergeRequest mergeReq = getRosNode().newServiceRequestFromType(Merge._TYPE);
			mergeReq.setContextQuery(Tools.removeQuotes((List<Term>) actionTerms.get(1)));
			mergeReq.setBaseQuery(sparqlInput);
			mergeReq.setPartial(false);
			MergeResponse mergeResp = getRosNode().callSyncService("ksp_merge", mergeReq);

			if(mergeResp == null || mergeResp.getMergedQuery().isEmpty()) {
				handleFailure("unpossible_merged");
				return;
			}

			OntologeniusSparqlServiceRequest ontoSparqlReq = getRosNode().newServiceRequestFromType(OntologeniusSparqlService._TYPE);
			ontoSparqlReq.setQuery(mergedQueryToString(mergeResp));
			OntologeniusSparqlServiceResponse ontoSparqlResp = getRosNode().callSyncService("sparql_human", ontoSparqlReq);

			if(ontoSparqlResp == null || ontoSparqlResp.getResults().isEmpty()) {
				handleFailure("no_sparql_match");
				return;
			}
			
			if(ontoSparqlResp.getResults().size() > 1) {
				rosAgArch.addBelief("mergedQuery(" + Tools.arrayToListTerm(mergeResp.getMergedQuery())+")");
				for(OntologeniusSparqlResponse resp : ontoSparqlResp.getResults()) {
					rosAgArch.addBelief("match("+Tools.arrayToStringArray(new ArrayList<String>(resp.getValues()))+")");
				}
				rosAgArch.addBelief("action", Arrays.asList("planned", action, robotName, new ArrayList<String>()));
			} else {
				List<String> object =  Arrays.asList(ontoSparqlResp.getResults().get(0).getValues().get(0));
//				rosAgArch.addBelief("uniqueMatch("+ object +")");
				//TODO faire un get robot name
				if(!action.isEmpty())
					rosAgArch.addBelief("action", Arrays.asList("todo", action, robotName, object));
				else {
					LiteralImpl actionBel =  (LiteralImpl) rosAgArch.findBel("action(\"planned\",_,\"robot\",_)");
					if(actionBel != null)
						rosAgArch.addBelief("action", Arrays.asList("todo", actionBel.getTerm(1), robotName, object));
					else {
						rosAgArch.addBelief("action", Arrays.asList("todo", action, robotName, object));
					}
						
				}
				clearBelief();
			}

			actionExec.setResult(true);
		} else {
			handleFailure("not_understood");
		}
	}

	private void handleAction(String action) {
		if(action.isEmpty() && rosAgArch.findBel("mergedQuery(_)") == null) {
			handleFailure("no_action");
			return;
		} else if(action.equals("drop")) {
			clearBelief();
			rosAgArch.addBelief("action", Arrays.asList("todo", action, robotName, "_"));
			actionExec.setResult(true);
			return;
		}
	}

	private String mergedQueryToString(MergeResponse mergeResp) {
		String query = new String();
		for(String element : mergeResp.getMergedQuery()) {
			query += ((query.isEmpty()) ? "" : ", ") + element;
		}
		return query;

	}
	
	private void clearBelief() {
		rosAgArch.removeBelief("mergedQuery(_)");
		rosAgArch.removeBelief("sparql_input(_)");
		rosAgArch.removeBelief("match(_)");
		rosAgArch.removeBelief("sentence(_)"); 
		rosAgArch.removeBelief("verba(disambi_objects_sentence,_)");
	}
	
	private void handleFailure(String failureReason) {
		String msg = new String();
		switch(failureReason) {
		case "not_understood":
			msg = "sentence could not be understood (srv understand)";
			rosAgArch.addBelief("verba(not_understood,"+sentence+")");
			break;
		case "no_action":
			msg = "there was no action in the sentence";
			rosAgArch.addBelief("verba(not_understood,"+sentence+")");
			break;
		case "unpossible_merged":
			msg = "it was not possible to perform the merge (srv ksp_merge)";
			break;
		case "no_sparql_match":
			msg = "could not find any sparl match (srv sparql_human)";
			break;
		}
		actionExec.setResult(false);
		actionExec.setFailureReason(new Atom(failureReason), msg);
		clearBelief();
	}

}
