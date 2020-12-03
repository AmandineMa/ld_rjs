package arch.agarch;

import arch.actions.ActionFactoryImpl;
import rjs.arch.agarch.AbstractROSAgArch;

public class SupervisorAgArch extends AbstractROSAgArch {
	
	public SupervisorAgArch() {
		super();
	}
	
	@Override
	public void init() {
		super.init();
		setActionFactory(new ActionFactoryImpl());
	}

}
