{ include("actions.asl")}

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- 
	rjs.jia.log_beliefs;
	.verbose(2);
	configureNode;
	startParameterLoaderNode("/general.yaml", "/robot_decision.yaml");
	startROSNode;
	!pick("obj1");
//	!place("box1");
	!drop;
	!move("left_arm_home");
//	!test1 |&| !test2;
	.print("bouh").
//	initServices;
//	.create_agent(plan_manager, "src/asl/plan_manager.asl", [agentArchClass("arch.agarch.AgArch"), beliefBaseClass("rjs.agent.TimeBB"), agentClass("rjs.agent.LimitedAgent")]);
//	.create_agent(robot_decision, "src/asl/robot_decision.asl", [agentArchClass("arch.agarch.AgArch"), beliefBaseClass("rjs.agent.TimeBB"), agentClass("rjs.agent.LimitedAgent")]);.
//	.create_agent(robot, "src/asl/disambiguation_task.asl", [agentArchClass("arch.agarch.RobotAgArch"), beliefBaseClass("rjs.agent.TimeBB"), agentClass("rjs.agent.LimitedAgent")]);.
	
	
+~connected_srv(S) : true <- .print("service not connected : ", S).

+!test1 : true <- listen(["hello", "bonjour", "salut"]).
+!test2 : true <- .wait(5000); listen(["bye", "au revoir"]).