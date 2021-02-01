package arch.agarch;

import org.ros.message.Time;

import mementar.MementarAction;

public class ExecutorAgArch extends LAASAgArch {

	public ExecutorAgArch() {
		super();
	}
	
	public enum ActionIndicator {
		START,
		END
	}
	
	public void callInsertAction(String action, Time time, ActionIndicator actionIndicator) {
		MementarAction memAction = createMessage(std_msgs.String._TYPE);
		memAction.setName(action);
		switch(actionIndicator) {
		case START:
			memAction.setStartStamp(time);
			break;
		case END:
			memAction.setEndStamp(time);
			break;
		}
		rosnode.publish("insert_action", memAction); 
	}

}
