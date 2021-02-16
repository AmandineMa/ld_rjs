
+!getRobotName : true <- 
	rjs.jia.get_param("/supervisor/robot_name", "String", Name);
	+robotName(Name)[ground].
	
+!getHumanName : true <-
	rjs.jia.get_param("/supervisor/human_name", "String", Name);
	+humanName(Name)[ground].

	