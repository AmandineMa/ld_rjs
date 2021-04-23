package agent;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import jason.asSemantics.Event;
import jason.asSemantics.Intention;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Literal;
import jason.asSyntax.PredicateIndicator;
import jason.asSyntax.Trigger;
import jason.asSyntax.Trigger.TEOperator;
import jason.asSyntax.Trigger.TEType;
import jason.bb.BeliefBase;
import rjs.agent.LimitedAgent;

public class OntoAgent extends LimitedAgent {

	private HashSet<String> predWithList = new HashSet<String>();

	public OntoAgent() {
		super();
	}

	@Override
	public int buf(Collection<Literal> percepts) {
		if (percepts == null) {
			return 0;
		}
		int dels = 0;
		int adds = 0;
		HashMap<String,ListTerm> objPredWithList = new HashMap<String,ListTerm>();

		for (Literal lw: percepts) {
			Literal lp = lw.copy().forceFullLiteralImpl();
			if(predWithList.contains(lp.getFunctor().toString())) {
				String belFunctor = lp.getFunctor().toString();
				if(objPredWithList.get(belFunctor) == null) {
					Iterator<Literal> belIt = getBB().getCandidateBeliefs(new PredicateIndicator(belFunctor, lp.getArity()));
					if(belIt != null) {
						objPredWithList.put(belFunctor, (ListTerm) belIt.next().copy().getTerm(1));
					} else {
						ListTerm newTerm =  new ListTermImpl();
						newTerm.add(lp.getTerm(1));
						objPredWithList.put(belFunctor, newTerm);
					}
				} 
				if(objPredWithList.get(belFunctor).contains(lp.getTerm(1)) && lp.negated() ) {
					objPredWithList.get(belFunctor).remove(lp.getTerm(1));
				} else if(!objPredWithList.get(belFunctor).contains(lp.getTerm(1))) {
					objPredWithList.get(belFunctor).add(lp.getTerm(1));
				}
			} else {
				lp.addAnnot(BeliefBase.TPercept);
				Literal lc = lp.copy().setNegated(lp.negated());
				if(getBB().remove(lc)) {
					dels++;
				}
				if (getBB().add(lp)) {
					adds++;
					ts.updateEvents(new Event(new Trigger(TEOperator.add, TEType.belief, lp), Intention.EmptyInt));
				}
			}
		}

		for(Entry<String, ListTerm> predWithListElement : objPredWithList.entrySet() ) {
			
			if(predWithListElement != null) {
				Iterator<Literal> belIt = getBB().getCandidateBeliefs(new PredicateIndicator(predWithListElement.getKey(), 2));
				if(!predWithListElement.getValue().isEmpty()) {
					if(belIt != null) {
						Literal belInBB = belIt.next();
						Literal newBelInBB = belInBB.copy();
						newBelInBB.delAnnot(belInBB.getAnnot("add_time"));
						newBelInBB.setTerm(1, predWithListElement.getValue());
						if(!newBelInBB.getTerm(1).equals(belInBB.getTerm(1))) {
							getBB().remove(belInBB);
							getBB().add(newBelInBB);
							ts.updateEvents(new Event(new Trigger(TEOperator.add, TEType.belief, newBelInBB), Intention.EmptyInt));
						}
					} else {
						for (Literal lw: percepts) {
							if(predWithListElement.getKey().equals(lw.getFunctor())) {
								Literal lp = lw.copy().forceFullLiteralImpl();
								lp.setTerm(1, predWithListElement.getValue());
								lp.addAnnot(BeliefBase.TPercept);
								getBB().add(lp);
								adds++;
								ts.updateEvents(new Event(new Trigger(TEOperator.add, TEType.belief, lp), Intention.EmptyInt));
								break;
							}
						}
					}
				} else {
					if(belIt != null) {
						dels++;
						Literal belInBB = belIt.next();
						getBB().remove(belInBB);
						Trigger te = new Trigger(TEOperator.del, TEType.belief, belInBB);
						if (ts.getC().hasListener() || pl.hasCandidatePlan(te)) {
							belInBB.addAnnot(BeliefBase.TPercept);
							te.setLiteral(belInBB);
							ts.getC().addEvent(new Event(te, Intention.EmptyInt));
						}
					}
				}
			}
		}
		return adds+dels;
	}

	public void addPredWithList(String pred) {
		predWithList.add(pred);
	}

}
