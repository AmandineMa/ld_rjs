package jia;

import agent.OntoAgent;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import rjs.utils.Tools;

public class set_predicate_with_list extends DefaultInternalAction {

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		String property = Tools.removeQuotes(args[0].toString());
		((OntoAgent) ts.getAg()).addPredWithList(property);
		return true;
	}
}
