@pick[atomic]
+!pick(Object): true <-
	planPick(Object);
	execute("pick").

@place[atomic]
+!place(Box) : planPick("armUsed", Arm) <-
	planPlace(Box, Arm);
	execute("place");
	-planPick("armUsed", Arm).

-!place : true <- 
	.print("no arm to use available").
	
	
@drop[atomic]
+!drop : planPick("armUsed", Arm) <-
	planDrop(Arm);
	execute("drop");
	-planPick("armUsed", Arm).
	
-!drop : true <- 
	.print("no arm to use available").
	
	
@move[atomic]
+!move(Pose) : true <-
	planMove(Pose);
	execute("move").

+!getUnRef(Object, Human): true <-
	disambiguate(Object,robot, false);
	?sparql_result(Object,S);
	sparqlVerbalization(S, Human).
	