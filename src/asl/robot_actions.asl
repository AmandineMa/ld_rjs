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
	.findall(P,.member(P,Params) & jia.is_of_class(individual,P,"Cube"),CubeL);
	.findall(P,.member(P,Params) & jia.is_of_class(individual,P,"Box"),BoxL);
	?action(ID,_,Name,_,Params);
	.send(communication,askOne,askActionsTodo([action(ID,"PickAction",CubeL),action(ID,"DropAction",BoxL)],and),Answer).

+!robot_wait_for_human_to_tidy(Params): true <- true.

@strafe[atomic]
+!strafe(Params): true <-
	.nth(0,Params,Type);
	.nth(1,Params,Object);
	strafe(Type,Object).


+!head_scan(Params): true <-
	scanTable.

+!robot_congratulate(Params): true <-
	.send(communication,askOne,talk("Bravo, we did it !"),Answer).
	