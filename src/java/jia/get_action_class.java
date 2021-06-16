package jia;

import java.util.Iterator;
import java.util.List;

import arch.agarch.LAASAgArch;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.Rule;
import jason.asSyntax.Term;
import rjs.utils.Tools;

/**
 * args0 : executed action name
 * args1 : actionModel name
 * args2 : action of human or robot
 * 
 * @author amdia
 *
 */

public class get_action_class extends is_same_action_class {
	
	@Override public int getMinArgs() {
        return 3;
    }
    @Override public int getMaxArgs() {
        return 3;
    }

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		String action = Tools.removeQuotes(args[0].toString());
		String bel = "";
		if(args[2].toString().equals("human"))
			bel = "actionModel(_,_,_,_,_)";
		else if(args[2].toString().equals("robot"))
			bel = "actionModel(_,_)[robot]";
		Iterator<Literal> it = ((LAASAgArch) ts.getAgArch()).get_beliefs_iterator(bel);
		while(it.hasNext()) {
			LiteralImpl actionModel = (LiteralImpl) ((Rule) it.next()).getHead();
			String actionModelName = supervisorNameToOntoName(((Literal) actionModel.getTerm(0)).getFunctor().toString());
			List<String> ontoClass = ((LAASAgArch) ts.getAgArch()).callOnto("class","getUp", action+" -s "+actionModelName).getValues();
			if(ontoClass != null && !ontoClass.isEmpty()) {
				return un.unifies(args[1], actionModel);
			}
		}
		return false;
	}
	

}
