package jia;

import java.util.List;

import arch.agarch.LAASAgArch;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import rjs.utils.Tools;

public class is_same_action_class extends DefaultInternalAction {
	
	@Override public int getMinArgs() {
        return 2;
    }
    @Override public int getMaxArgs() {
        return 2;
    }

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
			String recognizedAction = supervisorNameToOntoName(Tools.removeQuotes(args[0].toString()));
			String planAction =  Tools.removeQuotes(args[1].toString());
			List<String> ontoClass = ((LAASAgArch) ts.getAgArch()).callOnto("class","getUp", planAction+" -d 1 ").getValues();
			if(ontoClass != null && !ontoClass.isEmpty() && ontoClass.contains(recognizedAction)) {
				return true;
			}else {
				return false;
			}
	}
	
	protected String supervisorNameToOntoName(String supName) {
		return supName.substring(0, 1).toUpperCase() + supName.substring(1) + "Action";
	}


}
