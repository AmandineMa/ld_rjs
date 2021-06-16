{include("plan_manager.asl")}
{include("receiver.asl")}
{ register_function("rjs.function.length_allow_unground") } 

//action(2503, "planned", "PickAndPlace", "AGENTX", ["?0 isA Cube. ?0 isReachableBy ?1 NOT EXISTS { ?0 isOnTopOf ?2. ?2 isA Cube }","?0 isA Spot NOT EXISTS { ?0 isUnder ?2. ?2 isA Cube }"], [], 2501).
//action(2508, "planned", "PickAndPlace", "AGENTX", ["?0 isA Cube. ?0 isReachableBy ?1 NOT EXISTS { ?0 isOnTopOf ?2. ?2 isA Cube }","?0 isA Spot NOT EXISTS { ?0 isUnder ?2. ?2 isA Cube }"], [], 2505).
//action(2521, "planned", "PickAndPlaceStick", "HERAKLES_HUMAN1", ["?0 isA Stick. ?0 isReachableBy ?1 NOT EXISTS { ?0 isOnTopOf ?2. ?2 isA Cube }","RED_CUBE1","RED_CUBE2"], [2508,2503], 2499).
//action(2525, "planned", "PickAndPlace", "AGENTX", ["?0 isA Cube. ?0 hasColor blue. ?0 isReachableBy ?1 NOT EXISTS { ?0 isOnTopOf ?2. ?2 isA Cube }","STICK1"], [2503,2521], 2522).
//action(2527, "planned", "PickAndPlace", "PR2_ROBOT", ["?0 isA Cube. ?0 hasColor green. ?0 isReachableBy ?1 NOT EXISTS { ?0 isOnTopOf ?2. ?2 isA Cube }","BLUE_CUBE1"], [2525], 2523).
//action(2528, "planned", "PickAndPlace", "AGENTX", ["?0 isA Cube. ?0 hasColor blue. ?0 isReachableBy ?1 NOT EXISTS { ?0 isOnTopOf ?2. ?2 isA Cube }","GREEN_CUBE1"], [2525,2527], 2524).

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
//action(97, "planned", "robot_pick_coffee", "robot", ["pantry_cupboard"], [84], 93).
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

isBusy(Human) :- action(ID,"ongoing",Name,Human,Params,Preds,Decompo) | (robotName(Robot) & not jia.is_relation_in_onto(Human,isLookingAt,Robot,false,robot)).

!start.

+!start : true <-
	rjs.jia.log_beliefs;
	.verbose(2);
	!initRosComponents;
	jia.createGUI;
	!getAgentNames.
	
    
+goal(Name, State) : State == received & not .substring("dtRR",Name) <-
	?humanName(Human);
	.concat("plan_manager/goals/",Name,"/name",GoalName);
//	.concat("plan_manager/goals/",Name,"/worldstate",GoalWS);
	rjs.jia.get_param(GoalName, "String", N);
//	rjs.jia.get_param(GoalWS, "Map", G);
	//[[name_t1,param1_t1,param2_t1],[name_t2, param1_t2]]
//	getMATHNPlan([[N,G]], [Human]);
	getHATPPlan(N);
	.findall(action(AID,AState,AName,AAgent,AParams,Preds,Decompo),action(AID,AState,AName,AAgent,AParams,Preds,Decompo),Actions);
	for(.member(A,Actions)){
		.send(human_management,tell,A);
	}
	-goal(Name, received)[source(supervisor)];
	+goal(Name,active);
	.send(human_management,tell,goal(Name,active));
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
//	setHMBuff([environment_monitoring,prioritize]);
	!waitParamSeenOrActionStateChange(ID,Name,Agent,Params,Preds,Decompo,P).
	
+!waitParamSeenOrActionStateChange(ID,Name,Agent,Params,Preds,Decompo,P) : true <-
	!waitParamSeen(P) ||| !waitActionStateChange(ID,Name,Agent,Params,Preds,Decompo,P).
	
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
+!chooseParamToMonitor(Name,Params,P) : true <-
	.nth(0,Params,P).

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
	
@evExe[atomic]
+action(ID,"executed",Name,Agent,Params)[source(S)] : wantedAction(ID) & humanName(Human) & robotName(Robot)<-
	!chooseParamToMonitor(Name,Params,P);
	setHMAtemp(P,environment_monitoring,void);
	-action(ID,"executed",Name,Agent,Params)[source(S)];
	-action(ID,_,Name,_,_,Preds,Decompo)[source(_)];
	if(.substring(human,S)){
		+action(ID,"executed",Name,Human,Params,Preds,Decompo);
	}elif(.substring(robot,S)){
		+action(ID,"executed",Name,Robot,Params,Preds,Decompo);
	}
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
	jia.insert_abstract_task_mementar(ID,NameX);
	-abstractTask(ID,nameX,NameX).

+abstractTask(ID,"unplanned",Name,Decompo) : true <-
	?abstractTask(ID,nameX,NameX);
	-abstractTask(ID,nameX,NameX).

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
				.nth(0,SparqlRes,FirstRes);
				.nth(0,FirstRes,NewParam);
			}// multiple or one possible entities which depend on an agent
			else{
				.findall(Ag,.member(Res,SparqlRes) & .nth(1,Res,Ag),Agents);
				.union(Agents,Agents,InvolvedAg);
				//only one agent is possible for this parameter
				if(.length(InvolvedAg)==1){
					.nth(0,InvolvedAg,Ag);
					+shouldBeAgentX(Ag);
					.nth(0,SparqlRes,FirstRes);
					.nth(0,FirstRes,NewParam);
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
