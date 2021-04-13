package arch.actions.internal;

import java.util.List;

import jason.asSemantics.ActionExec;
import jason.asSyntax.Atom;
import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import knowledge_sharing_planner_msgs.Merge;
import knowledge_sharing_planner_msgs.MergeRequest;
import knowledge_sharing_planner_msgs.MergeResponse;
import rjs.arch.actions.AbstractAction;
import rjs.arch.agarch.AbstractROSAgArch;

public abstract class AbstractDisambiguationAction extends AbstractAction {

	public AbstractDisambiguationAction(ActionExec actionExec, AbstractROSAgArch rosAgArch) {
		super(actionExec, rosAgArch);
	}

	protected abstract String setFailure(String failureReason);
	
	protected void handleFailure(Literal failureReason) {
		String msg = setFailure(failureReason.getFunctor());
		actionExec.setResult(false);
		actionExec.setFailureReason(new Atom(failureReason), msg);
		clearBelief();
	}
	
	protected void handleFailure(String failureReason) {
		Literal failure = new LiteralImpl(failureReason);
		handleFailure(failure);
	}
	
	protected void clearBelief() {
		rosAgArch.removeBelief("mergedQuery(_)");
		rosAgArch.removeBelief("sparql_input(_)");
		rosAgArch.removeBelief("match(_)");
		rosAgArch.removeBelief("sentence(_)"); 
		rosAgArch.removeBelief("verba(disambi_objects_sentence,_)");
		rosAgArch.removeBelief("action(\"planned\",_,_,_)");
	}
	
	protected MergeResponse callMergeService(List<String> contextQuery, List<String> baseQuery, boolean partial) {
		MergeRequest mergeReq = getRosNode().newServiceRequestFromType(Merge._TYPE);
		mergeReq.setContextQuery(contextQuery);
		mergeReq.setBaseQuery(baseQuery);
		mergeReq.setPartial(partial);
		return getRosNode().callSyncService("ksp_merge", mergeReq);
	}

}
