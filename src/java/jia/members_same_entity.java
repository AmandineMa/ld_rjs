package jia;

import java.util.Collection;

import jason.JasonException;
import jason.asSemantics.InternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.ObjectTerm;
import jason.asSyntax.Term;
import rjs.utils.Tools;

/**

To check if the params of the monitored actions (list 1) correspond to the params of an action in the plan (list 2)

*/


public class members_same_entity extends get_indiv_from_sparql {

    private static InternalAction singleton = null;
    public static InternalAction create() {
        if (singleton == null)
            singleton = new members_same_entity();
        return singleton;
    }

    @Override public int getMinArgs() {
        return 2;
    }
    @Override public int getMaxArgs() {
        return 3;
    }

    @Override protected void checkArguments(Term[] args) throws JasonException {
        super.checkArguments(args); // check number of arguments
        if (!args[0].isList() && !args[1].isList() && ((ListTerm) args[0]).size() != ((ListTerm) args[1]).size()) {
            if (args[1] instanceof ObjectTerm) {
                ObjectTerm o = (ObjectTerm)args[1];
                if (o.getObject() instanceof Collection) {
                    return;
                }
            }
            throw JasonException.createWrongArgument(this,"second argument must be a list or a set");
        }
    }


    @Override
    public Object execute(TransitionSystem ts, final Unifier un, Term[] args) throws Exception {
        checkArguments(args);
        ListTermImpl list1 = (ListTermImpl) args[0];
        ListTermImpl list2 = (ListTermImpl) args[1];
        ListTermImpl newParamList = iterateOnLists(ts,list1,list2);
        if(newParamList == null) {
        	if(list1.size() == 3) {
        		list1.swap(1, 2);
        		newParamList = iterateOnLists(ts,list1,list2);
        	}
        	if(newParamList == null) {
        		return false;
        	}
        }
        
        if(args.length == 3) 
        	return un.unifies(args[2], newParamList);
    	else
        	return true;
    }
    
    protected ListTermImpl iterateOnLists(TransitionSystem ts, ListTerm list1, ListTerm list2) {
    	ListTermImpl newParamList = new ListTermImpl();
    	for(int i=0; i < list1.size(); i++) {
        	Term el1 =  list1.get(i);
        	String el2 = Tools.removeQuotes(list2.get(i).toString());
        	// same param
        	if(Tools.removeQuotes(el1.toString()).equals(el2)) {
        		newParamList.add(el1);
        	// param of recognized action is not ground
        	} else if(el1.isUnnamedVar()) {
        		newParamList.add(list2.get(i));
        	// param of recognized action is matching the sparql request
        	} else if(el2.contains("?") && Tools.listTermStringTolist((ListTermImpl) getSparqlResult(ts, el2, "objectsOnly")).contains(el1.toString())) {
        		newParamList.add(el1);
        	} else {
        		newParamList = null;
        		break;
        	}
        } 
    	return newParamList;
    }
}
