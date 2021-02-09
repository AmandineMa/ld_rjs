{include("common.asl")}

actionStates(["planned","todo","ongoing","executed"]).

!start.

+!start : true <-
	rjs.jia.log_beliefs;
	.verbose(2);
	!getRobotName;
	!getHumanName;
	+goal(0,active,"pile").
	
+goal(ID, State, Task) : State == active <-
	?humanName(Human);
	rjs.jia.get_param("plan_manager/goals/dt1/name", "String", N);
	rjs.jia.get_param("plan_manager/goals/dt1/worldstate", "Map", G);
	//[[name_t1,param1_t1,param2_t1],[name_t2, param1_t2]]
	getPlan([[N,G]], [Human]);
	!setMementarSub;
	!updatePlanActions.
	
+goal(ID, State, Task) : State == preempted | State == aborted <-	
	true.
	
+goal(ID,State, Task) : State == succeeded <-
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
	
+!updatePlanActions : true <-
	for(action(ID,"planned",Name,Agent,Params,Preds,Decompo)){
		.findall(P, 
			(action(P,S,_,_,_,_,_) 
			& .member(P,Preds) 
			& actionStates(AS) 
			& .difference(AS,["executed"],NotEx) 
			& .member(S,NotEx)
		), PredsL);
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
							  & Decompo \== 0 <-	
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
	jia.insertTaskMementar(NameX,start).
	
+abstractTask(ID,"executed",Name,Decompo) : true <-
	?abstractTask(ID,nameX,NameX);
	-abstractTask(ID,nameX,NameX);
	jia.insertTaskMementar(NameX,end).
	
wantedAction(Name,Agent,Params) :- action(ID,S,Name,Agent,Params,_,_) & (S=="todo" | S=="ongoing").

@evOngoing[atomic]
+action(_,"ongoing",Name,Agent,Params)[source(_)] : action(ID,"todo",Name,Agent,Params,Preds,Decompo) <-
	-action(_,"ongoing",Name,Agent,Params)[source(_)];
	-action(ID,"todo",Name,Agent,Params,Preds,Decompo);
	+action(ID,"ongoing",Name,Agent,Params,Preds,Decompo).
	
+action(_,"ongoing",Name,Agent,Params)[source(_)] : action(ID,"executed",Name,Agent,Params,Preds,Decompo) <- true.

@evExe[atomic]	
+action(_,"executed",Name,Agent,Params)[source(_)] : wantedAction(Name,Agent,Params) <-
	-action(_,"executed",Name,Agent,Params)[source(_)];
	-action(ID,_,Name,Agent,Params,Preds,Decompo);
	+action(ID,"executed",Name,Agent,Params,Preds,Decompo);
	!updatePlanTasksEnd(Decompo);
	!updatePlanActions;
	!testRemainingActions.

+!testRemainingActions : .count(action(_,S,_,_,_,_,_) & S \== "executed", C) & C == 0
	<- ?goal(GoalID,active,Task);
		-goal(GoalID,active,Task);
		.send(robot_executor, tell, planOver);
		+goal(GoalID,succeeded,Task).
		
+!testRemainingActions : true <- true.

+action(ID,"executed",Name,Agent,Params) :  not wantedAction(Name,Params) <- true.
	
+action(ID,"todo",Name,Agent,Params,Preds,Decompo) : robotName(Agent) <-
	.send(robot_executor, tell, action(ID,Name,Agent,Params)).

//temporary
+action(ID,"todo",Name,Agent,Params,Preds,Decompo) : humanName(Agent) & Name == "IDLE" <-
	+action(ID,"executed",Name,Agent,Params).

