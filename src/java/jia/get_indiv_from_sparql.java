package jia;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import arch.agarch.LAASAgArch;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.StringTermImpl;
import jason.asSyntax.Term;
import rjs.utils.Tools;

public class get_indiv_from_sparql extends DefaultInternalAction {

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		String sparqlReq = Tools.removeQuotes(args[0].toString());
		String shouldReturn = args[1].toString();
		ListTerm params = getSparqlResult(ts, sparqlReq, shouldReturn);
		if(!params.isEmpty())
			return un.unifies(args[2], params);
		else
			return false;
	}
	
	protected ListTerm getSparqlResult(TransitionSystem ts, String sparqlReq, String shouldReturn) {
		ListTerm params = new ListTermImpl();
		switch(shouldReturn) {
		case "objectsOnly":
			sparqlReq = "SELECT ?0 WHERE { "+sparqlReq+" }";
			List<List<String>> sparqlResp = ((LAASAgArch) ts.getAgArch()).sparqlToEntity(sparqlReq);
			Set<StringTermImpl> set = new HashSet<StringTermImpl>();
			for(List<String> insideList : sparqlResp) {
				set.add(new StringTermImpl(insideList.get(0)));
			}
			params.addAll(set);
			break;
		case "objectsAndAgents":
			sparqlReq = "SELECT ?0 ?1 WHERE { "+sparqlReq+" }";
			params = Tools.listOfListToListTerm( ((LAASAgArch) ts.getAgArch()).sparqlToEntity(sparqlReq) );
			break;
		}
		return params;
	}
}
