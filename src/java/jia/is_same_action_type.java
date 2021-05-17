package jia;

import java.util.List;

import arch.agarch.LAASAgArch;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import rjs.utils.Tools;

public class is_same_action_type extends DefaultInternalAction {
	
	@Override public int getMinArgs() {
        return 3;
    }
    @Override public int getMaxArgs() {
        return 3;
    }

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		String recognizedAction = Tools.removeQuotes(args[0].toString());
		recognizedAction = recognizedAction.substring(0, 1).toUpperCase() + recognizedAction.substring(1) + "Action";
		String planAction =  Tools.removeQuotes(args[1].toString());
		List<String> ontoClass = ((LAASAgArch) ts.getAgArch()).callOnto("class","getUp", planAction+" -s "+recognizedAction).getValues();
		if(ontoClass != null && !ontoClass.isEmpty()) {
			return true;
		}else {
			return false;
		}
	}


}
