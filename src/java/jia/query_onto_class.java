package jia;

import java.util.List;

import arch.agarch.LAASAgArch;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import rjs.utils.Tools;

public class query_onto_class extends DefaultInternalAction {

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		String subject =  Tools.removeQuotes(args[0].toString());
		String subjectClass =  Tools.removeQuotes(args[1].toString());
		List<String> isCube = ((LAASAgArch) ts.getAgArch()).callOnto("getUp", subject+" -s "+subjectClass).getValues();
		if(isCube != null && !isCube.isEmpty()) {
			return true;
		}else {
			return false;
		}
	}


}
