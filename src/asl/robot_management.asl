{include("plan_manager.asl")}
{include("receiver.asl")}
{ register_function("rjs.function.length_allow_unground") } 

//action(2503, "planned", "PickAndPlace", "AGENTX", ["?0 isA Cube. ?0 isReachableBy ?1 NOT EXISTS { ?0 isOnTopOf ?2. ?2 isA Cube }","?0 isA Spot NOT EXISTS { ?0 isUnder ?2. ?2 isA Cube }"], [], 2501).
//action(2508, "planned", "PickAndPlace", "AGENTX", ["?0 isA Cube. ?0 isReachableBy ?1 NOT EXISTS { ?0 isOnTopOf ?2. ?2 isA Cube }","?0 isA Spot NOT EXISTS { ?0 isUnder ?2. ?2 isA Cube }"], [], 2505).
//action(2521, "planned", "PickAndPlaceStick", "HERAKLES_HUMAN1", ["?0 isA cube_GGTB. ?0 isReachableBy ?1 NOT EXISTS { ?0 isOnTopOf ?2. ?2 isA Cube }","red_cube_1","red_cube_2"], [2508,2503], 2499).
//action(2525, "planned", "PickAndPlace", "AGENTX", ["?0 isA Cube. ?0 hasColor blue. ?0 isReachableBy ?1 NOT EXISTS { ?0 isOnTopOf ?2. ?2 isA Cube }","cube_GGTB1"], [2503,2521], 2522).
//action(2527, "planned", "PickAndPlace", "PR2_ROBOT", ["?0 isA Cube. ?0 hasColor green. ?0 isReachableBy ?1 NOT EXISTS { ?0 isOnTopOf ?2. ?2 isA Cube }","blue_cube_1"], [2525], 2523).
//action(2528, "planned", "PickAndPlace", "AGENTX", ["?0 isA Cube. ?0 hasColor blue. ?0 isReachableBy ?1 NOT EXISTS { ?0 isOnTopOf ?2. ?2 isA Cube }","green_cube"], [2525,2527], 2524).

//abstractTask(0, "planned", "tidy_cubes", (-1)).
//abstractTask(1, "planned", "tidy_one", 0).
//action(3, "planned", "robot_congratulate", "robot", ["human_0"], [8], 0).
//action(4, "planned", "robot_tell_human_to_tidy", "robot", ["cube_BBCG","human_0","throw_box_green"], [], 1).
//abstractTask(5, "planned", "wait_for_human", 1).
//abstractTask(6, "planned", "tidy", 0).
//action(7, "planned", "human_pick_cube", "human_0", ["cube_BBCG"], [4], 6).
//action(8, "planned", "human_drop_cube", "human_0", ["throw_box_green"], [9], 6).
//action(9, "planned", "robot_wait_for_human_to_tidy", "robot", [], [7], 5).
//action(11, "planned", "IDLE", "human_0", [], [3], 0).

//abstractTask(0, "planned", "tidy_cubes", (-1)).
//abstractTask(1, "planned", "tidy_one", 0).
//abstractTask(2, "planned", "tidy_cubes", 0).
//action(3, "planned", "robot_congratulate", "robot", ["human_0"], [17], 0).
//action(4, "planned", "robot_tell_human_to_tidy", "robot", ["human_0","cube_BGTG","table_2"], [], 1).
//abstractTask(5, "planned", "wait_for_human", 1).
//abstractTask(6, "planned", "tidy", 0).
//action(7, "planned", "human_pick_cube", "human_0", ["cube_BGTG"], [4], 6).
//action(8, "planned", "human_place_cube", "human_0", ["cube_BGTG","table_2"], [9], 6).
//action(9, "planned", "robot_wait_for_human_to_tidy", "robot", [], [7], 5).
//abstractTask(11, "planned", "tidy_one", 2).
//action(13, "planned", "robot_tell_human_to_tidy", "robot", ["human_0","cube_BBCG","table_2"], [8], 11).
//abstractTask(14, "planned", "wait_for_human", 11).
//abstractTask(15, "planned", "tidy", 0).
//action(16, "planned", "human_pick_cube", "human_0", ["cube_BBCG"], [13], 15).
//action(17, "planned", "human_place_cube", "human_0", ["cube_BBCG","table_2"], [18], 15).
//action(18, "planned", "robot_wait_for_human_to_tidy", "robot", [], [16], 14).
//action(20, "planned", "IDLE", "human_0", [], [3], (-1)).

