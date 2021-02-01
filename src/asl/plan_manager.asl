{ include("common.asl")}

//action(0,"planned","PickAction", "human_0", ["cube_412", "human_0"], []).
//
//action(1,"planned","PlaceAction", "human_0", ["box_2","cube_412", "human_0"], [0]).
//
//action(2,"planned","PickAction", "human_0", ["cube_415", "human_0"], [1]).
//
//action(3,"planned","PlaceAction", "human_0", ["box_1", "cube_415", "human_0"], [1]).

//action(2,"planned","Robot_wait_for_human_to_tidy","robot",["cube_BGCB","human_0","throw_box_green"],[]).
//action(12,"planned","robot_tell_human_to_tidy","robot",["cube_BGCB","human_0","box_A1"],[]).
action(3,"planned","human_pick_cube", "human_0", ["cube_BBCG"], [1]).

actionStates(["planned","todo","ongoing","executed"]).

!start.

+!start : true <-
	rjs.jia.log_beliefs.
//	.verbose(2);
//	!getRobotName;
//	!getHumanName;
//	+goal(0,active,"pile").
	
+goal(ID, State, Task) : State == active <-
	?humanName(Human);
	rjs.jia.get_param("plan_manager/goals/dt1/name", "String", N);
	rjs.jia.get_param("plan_manager/goals/dt1/worldstate", "Map", G);
	//[[name_t1,param1_t1,param2_t1],[name_t2, param1_t2]]
//	getPlan([[N,G]], [Human]);
	!setMementarSub;
	!updatePlan.
	
+goal(ID, State, Task) : State == preempted | State == aborted <-	
	true.
	
+goal(ID,State, Task) : State == succeeded <-
	for(monitoring(MonID,Act,_,_)){
		mementarUnsubscribe(MonID,Act);
	}.
	
+!setMementarSub : true <-
	rjs.jia.get_param("/plan_manager/actsToMonitor", "List", ActsToMonitor);
	for(.member(Y,ActsToMonitor)){
//		.count(action(_,"planned",Y,_,_,_),C);
		mementarSubscribe(Y,start,-1);
		mementarSubscribe(Y,end,-1);
	}.	

+!updatePlan : true <-
	for(action(ID,"planned",Name,Agent,Params,Preds)){
		.findall(P, 
			(action(P,S,_,_,_,_) 
			& .member(P,Preds) 
			& actionStates(AS) 
			& .difference(AS,["executed"],NotEx) 
			& .member(S,NotEx)
		), PredsL);
		if(.empty(PredsL)){
			-action(ID,"planned",Name,Agent,Params,Preds);
			+action(ID,"todo",Name,Agent,Params,Preds);
		}
	}.
	
wantedAction(Name,Agent,Params) :- action(ID,S,Name,Agent,Params,_) & (S=="todo" | S=="ongoing").

@evOngoing[atomic]
+action(_,"ongoing",Name,Agent,Params)[source(_)] : action(ID,"todo",Name,Agent,Params,Preds) <-
	-action(_,"ongoing",Name,Agent,Params)[source(_)];
	-action(ID,"todo",Name,Agent,Params,Preds);
	+action(ID,"ongoing",Name,Agent,Params,Preds).
	
+action(_,"ongoing",Name,Agent,Params)[source(_)] : action(ID,"executed",Name,Agent,Params,Preds) <- true.

@evExe[atomic]	
+action(_,"executed",Name,Agent,Params)[source(_)] : wantedAction(Name,Agent,Params) <-
	-action(_,"executed",Name,Agent,Params)[source(_)];
	-action(ID,_,Name,Agent,Params,Preds);
	+action(ID,"executed",Name,Agent,Params,Preds);
	!updatePlan;
	!testRemainingActions.

+!testRemainingActions : .count(action(_,S,_,_,_,_) & S \== "executed", C) & C = 0
	<- ?goal(GoalID,active,Task);
		-goal(GoalID,active,Task);
		.send(robot_executor, tell, planOver);
		+goal(GoalID,succeeded,Task).
		
+!testRemainingActions : true <- true.

+action(ID,"executed",Name,Agent,Params) :  not wantedAction(Name,Params) <- true.
	
	
+action(ID,"todo",Name,Agent,Params,Preds) : robotName(Agent) <-
	.send(robot_executor, tell, action(ID,Name,Agent,Params)).



