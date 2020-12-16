package arch.actions.ros;

import jason.asSemantics.ActionExec;
import rjs.arch.actions.ros.AbstractStartROSNode;
import rjs.arch.agarch.AbstractROSAgArch;
import ros.RosNode;

public class StartROSNode extends AbstractStartROSNode {
	
	public StartROSNode(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		super(actionExec, rosAgArch);
		setSync(true);
	}

	@Override
	protected void instantiateRosNode() {
		AbstractROSAgArch.setRosnode(new RosNode("supervisor"));		
	}

}