//action(1, "planned", "robot_tell_human_to_tidy", "robot", ["cube_BBCG","human_0","throw_box_green"], [], []).
//action(2, "planned", "human_pick_cube", "human_0", ["cube_BBCG"], [1], []).
//action(3, "planned", "human_drop_cube", "human_0", ["throw_box_green"], [2], []).
//action(4, "planned", "robot_tell_human_to_tidy", "robot", ["cube_Ggreen_cube_1B","human_0","throw_box_green"], [3], []).
//action(5, "planned", "human_pick_cube", "human_0", ["cube_Ggreen_cube_1B"], [4], []).
//action(6, "planned", "human_drop_cube", "human_0", ["throw_box_green"], [5], []).
//action(7, "planned", "robot_tell_human_to_tidy", "robot", ["cube_GBTB","human_0","throw_box_green"], [6], []).
//action(8, "planned", "human_pick_cube", "human_0", ["cube_GBTB"], [7], []).
//action(9, "planned", "human_drop_cube", "human_0", ["throw_box_green"], [8], []).

//abstractTask(0, "planned", "robot_make_coffee",(-1)).
//action(71, "planned", "robot_update_human_inventory", "robot", ["human","kitchen_cupboard"], [], 0).
//action(72, "planned", "IDLE", "human", [], [71],(-1)).
//action(74, "planned", "robot_ask_human_for_help", "robot", ["human"], [72], 0).
////
//abstractTask(73, "planned", "human_help_make_coffee",(-1)).
//abstractTask(14, "planned", "robot_help_make_coffee", 0).
//
//action(84, "planned", "human_get_water", "human", [], [74], 73).
//abstractTask(93, "planned", "robot_get_coffee", 14).
//action(97, "planned", "pick_coffee", "robot", ["pantry_cupboard"], [84], 93).
//action(98, "planned", "human_pour_water_in_machine", "human", [], [97], 73).
//action(99, "planned", "robot_put_coffee_in_machine", "robot", [], [98], 14).
//action(100, "planned", "IDLE", "human", [], [99],(-1)).
//action(101, "planned", "robot_serve_coffee", "robot", [], [100],(-1)).
//action(102, "planned", "IDLE", "human", [], [101],(-1)).
//
//abstractTask(75, "planned", "human_get_coffee", 73).
//action(80, "planned", "human_try_pick_coffee", "human", ["pantry_cupboard"], [74], 75).
//action(87, "planned", "robot_get_water", "robot", [], [80], 14).
//action(88, "planned", "human_put_coffee_in_machine", "human", [], [87], 73).
//action(89, "planned", "robot_pour_water_in_machine", "robot", [], [88], 14).
//action(90, "planned", "IDLE", "human", [], [89],(-1)).
//action(91, "planned", "robot_serve_coffee", "robot", [], [90],(-1)).
//action(92, "planned", "IDLE", "human", [], [91],(-1)).

