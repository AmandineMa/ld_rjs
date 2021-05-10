//TODO faire finir les actions auprès du plan_manager quand il y a un fail

@pick[atomic]
+!pick(Params): true <-
	.nth(0, Params, Object);
	lookAt(Object);
//	.concat("I take ", Object, Sentence);
	say("I take it");
	+planPick("armUsed", right_arm);
	planPick(Object);
	execute("pick").
	
+!take(Params): true <-
	!pick(Params).
	
+!remove(Params): true <-
	.nth(0, Params, Object);
	.nth(1, Params, Container);
	!pick([Object]);
	!drop([Container]).

@place[atomic]
+!place(Params) : planPick("armUsed", Arm) <-
	.nth(0, Params,Container);
	planPlace(Container, Arm);
	execute("place");
	-planPick("armUsed", Arm).

-!place : true <- 
	.print("place failed").
	
	
@drop[atomic]
+!drop(Params) : planPick("armUsed", Arm) <-
		Sentence = "I drop it";
	say(Sentence);
	.nth(0, Params, Container);
	planDrop(Arm, Container);
	execute("drop"); 
	.concat(Arm,"_home",PoseName);
	!move_arm([Arm, PoseName]);
	-planPick("armUsed", Arm).
	
-!drop(Params) : true <- 
	.print("drop failed").
	
	
@move[atomic]
+!move_arm(Params) : true <-
	.nth(0, Params, Arm);
	.nth(1, Params, Pose);
	planMoveArm(Arm,Pose);
	execute("moveArm").

+!getUnRef(Object, Human): true <-
	disambiguate(Object,robot, false);
	?sparql_result(Object,S);
	sparqlVerbalization(S, Human).
	
+!sayPickPlace(VerbaCube, VerbaBox) : true <-
	.concat("Can you put ",VerbaCube, " in ",VerbaBox,"?", Vc);
	say(Vc).
	

+!robot_tell_human_to_tidy(Params): true <-
	?humanName(Human);
	rjs.jia.delete_from_list(Human,Params,ParamsNoH);
	for(.member(P,ParamsNoH)){
		!getUnRef(P,Human);
	}
	.findall(P,.member(P,ParamsNoH) & jia.query_onto_class(P,"Cube"),CubeL);
	.findall(P,.member(P,ParamsNoH) & jia.query_onto_class(P,"Box"),BoxL);
	.nth(0,CubeL,Cube);
	.nth(0,BoxL,Box);
	?verba(Cube,VerbaCube);
	?verba(Box,VerbaBox);
	!sayPickPlace(VerbaCube, VerbaBox).

+!robot_wait_for_human_to_tidy(Params): true <- true.

@strafe[atomic]
+!strafe(Params): true <-
	.nth(0,Params,Type);
	.nth(1,Params,Object);
	strafe(Type,Object).


+!head_scan(Params): true <-
	scanTable.

	
	