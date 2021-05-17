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
	
+!reset : true <-
	.broadcast(achieve, reset).
	