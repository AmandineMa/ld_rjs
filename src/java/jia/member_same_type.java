package jia;

import java.util.Collection;
import java.util.Iterator;
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
import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.ObjectTerm;
import jason.asSyntax.SetTerm;
import jason.asSyntax.Term;
import rjs.utils.Tools;

/**

Based on .member
To check if the element is in the list and if the terms of the element are of the same types as the desired one. 
Ex : hasInHand(human_0, red_box) has its terms corresponding to hasInHand(Human,Container) but not hasInHand(human_0, cube_BCBG)

*/


@SuppressWarnings("serial")
public class member_same_type extends DefaultInternalAction {

    private static InternalAction singleton = null;
    public static InternalAction create() {
        if (singleton == null)
            singleton = new member_same_type();
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
        if (!args[1].isList() && !args[1].isSet()) {
            if (args[1] instanceof ObjectTerm) {
                ObjectTerm o = (ObjectTerm)args[1];
                if (o.getObject() instanceof Collection) {
                    return;
                }
            }
            throw JasonException.createWrongArgument(this,"second argument must be a list or a set");
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public Object execute(TransitionSystem ts, final Unifier un, Term[] args) throws Exception {
        checkArguments(args);

        final Term member = args[0];
        final Iterator<Term> i;
        if (args[1].isList()) {
            i = ((ListTerm)args[1]).iterator();
        } else if (args[1].isSet()) {
            if (args[0].isGround()) // use contains
                return ((SetTerm)args[1]).contains(args[0]); // fast track for sets
            i = ((SetTerm)args[1]).iterator();
        } else if (args[1] instanceof ObjectTerm) { // case of queue
            ObjectTerm o = (ObjectTerm)args[1];
            if (o.getObject() instanceof Collection) {
                if (args[0].isGround())
                    return ((Collection<Term>)o.getObject()).contains(args[0]);
                i = ((Collection<Term>)o.getObject()).iterator();
            } else {
                i = ListTerm.EMPTY_LIST.iterator();
            }
        } else {
            i = ListTerm.EMPTY_LIST.iterator();
        }

        return new Iterator<Unifier>() {
            Unifier c = null; // the current response (which is an unifier)

            public boolean hasNext() {
                if (c == null) // the first call of hasNext should find the first response
                    find();
                return c != null;
            }

            public Unifier next() {
                if (c == null) find();
                Unifier b = c;
                find(); // find next response
                return b;
            }

            void find() {
                while (i.hasNext()) {
                    c = un.clone();
                    Term listElement = i.next();
                    if (c.unifiesNoUndo(member, listElement)) {
                    	if(member.isLiteral()) {
	                    	List<Term> termsMember = ((LiteralImpl) member).getTerms();
	                    	for(int i = 0; i < termsMember.size() ; i++) {
	                    		String term = Tools.removeQuotes(termsMember.get(i).toString());
	                    		String object = Tools.removeQuotes(((LiteralImpl)listElement).getTerms().get(i).toString());
	                    		String type;
	                    		Pattern p = Pattern.compile("[_0-9]+([A-Za-z]+)");
								Matcher m = p.matcher(object);
								if(m.find()) {
									type = m.group(1);
									List<String> isRightType = ((LAASAgArch) ts.getAgArch()).callOnto("getUp",term+" -s "+type).getValues();
									if(isRightType == null || isRightType.isEmpty()) {
										c = null;
										break;
									}
								}
	                    	}
	                    	if(c != null)
	                    		return; //all elements match
                    	}
                    }
                }
                c = null;
            }

            public void remove() {}
        };

        /* -- old version of the implementation
         * -- problem: even if the user wants only the first member, if search all
        List<Unifier> answers = new ArrayList<Unifier>();
        Unifier newUn = (Unifier)un.clone(); // clone un so as not to change it
        for (Term t: lt) {
            if (newUn.unifies(member, t)) {
                // add this unification to the  answers
                answers.add(newUn);
                newUn = (Unifier)un.clone(); // creates a new clone of un
            }
        }
        return answers.iterator();
        */
    }
}
