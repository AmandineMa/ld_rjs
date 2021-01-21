{ include("actions.asl")}

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- 
	rjs.jia.log_beliefs;
	.verbose(2);
	configureNode;
	startParameterLoaderNode("/general.yaml", "/robot_decision.yaml", "/plan_manager.yaml");
	startROSNode;
	initServices;
	+started;
	rjs.jia.get_param("supervisor/scan_table", "Boolean", Scan);
	if(Scan == true){
		scanTable; //TODO pourquoi fail alors que service renvoie success
	}
	!create_agents.
	
+~connected_srv(S) : true <- .print("service not connected : ", S).

-!start [Failure, error(ErrorId), error_msg(Msg), code(CodeBody), code_src(CodeSrc), code_line(CodeLine), source(self)]: true <-
	if(.substring(Failure, "srv_not_connected")){
		!retry_init_services;
	}
	+started.

-!start [code(Code),code_line(_),code_src(_),error(_),error_msg(_),source(self)] : true <- true.
	
+!retry_init_services : true <-
	retryInitServices;
	!create_agents.	
	
-!retry_init_services : true <-
	.wait(3000);
	!retry_init_services.

+!create_agents : true <-
	.create_agent(plan_manager, "src/asl/plan_manager.asl", [agentArchClass("arch.agarch.PlanManagerAgArch"), beliefBaseClass("rjs.agent.TimeBB"), agentClass("agent.OntoAgent")]);
	.create_agent(robot_executor, "src/asl/robot_executor.asl", [agentArchClass("arch.agarch.AgArch"), beliefBaseClass("rjs.agent.TimeBB"), agentClass("rjs.agent.LimitedAgent")]).
