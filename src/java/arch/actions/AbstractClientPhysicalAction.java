package arch.actions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.ros.internal.message.Message;
import org.ros.message.Time;

import arch.agarch.ExecutorAgArch;
import arch.agarch.ExecutorAgArch.ActionIndicator;
import jason.asSemantics.ActionExec;
import rjs.arch.actions.AbstractClientAction;
import rjs.arch.actions.ros.RjsActionClient;
import rjs.utils.Tools;

public abstract class AbstractClientPhysicalAction<T_ACTION_GOAL extends Message, T_ACTION_FEEDBACK extends Message, T_ACTION_RESULT extends Message>
		extends AbstractClientAction<T_ACTION_GOAL, T_ACTION_FEEDBACK, T_ACTION_RESULT> {
			
		protected boolean firstFeedback = true;
		protected String actionID;
			
		public AbstractClientPhysicalAction(ActionExec actionExec, ExecutorAgArch rosAgArch, RjsActionClient<T_ACTION_GOAL, T_ACTION_FEEDBACK, T_ACTION_RESULT> actionClient) {
			super(actionExec, rosAgArch, actionClient);
			actionID = "_"+Math.round(Math.random()*200);
		}

		@Override
		public void feedbackReceived(T_ACTION_FEEDBACK actionFeedback) {
			if(firstFeedback) {
				Method getActionFbMethod;
				Method getTimeMethod;
				try {
					getActionFbMethod = actionFeedback.getClass().getMethod("getFeedback");
					getActionFbMethod.setAccessible(true);
					Object feedback = getActionFbMethod.invoke(actionFeedback);
					getTimeMethod = feedback.getClass().getMethod("getActionStart");
					getTimeMethod.setAccessible(true);
					Time startTime  = (Time) getTimeMethod.invoke(feedback);
					((ExecutorAgArch) rosAgArch).callInsertAction(actionName+actionID, startTime, ActionIndicator.START);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					Tools.getStackTrace(e);
				}
				firstFeedback = false;
			}
			endFeedbackReceived(actionFeedback);
		}
		
		protected abstract void endFeedbackReceived(T_ACTION_FEEDBACK fb);

		@Override
		public void resultReceived(T_ACTION_RESULT actionResult) {
			Method getActionResultMethod;
			Method getTimeMethod;
			try {
				getActionResultMethod = actionResult.getClass().getMethod("getResult");
				getActionResultMethod.setAccessible(true);
				Object result = getActionResultMethod.invoke(actionResult);
				getTimeMethod = result.getClass().getMethod("getActionEnd");
				getTimeMethod.setAccessible(true);
				Time endTime  = (Time) getTimeMethod.invoke(result);
				((ExecutorAgArch) rosAgArch).callInsertAction(actionName+actionID, endTime, ActionIndicator.END);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				Tools.getStackTrace(e);
			}
			super.resultReceived(actionResult);
			
		}
		
		
}
