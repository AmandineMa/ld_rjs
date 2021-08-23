package jia;

import java.util.List;

import arch.agarch.LAASAgArch;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import rjs.utils.Tools;


public class are_planned_preds_only_wait extends is_same_action_class {
	
	@Override public int getMinArgs() {
        return 1;
    }
    @Override public int getMaxArgs() {
        return 1;
    }

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		String humanName = Tools.removeQuotes(args[0].toString());
		String initialActionName = Tools.removeQuotes(args[1].toString());
		String lastHumanAction = initialActionName;
		List<Double> preds = Tools.listTermNumbers_to_list((ListTermImpl) args[2]);
		if(preds.size() == 1) {
			String agent = "";
			boolean waitClass = true;
			int pred = preds.get(0).intValue();
			while(waitClass) {
				Literal actionBel = ((LAASAgArch) ts.getAgArch()).findBel("action("+pred+",_,_,_,_,_,_)");
				List<Term> actionBelTerms = (List<Term>) actionBel.getTerms();
				agent = Tools.removeQuotes(actionBelTerms.get(3).toString());
				preds = Tools.listTermNumbers_to_list((ListTermImpl) actionBelTerms.get(5));
				pred = preds.get(0).intValue();
				if(agent.equals(humanName)) {
					String actionName = Tools.removeQuotes(actionBelTerms.get(2).toString());
					List<String> ontoClass = ((LAASAgArch) ts.getAgArch()).callOnto("class","getUp", actionName+" -s DefaultAction").getValues();
					if(ontoClass != null && !ontoClass.isEmpty()) {
						lastHumanAction = actionName;
					}else {
						waitClass = false;
					}
				}
			}
			if(!lastHumanAction.equals(initialActionName))
				return true;
			else
				return false;
		}else {
			//TODO
		}
		return false;
	}
	

}
