package arch.agarch;

import arch.actions.ActionFactoryImpl;
import rjs.arch.agarch.AbstractROSAgArch;
import ros.RosNode;

public class SupervisorAgArch extends AbstractROSAgArch {
	
	public SupervisorAgArch() {
		super();
	}
	
	@Override
	public void init() {
		super.init();
		setActionFactory(new ActionFactoryImpl());
	}

	@Override
	public void initRosNode() {
		rosnode = new RosNode(getAgName());	
	}

	@Override
	public void initListeners() {
	}

}