//action(50, "planned", "IDLE", "human_0", [], [49], (-1)).
//action(49, "planned", "robot_place_cube", "pr2_robot", ["blue_cube_1","green_cube"], [48], 9).
//abstractTask(9, "planned", "place_blue_cube_2", 0).
//abstractTask(0, "planned", "r_make_stack", (-1)).
//action(48, "planned", "IDLE", "human_0", [], [47], (-1)).
//action(47, "planned", "robot_pick_cube", "pr2_robot", ["blue_cube_1"], [44], 9).
//action(44, "planned", "IDLE", "human_0", [], [43], (-1)).
//action(43, "planned", "robot_place_cube", "pr2_robot", ["green_cube","blue_cube_2"], [41], 8).
//abstractTask(8, "planned", "robot_place_green_cube", 0).
//action(41, "planned", "human_place_cube", "human_0", ["blue_cube_2","cube_GGTB"], [40], 16).
//abstractTask(16, "planned", "h_handle_blue_cube_1", 1).
//abstractTask(1, "planned", "h_make_stack", (-1)).
//action(40, "planned", "robot_pick_cube", "pr2_robot", ["green_cube"], [35], 7).
//abstractTask(7, "planned", "place_blue_cube_1", 0).
//action(35, "planned", "human_pick_cube", "human_0", ["blue_cube_2"], [32], 16).
//action(32, "planned", "robot_wait", "pr2_robot", [], [31], 0).
//action(31, "planned", "human_place_stick", "human_0", ["cube_GGTB","red_cube_1","red_cube_2"], [30], 1).
//action(30, "planned", "robot_wait", "pr2_robot", [], [27], 5).
//abstractTask(5, "planned", "wait_cube_stick", 0).
//action(27, "planned", "human_pick_cube", "human_0", ["cube_GGTB"], [26], 1).
//action(26, "planned", "robot_place_cube", "pr2_robot", ["red_cube_1","cube_BBCG"], [21], 4).
//abstractTask(4, "planned", "place_red_cube", 0).
//action(21, "planned", "human_place_cube", "human_0", ["red_cube_2","cube_BBTG"], [19], 13).
//abstractTask(13, "planned", "h_place_red_cube", 1).
//action(19, "planned", "robot_wait", "pr2_robot", [], [18], 0).
//action(18, "planned", "human_pick_cube", "human_0", ["red_cube_2"], [11], 1).
//action(11, "planned", "robot_pick_cube", "pr2_robot", ["red_cube_1"], [], 0).
//action(68, "planned", "human_place_cube", "human_0", ["blue_cube_2","green_cube"], [67], 17).
//abstractTask(17, "planned", "h_handle_blue_cube_2", 1).
//action(67, "planned", "robot_wait", "pr2_robot", [], [64], 10).
//abstractTask(10, "planned", "assert_plan_over", 0).
//action(64, "planned", "human_pick_cube", "human_0", ["blue_cube_2"], [61], 17).
//action(61, "planned", "robot_place_cube", "pr2_robot", ["green_cube","blue_cube_1"], [59], 8).
//action(59, "planned", "WAIT", "human_0", [], [58], (-1)).
//action(58, "planned", "robot_pick_cube", "pr2_robot", ["green_cube"], [57], 7).
//action(57, "planned", "WAIT", "human_0", [], [56], (-1)).
//action(56, "planned", "robot_place_cube", "pr2_robot", ["blue_cube_1","cube_GGTB"], [55], 7).
//action(55, "planned", "WAIT", "human_0", [], [54], (-1)).
//action(54, "planned", "robot_pick_cube", "pr2_robot", ["blue_cube_1"], [37], 7).
//action(37, "planned", "human_wait_for_cube", "human_0", [], [32], 16).
//action(95, "planned", "IDLE", "human_0", [], [94], (-1)).
//action(94, "planned", "robot_place_cube", "pr2_robot", ["blue_cube_1","green_cube"], [93], 9).
//action(93, "planned", "IDLE", "human_0", [], [92], (-1)).
//action(92, "planned", "robot_pick_cube", "pr2_robot", ["blue_cube_1"], [89], 9).
//action(89, "planned", "IDLE", "human_0", [], [88], (-1)).
//action(88, "planned", "robot_place_cube", "pr2_robot", ["green_cube","blue_cube_2"], [86], 8).
//action(86, "planned", "human_place_cube", "human_0", ["blue_cube_2","cube_GGTB"], [85], 16).
//action(85, "planned", "robot_pick_cube", "pr2_robot", ["green_cube"], [80], 7).
//action(80, "planned", "human_pick_cube", "human_0", ["blue_cube_2"], [77], 16).
//action(77, "planned", "robot_wait", "pr2_robot", [], [76], 0).
//action(76, "planned", "human_place_stick", "human_0", ["cube_GGTB","red_cube_1","red_cube_2"], [75], 1).
//action(75, "planned", "robot_wait", "pr2_robot", [], [72], 5).
//action(72, "planned", "human_pick_cube", "human_0", ["cube_GGTB"], [71], 1).
//action(71, "planned", "robot_place_cube", "pr2_robot", ["red_cube_1","cube_BBTG"], [23], 4).
//action(23, "planned", "human_place_cube", "human_0", ["red_cube_2","cube_BBCG"], [19], 13).
//action(113, "planned", "human_place_cube", "human_0", ["blue_cube_2","green_cube"], [112], 17).
//action(112, "planned", "robot_wait", "pr2_robot", [], [109], 10).
//action(109, "planned", "human_pick_cube", "human_0", ["blue_cube_2"], [106], 17).
//action(106, "planned", "robot_place_cube", "pr2_robot", ["green_cube","blue_cube_1"], [104], 8).
//action(104, "planned", "WAIT", "human_0", [], [103], (-1)).
//action(103, "planned", "robot_pick_cube", "pr2_robot", ["green_cube"], [102], 7).
//action(102, "planned", "WAIT", "human_0", [], [101], (-1)).
//action(101, "planned", "robot_place_cube", "pr2_robot", ["blue_cube_1","cube_GGTB"], [100], 7).
//action(100, "planned", "WAIT", "human_0", [], [99], (-1)).
//action(99, "planned", "robot_pick_cube", "pr2_robot", ["blue_cube_1"], [82], 7).
//action(82, "planned", "human_wait_for_cube", "human_0", [], [77], 16).

