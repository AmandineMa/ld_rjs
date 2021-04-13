package arch.actions.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jason.asSemantics.ActionExec;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.Term;
import knowledge_sharing_planner_msgs.MergeResponse;
import knowledge_sharing_planner_msgs.Understand;
import knowledge_sharing_planner_msgs.UnderstandRequest;
import knowledge_sharing_planner_msgs.UnderstandResponse;
import ontologenius.OntologeniusSparqlResponse;
import ontologenius.OntologeniusSparqlService;
import ontologenius.OntologeniusSparqlServiceRequest;
import ontologenius.OntologeniusSparqlServiceResponse;
import rjs.arch.agarch.AbstractROSAgArch;
import rjs.utils.Tools;

public class AnalyzeSentence extends AbstractDisambiguationAction {
	
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
			handleActionSaid(action);

			rosAgArch.addBelief("sparql_input("+Tools.arrayToStringArray(sparqlInput)+")");
			
			MergeResponse mergeResp = callMergeService(Tools.removeQuotes((List<Term>) actionTerms.get(1)), sparqlInput, false);

			if(mergeResp == null || mergeResp.getMergedQuery().isEmpty()) {
				handleFailure(new LiteralImpl("unpossible_merged("+Tools.arrayToStringArray(sparqlInput)+")"));
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
				List<String> params = new ArrayList<String>();
				params.add(ontoSparqlResp.getResults().get(0).getValues().get(0));
				if (action.equals("remove")) {
					params.add(rosAgArch.findBel("container(_)").getTerm(0).toString());
				}
				if(!action.isEmpty())
					rosAgArch.addBelief("action", Arrays.asList("todo", action, robotName, params));
				else {
					LiteralImpl actionBel =  (LiteralImpl) rosAgArch.findBel("action(\"planned\",_,\"robot\",_)");
					if(actionBel != null)
						rosAgArch.addBelief("action", Arrays.asList("todo", actionBel.getTerm(1), robotName, params));
					else {
						rosAgArch.addBelief("action", Arrays.asList("todo", action, robotName, params));
					}
						
				}
				clearBelief();
			}

			actionExec.setResult(true);
		} else {
			handleFailure("not_understood");
		}
	}

	private void handleActionSaid(String action) {
		if(action.isEmpty() && rosAgArch.findBel("mergedQuery(_)") == null) {
			handleFailure("no_action");
			return;
		} else if(action.equals("drop")) {
			clearBelief();
			rosAgArch.addBelief("action", Arrays.asList("todo", action, robotName, Arrays.asList(rosAgArch.findBel("container(_)").getTerm(0).toString())));
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
	
	protected String setFailure(String failureReason) {
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
		return msg;
	}

}
