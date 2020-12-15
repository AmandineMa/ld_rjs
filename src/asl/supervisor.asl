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
	initServices;
	+started;
//	!pick("obj1");
////	!place("box1");
//	!drop;
//	!move("left_arm_home");
////	!test1 |&| !test2;
//	initServices;
	.create_agent(plan_manager, "src/asl/plan_manager2.asl", [agentArchClass("arch.agarch.PlanManagerAgArch"), beliefBaseClass("rjs.agent.TimeBB"), agentClass("agent.OntoAgent")]).
//	.create_agent(robot_decision, "src/asl/robot_decision.asl", [agentArchClass("arch.agarch.AgArch"), beliefBaseClass("rjs.agent.TimeBB"), agentClass("rjs.agent.LimitedAgent")]);.
//	.create_agent(robot, "src/asl/disambiguation_task.asl", [agentArchClass("arch.agarch.RobotAgArch"), beliefBaseClass("rjs.agent.TimeBB"), agentClass("rjs.agent.LimitedAgent")]);.
	
	
+~connected_srv(S) : true <- .print("service not connected : ", S).

-!start [Failure, error(ErrorId), error_msg(Msg), code(CodeBody), code_src(CodeSrc), code_line(CodeLine), source(self)]: true <-
	if(.substring(Failure, "srv_not_connected")){
		!retry_init_services;
	}
	+started.

-!start [code(Code),code_line(_),code_src(_),error(_),error_msg(_),source(self)] : true <- true.
	
+!retry_init_services : true <-
	retryInitServices.	
	
-!retry_init_services : true <-
	.wait(3000);
	!retry_init_services.

+!test1 : true <- listen(["hello", "bonjour", "salut"]).
+!test2 : true <- .wait(5000); listen(["bye", "au revoir"]).

//+started : true <- !getUnRef("test", "h_1"); .print("bouh").