isBusy(Human) :- action(ID,"ongoing",Name,Human,Params,Preds,Decompo) | (robotName(Robot) & not jia.is_relation_in_onto(Human,isLookingAt,Robot,false,robot)).

!start.

+!start : true <-
	rjs.jia.log_beliefs;
	.verbose(2);
	!initRosComponents;
	jia.createGUI;
	!getAgentNames.
	
+!add_plan_a_la_mano : true <-
	+abstractTask(0, "planned", "r_make_stack", (-1));
	+abstractTask(1, "planned", "h_make_stack", (-1));
	+abstractTask(4, "planned", "place_red_cube", 0);
	+abstractTask(5, "planned", "wait_stick", 0);
	+abstractTask(7, "planned", "place_blue_cube_1", 0);
	+abstractTask(8, "planned", "robot_place_green_cube", 0);
	+abstractTask(9, "planned", "place_blue_cube_2", 0);
	+abstractTask(10, "planned", "assert_plan_over", 0);
	+action(11, "planned", "robot_pick_cube", "pr2_robot", ["red_cube_1"], [], 0);
	+abstractTask(13, "planned", "h_place_red_cube", 1);
	+abstractTask(16, "planned", "h_handle_blue_cube_1", 1);
	+abstractTask(17, "planned", "h_handle_blue_cube_2", 1);
	+action(18, "planned", "human_pick_cube", "human_0", ["red_cube_2"], [11], 1);
	+action(19, "planned", "robot_wait", "pr2_robot", [], [18], 0);
	+action(21, "planned", "human_place_cube", "human_0", ["red_cube_2","cube_BBTG"], [19], 13);
	+action(23, "planned", "human_place_cube", "human_0", ["red_cube_2","cube_BBCG"], [19], 13);
	+action(26, "planned", "robot_place_cube", "pr2_robot", ["red_cube_1","cube_BBCG"], [21], 4);
	+action(27, "planned", "human_pick_cube", "human_0", ["cube_GGTB"], [26], 1);
	+action(30, "planned", "robot_wait", "pr2_robot", [], [27], 5);
	+action(31, "planned", "human_place_stick", "human_0", ["cube_GGTB","red_cube_1","red_cube_2"], [30], 1);
	+action(32, "planned", "robot_wait", "pr2_robot", [], [31], 0);
	+action(35, "planned", "human_pick_cube", "human_0", ["blue_cube_2"], [32], 16);
	+action(37, "planned", "human_wait_for_cube", "human_0", [], [32], 16);
	+action(40, "planned", "robot_pick_cube", "pr2_robot", ["green_cube"], [35], 7);
	+action(41, "planned", "human_place_cube", "human_0", ["blue_cube_2","cube_GGTB"], [40], 16);
	+action(43, "planned", "robot_place_cube", "pr2_robot", ["green_cube","blue_cube_2"], [41], 8);
	+action(44, "planned", "IDLE", "human_0", [], [43], (-1));
	+action(47, "planned", "robot_pick_cube", "pr2_robot", ["blue_cube_1"], [44], 9);
	+action(48, "planned", "IDLE", "human_0", [], [47], (-1));
	+action(49, "planned", "robot_place_cube", "pr2_robot", ["blue_cube_1","green_cube"], [48], 9);
	+action(50, "planned", "IDLE", "human_0", [], [49], (-1));
	+action(54, "planned", "robot_pick_cube", "pr2_robot", ["blue_cube_1"], [37], 7);
	+action(55, "planned", "WAIT", "human_0", [], [54], (-1));
	+action(56, "planned", "robot_place_cube", "pr2_robot", ["blue_cube_1","cube_GGTB"], [55], 7);
	+action(57, "planned", "WAIT", "human_0", [], [56], (-1));
	+action(58, "planned", "robot_pick_cube", "pr2_robot", ["green_cube"], [57], 7);
	+action(59, "planned", "WAIT", "human_0", [], [58], (-1));
	+action(61, "planned", "robot_place_cube", "pr2_robot", ["green_cube","blue_cube_1"], [59], 8);
	+action(64, "planned", "human_pick_cube", "human_0", ["blue_cube_2"], [61], 17);
	+action(67, "planned", "robot_wait", "pr2_robot", [], [64], 10);
	+action(68, "planned", "human_place_cube", "human_0", ["blue_cube_2","green_cube"], [67], 17);
	+action(71, "planned", "robot_place_cube", "pr2_robot", ["red_cube_1","cube_BBTG"], [23], 4);
	+action(72, "planned", "human_pick_cube", "human_0", ["cube_GGTB"], [71], 1);
	+action(75, "planned", "robot_wait", "pr2_robot", [], [72], 5);
	+action(76, "planned", "human_place_stick", "human_0", ["cube_GGTB","red_cube_1","red_cube_2"], [75], 1);
	+action(77, "planned", "robot_wait", "pr2_robot", [], [76], 0);
	+action(80, "planned", "human_pick_cube", "human_0", ["blue_cube_2"], [77], 16);
	+action(82, "planned", "human_wait_for_cube", "human_0", [], [77], 16);
	+action(85, "planned", "robot_pick_cube", "pr2_robot", ["green_cube"], [80], 7);
	+action(86, "planned", "human_place_cube", "human_0", ["blue_cube_2","cube_GGTB"], [85], 16);
	+action(88, "planned", "robot_place_cube", "pr2_robot", ["green_cube","blue_cube_2"], [86], 8);
	+action(89, "planned", "IDLE", "human_0", [], [88], (-1));	
	+action(92, "planned", "robot_pick_cube", "pr2_robot", ["blue_cube_1"], [89], 9);
	+action(93, "planned", "IDLE", "human_0", [], [92], (-1));
	+action(94, "planned", "robot_place_cube", "pr2_robot", ["blue_cube_1","green_cube"], [93], 9);
	+action(95, "planned", "IDLE", "human_0", [], [94], (-1));
	+action(99, "planned", "robot_pick_cube", "pr2_robot", ["blue_cube_1"], [82], 7);
	+action(100, "planned", "WAIT", "human_0", [], [99], (-1));
	+action(101, "planned", "robot_place_cube", "pr2_robot", ["blue_cube_1","cube_GGTB"], [100], 7);
	+action(102, "planned", "WAIT", "human_0", [], [101], (-1));
	+action(103, "planned", "robot_pick_cube", "pr2_robot", ["green_cube"], [102], 7);
	+action(104, "planned", "WAIT", "human_0", [], [103], (-1));
	+action(106, "planned", "robot_place_cube", "pr2_robot", ["green_cube","blue_cube_1"], [104], 8);
	+action(109, "planned", "human_pick_cube", "human_0", ["blue_cube_2"], [106], 17);
	+action(112, "planned", "robot_wait", "pr2_robot", [], [109], 10);
	+action(113, "planned", "human_place_cube", "human_0", ["blue_cube_2","green_cube"], [112], 17).
	
	
