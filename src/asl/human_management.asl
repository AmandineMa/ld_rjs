!start.

+!start : true <-
    rjs.jia.log_beliefs.
//    .verbose(2).

isActionMatching(Name,Agent,Params,Type) :-
    Type & Type=..[T,[A],[]] & .member(M,A)
    & (   M=..[Prop,[T1,T2],[]] & .member(T2,Params)
    	| M=..[Prop,[T1,T2,T3],[]] & (.member(T2,Params) | .member(T3,Params))
    )
    & .term2string(N,Name) & Prop==N & T1==Agent.



+action(ID,Name,Agent,Params)[source(plan_manager)] : true <-
    !waitForActionToStart(ID,Name,Agent,Params).

+!waitForActionToStart(ID,Name,Agent,Params) : true <-
    +waitingForActionToStart(ID,Name,Agent,Params);
    .wait(isActionMatching(Name,Agent,Params,possibleStartedActions(A)[source(action_monitoring)]), 20000);
    -waitingForActionToStart(ID,Name,Agent,Params).

-!waitForActionToStart(ID,Name,Agent,Params) : true <-
    +actionNotStarting(ID,Name,Agent,Params).

+possibleStartedActions(A)[source(action_monitoring)] : true <-
	.print(A," received").