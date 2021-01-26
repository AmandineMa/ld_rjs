package ros;

import java.util.HashMap;
import org.ros.namespace.GraphName;

import rjs.ros.AbstractRosNode;

public class RosNode extends AbstractRosNode {
	
//	private List<MementarOccasion> perceptions = Collections.synchronizedList(new ArrayList<MementarOccasion>());
	
	public RosNode(String name) {
		super(name);
	}

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("supervisor_director");
	}
	

	public void init() {
		super.init();
		
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
