package arch.actions.robot;

import jason.asSemantics.ActionExec;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.NumberTermImpl;
import resource_management_msgs.MessagePriority;
import rjs.arch.actions.AbstractAction;
import rjs.arch.agarch.AbstractROSAgArch;
import rjs.utils.Tools;

public class LookAt extends AbstractAction {

	public LookAt(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		super(actionExec, rosAgArch);
		setSync(true);
	}

	@Override
	public void execute() {
		String frame = Tools.removeQuotes(actionTerms.get(0).toString());
		pr2_head_manager_msgs.Point point = rosAgArch.createMessage(pr2_head_manager_msgs.Point._TYPE);
		MessagePriority priority = rosAgArch.createMessage(MessagePriority._TYPE);
		priority.setValue(MessagePriority.HIGH);
		point.setPriority(priority);
		ListTermImpl aBitLower = new ListTermImpl();
		aBitLower.add(new NumberTermImpl(0));
		aBitLower.add(new NumberTermImpl(0));
		aBitLower.add(new NumberTermImpl(-0.3));
		point.setData(getRosNode().buildPointStamped(frame, aBitLower));
		getRosNode().publish("pr2_head", point); 
		actionExec.setResult(true);
	}
}
