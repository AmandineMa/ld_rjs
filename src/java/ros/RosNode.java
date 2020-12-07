package ros;

import java.util.HashMap;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.topic.Subscriber;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import rjs.ros.AbstractRosNode;
import rjs.utils.SimpleFact;
import toaster_msgs.Fact;
import toaster_msgs.FactList;

public class RosNode extends AbstractRosNode {
	
	private Subscriber<FactList> factsSub;
	private Multimap<String, SimpleFact> perceptions = Multimaps.synchronizedMultimap(ArrayListMultimap.<String, SimpleFact>create());

	public RosNode(String name) {
		super(name);
	}

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("supervisor_director");
	}
	
	@SuppressWarnings("unchecked")
	public void init() {
		super.init();
		if(parameters.has("/supervisor/services"))
			servicesMap = (HashMap<String, HashMap<String, String>>) parameters.getMap("/supervisor/services");
		
		factsSub = connectedNode.newSubscriber(parameters.getString("/supervisor/topics/current_facts"), FactList._TYPE);
		
		factsSub.addMessageListener(new MessageListener<FactList>() {

			public void onNewMessage(FactList facts) {
				synchronized (perceptions) {
					perceptions.clear();
					for (Fact fact : facts.getFactList()) {
						final SimpleFact simple_fact;
						String predicate = fact.getProperty();
						String subject = fact.getSubjectId();
						String object = fact.getTargetId();
						if (!object.isEmpty()) {
							if (!object.startsWith("\""))
								object = "\"" + object + "\"";
							simple_fact = new SimpleFact(predicate, object);
						} else {
							simple_fact = new SimpleFact(predicate);
						}
						if (!subject.startsWith("\""))
							subject = "\"" + subject + "\"";			
						perceptions.put(subject, simple_fact);
					}
				}
			}
		}, 10);
	}
	
	public Multimap<String, SimpleFact> getPerceptions() {
		return perceptions;
	}

}
