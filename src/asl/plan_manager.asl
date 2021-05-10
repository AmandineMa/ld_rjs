{include("common.asl")}

// TODO: plan manager is generic but not receiver. should put the receiver somewhere else

actionStates(["planned","todo","ongoing","executed","unnplanned","not_starting","not_finished","not_seen"])[ground].

wantedAction(Name,Agent,Params) :- action(ID,S,Name,Agent,Params,_,_) & (S=="todo" | S=="ongoing").
isNotOver(S) :- S\=="executed" & S\=="unplanned".

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

+!removeParallelStreams(Agent,Preds) : true <-
	.findall(action(ID,"todo",Name,Agent,Params,Preds,Decompo), action(ID,"todo",Name,Agent,Params,Preds,Decompo), Actions);
	!setUnplannedActions(Actions).

+!removeChild(Parent) : action(ID,"planned",Name,Agent,Params,Preds,Decompo)  & .member(Parent,Preds) <-
	.findall(action(ID,"planned",Name,Agent,Params,Preds,Decompo), action(ID,"planned",Name,Agent,Params,Preds,Decompo) & .member(Parent,Preds), Actions);
	!setUnplannedActions(Actions).
	
+!setUnplannedActions(Actions) : true <-
	for(.member(A,Actions)){
		A=..[action,[ID,State,Name,Agent,Params,Preds,Decompo],[]];
		-A;
		+action(ID,"unplanned",Name,Agent,Params,Preds,Decompo);
		!updatePlanTasksEnd(Decompo,"unplanned");
		!removeChild(ID);
	}.

+!removeChild(Parent) : true.
	
+!updatePlanActions : true <-
	!updateActionState;
	!testRemainingActions.

+!testRemainingActions : .count(action(_,S,_,_,_,_,_) & isNotOver(S) , C) & C == 0
	<- ?goal(Name,active);
		-goal(Name,active);
		.send(robot_executor, tell, planOver);
		+goal(Name,succeeded).
		
+!testRemainingActions : true <- true.

