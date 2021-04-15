!start.

/* Plans */

+!start : true <- 
//	rjs.jia.set_displayed_beliefs_to_file;
//	rjs.jia.log_beliefs;
//	.verbose(2);
	configureNode;
	startParameterLoaderNode("/general.yaml", "/plan_manager.yaml");
	startROSNode;
	!init_services;
//	!init_sub;
	+started;
//	!create_agents;
	jia.createGUI.
	

+!init_services : true <-
	initServices.

-!init_services: true <-
	.wait(3000);
	!init_services.

+!init_sub : true <-
	initSub.
	
-!init_sub : true <-
	.wait(3000);
	!init_sub.

+!create_agents : true <-
	.create_agent(plan_manager, "src/asl/plan_manager.asl", [agentArchClass("arch.agarch.PlanManagerAgArch"), beliefBaseClass("rjs.agent.TimeBB"), agentClass("agent.OntoAgent")]);
	.create_agent(robot_executor, "src/asl/robot_executor.asl", [agentArchClass("arch.agarch.LAASAgArch"), beliefBaseClass("rjs.agent.TimeBB"), agentClass("rjs.agent.LimitedAgent")]).

	
+!head_scan : true <-
	.send(robot_executor, tell, action(_,"head_scan","robot",[])).
	
+!send_goal(Goal) : true <-
	.send(plan_manager, tell, goal(Goal,received)).
	
+!reset : true <-
	.broadcast(achieve, reset).
	
