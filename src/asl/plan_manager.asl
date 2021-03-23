{include("common.asl")}
{include("receiver.asl")}

actionStates(["planned","todo","ongoing","executed"])[ground].

//abstractTask(0, "planned", "tidy_cubes", 0).
//abstractTask(1, "planned", "tidy_one", 0).
//action(3, "planned", "robot_congratulate", "robot", ["human_0"], [8], 0).
//action(4, "planned", "robot_tell_human_to_tidy", "robot", ["cube_GGTB","human_0","throw_box_green"], [], 1).
//abstractTask(5, "planned", "wait_for_human", 1).
//abstractTask(6, "planned", "tidy", 0).
//action(7, "planned", "human_pick_cube", "human_0", ["cube_GGTB"], [4], 6).
//action(8, "planned", "human_drop_cube", "human_0", ["throw_box_green"], [9], 6).
//action(9, "planned", "robot_wait_for_human_to_tidy", "robot", [], [7], 5).
//action(11, "planned", "IDLE", "human_0", [], [3], 0).

//abstractTask(0, "planned", "tidy_cubes", (-1)).
//abstractTask(1, "planned", "tidy_one", 0).
//abstractTask(2, "planned", "tidy_cubes", 0).
//action(3, "planned", "robot_congratulate", "robot", ["human_0"], [17], 0).
//action(4, "planned", "robot_tell_human_to_tidy", "robot", ["cube_GBTB","human_0","throw_box_green"], [], 1).
//abstractTask(5, "planned", "wait_for_human", 1).
//abstractTask(6, "planned", "tidy", 0).
//action(7, "planned", "human_pick_cube", "human_0", ["cube_GBTB"], [4], 6).
//action(8, "planned", "human_drop_cube", "human_0", ["throw_box_green"], [9], 6).
//action(9, "planned", "robot_wait_for_human_to_tidy", "robot", [], [7], 5).
//abstractTask(11, "planned", "tidy_one", 2).
//action(13, "planned", "robot_tell_human_to_tidy", "robot", ["cube_GGCB","human_0","throw_box_green"], [8], 11).
//abstractTask(14, "planned", "wait_for_human", 11).
//abstractTask(15, "planned", "tidy", 0).
//action(16, "planned", "human_pick_cube", "human_0", ["cube_GGCB"], [13], 15).
//action(17, "planned", "human_drop_cube", "human_0", ["throw_box_green"], [18], 15).
//action(18, "planned", "robot_wait_for_human_to_tidy", "robot", [], [16], 14).
//action(20, "planned", "IDLE", "human_0", [], [3], (-1)).


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
	getPlan([[N,G]], [Human]);
	-goal(Name, received)[source(supervisor)];
	+goal(Name,active);
	!setMementarSub;
	!updatePlanActions.
	
+goal(Name, State) : State == preempted | State == aborted <-	
	true.
	
+goal(Name,State) : State == succeeded <-
	for(monitoring(MonID,Act,_,_)){
		mementarUnsubscribe(MonID,Act);
	}.
	
+!setMementarSub : true <-
	rjs.jia.get_param("/plan_manager/actsToMonitor", "List", ActsToMonitor);
	for(.member(Y,ActsToMonitor)){
//		.count(action(_,"planned",Y,_,_,_,_),C);
		mementarSubscribe(Y,start,-1);
		mementarSubscribe(Y,end,-1);
	}.	
	
+!updateActionState : true <-
	for(action(ID,"planned",Name,Agent,Params,Preds,Decompo)){
		// find all action ID "P" which belongs to the current action Preds and that has not been executed yet
		.findall(P, 
			(action(P,S,_,_,_,_,_) 
			& .member(P,Preds) 
			& actionStates(AS) 
			& .difference(AS,["executed"],NotEx) 
			& .member(S,NotEx)
		), PredsL);
		// if PredsL is empty, it means all the action predecessors has been correctly executed and the action can become "todo"
		if(.empty(PredsL)){
			-action(ID,"planned",Name,Agent,Params,Preds,Decompo);
			++action(ID,"todo",Name,Agent,Params,Preds,Decompo);
			!updatePlanTasksStart(Decompo);
		}
	}.
	
