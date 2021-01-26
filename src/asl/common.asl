
+!getRobotName : true <- 
	rjs.jia.get_param("/supervisor/robot_name", "String", Name);
	+robotName(Name).
	
+!getHumanName : true <-
	rjs.jia.get_param("/supervisor/human_name", "String", Name);
	+humanName(Name).

	