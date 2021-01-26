{ include("actions.asl")}

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- 
	rjs.jia.log_beliefs;
//	.verbose(2);
	configureNode;
	startParameterLoaderNode("/general.yaml", "/plan_manager.yaml");
	startROSNode;
	!init_services;
	!init_sub;
	+started;
	rjs.jia.get_param("supervisor/scan_table", "Boolean", Scan);
	if(Scan == true){
		scanTable; 
	}
	!create_agents.
	
-!start [code(Code),code_line(_),code_src(_),error(_),error_msg(_),source(self)] : true <- true.

+!init_services : true <-
	initServices.

-!init_services: true <-
	!retry_init_services.

+!retry_init_services : true <-
	retryInitServices.	
	
-!retry_init_services : true <-
	.wait(3000);
	!retry_init_services.

+!init_sub : true <-
	initSub.
	
-!init_sub : true <-
	.wait(3000);
	!init_sub.

+!create_agents : true <-
	.create_agent(plan_manager, "src/asl/plan_manager.asl", [agentArchClass("arch.agarch.PlanManagerAgArch"), beliefBaseClass("rjs.agent.TimeBB"), agentClass("agent.OntoAgent")]);
	.create_agent(robot_executor, "src/asl/robot_executor.asl", [agentArchClass("arch.agarch.AgArch"), beliefBaseClass("rjs.agent.TimeBB"), agentClass("rjs.agent.LimitedAgent")]).