+!updatePlanTasksStart(Decompo) : abstractTask(Decompo,S,_,_) & S == "planned" <-	
	-abstractTask(Decompo,"planned",Name,Decompo2);
	++abstractTask(Decompo,"ongoing",Name,Decompo2);
	!updatePlanTasksStart(Decompo2).
	
+!updatePlanTasksStart(Decompo) : true.

+!updatePlanTasksEnd(Decompo) : .count(action(P,S,_,_,_,_,Decompo) & S \== "executed", C) 	
							  & .count(abstractTask(PT,S,_,Decompo) & S \== "executed", CT)
							  & C+CT == 0
							  & Decompo \== -1<-	
	-abstractTask(Decompo,_,Name,Decompo2);
	++abstractTask(Decompo,"executed",Name,Decompo2);
	!updatePlanTasksEnd(Decompo2).
	
+!updatePlanTasksEnd(Decompo) : true.

@evOngoingT[atomic]
+abstractTask(ID,"ongoing",Name,Decompo) : true <-
	.random(X);
	Y=math.round(X*1000000);
	.concat(Name,"_",Y, NameX);
	+abstractTask(ID,nameX,NameX);
	jia.insertTaskMementar(ID,NameX,start).
	
+abstractTask(ID,"executed",Name,Decompo) : true <-
	?abstractTask(ID,nameX,NameX);
	-abstractTask(ID,nameX,NameX);
	jia.insertTaskMementar(ID,NameX,end).
	
wantedAction(Name,Agent,Params) :- action(ID,S,Name,Agent,Params,_,_) & (S=="todo" | S=="ongoing").

@evOngoing[atomic]
+action(_,"ongoing",Name,Agent,Params)[source(_)] : action(ID,"todo",Name,Agent,Params,Preds,Decompo) <-
	-action(_,"ongoing",Name,Agent,Params)[source(_)];
	-action(ID,"todo",Name,Agent,Params,Preds,Decompo);
	+action(ID,"ongoing",Name,Agent,Params,Preds,Decompo).
	
+action(_,"ongoing",Name,Agent,Params)[source(_)] : action(ID,"executed",Name,Agent,Params,Preds,Decompo) <- true.

//TODO @evExe[atomic]	 /!\ est-ce que peut causer des soucis si non atomic ?? après plusieurs run ça a l'air d'aller
+action(_,"executed",Name,Agent,Params)[source(_)] : wantedAction(Name,Agent,Params) <-
	-action(_,"executed",Name,Agent,Params)[source(_)];
	-action(ID,_,Name,Agent,Params,Preds,Decompo);
	+action(ID,"executed",Name,Agent,Params,Preds,Decompo);
	!!updatePlanTasksEnd(Decompo);
	!!updatePlanActions.
	
+!updatePlanActions : true <-
	!updateActionState;
	!testRemainingActions.

+!testRemainingActions : .count(action(_,S,_,_,_,_,_) & S \== "executed", C) & C == 0
	<- ?goal(Name,active);
		-goal(Name,active);
		.send(robot_executor, tell, planOver);
		+goal(Name,succeeded).
		
+!testRemainingActions : true <- true.

+action(ID,"executed",Name,Agent,Params) :  not wantedAction(Name,Params) <- true.
	
+action(ID,"todo",Name,Agent,Params,Preds,Decompo) : robotName(Agent) <-
	.send(robot_executor, tell, action(ID,Name,Agent,Params)).

//temporary
+action(ID,"todo",Name,Agent,Params,Preds,Decompo) : humanName(Agent) & Name == "IDLE" <-
	+action(ID,"executed",Name,Agent,Params).
	
+!reset : true <-
	.drop_all_desires;
	rjs.jia.abolish_all_except_ground.

