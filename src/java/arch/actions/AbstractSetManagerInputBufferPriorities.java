package arch.actions;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.netty.buffer.ChannelBuffers;

import jason.asSemantics.ActionExec;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Term;
import resource_management_msgs.PrioritiesSetter;
import rjs.arch.actions.AbstractAction;
import rjs.arch.agarch.AbstractROSAgArch;
import rjs.utils.Tools;

public abstract class AbstractSetManagerInputBufferPriorities extends AbstractAction {
	
	protected String managerName;

	public AbstractSetManagerInputBufferPriorities(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		super(actionExec, rosAgArch);
		managerName = setManagerName();
	}
	

	@Override
	public void execute() {
		ListTerm list = (ListTerm) actionTerms.get(0);
		PrioritiesSetter setter = rosAgArch.createMessage(PrioritiesSetter._TYPE);
		List<String> bufferList;
		byte[] priorityList;
		// for action arguments in the form of [INPUTNAME, PRIORITY]
		if(actionTerms.size() == 1 && list.size() == 2) {
			String inputName = Tools.removeQuotes(list.get(0).toString());
			byte priorityValue =  (byte) Tools.getStaticValue("resource_management_msgs.PrioritiesSetter",list.get(1).toString());
			HashMap<String, HashMap<String, String>> topics = rosAgArch.getRosnode().getTopicsMap();
			bufferList = new ArrayList<String>();
			for(String topic : topics.keySet()) {
				if(topic.startsWith(managerName+"_") && !topic.endsWith("prio")) {
					String topicName = topics.get(topic).get("name");
					Pattern p = Pattern.compile("\\/.*\\/(.*)\\/.*");
					Matcher m = p.matcher(topicName);
					if(m.find()) {
						String bufferName = m.group(1);
						bufferList.add(bufferName);
					} 
				}
			}
			priorityList = new byte[bufferList.size()];
			for(int i = 0; i < bufferList.size(); i++) {
				if(bufferList.get(i).equals(inputName)) 
					priorityList[i] = priorityValue;
				else
					priorityList[i] =  PrioritiesSetter.BACKGROUND;
			}
		    
		 // for action arguments in the form of [INPUTNAME1, INPUTNAME2,...],[PRIORITY1,PRIORITY2,...]
		} else if(actionTerms.size() == 2 && actionTerms.get(1).isList()){
			bufferList = Tools.listTermStringTolist((ListTermImpl) list);
			priorityList = new byte[bufferList.size()];
			Iterator<Term> list2Ite = ((ListTerm) actionTerms.get(1)).iterator();
			int i = 0;
			while(list2Ite.hasNext()) {
				priorityList[i++] = (byte) Tools.getStaticValue("resource_management_msgs.PrioritiesSetter",list2Ite.next().toString());
			}
		} else {
			actionExec.setResult(false);
			actionExec.setFailureReason(new Atom("wrong_arguments"), "wrong number of arguments or type");
			rosAgArch.actionExecuted(actionExec);
			return;
		}
		setter.setBuffers(bufferList);
	    setter.setValues(ChannelBuffers.wrappedBuffer(ByteOrder.LITTLE_ENDIAN, priorityList));
	    getRosNode().publish(managerName+"_prio",setter);
	    
		actionExec.setResult(true);
		rosAgArch.actionExecuted(actionExec);
	}
	
	protected abstract String setManagerName();
	
}
