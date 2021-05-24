package arch.actions.robot;

import jason.asSemantics.ActionExec;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.NumberTermImpl;
import resource_management_msgs.MessagePriority;
import rjs.arch.actions.AbstractAction;
import rjs.arch.agarch.AbstractROSAgArch;
import rjs.utils.Tools;

public class SetHeadManagerAtemporalInput extends AbstractAction {

	public SetHeadManagerAtemporalInput(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		super(actionExec, rosAgArch);
		setSync(true);
	}

	@Override
	public void execute() {
		String frame = Tools.removeQuotes(actionTerms.get(0).toString());
		String input = Tools.removeQuotes(actionTerms.get(1).toString());
		byte priorityValue =  (byte) Tools.getStaticValue("resource_management_msgs.MessagePriority",actionTerms.get(2).toString());
		pr2_head_manager_msgs.Point point = rosAgArch.createMessage(pr2_head_manager_msgs.Point._TYPE);
		MessagePriority priority = rosAgArch.createMessage(MessagePriority._TYPE);
		priority.setValue(priorityValue);
		point.setPriority(priority);
		ListTermImpl aBitLower = new ListTermImpl();
		aBitLower.add(new NumberTermImpl(0));
		aBitLower.add(new NumberTermImpl(0));
		aBitLower.add(new NumberTermImpl(-0.3));
		point.setData(getRosNode().buildPointStamped(frame, aBitLower));
		getRosNode().publish("pr2_head_"+input, point); 
		actionExec.setResult(true);
	}
}
