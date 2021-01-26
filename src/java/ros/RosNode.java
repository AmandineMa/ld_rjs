package ros;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;

import mementar.MementarOccasion;
import rjs.ros.AbstractRosNode;

public class RosNode extends AbstractRosNode {
	
	private List<MementarOccasion> perceptions = Collections.synchronizedList(new ArrayList<MementarOccasion>());
	
	public RosNode(String name) {
		super(name);
	}

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("supervisor_director");
	}
	

	public void init() {
		super.init();
		
//		setSubListener("mementar_occasions", new MessageListener<MementarOccasion>() {
//
//			@Override
//			public void onNewMessage(MementarOccasion occasion) {
//				synchronized (perceptions) {
//					perceptions.add(occasion);
//				}
//				
//			}
//		});
	}
	
	public List<MementarOccasion> getPerceptions() {
		return perceptions;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void setServicesMap() {
		if(parameters.has("/supervisor/services"))
			servicesMap = (HashMap<String, HashMap<String, String>>) parameters.getMap("/supervisor/services");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setTopicsMap() {
		if(parameters.has("/supervisor/topics"))
			topicsMap = (HashMap<String, HashMap<String, String>>) parameters.getMap("/supervisor/topics");
		
	}
	
	

}
