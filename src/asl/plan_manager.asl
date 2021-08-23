{include("common.asl")}

// TODO: plan manager is generic but not receiver. should put the receiver somewhere else

actionStates(["planned","todo","ongoing","executed","unplanned","not_starting","not_finished","not_seen","suspended"])[ground].

wantedAction(ID) :-  action(ID,S,_,_,_,_,_) & (S=="todo" | S=="ongoing").
isNotOver(S) :- S\=="executed" & S\=="unplanned".

@uas[atomic]
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
			-action(ID,"planned",Name,Agent,Params,Preds,Decompo)[source(_)];
			++action(ID,"todo",Name,Agent,Params,Preds,Decompo);
			!updatePlanTasksStart(Decompo);
		}
	}.

@rps[atomic]
+!removeParallelStreams(Agent,Preds) : true <-
	.findall(action(ID,State,Name,Agent,Params,Preds,Decompo), action(ID,State,Name,Agent,Params,Preds,Decompo) & (State=="suspended" | State=="todo"), Actions);
	!setUnplannedActions(Actions).
	
+!setUnplannedActions(Actions) : true <-
	for(.member(A,Actions)){
		A=..[action,[ID,State,Name,Agent,Params,Preds,Decompo],[]];
		-A[source(_)];
		+action(ID,"unplanned",Name,Agent,Params,Preds,Decompo);
		!updatePlanTasksEnd(Decompo,"unplanned");
		!removeChild(ID);
	}.
	
+!removeChild(Parent) : action(IDi,"planned",Namei,Agenti,Paramsi,Preds,Decompoi)  & .member(Parent,Preds) <-
	.findall(action(ID,"planned",Name,Agent,Params,Preds,Decompo), action(ID,"planned",Name,Agent,Params,Preds,Decompo) & .member(Parent,Preds), Actions);
	!setUnplannedActions(Actions).
	
+!removeChild(Parent) : true.

@upa[atomic]	
+!updatePlanActions : true <-
	!updateActionState;
	!testRemainingActions.

+!testRemainingActions : .count(action(_,S,_,_,_,_,_) & isNotOver(S) , C) & C == 0
	<- ?goal(Name,active)[source(_)];
		-goal(Name,active)[source(_)];
		+goal(Name,succeeded).
		
+!testRemainingActions : true <- true.

