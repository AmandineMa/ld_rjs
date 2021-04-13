package arch.actions.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jason.asSemantics.ActionExec;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.StringTermImpl;
import jason.asSyntax.Term;
import knowledge_sharing_planner_msgs.Disambiguation;
import knowledge_sharing_planner_msgs.DisambiguationRequest;
import knowledge_sharing_planner_msgs.DisambiguationResponse;
import knowledge_sharing_planner_msgs.MergeResponse;
import knowledge_sharing_planner_msgs.SymbolTable;
import knowledge_sharing_planner_msgs.Triplet;
import knowledge_sharing_planner_msgs.Verbalization;
import knowledge_sharing_planner_msgs.VerbalizationRequest;
import knowledge_sharing_planner_msgs.VerbalizationResponse;
import rjs.arch.agarch.AbstractROSAgArch;
import rjs.utils.Tools;

public class DisambiguateSentence extends AbstractDisambiguationAction {

	public DisambiguateSentence(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		super(actionExec, rosAgArch);
		setSync(true);
	}

	@Override
	public void execute() {

		DisambiguationRequest disambiReq = getRosNode().newServiceRequestFromType(Disambiguation._TYPE);
		disambiReq.setOntology(getRosNode().getParameters().getString("supervisor/human_name"));
		SymbolTable symbT = rosAgArch.createMessage(SymbolTable._TYPE);
		Iterator<Literal> matchsIterator = rosAgArch.get_beliefs_iterator("match(_)");

		String question = new String();
		if(matchsIterator != null) {
			while(matchsIterator.hasNext()) {
				List<String> individuals = Tools.removeQuotes((ListTermImpl) matchsIterator.next().getTerms().get(0));
				String individual = individuals.get(0);
				List<String> symbols = new ArrayList<String>();
				for(int i = 0; i < individuals.size(); i++) {
					symbols.add(Integer.toString(i));
				}
				symbT.setIndividuals(individuals);
				symbT.setSymbols(symbols);
				disambiReq.setSymbolTable(symbT);
				disambiReq.setIndividual(individual);

				@SuppressWarnings("unchecked")
				List<String> mergedQuery = Tools.removeQuotes((List<Term>) rosAgArch.findBel("mergedQuery(_)").getTerm(0));
				List<Triplet> triplets = new ArrayList<Triplet>();
				for(String query : mergedQuery)
					triplets.add(getTriplet(query.toString()));

				disambiReq.setBaseFacts(triplets);

				DisambiguationResponse disambiResp = getRosNode().callSyncService("disambiguate", disambiReq);

				if(!disambiResp.getSuccess()) {
					handleFailure(new LiteralImpl("disambiguation_not_found("+individual+")"));
					return;
				}

				List<String> matchSparql = new ArrayList<String>();
				matchSparql = disambiResp.getSparqlResult();
				
				MergeResponse mergeResp = callMergeService(mergedQuery, matchSparql, true);

				if(mergeResp == null || mergeResp.getMergedQuery().isEmpty()) {
					handleFailure(new LiteralImpl("unpossible_merged("+Tools.arrayToStringArray(matchSparql)+")"));
					return;
				}

				matchSparql = mergeResp.getMergedQuery();
				VerbalizationRequest verbaReq = getRosNode().newServiceRequestFromType(Verbalization._TYPE);
				verbaReq.setSparqlQuery(matchSparql);
				VerbalizationResponse verbaResp = getRosNode().callSyncService("verbalize", verbaReq);

				if(verbaResp == null || verbaResp.getVerbalization().isEmpty()) {
					handleFailure(new LiteralImpl("no_verbalization("+Tools.arrayToStringArray(matchSparql)+")"));
					return;
				}
				question += ((question.isEmpty()) ? "" : "or ") + verbaResp.getVerbalization();

			}
			rosAgArch.addBelief("verba(disambi_objects_sentence,"+new StringTermImpl(question)+")");
			actionExec.setResult(true);
		} else {
			handleFailure("no_match_found");
		}

	}


	private Triplet getTriplet(String fact) {
		Triplet triplet = rosAgArch.createMessage(Triplet._TYPE);
		Pattern p = Pattern.compile("\\s*([^\\s]*)\\s+([^\\s]*)\\s+([^\\s]*)\\s*");
		Matcher m = p.matcher(fact);
		if(m.find()) {
			triplet.setFrom(m.group(1));
			triplet.setRelation(m.group(2));
			triplet.setOn(m.group(3));
		}
		return triplet;
	}

	@Override
	protected String setFailure(String failureReason) {
		String msg = new String();
		switch(failureReason) {
		case "disambiguation_not_found":
			msg = "could not find any disambiguation (srv disambiguate)";
			break;
		case "unpossible_merged":
			msg = "it was not possible to perform the merge (srv ksp_merge)";
			break;
		case "no_verbalization":
			msg = "could not find any verbalization (srv verbalize)";
			break;
		case "no_match_found":
			msg = "ask for disambiguation of sentence but no previous match";
			break;
		}
		return msg;
	}

}