+goal(Name, State) : State == received & not .substring("dtRR",Name) <-
	?humanName(Human);
	.concat("plan_manager/goals/",Name,"/name",GoalName);
	.concat("plan_manager/goals/",Name,"/worldstate",GoalWS);
	rjs.jia.get_param(GoalName, "String", N);
	rjs.jia.get_param(GoalWS, "Map", G);
	//[[name_t1,param1_t1,param2_t1],[name_t2, param1_t2]]
//	getMAHTNPlan([[N,G]], [Human]);
//	getHATPPlan(N);
	!add_plan_a_la_mano;
	.findall(action(AID,AState,AName,AAgent,AParams,Preds,Decompo),action(AID,AState,AName,AAgent,AParams,Preds,Decompo),Actions);
	for(.member(A,Actions)){
		.send(human_management,tell,A);
	}
	-goal(Name, received)[source(supervisor)];
	+goal(Name,active);
	.send(human_management,tell,goal(Name,active));
	setHMBuff([environment_monitoring,human_monitoring],[normal,normal]);
	setHMAtemp("",environment_monitoring,void);
	!updatePlanActions.
	
+goal(Name, State) : State == preempted | State == aborted <-	
	true.
	
+goal(Name,State) : State == succeeded <-
	true.
	
//TODO to implement	
+replan(Name)[source(_)] : true.
//TODO is it enough ?
+drop(Name)[source(_)] : true <-
	!reset.
	
