package jia;

import java.util.Iterator;
import java.util.List;

import arch.agarch.LAASAgArch;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.StringTermImpl;
import jason.asSyntax.Term;
import rjs.utils.Tools;

public class exist_possible_agent extends DefaultInternalAction {
	
	@Override public int getMinArgs() {
        return 1;
    }
    @Override public int getMaxArgs() {
        return 1;
    }
    
	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		ListTerm actionList =  (ListTerm) args[0];
		Iterator<Term> it = actionList.iterator();
		while(it.hasNext()) {
			Literal action = (Literal) it.next();
			// object always element 1
			List<String> isReachableBy = ((LAASAgArch) ts.getAgArch()).callOntoIndivRobot("getOn", 
					Tools.removeQuotes(action.getTerm(1).toString())+":isReachableBy -s Human").getValues();
			if(isReachableBy != null && !isReachableBy.isEmpty()) {
				if(isReachableBy.size() == 1)
					// agent always element 0
					action.setTerm(0, new StringTermImpl(isReachableBy.get(0)));
				else {
					//TODO find closest human
					action.setTerm(0, new StringTermImpl(isReachableBy.get(0)));
				}
			}else {
				it.remove();
			}
		}
		if(actionList.isEmpty())
			return false;
		return un.unifies(args[1], actionList);
		
	}

}
