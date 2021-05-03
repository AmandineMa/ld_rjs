package jia;

import java.util.Iterator;
import java.util.List;

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
				Literal belCopy = bel.copy().clearAnnots();
				ListTerm actionList = (ListTerm) belCopy.getTerm(0);
				Iterator<Term> actionListIte = actionList.iterator();
				List<Term> termsToRemove = (ListTerm) args[1];
				for(Term t1 : termsToRemove) {
					while(actionListIte.hasNext()) {
						if(un.unifies(t1, actionListIte.next())) {
							actionListIte.remove();
							break;
						}
					}
					actionListIte = actionList.iterator();
				}
				if(!actionList.isEmpty() && !belCopy.equals(bel.copy().clearAnnots())) {
					ts.getAg().delBel(bel);
					ts.getAg().addBel(belCopy);
				} else if(actionList.isEmpty())
					ts.getAg().delBel(bel);
			}
		}
		return true;
	}
}
