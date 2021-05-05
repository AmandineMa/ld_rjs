package jia;

import java.util.Arrays;
import java.util.logging.Logger;

import org.ros.message.Time;

import arch.agarch.LAASAgArch;
import arch.agarch.LAASAgArch.ActionIndicator;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;
import rjs.utils.Tools;

public class insert_task_mementar extends DefaultInternalAction {
	
	protected Logger logger = Logger.getLogger(insert_task_mementar.class.getName());
	
	@Override public int getMinArgs() {
        return 2;
    }
    @Override public int getMaxArgs() {
        return 2;
    }

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		checkArguments(args);

		Literal abstractTask = ts.getAg().findBel(Tools.stringFunctorAndTermsToBelLiteral("abstractTask", Arrays.asList((NumberTermImpl)args[0],"_","_")), new Unifier());
		if(abstractTask == null)
			return false;
		Double taskTime = ((NumberTermImpl) abstractTask.getAnnot("add_time").getTerm(0)).solve();
		Time rosTime = Tools.rosTimeFromMSec(taskTime);
		((LAASAgArch)ts.getAgArch()).callInsertAction(Tools.removeQuotes(args[1].toString()), rosTime, ActionIndicator.START);
		
		abstractTask = ts.getAg().findBel(Tools.stringFunctorAndTermsToBelLiteral("abstractTask", Arrays.asList((NumberTermImpl)args[0],"_","_","_")), new Unifier());
		if(abstractTask == null)
			return false;
		taskTime = ((NumberTermImpl) abstractTask.getAnnot("add_time").getTerm(0)).solve();
		rosTime = Tools.rosTimeFromMSec(taskTime);
		((LAASAgArch)ts.getAgArch()).callInsertAction(Tools.removeQuotes(args[1].toString()), rosTime, ActionIndicator.END);
		return true;
	}

}
