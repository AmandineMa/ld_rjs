package jia;

import java.util.Arrays;
import java.util.List;

import arch.agarch.LAASAgArch;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import rjs.utils.Tools;

public class query_onto_individual extends DefaultInternalAction {

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		String subject = Tools.removeQuotes(args[0].toString());
		String property = Tools.removeQuotes(args[1].toString());
		String object = Tools.removeQuotes(args[2].toString());
		boolean addBelToBB = Boolean.parseBoolean(args[3].toString());
		List<String> isBelieved = ((LAASAgArch) ts.getAgArch()).callOnto("relationExists", subject+":"+property+":"+object).getValues();
		if(isBelieved != null && !isBelieved.isEmpty()) {
			if(addBelToBB)
				ts.getAg().addBel(Tools.stringFunctorAndTermsToBelLiteral(property, Arrays.asList(subject, object)));
			return true;
		}else {
			return false;
		}
	}
}
