package arch.actions.robot;

import java.util.ArrayList;
import actionlib_msgs.GoalStatusArray;
import dialogue_as.dialogue_actionActionFeedback;
import dialogue_as.dialogue_actionActionGoal;
import dialogue_as.dialogue_actionActionResult;
import dialogue_as.dialogue_actionFeedback;
import dialogue_as.dialogue_actionGoal;
import jason.asSemantics.ActionExec;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Term;
import rjs.arch.actions.AbstractClientAction;
import rjs.arch.actions.ros.RjsActionClient;
import rjs.arch.agarch.AbstractROSAgArch;

public class Listen extends AbstractClientAction<dialogue_actionActionGoal, dialogue_actionActionFeedback, dialogue_actionActionResult>{
	
	
	public Listen(ActionExec actionExec, AbstractROSAgArch rosAgArch, RjsActionClient<dialogue_actionActionGoal, dialogue_actionActionFeedback, dialogue_actionActionResult> das) {
		super(actionExec, rosAgArch, das);
	}

	public void execute() {
		super.execute();
	}
	
	@Override
	public void feedbackReceived(dialogue_actionActionFeedback feedback) {
		dialogue_actionFeedback fb = feedback.getFeedback();
		logger.info("feedback :"+fb.getSubject());
	}

	@Override
	public dialogue_actionActionGoal computeGoal() {
		ArrayList<String> words = new ArrayList<String>();
		if(actionTerms.get(0).isList()) {
			for (Term term : (ListTermImpl) actionTerms.get(0)) {
				words.add(term.toString().replaceAll("^\"|\"$", ""));
			}
		}else {
			words.add(actionTerms.get(1).toString().replaceAll("^\"|\"$", ""));
		}
		dialogue_actionActionGoal actionGoal = (dialogue_actionActionGoal) newGoalMessage();
		dialogue_actionGoal goal = actionGoal.getGoal();
		goal.setSubjects(words); 
		goal.setEnableOnlySubject(true);
		return actionGoal;
	}

	@Override
	public void setResultSucceeded(dialogue_actionActionResult result) {
		logger.info("result succeeded :"+result.getResult().getSubject()+" received from goal "+result.getStatus().getGoalId().getId());
		
	}

	@Override
	public void statusReceived(GoalStatusArray status) {
		
	}

	@Override
	protected void setResultAborted(dialogue_actionActionResult result) {
	}

}
