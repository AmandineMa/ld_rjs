@pick[atomic]
+!pick(Params): true <-
	.nth(0, Params,Object);
	planPick(Object);
	execute("pick").

@place[atomic]
+!place(Params) : planPick("armUsed", Arm) <-
	.nth(0, Params,Box);
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
+!move(Params) : true <-
	.nth(0, Params,Pose);
	planMove(Pose);
	execute("move").

+!getUnRef(Object, Human): true <-
	disambiguate(Object,robot, false);
	?sparql_result(Object,S);
	sparqlVerbalization(S, Human).
	
+!sayPickPlace(VerbaCube, VerbaBox) : true <-
	.concat("Can you put ",VerbaCube, " in ",VerbaBox,"?", Vc);
	say(Vc).

//TODO envoyer les bons params, peu importe l'ordre
+!robot_tell_human_to_tidy(Params): true <-
	.nth(1, Params,Human);
	.nth(0, Params,Cube);
	!getUnRef(Cube,Human);
	.nth(2, Params,Box);
	!getUnRef(Box,Human);
	?verba(Cube,VerbaCube);
	?verba(Box,VerbaBox);
	!sayPickPlace(VerbaCube, VerbaBox).

+!robot_wait_for_human_to_tidy(Params): true <- true.






	
	