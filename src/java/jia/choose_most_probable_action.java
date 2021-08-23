package jia;

import java.util.Iterator;
import java.util.List;

import arch.agarch.LAASAgArch;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.Term;
import rjs.utils.Tools;

public class choose_most_probable_action extends DefaultInternalAction {
	
	@Override public int getMinArgs() {
        return 3;
    }
    @Override public int getMaxArgs() {
        return 3;
    }

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		ListTerm planActionList = (ListTerm) args[0];
		Iterator<Term> it = planActionList.iterator();
		Literal chosenAction = new LiteralImpl("");
		while(it.hasNext()) {
			Literal action = (Literal) it.next();
			if(Tools.removeQuotes(action.getTerm(1).toString()).equals("todo")) {
				chosenAction = action;
			}
		}
		if(chosenAction.getFunctor().isEmpty()) {
			it = planActionList.iterator();
			while(it.hasNext()) {
				Literal action = (Literal) it.next();
				if(Tools.removeQuotes(action.getTerm(1).toString()).equals("not_starting")||Tools.removeQuotes(action.getTerm(1).toString()).equals("not_finished")) {
					chosenAction = action;
				}
			}
		}
		if(chosenAction.getFunctor().isEmpty()) {
			it = planActionList.iterator();
			while(it.hasNext()) {
				Literal action = (Literal) it.next();
				if(Tools.removeQuotes(action.getTerm(1).toString()).equals("planned")) {
					chosenAction = action;
				}
			}
		}
		
		return un.unifies(args[1], chosenAction);
	}


}
