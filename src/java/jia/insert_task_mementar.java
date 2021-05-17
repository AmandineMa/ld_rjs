package jia;

import org.ros.message.Time;

import arch.agarch.LAASAgArch;
import arch.agarch.LAASAgArch.ActionIndicator;
import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import rjs.arch.agarch.AbstractROSAgArch;
import rjs.utils.Tools;

public class insert_task_mementar extends DefaultInternalAction {
	
	@Override public int getMinArgs() {
        return 2;
    }
    @Override public int getMaxArgs() {
        return 2;
    }

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		checkArguments(args);
		
		Time rosTime = ((AbstractROSAgArch) ts.getAgArch()).getConnectedNode().getCurrentTime();
		ActionIndicator i;
		if(args[1].toString().equals("start")) {
			i = ActionIndicator.START;
		}else if(args[1].toString().equals("end")) {
			i = ActionIndicator.END;
		}else {
			throw JasonException.createWrongArgument(this,"third argument should be start or end");
		}
		((LAASAgArch)ts.getAgArch()).callInsertAction(Tools.removeQuotes(args[0].toString()), rosTime, i);
		return true;
	}

}
