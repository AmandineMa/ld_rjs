package jia;

import java.util.List;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Term;

public class intersection_literal_list extends DefaultInternalAction {

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		List<Term> list1 = (ListTerm)args[0];
		List<Term> list2 = (ListTerm)args[1];
		ListTerm intersection = new ListTermImpl();
        for (Term t1 : list1) {
        	for (Term t2 : list2) {
	            if(un.unifies(t1, t2)) {
	            	intersection.add(t2);
	            }
        	}
        }
        return un.unifies(args[2], intersection);
		
	}


}
