//TODO faire finir les actions auprès du plan_manager quand il y a un fail

// object monitoring added at the beginning of the plan and at the end remove it
{begin rad}
@pick[max_attempts(2)]
+!pick(Params): true <-
	//TODO arm to used should be computed somewhere
	+planPick("armUsed", right_arm);
	planPick(Obj);
	execute("pick").
	
@place[max_attempts(2)]
+!place(Params) : planPick("armUsed", Arm) <-
	planPlace(Obj, Arm);
	execute("place");
	!move_arm([Arm]);
	-planPick("armUsed", Arm).
	
@drop[atomic]
+!drop(Params) : planPick("armUsed", Arm) <-
	planDrop(Arm, Obj);
	execute("drop"); 
	!move_arm([Arm]);
	-planPick("armUsed", Arm).
	
{end}
	
	
@move[atomic]
+!move_arm(Params) : true <-
	.nth(0, Params, Arm);
	.concat(Arm,"_home",PoseName);
	planMoveArm(Arm,PoseName);
	execute("moveArm").
	
// Actions in shared plan
	
+!take(Params): true <-
	!pick(Params).
	
+!remove(Params): true <-
	.nth(0, Params, Object);
	?container(C);
	!pick([Object]);
	!drop([C]).
	
+!robot_pick_cube(Params) : true <-
	!pick(Params).
	
+!robot_place_cube(Params): true <-
	!place(Params).

+!robot_tell_human_to_tidy(Params): true <-
	.nth(1, Params, Object);
	.nth(2, Params, Table);
	?action(ID,_,Name,_,Params);
	.send(communication,askOne,askActionsTodo([action(ID,"PickAndPlaceAction",[Object,Table])],and),Answer).

@strafe[atomic]
+!strafe(Params): true <-
	.nth(0,Params,Type);
	.nth(1,Params,Object);
	strafe(Type,Object).


+!head_scan(Params): true <-
	scanTable.

+!robot_congratulate(Params): true <-
	.send(communication,askOne,talk("Bravo, we did it !"),Answer).
	