+!updatePlanTasksStart(Decompo) : abstractTask(Decompo,S,_,_) & S == "planned" <-	
	-abstractTask(Decompo,"planned",Name,Decompo2);
	++abstractTask(Decompo,"ongoing",Name,Decompo2);
	!updatePlanTasksStart(Decompo2).
	
+!updatePlanTasksStart(Decompo) : true.

@upte[atomic]
+!updatePlanTasksEnd(Decompo,State) : abstractTask(_,_,_,_)
							  &	.count(action(P,S,_,_,_,_,Decompo) & isNotOver(S), C) 	
							  & .count(abstractTask(PT,S,_,Decompo) & isNotOver(S), CT)
							  & C+CT == 0
							  & Decompo \== -1<-	
	-abstractTask(Decompo,_,Name,Decompo2);
	++abstractTask(Decompo,State,Name,Decompo2);
	!updatePlanTasksEnd(Decompo2,State).
	
+!updatePlanTasksEnd(Decompo,State) : true.

+action(ID,"todo",Name,Agent,Params,Preds,Decompo) : humanName(Agent) & jia.is_of_class(class,Name,"PhysicalAction") <-
	!chooseParamToMonitor(Name,Params,P);
	setHMAtemp(P,environment_monitoring,urgent);
	setHMBuff([environment_monitoring,atomic]);
	!waitParamSeenOrActionStateChange(ID,Name,Agent,Params,Preds,Decompo,P).
	
+!waitParamSeenOrActionStateChange(ID,Name,Agent,Params,Preds,Decompo,P) : true <-
	!waitParamSeen(Name,P) ||| !waitActionStateChange(ID,Name,Agent,Params,Preds,Decompo,P).
	
+!waitParamSeen(Name,P) : true <-
	.wait(isPerceiving(P),100000).

-!waitParamSeen(Name,P) : true <-
	!lookForObject(Name,P).

+!lookForObject(Name,P) : true <-
	!scanTable(P) ||| !waitToPerceive(P).
	
+!scanTable(P) : true <-
	.send(robot_executor,askOne,scanTable,A).
	
+!waitToPerceive(P) : true <-
	.wait(isPerceiving(P));
	.send(robot_executor,tell,cancel(scanTable)).
	
+!waitActionStateChange(ID,Name,Agent,Params,Preds,Decompo,P) : true <-
	.wait(action(ID,"ongoing",Name,Agent,Params,Preds,Decompo) | action(ID,"executed",Name,Agent,Params,Preds,Decompo));
	if(.intend(scanTable(P))){
		.send(robot_executor,tell,cancel(scanTable));
	}.
	
//TODO refine with another way to choose the param to monitor if there are several 	
//TODO case where it is a sparql query - closer to the human
+!chooseParamToMonitor(Name,Params,P) : .length(Params) > 0 <-
	if(.substring(pick,Name)){
		.nth(0,Params,P);
	}elif(.substring(place,Name)){
		.nth(1,Params,P);
	}.
	
+!chooseParamToMonitor(Name,Params,P) : true.

@evOngoing[atomic]
+action(ID,"ongoing",Name,Agent,Params)[source(_)] : action(ID,"todo",Name,_,_,Preds,Decompo) <-
	-action(ID,"ongoing",Name,_,_)[source(_)];
	-action(ID,"todo",Name,_,_,Preds,Decompo);
	+action(ID,"ongoing",Name,Agent,Params,Preds,Decompo).
	
+action(_,"ongoing",Name,Agent,Params)[source(_)] : action(ID,"executed",Name,Agent,Params,Preds,Decompo) <- true.

@evTodo[atomic]
+action(ID,"todo",Name,Agent,Params)[source(_)] : action(ID,"ongoing",Name,Agent,Params,Preds,Decompo) <-
	-action(ID,"todo",Name,Agent,Params)[source(_)];
	-action(ID,"ongoing",Name,Agent,Params,Preds,Decompo);
	+action(ID,"todo",Name,Agent,Params,Preds,Decompo).
	
