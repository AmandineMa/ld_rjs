package arch.agarch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ros.exception.ServiceNotFoundException;
import org.ros.message.Time;
import org.ros.node.service.ServiceClient;

import arch.actions.ActionFactoryImpl;
import jason.asSyntax.Literal;
import knowledge_sharing_planner_msgs.Disambiguation;
import knowledge_sharing_planner_msgs.DisambiguationRequest;
import knowledge_sharing_planner_msgs.DisambiguationResponse;
import knowledge_sharing_planner_msgs.SymbolTable;
import knowledge_sharing_planner_msgs.Triplet;
import knowledge_sharing_planner_msgs.Verbalization;
import knowledge_sharing_planner_msgs.VerbalizationRequest;
import knowledge_sharing_planner_msgs.VerbalizationResponse;
import mementar.MementarAction;
import ontologenius.OntologeniusService;
import ontologenius.OntologeniusServiceRequest;
import ontologenius.OntologeniusServiceResponse;
import rjs.arch.agarch.AbstractROSAgArch;
import rjs.utils.Tools;
import ros.RosNode;

public class LAASAgArch extends AbstractROSAgArch {
	
	protected ArrayList<Integer> monitoringIDs = new ArrayList<Integer>();

	public LAASAgArch() {
		super();
	}
	
	@Override
	public void initRosNode() {
		rosnode = new RosNode(getAgName());	
	}
	
	@Override
	public void initListeners() {
	}
	
	@Override
	public void init() {
		super.init();
		setActionFactory(new ActionFactoryImpl());
	}
	
	public OntologeniusServiceResponse callOntoIndiv(String action, String param, String agentType) {
		if(agentType.equals("robot"))
			return callOntoIndivRobot(action, param);
		else if(agentType.equals("human")) 
			return callOntoIndivHuman(action,param);
		else 
			return null;
	}
	
	public OntologeniusServiceResponse callOntoIndivRobot(String action, String param) {
		return callOnto("individual", action, param);
	}
	
	public OntologeniusServiceResponse callOntoIndivHuman(String action, String param) {
		OntologeniusServiceRequest req = rosnode.newServiceRequestFromType(OntologeniusService._TYPE);
		req.setAction(action);
		req.setParam(param);
		ServiceClient<OntologeniusServiceRequest, OntologeniusServiceResponse> serviceClient;
		try {
			Literal robotNameL = findBel("robotName(_)");
			String robotName = "robot";
			Literal humanNameL = findBel("humanName(_)");
			String humanName = "";
			if(humanNameL != null)
				humanName =  Tools.removeQuotes(humanNameL.getTerm(0).toString());
			if(robotNameL != null)
				robotName = Tools.removeQuotes(robotNameL.getTerm(0).toString());
			serviceClient = getConnectedNode().newServiceClient(rosnode.getParameters().getString("supervisor/services/onto_individual/name").replace(robotName, humanName), OntologeniusService._TYPE);
			return  rosnode.callSyncService(serviceClient, req);
		} catch (ServiceNotFoundException e) {
			Tools.getStackTrace(e);
		}
		return null;
	}
	
	public OntologeniusServiceResponse callOntoClass(String action, String param) {
		return callOnto("class", action, param);
	}
	
	public OntologeniusServiceResponse callOnto(String onto, String action, String param) {
		OntologeniusServiceRequest req = rosnode.newServiceRequestFromType(OntologeniusService._TYPE);
		req.setAction(action);
		req.setParam(param);
		return  rosnode.callSyncService("onto_"+onto, req);
	}
	
	public List<String> getEntitySparql(String individual, String agent, boolean replan) {
		DisambiguationRequest disambiReq = rosnode.newServiceRequestFromType(Disambiguation._TYPE);
		disambiReq.setIndividual(individual);
		disambiReq.setOntology(agent);
		List<String> ontoRep = callOntoIndivRobot("getOn", individual+":isAbove").getValues();
		if(!ontoRep.isEmpty()) {
			Triplet ctx = createMessage(Triplet._TYPE);
			ctx.setFrom("?0");
			ctx.setRelation("isAbove");
			ctx.setOn(rosnode.getParameters().getString("supervisor/table_name"));
			disambiReq.setBaseFacts(Arrays.asList(ctx));
		}
		disambiReq.setReplan(replan);
		SymbolTable symbT = createMessage(SymbolTable._TYPE);
		symbT.setIndividuals(Arrays.asList(individual));
		symbT.setSymbols(Arrays.asList("?0"));
		disambiReq.setSymbolTable(symbT);
		return ((DisambiguationResponse) rosnode.callSyncService("disambiguate",disambiReq)).getSparqlResult();
	}
	
	public String sparqlToVerba(List<String>  sparql, String receiverID) {
		VerbalizationRequest verbaReq = rosnode.newServiceRequestFromType(Verbalization._TYPE);
		verbaReq.setSparqlQuery(sparql);
		verbaReq.setReceiverId(receiverID);
		return ((VerbalizationResponse) rosnode.callSyncService("verbalize", verbaReq)).getVerbalization();
	}
	
	public enum ActionIndicator {
		START,
		END
	}
	
	public void callInsertAction(String action, Time time, ActionIndicator actionIndicator) {
		MementarAction memAction = createMessage(MementarAction._TYPE);
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
	
	public void addMonitoringID(Integer id) {
		monitoringIDs.add(id);
	}
	
	public void removeMonitoringID(Integer id) {
		monitoringIDs.remove(id);
	}

}
