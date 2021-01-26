package arch.actions.robot;

import jason.asSemantics.ActionExec;
import rjs.arch.actions.AbstractAction;
import rjs.arch.agarch.AbstractROSAgArch;
import rjs.utils.Tools;

public class Say extends AbstractAction {

	public Say(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		super(actionExec, rosAgArch);
		setSync(true);
	}

	@Override
	public void execute() {
		String param = Tools.removeQuotes(actionTerms.get(0).toString());
		std_msgs.String str = rosAgArch.createMessage(std_msgs.String._TYPE);
		str.setData(param);
		getRosNode().publish("say", str); 
		rosAgArch.addBelief("said(\""+param+"\")");
		actionExec.setResult(true);
	}

}
