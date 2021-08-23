package arch.actions.robot;

import java.util.Arrays;

import jason.asSemantics.ActionExec;
import jason.asSyntax.ListTerm;
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
		String commType = Tools.removeQuotes(actionTerms.get(0).toString());
		ListTerm commParams = (ListTerm) actionTerms.get(1);
		std_msgs.String str = rosAgArch.createMessage(std_msgs.String._TYPE);
		str.setData(Tools.removeQuotes(actionTerms.get(2).toString()));
		getRosNode().publish("say", str); 
		rosAgArch.addBelief("comm", Arrays.asList("r2h",commType,commParams));
		actionExec.setResult(true);
	}

}
