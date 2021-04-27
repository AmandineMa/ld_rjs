package jia;

import java.util.Iterator;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.PredicateIndicator;
import jason.asSyntax.Term;

public class update_action_list extends DefaultInternalAction {

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		String param = args[0].toString();
		Iterator<Literal> it = ts.getAg().getBB().getCandidateBeliefs(new PredicateIndicator(param, 1));
		if(it != null) {
			while(it.hasNext()) {
				Literal bel = it.next();
				ts.getAg().delBel(bel);
				Literal belCopy = bel.copy();
				ListTerm actionList = (ListTerm) belCopy.getTerm(0);
				actionList.removeAll((ListTerm) args[1]);
				if(!actionList.isEmpty())
					ts.getAg().addBel(belCopy);
			}
		}
		return true;
	}
}
