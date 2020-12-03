// Agent robot_decision in project disambi_task
{ include("common.asl")}
{ include("actions.asl")}

/* Initial beliefs and rules */
robotState(idle).

/* Initial goals */

!start.

/* Plans */

+!start : true <-
	.verbose(2);
	rjs.jia.log_beliefs;
	!getRobotName.

+action(ID,Name,Agents,Cost) : robotState(idle) & robotName(Agent) & .member(Agent,Agents) <-
	-action(ID,Name,Agents,Cost)[source(_)];
	!executeAction(ID,Name).
	
+action(ID,Name,Agents,Cost) : not robotState(idle) & robotName(Agent) & .member(Agent,Agents) <-
	-action(ID,Name,Agents,Cost)[source(_)];
	+action(ID,Name,"pending").
	
+robotState(idle) : true <-
	if(rjs.jia.believes(action(_,_,"pending"))){
		?action(ID,Name,"pending");
		!executeAction(ID,Name)
	}.
	

+!executeAction(ID,Name) : true <-
	-+robotState(acting);
	.concat("/robot_decision/action_names/",Name,HATPName);
	rjs.jia.get_param(HATPName, "String", ActName);
	.term2string(Action,ActName);
	.send(plan_manager, tell, action(ID,"ongoing"));
	if(rjs.jia.believes(actionParams(ID,Params))){
		?actionParams(ID,Params);
//		-actionParams(ID,Params)[source(_)];
		Act =.. [Action, [Params],[]];
	}else{
		Act =.. [Action, [],[]];
	}
	+action(ID,Name,"ongoing");
	!Act;
	-+robotState(idle);
	-+action(ID,Name,"executed");
	.send(plan_manager, tell, action(ID,"executed")).
	
+planOver : true <-
	say("bravo ! we did it !").
	