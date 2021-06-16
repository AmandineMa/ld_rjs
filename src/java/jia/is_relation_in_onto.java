package jia;

import java.util.Arrays;
import java.util.List;

import arch.agarch.LAASAgArch;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.Term;
import rjs.utils.Tools;

public class is_relation_in_onto extends DefaultInternalAction {

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		String subject = Tools.removeQuotes(args[0].toString());
		String property = "";
		LiteralImpl propertyL;
		if(args[1].isAtom()) {
			property = args[1].toString();
			propertyL = new LiteralImpl(property);
		} else {
			propertyL = (LiteralImpl) args[1];
			property = Tools.removeQuotes(propertyL.getFunctor().toString());
		}
		String object = Tools.removeQuotes(args[2].toString());
		boolean addBelToBB = Boolean.parseBoolean(args[3].toString());
		String agent = Tools.removeQuotes(args[4].toString());
		List<String> isBelieved;
		if(!args[0].isGround()) {
			isBelieved = ((LAASAgArch) ts.getAgArch()).callOntoIndiv("getFrom",object+":"+property,agent).getValues();
		}else if(!args[2].isGround()) {
			isBelieved = ((LAASAgArch) ts.getAgArch()).callOntoIndiv("getOn",subject+":"+property,agent).getValues();
		}else {
			isBelieved = ((LAASAgArch) ts.getAgArch()).callOntoIndiv("relationExists", subject+":"+property+":"+object,agent).getValues();
		}
		if(propertyL.negated()) {
			if(isBelieved != null && isBelieved.isEmpty()) {
				if(addBelToBB)
					ts.getAg().addBel(Tools.stringFunctorAndTermsToBelLiteral(property, Arrays.asList(subject, object)));
				return true;
			}else {
				return false;
			}
		}else {
			if(isBelieved != null && !isBelieved.isEmpty()) {
				if(addBelToBB)
					ts.getAg().addBel(Tools.stringFunctorAndTermsToBelLiteral(property, Arrays.asList(subject, object)));
				return true;
			}else {
				return false;
			}
		}
	}
}
