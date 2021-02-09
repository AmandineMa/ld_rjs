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
+!drop(Params) : planPick("armUsed", Arm) <-
	planDrop(Arm);
	execute("drop");
	-planPick("armUsed", Arm).
	
-!drop(Params) : true <- 
	.print("no arm to use available").
	
	
@move[atomic]
+!move_arm(Params) : true <-
	.nth(0, Params,Pose);
	planMoveArm(Pose);
	execute("moveArm").

+!getUnRef(Object, Human): true <-
	disambiguate(Object,robot, false);
	?sparql_result(Object,S);
	sparqlVerbalization(S, Human).
	
+!sayPickPlace(VerbaCube, VerbaBox) : true <-
	.concat("Can you put ",VerbaCube, " in ",VerbaBox,"?", Vc);
	say(Vc).
	

+!robot_tell_human_to_tidy(Params): true <- say("Can you put the cube in the box ?").
//	?humanName(Human);
//	rjs.jia.delete_from_list(Human,Params,ParamsNoH);
//	for(.member(P,ParamsNoH)){
//		!getUnRef(P,Human);
//	}
//	.findall(P,.member(P,ParamsNoH) & jia.isCube(P),CubeL);
//	.findall(P,.member(P,ParamsNoH) & jia.isBox(P),BoxL);
//	.nth(0,CubeL,Cube);
//	.nth(0,BoxL,Box);
//	?verba(Cube,VerbaCube);
//	?verba(Box,VerbaBox);
//	!sayPickPlace(VerbaCube, VerbaBox).
	


+!robot_wait_for_human_to_tidy(Params): true <- true.

+!robot_congratulate(Params): true <- say("bravo ! we did it !").

@strafe[atomic]
+!strafe(Params): true <-
	.nth(0,Params,Type);
	.nth(1,Params,Object);
	strafe(Type,Object).






	
	