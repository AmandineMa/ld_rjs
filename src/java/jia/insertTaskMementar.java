package jia;

import java.util.Arrays;
import java.util.logging.Logger;

import org.ros.message.Time;

import arch.agarch.LAASAgArch;
import arch.agarch.LAASAgArch.ActionIndicator;
import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;
import rjs.arch.agarch.AbstractROSAgArch;
import rjs.utils.Tools;

public class insertTaskMementar extends DefaultInternalAction {
	
	protected Logger logger = Logger.getLogger(insertTaskMementar.class.getName());
	
	@Override public int getMinArgs() {
        return 3;
    }
    @Override public int getMaxArgs() {
        return 3;
    }

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		checkArguments(args);
		Literal abstractTask = ts.getAg().findBel(Tools.stringFunctorAndTermsToBelLiteral("abstractTask", Arrays.asList((NumberTermImpl)args[0],"_","_","_")), new Unifier());
		Double taskTime = ((NumberTermImpl) abstractTask.getAnnot("add_time").getTerm(0)).solve();
		Time rosTime = Tools.rosTimeFromMSec(taskTime);
		ActionIndicator i;
		switch(args[2].toString()){
		case "start":
			i = ActionIndicator.START;
			break;
		case "end":
			i = ActionIndicator.END;
			break;
		default:
			throw JasonException.createWrongArgument(this,"third argument should be start or end");
		}
		((LAASAgArch)ts.getAgArch()).callInsertAction(Tools.removeQuotes(args[1].toString()), rosTime, i);
		return true;
	}

}
