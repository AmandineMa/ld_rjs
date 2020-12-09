package ros;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.topic.Subscriber;

import mementar.MementarOccasion;
import rjs.ros.AbstractRosNode;

public class RosNode extends AbstractRosNode {
	
	private Subscriber<MementarOccasion> mementSub;
	private List<MementarOccasion> perceptions = Collections.synchronizedList(new ArrayList<MementarOccasion>());

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
		
		mementSub = connectedNode.newSubscriber(parameters.getString("/supervisor/topics/mementar_occasions"), MementarOccasion._TYPE);
		
		mementSub.addMessageListener(new MessageListener<MementarOccasion>() {

			@Override
			public void onNewMessage(MementarOccasion occasion) {
				synchronized (perceptions) {
					perceptions.add(occasion);
				}
				
			}
		}, 10);
	}
	
	public List<MementarOccasion> getPerceptions() {
		return perceptions;
	}

}
