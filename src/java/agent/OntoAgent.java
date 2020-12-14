package agent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import jason.asSemantics.Event;
import jason.asSemantics.Intention;
import jason.asSyntax.Literal;
import jason.asSyntax.Trigger;
import jason.asSyntax.Trigger.TEOperator;
import jason.asSyntax.Trigger.TEType;
import jason.bb.BeliefBase;
import jason.bb.StructureWrapperForLiteral;
import rjs.agent.LimitedAgent;

public class OntoAgent extends LimitedAgent {

	public OntoAgent() {
		super();
	}

	@Override
	public int buf(Collection<Literal> percepts) {
		if (percepts == null) {
			return 0;
		}
		int adds = 0;
		Set<StructureWrapperForLiteral> perW = new HashSet<>();
        Iterator<Literal> iper = percepts.iterator();
        while (iper.hasNext()) {
            Literal l = iper.next();
            if (l != null)
                perW.add(new StructureWrapperForLiteral(l));
        }
		for (StructureWrapperForLiteral lw: perW) {
	            try {
	                Literal lp = lw.getLiteral().copy().forceFullLiteralImpl();
	                lp.addAnnot(BeliefBase.TPercept);
	                if (getBB().add(lp)) {
	                    adds++;
	                    ts.updateEvents(new Event(new Trigger(TEOperator.add, TEType.belief, lp), Intention.EmptyInt));
	                }
	            } catch (Exception e) {
	                logger.log(Level.SEVERE, "Error adding percetion " + lw.getLiteral(), e);
	            }
	        }
		return adds;
	}
	
	

}
