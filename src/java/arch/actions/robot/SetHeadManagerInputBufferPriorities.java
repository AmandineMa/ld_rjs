package arch.actions.robot;

import arch.actions.AbstractSetManagerInputBufferPriorities;
import jason.asSemantics.ActionExec;
import rjs.arch.agarch.AbstractROSAgArch;

public class SetHeadManagerInputBufferPriorities extends AbstractSetManagerInputBufferPriorities {

	public SetHeadManagerInputBufferPriorities(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		super(actionExec, rosAgArch);
	}

	@Override
	protected String setManagerName() {
		return "pr2_head";
	}

}
