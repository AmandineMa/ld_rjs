{include("plan_manager.asl")}
{include("receiver.asl")}

//abstractTask(0, "planned", "tidy_cubes", (-1)).
//abstractTask(1, "planned", "tidy_one", 0).
//action(3, "planned", "robot_congratulate", "robot", ["Helmet_2"], [8], 0).
//action(4, "planned", "robot_tell_human_to_tidy", "robot", ["cube_BBCG","Helmet_2","throw_box_green"], [], 1).
//abstractTask(5, "planned", "wait_for_human", 1).
//abstractTask(6, "planned", "tidy", 0).
//action(7, "planned", "human_pick_cube", "Helmet_2", ["cube_BBCG"], [4], 6).
//action(8, "planned", "human_drop_cube", "Helmet_2", ["throw_box_green"], [9], 6).
//action(9, "planned", "robot_wait_for_human_to_tidy", "robot", [], [7], 5).
//action(11, "planned", "IDLE", "Helmet_2", [], [3], 0).

//abstractTask(0, "planned", "tidy_cubes", (-1)).
//abstractTask(1, "planned", "tidy_one", 0).
//abstractTask(2, "planned", "tidy_cubes", 0).
//action(3, "planned", "robot_congratulate", "robot", ["Helmet_2"], [17], 0).
//action(4, "planned", "robot_tell_human_to_tidy", "robot", ["cube_GBTB","Helmet_2","throw_box_green"], [], 1).
//abstractTask(5, "planned", "wait_for_human", 1).
//abstractTask(6, "planned", "tidy", 0).
//action(7, "planned", "human_pick_cube", "Helmet_2", ["cube_GBTB"], [4], 6).
//action(8, "planned", "human_drop_cube", "Helmet_2", ["throw_box_green"], [9], 6).
//action(9, "planned", "robot_wait_for_human_to_tidy", "robot", [], [7], 5).
//abstractTask(11, "planned", "tidy_one", 2).
//action(13, "planned", "robot_tell_human_to_tidy", "robot", ["cube_GGCB","Helmet_2","throw_box_green"], [8], 11).
//abstractTask(14, "planned", "wait_for_human", 11).
//abstractTask(15, "planned", "tidy", 0).
//action(16, "planned", "human_pick_cube", "Helmet_2", ["cube_GGCB"], [13], 15).
//action(17, "planned", "human_drop_cube", "Helmet_2", ["throw_box_green"], [18], 15).
//action(18, "planned", "robot_wait_for_human_to_tidy", "robot", [], [16], 14).
//action(20, "planned", "IDLE", "Helmet_2", [], [3], (-1)).

//action(1, "planned", "robot_tell_human_to_tidy", "robot", ["cube_BBCG","Helmet_2","throw_box_green"], [], []).
//action(2, "planned", "human_pick_cube", "Helmet_2", ["cube_BBCG"], [1], []).
//action(3, "planned", "human_drop_cube", "Helmet_2", ["throw_box_green"], [2], []).
//action(4, "planned", "robot_tell_human_to_tidy", "robot", ["cube_GGCB","Helmet_2","throw_box_green"], [3], []).
//action(5, "planned", "human_pick_cube", "Helmet_2", ["cube_GGCB"], [4], []).
//action(6, "planned", "human_drop_cube", "Helmet_2", ["throw_box_green"], [5], []).
//action(7, "planned", "robot_tell_human_to_tidy", "robot", ["cube_GBTB","Helmet_2","throw_box_green"], [6], []).
//action(8, "planned", "human_pick_cube", "Helmet_2", ["cube_GBTB"], [7], []).
//action(9, "planned", "human_drop_cube", "Helmet_2", ["throw_box_green"], [8], []).

abstractTask(0, "planned", "robot_make_coffee",(-1)).
action(71, "planned", "robot_update_human_inventory", "robot", ["human","kitchen_cupboard"], [], 0).
action(72, "planned", "IDLE", "human", [], [71],(-1)).
action(74, "planned", "robot_ask_human_for_help", "robot", ["human"], [72], 0).

abstractTask(73, "planned", "human_help_make_coffee",(-1)).
abstractTask(14, "planned", "robot_help_make_coffee", 0).

action(84, "planned", "human_get_water", "human", [], [74], 73).
abstractTask(93, "planned", "robot_get_coffee", 14).
action(97, "planned", "robot_pick_coffee", "robot", ["pantry_cupboard"], [84], 93).
action(98, "planned", "human_pour_water_in_machine", "human", [], [97], 73).
action(99, "planned", "robot_put_coffee_in_machine", "robot", [], [98], 14).
action(100, "planned", "IDLE", "human", [], [99],(-1)).
action(101, "planned", "robot_serve_coffee", "robot", [], [100],(-1)).
action(102, "planned", "IDLE", "human", [], [101],(-1)).

