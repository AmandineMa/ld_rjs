package jia;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Literal;
import jason.asSyntax.LogicalFormula;
import jason.asSyntax.Term;
import jason.asSyntax.VarTerm;

/**

 Based on the .findall internal action
 Customized for the action_monitoring module.
 Allows to match UnnamedVar with their values and to check if action preconditions are true based on the filled values.
 
 TODO to refact and optimize
*/

@SuppressWarnings("serial")
public class findall extends DefaultInternalAction {

	@Override public int getMinArgs() {
        return 3;
    }
    @Override public int getMaxArgs() {
        return 3;
    }

    @Override public Term[] prepareArguments(Literal body, Unifier un) {
        return body.getTermsArray(); // we do not need to clone nor to apply for this internal action
    }

    @Override protected void checkArguments(Term[] args) throws JasonException {
        super.checkArguments(args); // check number of arguments
        if (! (args[1] instanceof LogicalFormula))
            throw JasonException.createWrongArgument(this,"second argument must be a formula");
    }

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        checkArguments(args);

        Term var = args[0];
        LogicalFormula logExpr = (LogicalFormula)args[1];
        ListTerm all = new ListTermImpl();
        ListTerm tail = all;
        Iterator<Unifier> iu = logExpr.logicalConsequence(ts.getAg(), un);
        ListTerm newAll = new ListTermImpl();
        while (iu.hasNext()) {
        	Unifier u = iu.next();
        	// found action predicates
        	ListTerm temp = new ListTermImpl();
            tail = tail.append(var.capply(u));
            temp = matchListElements(tail, u);
            temp = attribuateValuesFromPrecond(ts, temp, u);
            newAll.addAll(checkPrecond(ts, temp, u));
        }
        return un.unifies(args[2], newAll);
    }
    
    private ListTerm matchListElements(ListTerm tail, Unifier u) {
    	ListTerm newAll = new ListTermImpl();
    	Iterator<Term> tailIte = tail.iterator();
        // to match movement XList elements with the unnamed vars of the found action predicates
        while(tailIte.hasNext()) {
        	// an action predicate
        	Literal element = (Literal) tailIte.next();
        	// terms of the action predicate
        	List<Term> elementTerms = element.getTerms();
        	// iteration on the terms
        	for(int i = 0; i < elementTerms.size(); i++) {
        		// we check if the term is an unnamed var, if not do not need to do anything
        		if(elementTerms.get(i).isUnnamedVar()) {
        			// isolation of the "name" of the unnamed var
        			Pattern p = Pattern.compile("[_0-9]+([A-Za-z]+)");
					Matcher m = p.matcher(elementTerms.get(i).toString());
					if(m.find()) {
						// iteration on all the unnamed var of the unifier to found the XList match
						// with the unnamed var of the action predicate
            			Iterator<VarTerm> unifierIte = u.iterator();
            			while(unifierIte.hasNext()) {
            				VarTerm varUnnamed = unifierIte.next();
            				if(varUnnamed.toString().contains(m.group(1)) && varUnnamed.toString().contains("List")) {
            					ListTerm values = (ListTerm) u.get(varUnnamed);
            					Iterator<Term> valuesIte = values.iterator();
            					// get the value of the list and create new action predicates
            					while(valuesIte.hasNext()) {
            						Literal elementNew = element.copy();
            						elementNew.getTerms().remove(i);
            						elementNew.getTerms().add(i, valuesIte.next());
            						newAll.add(elementNew);
            					}
            						
            				}
            			}
					}
        		}
        	}
        }
        return newAll;
    }
    
    private ListTerm attribuateValuesFromPrecond(TransitionSystem ts, ListTerm tail, Unifier u) {
    	ListTerm newAll = new ListTermImpl();
    	Iterator<Term> preconditionIte = ((ListTerm) u.get("Preconditions")).iterator();
    	while(preconditionIte.hasNext()) {
    		Literal precondition = (Literal) preconditionIte.next();
    		Iterator<Literal> belIte = ts.getAg().getBB().getCandidateBeliefs((Literal) precondition, u);
    		if(belIte != null) {
    			
    			Iterator<Term> tailIte = tail.iterator();
    			while(tailIte.hasNext()) {
    				// an action predicate
    				Literal element = (Literal) tailIte.next();
    				// terms of the action predicate
    				List<Term> elementTerms = element.getTerms();
    				// iteration on the terms
    				boolean noUnnamedVar = true;
    				for(int i = 0; i < elementTerms.size(); i++) {
    					// we check if the term is an unnamed var, if not do not need to do anything
    					if(elementTerms.get(i).isUnnamedVar()) {
    						noUnnamedVar = false;
    						for(int j = 0; j < precondition.getTerms().size(); j++) {
    							Term precondTerm = precondition.getTerm(j);
    							if(precondTerm.equals(elementTerms.get(i))){
    								while(belIte.hasNext()) {
	    								Literal elementNew = element.copy();
	            						elementNew.getTerms().remove(i);
	            						elementNew.getTerms().add(i, belIte.next().getTerm(j));
	            						newAll.add(elementNew);
    								}
    								belIte = ts.getAg().getBB().getCandidateBeliefs((Literal) precondition, u);
    							}
    						}
    					}
    				}
    				if(noUnnamedVar && !newAll.contains(element)) {
    					newAll.add(element);
    				}
    			}
    		} else {
    			return new ListTermImpl();
    		}
    	}

    	return newAll;
    }
    
   private ListTerm checkPrecond(TransitionSystem ts, ListTerm tail, Unifier u) {
	   ListTerm newAll = new ListTermImpl();
	   Iterator<Term> preconditionIte = ((ListTerm) u.get("Preconditions")).iterator();
	   Literal actPred = (Literal) u.get("ActPred");
	   Iterator<Term> tailIte = tail.iterator();
		while(tailIte.hasNext()) {
			// an action predicate
			Literal element = (Literal) tailIte.next();
			// terms of the action predicate
			List<Term> elementTerms = element.getTerms();
			// iteration on the terms
			while(preconditionIte.hasNext()) {
				Literal precond =  (Literal) preconditionIte.next();
				Literal newPrecond = precond.copy();
				int j = 0;
				Iterator<Term> precondTermsIte = precond.getTerms().iterator();
				while(precondTermsIte.hasNext()) {
					VarTerm precondTerm = (VarTerm) precondTermsIte.next();
					for(int i = 0; i < elementTerms.size(); i++) {
						if(u.get(precondTerm) != null) {
							newPrecond.setTerm(j, u.get(precondTerm));
							break;
						} else if(precondTerm.equals(actPred.getTerm(i))) {
							newPrecond.setTerm(j, elementTerms.get(i));
							break;
						}
					}
					if(ts.getAg().believes(newPrecond, u) && !newAll.contains(element)) {
						newAll.add(element);
					}
					j++;
				}
			}
			preconditionIte = ((ListTerm) u.get("Preconditions")).iterator();
		}
		return newAll;
   }
}
