package jia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
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
import jason.util.Pair;
import rjs.utils.Predicate;

/**

 Based on the .findall internal action
 Customized for the action_monitoring module.
 Allows to match UnnamedVar with their values and to check if action preconditions are true based on the filled values.
 
 TODO to refact and optimize
*/

@SuppressWarnings("serial")
public class findall extends DefaultInternalAction {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

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
        // iteration on actions from the action models
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
    	Iterator<Term> tailIte = tail.iterator();
    	ArrayList<Pair<Literal,ListTerm>> actionsAndPreconds = new ArrayList<Pair<Literal,ListTerm>>();
    	Literal actPred = (Literal) u.get("ActPred");
    	List<Term> actPreTerms = actPred.getTerms();
    	while(tailIte.hasNext()) {
    		Iterator<Term> preconditionIte = ((ListTerm) u.get("Preconditions")).iterator();
    		Literal action = (Literal) tailIte.next();

    		ListTerm filledPreconditions = new ListTermImpl();
    		while(preconditionIte.hasNext()) {
    			Literal precondition = ((Literal) preconditionIte.next()).copy();
    			// iteration on the subject and object of the precondition (size = 2)
    			for(int j = 0; j < precondition.getTerms().size(); j++) {
    				Term t = precondition.getTerm(j);
    				// all t are unnamed var, we check if they have a value
    				Term res = u.get((VarTerm)t);
    				precondition.setTerm(j, res == null ? t : res);
    				if(res == null && actPreTerms.contains(t)) {
    					int index = actPreTerms.indexOf(t);
    					precondition.setTerm(j, action.getTerm(index));
    				}
    			}
    			filledPreconditions.add(precondition);
    		}
    		actionsAndPreconds.add(new Pair<Literal, ListTerm>(action, filledPreconditions));
    	}
    	
    	ListTerm newAll = new ListTermImpl();
    	for(Pair<Literal,ListTerm> pair : actionsAndPreconds) {
    		List<Literal> alternativeValues = new ArrayList<Literal>();
    		Iterator<Term> preconditionsIte = pair.getSecond().iterator();
    		boolean allPrecondsOK = true;
    		while(preconditionsIte.hasNext() && allPrecondsOK) {
    			Literal precondition = (Literal) preconditionsIte.next();
    			List<String> precondInOnto = checkPrecondInOnto(ts, precondition);
    			if(precondInOnto.isEmpty()) {
    				allPrecondsOK = false;
    				logger.info("precondition "+precondition+" is false");
    			} else {
    				for(Term t : precondition.getTerms()) {
    					if(t.isUnnamedVar() && pair.getFirst().getTerms().contains(t)) {
    						if(precondInOnto.size() > 1) {
    							for(int i = 1; i < precondInOnto.size(); i++) {
    								Literal newAction = pair.getFirst().copy();
    								newAction.getTerms().set(newAction.getTerms().indexOf(t), new StringTermImpl(precondInOnto.get(i)));
    								alternativeValues.add(newAction);
    							}
    						}
    						pair.getFirst().getTerms().set(pair.getFirst().getTerms().indexOf(t), new StringTermImpl(precondInOnto.get(0)));
    						// there is only one unnamed term in the preconditions as it is now
    						break;
    					}
    				}
    				
    				
    			}

    		}
    		if(allPrecondsOK) {
    			if(verifyAllDifferent(pair.getFirst().getTerms()))
    				newAll.add(pair.getFirst());
    			if(!alternativeValues.isEmpty()) {
    				for(Literal l : alternativeValues) {
    					if(verifyAllDifferent(l.getTerms()))
    						newAll.add(l);
    				}
    			}
    		}
    	}
    	return newAll;
    }
    
    private boolean verifyAllDifferent(List<Term> terms) {
    	HashSet<Term> set = new HashSet<Term>(terms);
    	if(set.size() == terms.size())
    		return true;
    	return false;
    }
    
    private List<String> checkPrecondInOnto(TransitionSystem ts,Literal precondition) throws Exception {
    	Predicate predicate = new Predicate(precondition);
		List<String> isInOnto = new ArrayList<String>();
		if(!predicate.isObjectUnnamed && !predicate.isSubjectUnnamed) {
			// should never happen with the written action model for now
			throw new Exception("not handled case");
		} else if(predicate.isObjectUnnamed && !predicate.isSubjectUnnamed) {
			isInOnto = ((LAASAgArch) ts.getAgArch()).callOntoIndivRobot("getOn",predicate.subject+":"+predicate.property).getValues();
		} else if(predicate.isSubjectUnnamed && !predicate.isObjectUnnamed) {
			isInOnto = ((LAASAgArch) ts.getAgArch()).callOntoIndivRobot("getFrom",predicate.object+":"+predicate.property).getValues();
		} else {
			throw new Exception("not handled case");
		}
		return isInOnto;
    }
    
}






