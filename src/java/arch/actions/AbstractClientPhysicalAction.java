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
			
		public AbstractClientPhysicalAction(ActionExec actionExec, ExecutorAgArch rosAgArch, RjsActionClient<T_ACTION_GOAL, T_ACTION_FEEDBACK, T_ACTION_RESULT> actionClient) {
			super(actionExec, rosAgArch, actionClient);
		}

		@Override
		public void feedbackReceived(T_ACTION_FEEDBACK fb) {
			Method getTimeMethod;
			try {
				getTimeMethod = fb.getClass().getMethod("getActionStart");
				getTimeMethod.setAccessible(true);
				Time startTime  = (Time) getTimeMethod.invoke(fb);
				((ExecutorAgArch) rosAgArch).callInsertAction(actionName, startTime, ActionIndicator.START);
				endFeedbackReceived(fb);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				Tools.getStackTrace(e);
			}
		}
		
		protected abstract void endFeedbackReceived(T_ACTION_FEEDBACK fb);

		@Override
		public void resultReceived(T_ACTION_RESULT result) {
			Method getTimeMethod;
			try {
				getTimeMethod = result.getClass().getMethod("getActionEnd");
				getTimeMethod.setAccessible(true);
				Time endTime  = (Time) getTimeMethod.invoke(result);
				((ExecutorAgArch) rosAgArch).callInsertAction(actionName, endTime, ActionIndicator.END);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				Tools.getStackTrace(e);
			}
			super.resultReceived(result);
			
		}
		
		
}