@evSusp[atomic]
+action(ID,"suspended",Name,Agent,Params)[source(_)] : action(ID,"ongoing",Name,Agent,Params,Preds,Decompo) <-
	-action(ID,"suspended",Name,Agent,Params)[source(_)];
	-action(ID,"ongoing",Name,Agent,Params,Preds,Decompo);
	+action(ID,"suspended",Name,Agent,Params,Preds,Decompo).
	
//@evExe[atomic]
+action(ID,"executed",Name,Agent,Params)[source(S)] : wantedAction(ID) & humanName(Human) & robotName(Robot)<-
	!chooseParamToMonitor(Name,Params,P);
//	setHMAtemp(P,environment_monitoring,void);
//	setHMBuff([environment_monitoring,normal]);
	!updateExecutedActionBels(ID,Name,Agent,Params);
	?action(ID,_,Name,_,_,Preds,Decompo);
	!removeParallelStreams(Agent,Preds);
	!updatePlanTasksEnd(Decompo,"executed");
	!updatePlanActions.
	
+action(ID,"executed",Name,Agent,Params)[source(S)] : not wantedAction(ID) & humanName(Human) <-
	!updateExecutedActionBels(ID,Name,Agent,Params).
	
//TODO check agent class and not names
+!updateExecutedActionBels(ID,Name,Agent,Params) : humanName(Human) & robotName(Robot) <-
	-action(ID,"executed",Name,Agent,Params)[source(S)];
	-action(ID,_,Name,_,_,Preds,Decompo)[source(_)];
	if(.substring(human,S)){
		+action(ID,"executed",Name,Human,Params,Preds,Decompo);
	}elif(.substring(robot,S)){
		+action(ID,"executed",Name,Robot,Params,Preds,Decompo);
	}.
	
@evOngoingT[atomic]
+abstractTask(ID,"ongoing",Name,Decompo) : true <-
	.random(X);
	Y=math.round(X*1000000);
	.concat(Name,"_",Y, NameX);
	+abstractTask(ID,nameX,NameX).
	
+abstractTask(ID,"executed",Name,Decompo) : true <-
	?abstractTask(ID,nameX,NameX);
	jia.insert_abstract_task_mementar(ID,NameX);
	-abstractTask(ID,nameX,NameX).

+abstractTask(ID,"unplanned",Name,Decompo) : true <-
	?abstractTask(ID,nameX,NameX);
	jia.remove_task_mementar(NameX);
	-abstractTask(ID,nameX,NameX).

//TODO write isExecutedActionInPlan rule
+action(ID,"executed",Name,Agent,Params) :  isExecutedActionInPlan(Name,Agent,Params) <- true.

+action(ID,"todo",Name,Agent,Params,Preds,Decompo) : robotName(Agent) & .count(.member(P,Params) & .substring("?",P), C) & C > 0<-
	!allocateParams(ID,"todo",Name,Agent,Params,Preds,Decompo);
	-newParams(NewParams);
	!allocateActionToRobot(ID,"todo",Name,Agent,NewParams,Preds,Decompo).
	
+action(ID,"todo",Name,Agent,Params,Preds,Decompo) : robotName(Agent) <-
	.send(robot_executor, tell, action(ID,Name,Agent,Params)).
	
+action(ID,"todo",Name,Agent,Params,Preds,Decompo) : agentXName(Agent) <-
	!allocateParamsAgentXAction(ID,"todo",Name,Agent,Params,Preds,Decompo).
	
+!allocateParams(ID,"todo",Name,Agent,Params,Preds,Decompo) : true <-
	for(.member(P,Params)){
		//test if P is a sparql request
		if(.substring("?",P)){
			jia.get_indiv_from_sparql(P,objectsAndAgents,SparqlRes);
			//multiple possible entities which does not depend on an agent  OR one possible entity
			if(not .substring("?1",P)){
				NewParam=SparqlRes;
//				.nth(0,SparqlRes,FirstRes);
//				.nth(0,FirstRes,NewParam);
			}// multiple or one possible entities which depend on an agent
			else{
				.findall(Ag,.member(Res,SparqlRes) & .nth(1,Res,Ag),Agents);
				.union(Agents,Agents,InvolvedAg);
				//only one agent is possible for this parameter
				if(.length(InvolvedAg)==1){
					.nth(0,InvolvedAg,Ag);
					+shouldBeAgentX(Ag);
//					.nth(0,SparqlRes,FirstRes);
//					.nth(0,FirstRes,NewParam);
					NewParam=SparqlRes;
				}else{
					NewParam=SparqlRes;
				}
			}
		}else{
			NewParam=P;
		}
		if(rjs.jia.believes(newParams(List))){
			-newParams(List);
		}else{
			List=[];
		}
		.concat(List,[NewParam],NewParams);
		+newParams(NewParams);
	}.
	
