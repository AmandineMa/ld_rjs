
+!tellToPack(Params): true <-
	.nth(2, Params,Area);
	!getUnRef(Area);
	?verba(Area,Verba);
	!sayPack(Verba).
	
+!tellToTake(Params): true <-
	.nth(1, Params,Object);
	!getUnRef(Object);
	?verba(Object,Verba);
	!sayTake(Verba).
	
+!getUnRef(Object): true <-
	disambiguate(Object,robot);
	?sparql_result(Object,S);
	sparql_verbalization(S).
	
+!sayPack(Area) : true <-
	.concat("Can you put it in ", Area, " ?", Vc);
	say(Vc).
	
+!sayTake(Verba) : .length(Verba,X) & X > 0 <-
	.concat("Can you take ", Verba, " ?", Vc);
	say(Vc).

+!sayTake(Verba) : true <-
	say("I could not find any disambiguation for this object").
	
+!pointObject(Params): true <-
	.nth(1, Params,Object);
	say("can you take this cube ?");
	pointObject(Object).

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

	