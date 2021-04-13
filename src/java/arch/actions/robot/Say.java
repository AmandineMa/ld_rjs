package arch.actions.robot;

import arch.agarch.LAASAgArch;
import arch.agarch.LAASAgArch.ActionIndicator;
import jason.asSemantics.ActionExec;
import rjs.arch.actions.AbstractAction;
import rjs.arch.agarch.AbstractROSAgArch;
import rjs.utils.Tools;

public class Say extends AbstractAction {
	
	private double actionID;

	public Say(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		super(actionExec, rosAgArch);
		setSync(true);
		actionID = Math.round(Math.random()*1000000);
	}

	@Override
	public void execute() {
		String param = Tools.removeQuotes(actionTerms.get(0).toString());
		std_msgs.String str = rosAgArch.createMessage(std_msgs.String._TYPE);
		str.setData(param);
		getRosNode().publish("say", str); 
		rosAgArch.addBelief("said(\""+param+"\","+actionID+")");
		((LAASAgArch) rosAgArch).callInsertAction("speak_"+actionID, AbstractROSAgArch.getRosnode().getConnectedNode().getCurrentTime(), ActionIndicator.START);
		Tools.sleep(4000);
		((LAASAgArch) rosAgArch).callInsertAction("speak_"+actionID,AbstractROSAgArch.getRosnode().getConnectedNode().getCurrentTime(), ActionIndicator.END);
		actionExec.setResult(true);
	}

}