+!allocateParamsAgentXAction(ID,"todo",Name,Agent,Params,Preds,Decompo) 
: not .intend(allocateParamsAgentXAction(_,"todo",Name,Agent,Params,_,_)) & humanName(Human) & robotName(Robot)
<- !allocateParams(ID,"todo",Name,Agent,Params,Preds,Decompo);
	-newParams(NewParams);
	if(.length(NewParams)>0){
		!allocateAgentXAction(ID,"todo",Name,Agent,NewParams,Preds,Decompo);
	}else{
		?goal(GoalName,active);
		+replan(GoalName);
	}.

+!allocateParamsAgentXAction(ID,"todo",Name,Agent,Params,Preds,Decompo) : .intend(allocateParamsAgentXAction(_,"todo",Name,Agent,Params,_,_)) <-
	true.
	
+!allocateAgentXAction(ID,"todo",Name,Agent,NewParams,Preds,Decompo) 
: action(ID,"todo",Name,Agent,Params,Preds,Decompo) & action(ID2,"todo",Name,Agent,Params,Preds2,Decompo2) & ID2 \== ID & humanName(Human)
<- 	!allocateActionToRobot(ID,"todo",Name,Agent,NewParams,Preds,Decompo);
	-action(ID2,"todo",Name,Agent,Params,Preds2,Decompo2);
	+action(ID2,"todo",Name,Human,Params,Preds2,Decompo2).
	
+!allocateAgentXAction(ID,"todo",Name,Agent,NewParams,Preds,Decompo) : shouldBeAgentX(Ag) <-
	-shouldBeAgentX(Ag);
	-action(ID,"todo",Name,Agent,_,Preds,Decompo);
	+action(ID,"todo",Name,Ag,NewParams,Preds,Decompo).
	
+!allocateAgentXAction(ID,"todo",Name,Agent,NewParams,Preds,Decompo) : humanName(Human) & isBusy(Human) <-
	!allocateActionToRobot(ID,"todo",Name,Agent,NewParams,Preds,Decompo).
	
+!allocateAgentXAction(ID,"todo",Name,Agent,NewParams,Preds,Decompo) : humanName(Human) <-
	.wait(action(ID,State,Name,Human,_,Preds,Decompo) & (State=="ongoing" | State=="executed"), 25000).

-!allocateAgentXAction(ID,"todo",Name,Agent,NewParams,Preds,Decompo)[Failure, code(Code),code_line(_),code_src(_),error(Error),error_msg(_)] 
: .substring(wait,Error) & robotName(Robot)<-
	!allocateActionToRobot(ID,"todo",Name,Agent,NewParams,Preds,Decompo).
	
+!allocateActionToRobot(ID,"todo",Name,Agent,NewParams,Preds,Decompo) : robotName(Robot) & humanName(Human) <-
	-shouldBeAgentX(Ag);
	for(.member(NP,NewParams)){
		if(.list(NP) & .length(NP)>1){
			.findall(P,.member(P,NP) & .member(A,P) & A==Robot,ToKeep);
			.nth(0,ToKeep,KeepFirst);
			.nth(0,KeepFirst,NewParam);
		}elif(.list(NP)){
			.nth(0,NP,FirstRes);
			.nth(0,FirstRes,NewParam);
		}else{
			NewParam=NP;
		}
		if(rjs.jia.believes(newParams(List))){
			-newParams(List);
		}else{
			List=[];
		}
		.concat(List,[NewParam],NewParams2);
		+newParams(NewParams2);
	}
	-newParams(NewParams2);
	-action(ID,"todo",Name,Agent,_,Preds,Decompo);
	+action(ID,"todo",Name,Robot,NewParams2,Preds,Decompo).
