package jia;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import arch.agarch.LAASAgArch;
import jason.JasonException;
import jason.asSemantics.InternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import rjs.utils.Tools;


public class is_todo_act_already_performed extends members_same_entity {
	
	private static InternalAction singleton = null;
    public static InternalAction create() {
        if (singleton == null)
            singleton = new is_todo_act_already_performed();
        return singleton;
    }
	
	@Override public int getMinArgs() {
        return 4;
    }
    @Override public int getMaxArgs() {
        return 4;
    }
    
    @Override 
    protected void checkArguments(Term[] args) throws JasonException {
    	 if (args.length < getMinArgs() || args.length > getMaxArgs())
             throw JasonException.createWrongArgumentNb(this);
    }

	@SuppressWarnings("unchecked")
	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		List<List<Literal>> actionListofList = (List<List<Literal>>) args[0];
		String todoActionName = Tools.removeQuotes(args[1].toString());
		String todoActionAgent = Tools.removeQuotes(args[2].toString());
		ListTermImpl todoActionParams = (ListTermImpl) args[3];
		Set<Literal> actionSet = actionListofList.stream().flatMap(List::stream).collect(Collectors.toSet());
		for(Literal action : actionSet) {
			//check actions same class
			String finishedActionName = ((LAASAgArch) ts.getAgArch()).supervisorNameToOntoName(action.getFunctor());
			List<String> ontoClass = ((LAASAgArch) ts.getAgArch()).callOnto("class","getUp", todoActionName+" -d 1 ").getValues();
			if(ontoClass != null && !ontoClass.isEmpty() && ontoClass.contains(finishedActionName)) {
				//check actions same agents
				String finishedActionAgent = action.getTerm(0).toString();
				String agentXName = Tools.removeQuotes(ts.getAg().findBel(Literal.parseLiteral("agentXName(_)"), un).toString());
				if(!action.getTerm(0).isGround() ||  finishedActionAgent.equals(todoActionAgent) || finishedActionAgent.equals(agentXName)) {
					//check action same params
					ListTermImpl finishedActionParams = new ListTermImpl();
					finishedActionParams.addAll(action.getTerms());
					finishedActionParams.remove(0);
					ListTermImpl newParamList = iterateOnLists(ts,finishedActionParams,todoActionParams);
					if(newParamList == null) {
			        	if(finishedActionParams.size() == 3) {
			        		finishedActionParams.swap(1, 2);
			        		newParamList = iterateOnLists(ts,finishedActionParams,todoActionParams);
			        	}
			        	if(newParamList != null) {
			        		return true;
			        	}
			        }else {
			        	return true;
			        }
				}
			}
			
		}
		return false;
	}
	

}
