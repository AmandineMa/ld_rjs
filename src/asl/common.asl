rmMsgPrio(vital,4)[ground].
rmMsgPrio(urgent,3)[ground].
rmMsgPrio(high,2)[ground].
rmMsgPrio(standard,1)[ground].
rmMsgPrio(low,0)[ground].
rmMsgPrio(void,-1)[ground].

rmBuffPrio(atomic,4)[ground].
rmBuffPrio(prioritize,3)[ground].
rmBuffPrio(normal,2)[ground].
rmBuffPrio(secondary,1)[ground].
rmBuffPrio(background,0)[ground].
rmBuffPrio(inhibit,-1)[ground].

+!getAgentNames : true <-
	!getRobotName;
	!getHumanName.

+!getRobotName : true <- 
	rjs.jia.get_param("/supervisor/robot_name", "String", Name);
	+robotName(Name)[ground].
	
+!getHumanName : true <-
	rjs.jia.get_param("/supervisor/human_name", "String", Name);
	+humanName(Name)[ground].

+!initRosComponents : true <- 
	!init_services;
	!init_sub.
	
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

+!head_scan : true <-
	.send(robot_executor, tell, action(_,"head_scan","robot",[])).
	
+!send_goal(Goal) : true <-
	.send(robot_management, tell, goal(Goal,received)).
	
//TODO idle human not removed
	
+!reset : true <-
	.drop_all_desires;
	rjs.jia.abolish_all_except_ground.
	
	