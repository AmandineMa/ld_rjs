package jia;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import arch.agarch.LAASAgArch;
import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.InternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ObjectTerm;
import jason.asSyntax.Term;
import jason.asSyntax.UnnamedVar;
import jason.asSyntax.VarTerm;
import rjs.utils.Tools;

/**

Based on .member
To check if the element is in the list and if the terms of the element are of the same types as the desired one. 
Ex : hasInHand(human_0, red_box) has its terms corresponding to hasInHand(Human,Container) but not hasInHand(human_0, cube_BCBG)

*/


public class members_same_type extends DefaultInternalAction {

    private static InternalAction singleton = null;
    public static InternalAction create() {
        if (singleton == null)
            singleton = new members_same_type();
        return singleton;
    }

    @Override public int getMinArgs() {
        return 2;
    }
    @Override public int getMaxArgs() {
        return 2;
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
        ListTerm list1 = (ListTerm) args[0];
        ListTerm list2 = (ListTerm) args[1];
        
        for(int i=0; i < list1.size(); i++) {
        	String el1 = Tools.removeQuotes(list1.get(i).toString());
        	Term el2 =  list2.get(i);
        	String type;
        	if(el2 instanceof UnnamedVar) {
	        	Pattern p = Pattern.compile("[_0-9]+([A-Za-z]+)");
				Matcher m = p.matcher(el2.toString());
				type = m.group(1);
        	} else if(el2 instanceof VarTerm)
        		type = el2.toString();
        	else
        		return false;
			List<String> isRightType = ((LAASAgArch) ts.getAgArch()).callOntoIndivRobot("getUp",el1+" -s "+type).getValues();
			if(isRightType == null || isRightType.isEmpty()) {
				return false;
			}
			un.unifies(list1.get(i), list2.get(i));
        }
        return true;
        
    }
}
