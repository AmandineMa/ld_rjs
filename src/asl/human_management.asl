{ include("action_monitoring.asl")}
{ include("common.asl")}

isActionMatching(Name,Agent,Params,Type) :-
    Type & Type=..[T,[A],[]] & .member(M,A)
    & (   M=..[Prop,[T1,T2],[]] & .member(T2,Params)
    	| M=..[Prop,[T1,T2,T3],[]] & (.member(T2,Params) | .member(T3,Params))
    )
    & .term2string(N,Name) & Prop==N & T1==Agent.

!start.

+!start : true <-
    rjs.jia.log_beliefs;
    .verbose(2);
    !getRobotName;
    !getHumanName;
    !setActionsMementarMonitoring.

+!setActionsMementarMonitoring : true <-
	.findall(X,actionModel(_,_,Y,_,_)[source(self)] & .member(X,Y),L);
	!setMementarMonitoring(L);
	.findall(X,actionModel(_,_,_,Z,_)[source(self)] & .member(X,Z),M);
	!setMementarMonitoring(M);
	.findall(X,actionModel(_,_,_,_,W)[source(self)] & .member(X,W),N);
	!setMementarMonitoring(N).

+!setMementarMonitoring(BelList) : true <-
    for(.member(B,BelList)){
        B=..[Prop,[T1,T2],[]];
        if(rjs.jia.unnamedvar2string(T2,T) & .substring("List",T)){
                Function="?";
                jia.set_predicate_with_list(Prop);
        }elif(rjs.jia.negated_literal(Prop)){
                Function="del";
        }else{
                Function="add";
        }
        .term2string(Prop,PropS);
        .delete("~",PropS,PropF)
        if(not rjs.jia.believes(monitoring(_,Function,"?",PropF,"?",_))){
                mementarSubscribe(Function,"?",PropF,"?",-1);
        };
    }.

+action(ID,Name,Agent,Params)[source(plan_manager)] : true <-
    !waitForActionToStart(ID,Name,Agent,Params).

+!waitForActionToStart(ID,Name,Agent,Params) : true <-
    +waitingForActionToStart(ID,Name,Agent,Params);
    .wait(isActionMatching(Name,Agent,Params,possibleStartedActions(A)), 20000);
    -waitingForActionToStart(ID,Name,Agent,Params).

-!waitForActionToStart(ID,Name,Agent,Params) : true <-
    +actionNotStarting(ID,Name,Agent,Params).
