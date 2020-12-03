package arch.agarch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.google.common.collect.Multimap;

import jason.asSyntax.Literal;
import rjs.utils.SimpleFact;
import ros.RosNode;

public class HumanMonitorAgArch extends AgArch {

	public HumanMonitorAgArch() {
		super();
	}
	
	public Collection<Literal> perceive() {
		Collection<Literal> l = new ArrayList<Literal>();
		if(rosnode != null) {
			Multimap<String,SimpleFact> mm = ((RosNode) rosnode).getPerceptions();
			synchronized (mm) {
				HashMap<String, Collection<SimpleFact>> perceptions = new HashMap<String, Collection<SimpleFact>>(mm.asMap());
				if(perceptions != null) {
					for(String subject : perceptions.keySet()) {
						for(SimpleFact percept : perceptions.get(subject)) {
							if(percept.getObject() != null)
								l.add(Literal.parseLiteral(percept.getPredicate()+"("+subject+","+percept.getObject()+")"));
							else
								l.add(Literal.parseLiteral(percept.getPredicate()+"("+subject+")"));
						}
					}
			}

		}
		}
		return l;

	}

}
