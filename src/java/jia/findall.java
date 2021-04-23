package jia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import arch.agarch.LAASAgArch;
import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Literal;
import jason.asSyntax.LogicalFormula;
import jason.asSyntax.StringTermImpl;
import jason.asSyntax.Term;
import jason.asSyntax.VarTerm;
import rjs.utils.Predicate;

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
        // iteration on actions from the human_actions model
        while (iu.hasNext()) {
        	Unifier u = iu.next();
        	ListTerm temp = new ListTermImpl();
            tail = tail.append(var.capply(u));
            // find terms of _NumXList and match them with the unnamed var of type _NumX
            temp = matchListElements(tail, u);
            try {
            	//check if the preconditions are in the ontology
            	newAll.addAll(handlePreconditions(ts, temp, u));
            } catch(Exception e) {
            	//no need to do anything
            }
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
    
    private ListTerm handlePreconditions(TransitionSystem ts, ListTerm tail, Unifier u) throws Exception {
    	HashSet<Literal> set = new  HashSet<Literal>();
    	// iterator on the preconditions of the given action
    	Iterator<Term> preconditionIte = ((ListTerm) u.get("Preconditions")).iterator();
    	while(preconditionIte.hasNext()) {
    		Literal precondition = (Literal) preconditionIte.next();
    		// list in case there are multiple possible predicates, with different values of an unnamed var
    		ListTerm preconditionVariations = new ListTermImpl();
    		// iteration on the subject and object of the precondition (size = 2)
    		for(int i = 0; i < precondition.getTerms().size(); i++) {
    			Term t = precondition.getTerm(i);
    			// all t are unnamed var, we check if they have a value
				Term res = u.get((VarTerm)t);
				precondition.setTerm(i, res == null ? t : res);
				// if t has no value in u, we try to see if it has a value in an unnamed var Xlist
				if(res == null) {
					Iterator<VarTerm> varTermIte = u.iterator();
					// iteration on all the elements of u
					while(varTermIte.hasNext()) {
						VarTerm varTerm = varTermIte.next();
						// only the unnamed elements are of interest and we are not interested in t with no type (of the form _Num_Num,just _ in the human_actions model)
						if(varTerm.isUnnamedVar() && t.toString().matches("[_0-9]+([A-Za-z]+)")) {
							// we check if varTerm name (_NumXList) contains t name (_NumY) -> check if X == Y
    						if(varTerm.toString().contains(t.toString().replaceAll("[_0-9]+",""))) {
    							// we get the values of the list (should always be a list)
    							Term termsVarTerm = u.get(varTerm);
    							// we add the precondition with the possible values in preconditionVariations
								Iterator<Term> termsVarTermIte = ((ListTerm) termsVarTerm).iterator();
								while(termsVarTermIte.hasNext()) {
									precondition.setTerm(i, termsVarTermIte.next());
									preconditionVariations.add(precondition.copy());
								}
    						}
						}
					}
    			}
    		}
    		if(!preconditionVariations.isEmpty()) {
	    		for(Term t : preconditionVariations) {
	    			set.addAll(checkPrecondInOnto(ts, tail, (Literal) t));
	    		}
    		} else {
    			set.addAll(checkPrecondInOnto(ts, tail, precondition));
    		}
    	}
    	ListTerm newAll = new ListTermImpl();
    	newAll.addAll(set);
    	return newAll;
    }
    
    private HashSet<Literal> checkPrecondInOnto(TransitionSystem ts, ListTerm tail, Literal precondition) throws Exception {
    	Predicate predicate = new Predicate(precondition);
		List<String> isInOnto = new ArrayList<String>();
		if(!predicate.object.startsWith("_") && !predicate.subject.startsWith("_")) {
			// should never happen with the written action model for now
			throw new Exception("not handled case");
		} else if(predicate.object.startsWith("_") && !predicate.subject.startsWith("_")) {
			isInOnto = ((LAASAgArch) ts.getAgArch()).callOnto("getOn",predicate.subject+":"+predicate.property).getValues();
			return addOntoResultsToList(ts, predicate, tail, precondition.getTerm(1), isInOnto);
		} else if(predicate.subject.startsWith("_") && !predicate.object.startsWith("_")) {
			isInOnto = ((LAASAgArch) ts.getAgArch()).callOnto("getFrom",predicate.object+":"+predicate.property).getValues();
			return addOntoResultsToList(ts, predicate, tail, precondition.getTerm(0), isInOnto);
		} 
		// should never happen with the written action model for now
		throw new Exception("not handled case");
    }
    
    private HashSet<Literal> addOntoResultsToList(TransitionSystem ts, Predicate predicate, ListTerm tail, Term varTerm, List<String> isInOnto) throws Exception {
    	HashSet<Literal> elementsToAdd = new HashSet<Literal>();
		if(!isInOnto.isEmpty()) {
			Iterator<Term> tailIte = tail.iterator();
			while(tailIte.hasNext()) {
				Literal element = (Literal) tailIte.next();
				List<Term> elementTerms = element.getTerms();
				int index = elementTerms.indexOf(varTerm);
				if(index != -1) {
    				for(String obj : isInOnto) {
    					Literal elementNew = element.copy();
						elementNew.getTerms().set(index, new StringTermImpl(obj));
						elementsToAdd.add(elementNew);
    				}
				} else {
					elementsToAdd.add(element);
				}
			}
		} else {
			throw new Exception("one of the precondition is invalid");
		}
		return elementsToAdd;
    }
}






