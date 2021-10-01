package jia;

import java.util.List;

import agent.OntoAgent;
import arch.agarch.LAASAgArch;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import rjs.utils.Tools;

public class set_predicate_with_list extends DefaultInternalAction {

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		String property = Tools.removeQuotes(args[0].toString());
		List<String> upClassProperty = ((LAASAgArch) ts.getAgArch()).callOntoObjProperty("getDown", property).getValues();
		for(String p : upClassProperty) {
			((OntoAgent) ts.getAg()).addPredWithList(p);
		}
		return true;
	}
}
