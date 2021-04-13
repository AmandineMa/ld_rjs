// Agent robot_decision in project disambi_task
{ include("common.asl")}
{ include("robot_actions.asl")}

/* Initial beliefs and rules */
robotState(idle)[ground].
/* Initial goals */

!start.

/* Plans */

+!start : true <-
	.verbose(2);
	rjs.jia.log_beliefs;
	!getRobotName;
	!getHumanName.

+action(ID,Name,Agent,Params) : robotState(idle) & robotName(Agent) <-
	-action(ID,Name,Agent,Params)[source(_)];
	!executeAction(ID,Name,Agent,Params).
	
//+action(ID,Name,Agent,Params) : not robotState(idle) & robotName(Agent) <-
//	-action(ID,Name,Agent,Params)[source(_)];
//	+action(ID,Name,"pending").
	
+robotState(idle) : true <-
	if(rjs.jia.believes(action(_,_,"pending"))){
		?action(ID,Name,"pending");
		!executeAction(ID,Name,Params);
	}.
	
//TODO cas quand action not found
+!executeAction(ID,Name,Agent,Params) : true <-
	-+robotState(acting)[ground];
	.lower_case(Name, Action);
	.send(plan_manager, tell, action(ID,"ongoing",Name,Agent,Params));
	Act =.. [Action, [Params],[]];
	+action(ID,Name,"ongoing");
	!Act;
	-+robotState(idle)[ground];
	-+action(ID,Name,"executed");
	.send(plan_manager, tell, action(ID,"executed",Name,Agent,Params)).
	
+planOver : true <-
	say("bravo ! we did it !").

+!reset : robotState(idle) <-
	.drop_all_desires;
	rjs.jia.abolish_all_except_ground.	
	
+!reset : robotState(acting) <-
	.wait(robotState(X) & X == idle);
	.drop_all_desires;
	rjs.jia.abolish_all_except_ground.	
	
	
	