{ include("common.asl")}

action(0,"planned","PickAction", ["human_0"], ["cube_412", "human_0"], 0, []).

action(1,"planned","PlaceAction", ["human_0"], ["box_2","cube_412", "human_0"], 0, [0]).

//action(2,"planned","PickAction", ["human_0"], ["cube_415", "human_0"], 0, [1]).
//
//action(3,"planned","PlaceAction", ["human_0"], ["box_1", "cube_415", "human_0"], 0, [1]).

actionStates(["planned","todo","ongoing","executed"]).

!start.

+!start : true <-
	rjs.jia.log_beliefs;
	.verbose(2);
	!getRobotName;
//	.set.create(S);             
//	.set.add_all(S,["cube_412", "human_0"]); 
//	+action(0,"planned","PickAction", ["human_0"], S, 0, []);
//	.set.create(U);                // S = {}
//	.set.add_all(U,["cube_412", "box_2", "human_0"]); 
//	+action(1,"planned","PlaceAction", ["human_0"], U, 0, [0]);
//	.set.create(V);             
//	.set.add_all(V,["cube_415", "human_0"]); 
//	+action(2,"planned","PickAction", ["human_0"], V, 0, [1]);
//	.set.create(W);             
//	.set.add_all(W,["cube_415",  "box_1", "human_0"]); 
//	+action(3,"planned","PlaceAction", ["human_0"], W, 0, [1]);
	+goal(0,active,"pile").
//	!updatePlan;
//	.wait(2000);
//	-action(0,"todo",Name,Agents,Cost,Params,Preds);
//	+action(0,"executed",Name,Agents,Cost,Params,Preds);
//	.wait(2000);
//	!updatePlan;
//	.wait(2000);
//	-action(1,"todo",Name2,Agents2,Cost2,Params2,Preds2);
//	+action(1,"executed",Name2,Agents2,Cost2,Params2,Preds2);
//	.wait(2000);
//	!updatePlan.
	
+goal(ID, State, Task) : State == active <-
//	getPlan(Task);
	!setMementarSub;
	!updatePlan.
	
+goal(ID, State, Task) : State == preempted | State == aborted <-	
	true.
	
+goal(ID,State, Task) : State == succeeded <-
	for(monitoring(MonID,Act,_,_)){
		mementarUnsubscribe(MonID,Act);
	}.
	
+!setMementarSub : true <-
	rjs.jia.get_param("/supervisor/actsToMonitor", "List", ActsToMonitor);
	for(.member(Y,ActsToMonitor)){
		.count(action(_,"planned",Y,_,_,_,_),C);
		mementarSubscribe(Y,start,-1);
		mementarSubscribe(Y,end,-1);
	}.	

+!updatePlan : true <-
	for(action(ID,"planned",Name,Agents,Params,Cost,Preds)){
		.findall(P, 
			(action(P,S,_,_,_,_,_) 
			& .member(P,Preds) 
			& actionStates(AS) 
			& .difference(AS,["executed"],NotEx) 
			& .member(S,NotEx)
		), PredsL);
		if(.empty(PredsL)){
			-action(ID,"planned",Name,Agents,Params,Cost,Preds);
			+action(ID,"todo",Name,Agents,Params,Cost,Preds);
		}
	}.
	
wantedAction(Name,Agents,Params) :- action(ID,S,Name,Agents,Params,_,_) & (S=="todo" | S=="ongoing").

@evOngoing[atomic]
+action(_,"ongoing",Name,Agents,Params)[source(_)] : action(ID,"todo",Name,Agents,Params,Cost,Preds) <-
	-action(_,"ongoing",Name,Agents,Params)[source(_)];
	-action(ID,"todo",Name,Agents,Params,Cost,Preds);
	+action(ID,"ongoing",Name,Agents,Params,Cost,Preds).
	
+action(_,"ongoing",Name,Agents,Params)[source(_)] : action(ID,"executed",Name,Agents,Params,Cost,Preds) <- true.

@evExe[atomic]	
+action(_,"executed",Name,Agents,Params)[source(_)] : wantedAction(Name,Agents,Params) <-
	-action(_,"executed",Name,Agents,Params)[source(_)];
	-action(ID,_,Name,Agents,Params,Cost,Preds);
	+action(ID,"executed",Name,Agents,Params,Cost,Preds);
	!updatePlan;
	!testRemainingActions.

+!testRemainingActions : .count(action(_,S,_,_,_,_,_) & S \== "executed", C) & C = 0
	<- ?goal(GoalID,active,Task);
		-goal(GoalID,active,Task);
		+goal(GoalID,succeeded,Task).

+action(ID,"executed",Name,Agents,Params) :  not wantedAction(Name,Params) <- true.
	
	
+action(ID,"todo",Name,Agents,Params,Cost,Preds) : robotName(Agent) & .member(Agent,Agents) <-
	.wait(2000);
	+action(ID,"executed",Name,Agents,Params)[source(robot_executor)].
//	.send(robot_executor, tell, action(ID,Name,Agents,Params)).