abstractTask(75, "planned", "human_get_coffee", 73).
action(80, "planned", "human_try_pick_coffee", "human", ["pantry_cupboard"], [74], 75).
action(87, "planned", "robot_get_water", "robot", [], [80], 14).
action(88, "planned", "human_put_coffee_in_machine", "human", [], [87], 73).
action(89, "planned", "robot_pour_water_in_machine", "robot", [], [88], 14).
action(90, "planned", "IDLE", "human", [], [89],(-1)).
action(91, "planned", "robot_serve_coffee", "robot", [], [90],(-1)).
action(92, "planned", "IDLE", "human", [], [91],(-1)).


!start.

+!start : true <-
	rjs.jia.log_beliefs;
	.verbose(2);
	!getRobotName;
	!getHumanName.
	
+goal(Name, State) : State == received & not .substring("dtRR",Name) <-
	?humanName(Human);
	.concat("plan_manager/goals/",Name,"/name",GoalName);
	.concat("plan_manager/goals/",Name,"/worldstate",GoalWS);
	rjs.jia.get_param(GoalName, "String", N);
	rjs.jia.get_param(GoalWS, "Map", G);
	//[[name_t1,param1_t1,param2_t1],[name_t2, param1_t2]]
//	getPlan([[N,G]], [Human]);
	.findall(action(AID,AState,AName,AAgent,AParams,Preds,Decompo),action(AID,AState,AName,AAgent,AParams,Preds,Decompo),Actions);
	for(.member(A,Actions)){
		.send(human_management,tell,A);
	}
	-goal(Name, received)[source(supervisor)];
	+goal(Name,active);
	!updatePlanActions.
	
+goal(Name, State) : State == preempted | State == aborted <-	
	true.
	
+goal(Name,State) : State == succeeded <-
	true.
	
+!updatePlanTasksStart(Decompo) : abstractTask(Decompo,S,_,_) & S == "planned" <-	
	-abstractTask(Decompo,"planned",Name,Decompo2);
	++abstractTask(Decompo,"ongoing",Name,Decompo2);
	!updatePlanTasksStart(Decompo2).
	
+!updatePlanTasksStart(Decompo) : true.

+!updatePlanTasksEnd(Decompo,State) : abstractTask(_,_,_,_)
							  &	.count(action(P,S,_,_,_,_,Decompo) & isNotOver(S), C) 	
							  & .count(abstractTask(PT,S,_,Decompo) & isNotOver(S), CT)
							  & C+CT == 0
							  & Decompo \== -1<-	
	-abstractTask(Decompo,_,Name,Decompo2);
	++abstractTask(Decompo,State,Name,Decompo2);
	!updatePlanTasksEnd(Decompo2,State).
	
+!updatePlanTasksEnd(Decompo,State) : true.

@evOngoing[atomic]
+action(_,"ongoing",Name,Agent,Params)[source(_)] : action(ID,"todo",Name,Agent,Params,Preds,Decompo) <-
	!updateBelOngoing(Name,Agent,Params,Decompo).
	
+action(_,"ongoing",Name,Agent,Params)[source(_)] : action(ID,"executed",Name,Agent,Params,Preds,Decompo) <- true.

@evExe[atomic]	
+action(_,"executed",Name,Agent,Params)[source(_)] : wantedAction(Name,Agent,Params) <-
	!updateBelExecuted(Name,Agent,Params,Decompo);
	!removeParallelStreams(Agent,Preds);
	!updatePlanTasksEnd(Decompo,"executed");
	!updatePlanActions.
	
@evOngoingT[atomic]
+abstractTask(ID,"ongoing",Name,Decompo) : true <-
	.random(X);
	Y=math.round(X*1000000);
	.concat(Name,"_",Y, NameX);
	+abstractTask(ID,nameX,NameX).
	
+abstractTask(ID,"executed",Name,Decompo) : true <-
	?abstractTask(ID,nameX,NameX);
	jia.insert_task_mementar(ID,NameX);
	-abstractTask(ID,nameX,NameX).

+abstractTask(ID,"unplanned",Name,Decompo) : true <-
	?abstractTask(ID,nameX,NameX);
	-abstractTask(ID,nameX,NameX).

+action(ID,"executed",Name,Agent,Params) :  not wantedAction(Name,Params) <- true.
	
+action(ID,"todo",Name,Agent,Params,Preds,Decompo) : robotName(Agent) <-
	.send(robot_executor, tell, action(ID,Name,Agent,Params)).
	
//temporary
+action(ID,"todo",Name,Agent,Params,Preds,Decompo) : humanName(Agent) & Name == "IDLE" <-
	+action(ID,"executed",Name,Agent,Params).
	

	
+!reset : true <-
	.drop_all_desires;
	rjs.jia.abolish_all_except_ground.

