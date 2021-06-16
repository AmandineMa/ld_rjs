{ include("action_models.asl")}
{ register_function("rjs.function.length_allow_unground") } 
{ include("common.asl")}

isMovementRelatedToActions(Predicate,ActionList) :-
	jia.findall(
		ActPred,
		actionModel(ActPred,Preconditions,Movement,ProgressionEffects,NecessaryEffect) &
		jia.member_same_class(Predicate,Movement),
		ActionList
	)
	& rjs.function.length_allow_unground(ActionList) > 0.

isProgressionEffect(Predicate,ActionList) :-
	.findall(
		ActPred,
		actionModel(ActPred,Preconditions,Movement,ProgressionEffects,NecessaryEffect) &
		jia.member_same_class(Predicate,ProgressionEffects),
		ActionList
	)
	& rjs.function.length_allow_unground(ActionList) > 0.
	
matchingStartedActions(Predicate,ActionList,ActionList2) :-
	.findall(
		ActPred,
		possibleStartedActions(PossibleActionList) &
		.member(ActPred,ActionList) &
		.member(ActPred,PossibleActionList),
		ActionList2
	)
	& rjs.function.length_allow_unground(ActionList2) > 0.
	
isNecessaryEffect(Predicate,ActionList) :-
	.findall(
		ActPred,
		actionModel(ActPred,Preconditions,Movement,ProgressionEffects,NecessaryEffect) &
		jia.member_same_class(Predicate,NecessaryEffect),
		ActionList
	)
	& rjs.function.length_allow_unground(ActionList) > 0.		
	
matchingProgressingActions(Predicate,ActionList,ActionList2) :-
	.findall(
		ActPred,
		possibleProgressingActions(ProgressingActionList) &
		.member(ActPred,ActionList) &
		.member(ActPred,ProgressingActionList),
		ActionList2
	)
	& rjs.function.length_allow_unground(ActionList2) > 0.

isPredicateRobotAction(NewPredicate, Params) :-
	  NewPredicate=..[P,[T1,T2],[]] 
	& humanName(H) & T1 \== H 
	& .member(T1,Params) | .member(T2,Params).
	
!start.

+!start : true <-
    rjs.jia.log_beliefs;
//    .verbose(2);
	!initRosComponents;
    !getAgentNames;
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
        mementarSubscribe(Function,"?",PropF,"?",-1);
    }.

+action(ID,"executed",Name,Agent,Params)[source(robot_executor)] : true <-
	-action(ID,"ongoing",Name,Agent,Params)[source(robot_executor)];
	.wait(2000);
	-action(ID,"executed",Name,Agent,Params)[source(robot_executor)].

+NewPredicate[source(percept)] :  action(_,_,Name,_,Params)[source(robot_executor)] & isPredicateRobotAction(NewPredicate, Params) <- true.

// trigger with an action -> action started
@startedSbis[atomic]
+NewPredicate[source(percept)] :  isMovementRelatedToActions(NewPredicate,ActionList) & possibleStartedActions(ActionList2) 
								& jia.intersection_literal_list(ActionList,ActionList2,I) & .length(I) >0 <-
	jia.update_action_list(possibleStartedActions,ActionList);		
	++possibleStartedActions(ActionList).	
	
@startedS[atomic]
+NewPredicate[source(percept)] :  isMovementRelatedToActions(NewPredicate,ActionList) <-
	++possibleStartedActions(ActionList).	
	
// from action started -> action progressing
@progressS1[atomic]
+NewPredicate[source(percept)] :isProgressionEffect(NewPredicate,ActionList) 
							   & matchingStartedActions(Predicate,ActionList,ActionList2) <-
	jia.update_action_list(possibleStartedActions,ActionList2);
	++possibleProgressingActions(ActionList2).
	
// trigger with a progression effect -> action progressing
// no check to see if it exists a possible human agent has all progression effect predicates have a human as object or subject
@progressS2[atomic]
+NewPredicate[source(percept)] : isProgressionEffect(NewPredicate,ActionList) <-
	++possibleProgressingActions(ActionList).	

// from action started or action progressing -> action over	
@overS[atomic]
+NewPredicate[source(percept)] : isNecessaryEffect(NewPredicate,ActionList) 
							   & (matchingProgressingActions(Predicate,ActionList,ActionList2) | matchingStartedActions(Predicate,ActionList,ActionList2))<-
	!addFinishedActions(ActionList2).
	
+!addFinishedActions(ActionList) : true <-
	jia.update_action_list(possibleStartedActions,ActionList);	
	jia.update_action_list(possibleProgressingActions,ActionList);
	++possibleFinishedActions(ActionList).

// trigger with a necessary effect -> action over
@overS2[atomic]
+NewPredicate[source(percept)] : isNecessaryEffect(NewPredicate,ActionList) & jia.exist_possible_agent(ActionList,ActionList2) <-
	++possibleFinishedActions(ActionList2).
	
// wait for an effect after an observed movement finished
@unknownS
+possibleStartedActions(ActionList) :  true <-
	.send(human_management,tell,possibleStartedActions(ActionList));
		.wait(possibleProgressingActions(A) & jia.intersection_literal_list(A,ActionList,I) & .length(I) >0) 
	||| .wait(possibleFinishedActions(A) & jia.intersection_literal_list(A,ActionList,I)  & .length(I) >0)
	||| !timeoutMovement(ActionList).

+!timeoutMovement(ActionList) : true <-
	.wait(30000);
	// ne fonctionne pas sans le add_time !! pourquoi ?? parce qu'ajouté via code ??
	-possibleStartedActions(ActionList)[add_time(_)].
	
+possibleProgressingActions(ActionList) : true <-
	.send(human_management,tell,possibleProgressingActions(ActionList));
	.wait(possibleFinishedActions(A) & jia.intersection_literal_list(A,ActionList,I) & .length(I) >0)
	||| !timeoutProgressing(ActionList).

+possibleFinishedActions(ActionList) : true <-
	.send(human_management,tell,possibleFinishedActions(ActionList));
	-possibleFinishedActions(ActionList).
	
-possibleStartedActions(ActionList) : true <-
	.send(human_management,untell,possibleStartedActions(ActionList)).

-possibleProgressingActions(ActionList) : true <-
	.send(human_management,untell,possibleProgressingActions(ActionList)).

// TODO timeout should be action type dependent
+!timeoutProgressing(ActionList) : true <-
	.wait(30000);
	-possibleProgressingActions(ActionList)[add_time(_)];
	.findall(possibleStartedActions(A),possibleStartedActions(A) & jia.intersection_literal_list(A,ActionList,I) & .length(I)>0,L);
	for(.member(M,L)){
		-M;
		+M;
	}.
	
+!drop(G) : true <-
	!reset.

	