{ include("common.asl")}
{ include("plan_manager.asl")}

isActionMatching(Name,Agent,Params,Type) :-
    Type & Type=..[T,[A],[Source]] & .member(M,A)
    & (   M=..[Prop,[T1,T2],[]] & .member(T2,Params)
    	| M=..[Prop,[T1,T2,T3],[]] & (.member(T2,Params) | .member(T3,Params))
    )
    & .term2string(N,Name) & Prop==N & T1==Agent.
    
!start.

+!start : true <-
    rjs.jia.log_beliefs.
//    .verbose(2).
 
+!updatePlanTasksStart(Decompo) : true.

+!updatePlanTasksEnd(Decompo,State) : true. 
    
////////////////// Action to perform by human //////////////////    
+action(ID,"todo",Name,Agent,Params,Preds,Decompo) : humanName(Agent) <-
    !waitForActionToStart(ID,Name,Agent,Params,Preds,Decompo).
    
 +!waitForActionToStart(ID,Name,Agent,Params,Preds,Decompo) : true <-
    .wait(isActionMatching(Name,Agent,Params,possibleStartedActions(A)[source(action_monitoring)]), 20000);
    -+action(ID,"ongoing",Name,Agent,Params,Preds,Decompo);
    !waitForActionToFinish(ID,Name,Agent,Params,Preds,Decompo).

-!waitForActionToStart(ID,Name,Agent,Params,Preds,Decompo) : true <-
    -+action(ID,"not_starting",Name,Agent,Params,Preds,Decompo).
    
 +!waitForActionToFinish(ID,Name,Agent,Params,Preds,Decompo) : true <-
    .wait(isActionMatching(Name,Agent,Params,possibleFinishedActions(A)[source(action_monitoring)]), 20000);
    -+action(ID,"executed",Name,Agent,Params,Preds,Decompo).
    
 -!waitForActionToStart(ID,Name,Agent,Params,Preds,Decompo) : true <-
    -+action(ID,"not_finished",Name,Agent,Params,Preds,Decompo).

//+action(ID,"not_starting",Name,Agent,Params,Preds,Decompo) : action(ID2,"not_seen",Name2,Agent2,Params2,Preds2,Decompo2) <-
	  
//////////////////// Action being performed by robot /////////////   
+action(ID,"ongoing",Name,Robot,Params)[source(robot_executor)] : humanName(HName) & jia.query_onto_individual(Robot,isInFoV,HName,true) <-
	!waitHumanToSeeAction(ID,Name,Robot,Params,HName).

+action(ID,"ongoing",Name,Robot,Params)[source(robot_executor)] : true <-
	mementarSubscribe("?",HName,isInFoV,Robot,-1);
//	+robotActionNotVisible;
	!waitFoV(ID,Name,Robot,Params,HName) ||| .wait(action(ID,"executed",Name,Robot,Params)[source(robot_executor)]).

+!waitHumanToSeeAction(ID,Name,Robot,Params,HName) : true <-
	if(not jia.query_onto_individual(Robot,isLookingAt,HName,true)){
		mementarSubscribe("?",HName,isLookingAt,Robot,-1);
	}
	!waitLookingAt(ID,Name,Robot,Params,HName) ||| .wait(action(ID,"executed",Name,Robot,Params)[source(robot_executor)]).

+!waitFoV(ID,Name,Robot,Params,HName) : true <-
	.wait(isInFoV(Robot,HName));
//	-robotActionNotVisible; 
	!waitHumanToSeeAction(ID,Name,Robot,Params,HName).

//+!waitActionExecuted(ID,Name,Robot,Params,HName) : true <-
//	.wait(action(ID,"executed",Name,Robot,Params)[source(robot_executor)]).
////	+~robotActionSeen(ID,Name,Robot,Params).
	
+!waitLookingAt(ID,Name,Robot,Params,HName) : true <-
	.wait(isLookingAt(HName,Robot));
	!updateBelOngoing(Name,Agent,Params,_).
	
+action(ID,"executed",Name,Robot,Params)[source(robot_executor)] :  action(ID,"ongoing",Name,Agent,Params,Preds,Decompo) <-
	!updateBelExecuted(Name,Agent,Params,_).

+action(ID,"executed",Name,Robot,Params)[source(robot_executor)] : true <-
	-action(ID,"todo",Name,Agent,Params,Preds,Decompo);
	+action(ID,"not_seen",Name,Agent,Params,Preds,Decompo).
	
+action(ID,"not_seen",Name,Agent,Params,Preds,Decompo) : jia.query_onto_individual(Robot,isPerceiving,HName,true) <-
	// TODO to implement
	communicateAction;
	!updateBelExecuted(Name,Agent,Params,Decompo);
	!updatePlanActions.

///////////////////


    
+actionNotStarting(ID,Name,Agent,Params): ~robotActionSeen(IDR,NameR,AgentR,ParamsR) & lastRobotAction(IDR) <-
	true.

+possibleStartedActions(A)[source(action_monitoring)] : true <-
	.print(A," received").
	
	
	
	