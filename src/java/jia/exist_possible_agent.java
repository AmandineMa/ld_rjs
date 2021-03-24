package jia;

import java.util.List;

import arch.agarch.LAASAgArch;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.LiteralImpl;
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
		String object =  Tools.removeQuotes(((LiteralImpl) args[0]).getTerm(0).toString());
		List<String> isReachableBy = ((LAASAgArch) ts.getAgArch()).callOnto("getOn", object+":isReachableBy -s Human").getValues();
		if(isReachableBy != null && !isReachableBy.isEmpty()) {
			// for now return the first agent of the list TODO find a differentiating criteria when more than one human
			return isReachableBy.get(0);
		}else {
			return false;
		}
	}

}
