package jia;

import java.util.List;

import arch.agarch.LAASAgArch;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import rjs.utils.Tools;

public class is_of_class extends DefaultInternalAction {
	
	@Override public int getMinArgs() {
        return 3;
    }
    @Override public int getMaxArgs() {
        return 3;
    }

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		String onto = Tools.removeQuotes(args[0].toString());
		String subject =  Tools.removeQuotes(args[1].toString());
		String subjectClass =  Tools.removeQuotes(args[2].toString());
		List<String> ontoClass = ((LAASAgArch) ts.getAgArch()).callOnto(onto,"getUp", subject+" -s "+subjectClass).getValues();
		if(ontoClass != null && !ontoClass.isEmpty()) {
			return true;
		}else {
			return false;
		}
	}


}
