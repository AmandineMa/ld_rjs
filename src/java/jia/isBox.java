package jia;

import java.util.List;

import arch.agarch.AgArch;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import rjs.utils.Tools;

public class isBox extends DefaultInternalAction {

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		String param = Tools.removeQuotes(args[0].toString());
		List<String> isBox = ((AgArch) ts.getAgArch()).callOnto("getUp", param+" -s Box").getValues();
		if(isBox != null && !isBox.isEmpty()) {
			return true;
		}else {
			return false;
		}
	}
}
