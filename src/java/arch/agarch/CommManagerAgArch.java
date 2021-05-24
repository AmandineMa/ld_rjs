package arch.agarch;

import org.ros.message.MessageListener;

import jason.asSyntax.StringTermImpl;

public class CommManagerAgArch extends ActionMonitoringAgArch {
	
	
	public CommManagerAgArch() {
		super();
	}
	
	@Override
	public void init() {
		super.init();
	}

	public void initListeners() {
		rosnode.setSubListener("listen", new MessageListener<std_msgs.String>() {
			
			@Override
			public void onNewMessage(std_msgs.String sentence) {
				addBelief("sentence("+new StringTermImpl(sentence.getData())+")");
			}
		});
	}

	

}
