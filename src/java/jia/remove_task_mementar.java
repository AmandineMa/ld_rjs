package jia;

import arch.agarch.LAASAgArch;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import rjs.utils.Tools;

public class remove_task_mementar extends DefaultInternalAction {
	
	@Override public int getMinArgs() {
        return 1;
    }
    @Override public int getMaxArgs() {
        return 1;
    }

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		checkArguments(args);
		
		((LAASAgArch)ts.getAgArch()).callMementarAction("removeAction", Tools.removeQuotes(args[0].toString()));
		return true;
	}

}
