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
	!initRosComponents;
	!getAgentNames.

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
	if(jia.is_of_class(class,Name,"PhysicalAction") | jia.is_of_class(class,Name,"Wait")){
//		.send([robot_management,action_monitoring,human_management], tell, action(ID,"ongoing",Name,Agent,Params));
		.send(robot_management, tell, action(ID,"ongoing",Name,Agent,Params));
		+action(ID,"ongoing",Name,Agent,Params);
	} elif(jia.is_of_class(class,Name,"CommunicateAction")){
		.send([communication], tell, action(ID,"todo",Name,Agent,Params));
		+action(ID,"todo",Name,Agent,Params);
	}
	.random(X);
	Y=math.round(X*1000000);
	.concat(Name,"_",Y, NameX);
	jia.insert_task_mementar(NameX,start);
	Act =.. [Action, [Params],[]];
	!Act;
	jia.insert_task_mementar(NameX,end);
	-+robotState(idle)[ground];
	-action(ID,"ongoing",Name,Agent,Params)[source(_)];
	+action(ID,Name,"executed");
	if(jia.is_of_class(class,Name,"PhysicalAction") | jia.is_of_class(class,Name,"Wait")){
		.send(robot_management, tell, action(ID,"executed",Name,Agent,Params));
//		.send([robot_management,action_monitoring,human_management], tell, action(ID,"executed",Name,Agent,Params));
	} elif(jia.is_of_class(class,Name,"CommunicateAction")){
		.send([robot_management,human_management], tell, action(ID,"executed",Name,Agent,Params));	
	}.
	
+?scanTable : true <-
	scanTable.
	
-?scanTable : true.

+action(ID,"ongoing",Name,Agent,Params) : true <-
	-action(ID,"todo",Name,Agent,Params).
	
+!drop(G) : true <-
	!reset.
	
+!reset : robotState(idle) <-
	.drop_all_desires;
	rjs.jia.abolish_all_except_ground.	
	
+!reset : robotState(acting) <-
	.wait(robotState(X) & X == idle);
	.drop_all_desires;
	rjs.jia.abolish_all_except_ground.	
	
