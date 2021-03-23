package arch.actions.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jason.asSemantics.ActionExec;
import jason.asSyntax.Atom;
import jason.asSyntax.Literal;
import jason.asSyntax.StringTermImpl;
import jason.asSyntax.Term;
import knowledge_sharing_planner_msgs.Disambiguation;
import knowledge_sharing_planner_msgs.DisambiguationRequest;
import knowledge_sharing_planner_msgs.DisambiguationResponse;
import knowledge_sharing_planner_msgs.Merge;
import knowledge_sharing_planner_msgs.MergeRequest;
import knowledge_sharing_planner_msgs.MergeResponse;
import knowledge_sharing_planner_msgs.SymbolTable;
import knowledge_sharing_planner_msgs.Triplet;
import knowledge_sharing_planner_msgs.Verbalization;
import knowledge_sharing_planner_msgs.VerbalizationRequest;
import knowledge_sharing_planner_msgs.VerbalizationResponse;
import rjs.arch.actions.AbstractAction;
import rjs.arch.agarch.AbstractROSAgArch;
import rjs.utils.Tools;

public class DisambiguateSentence extends AbstractAction {

	public DisambiguateSentence(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		super(actionExec, rosAgArch);
		setSync(true);
	}

	@Override
	public void execute() {
		
		DisambiguationRequest disambiReq = getRosNode().newServiceRequestFromType(Disambiguation._TYPE);
		disambiReq.setOntology(getRosNode().getParameters().getString("supervisor/ontologies/robot"));
		SymbolTable symbT = rosAgArch.createMessage(SymbolTable._TYPE);
		Iterator<Literal> matchsIterator = rosAgArch.get_beliefs_iterator("match(_,_,_)");
		
		String question = new String();
		
		while(matchsIterator.hasNext()) {
			List<String> individuals = Tools.removeQuotes(matchsIterator.next().getTerms());
			List<String> symbols = new ArrayList<String>();
			for(int i = 0; i < individuals.size(); i++) {
				symbols.add(Integer.toString(i));
			}
			symbT.setIndividuals(individuals);
			symbT.setSymbols(symbols);
			disambiReq.setSymbolTable(symbT);
			disambiReq.setIndividual(individuals.get(0));
			
			@SuppressWarnings("unchecked")
			List<String> mergedQuery = Tools.removeQuotes((List<Term>) rosAgArch.findBel("mergedQuery(_)").getTerm(0));
			List<Triplet> triplets = new ArrayList<Triplet>();
			for(String query : mergedQuery)
				triplets.add(getTriplet(query.toString()));
			
			disambiReq.setBaseFacts(triplets);
			
			DisambiguationResponse disambiResp = getRosNode().callSyncService("disambiguate", disambiReq);
			
			if(!disambiResp.getSuccess()) {
				actionExec.setResult(false);
				actionExec.setFailureReason(new Atom("disambiguation_not_found"), "could not find any disambiguation");
				return;
			}
			
			List<String> matchSparql = new ArrayList<String>();
			matchSparql = disambiResp.getSparqlResult();
			
			MergeRequest mergeReq = getRosNode().newServiceRequestFromType(Merge._TYPE);
			mergeReq.setContextQuery(mergedQuery);
			mergeReq.setBaseQuery(matchSparql);
			mergeReq.setPartial(true);
			MergeResponse mergeResp = getRosNode().callSyncService("ksp_merge", mergeReq);
			
			if(mergeResp == null || mergeResp.getMergedQuery().isEmpty()) {
				actionExec.setResult(false);
				actionExec.setFailureReason(new Atom("unpossible_merged"), "could not merge");
				return;
			}
			
			matchSparql = mergeResp.getMergedQuery();
			VerbalizationRequest verbaReq = getRosNode().newServiceRequestFromType(Verbalization._TYPE);
			verbaReq.setSparqlQuery(matchSparql);
			VerbalizationResponse verbaResp = getRosNode().callSyncService("verbalize", verbaReq);
			
			if(verbaResp == null || verbaResp.getVerbalization().isEmpty()) {
				actionExec.setResult(false);
				actionExec.setFailureReason(new Atom("no_verbalization"), "could not find any verbalization");
				return;
			}
			question += ((question.isEmpty()) ? "" : "or ") + verbaResp.getVerbalization();
			
		}
		rosAgArch.addBelief("verba(disambi_objects_sentence,"+new StringTermImpl(question)+")");
		rosAgArch.removeBelief("match(_,_,_)");
		rosAgArch.removeBelief("mergedQuery(_)");
		actionExec.setResult(true);
	
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

}
