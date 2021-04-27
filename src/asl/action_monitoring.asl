{ include("human_actions.asl")}
{ register_function("rjs.function.length_allow_unground") } 

isMovementRelatedToActions(Predicate,ActionList) :-
	jia.findall(
		ActPred,
		action(ActPred,Preconditions,Movement,ProgressionEffects,NecessaryEffect) &
		jia.member_same_type(Predicate,Movement),
		ActionList
	)
	& rjs.function.length_allow_unground(ActionList) > 0.

isProgressionEffect(Predicate,ActionList) :-
	.findall(
		ActPred,
		action(ActPred,Preconditions,Movement,ProgressionEffects,NecessaryEffect) &
		jia.member_same_type(Predicate,ProgressionEffects),
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
		action(ActPred,Preconditions,Movement,ProgressionEffects,NecessaryEffect) &
		jia.member_same_type(Predicate,NecessaryEffect),
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

//!start.

// trigger with an action -> action started
@startedS[atomic]
+NewPredicate[source(percept)] :  isMovementRelatedToActions(NewPredicate,ActionList) <-
	!addPossibleStartedActions(ActionList).	
	
+!addPossibleStartedActions(ActionList) : possibleStartedActions(ActionListPrev) & .intersection(ActionList,ActionListPrev,I) & .length(I) > 0 <-
	-possibleStartedActions(ActionListPrev);
	++possibleStartedActions(ActionList).

+!addPossibleStartedActions(ActionList) : true <-
	++possibleStartedActions(ActionList).

// from action started -> action progressing
@progressS1[atomic]
+NewPredicate[source(percept)] :isProgressionEffect(NewPredicate,ActionList) 
							   & matchingStartedActions(Predicate,ActionList,ActionList2) <-
	++possibleProgressingActions(ActionList2).

// from action started or action progressing -> action over	
@overS[atomic]
+NewPredicate[source(percept)] : isNecessaryEffect(NewPredicate,ActionList) 
							   & (matchingProgressingActions(Predicate,ActionList,ActionList2) | matchingStartedActions(Predicate,ActionList,ActionList2))<-
	jia.update_action_list(possibleStartedActions,ActionList2);	
	jia.update_action_list(possibleProgressingActions,ActionList2);								   
	++possibleFinishedActions(ActionList2).

// trigger with a progression effect -> action progressing
// no check to see if it exists a possible human agent has all progression effect predicates have a human as object or subject
@progressS2[atomic]
+NewPredicate[source(percept)] : isProgressionEffect(NewPredicate,ActionList) <-
	++possibleProgressingActions(ActionList).

// trigger with a necessary effect -> action over
@overS2[atomic]
+NewPredicate[source(percept)] : isNecessaryEffect(NewPredicate,ActionList) & jia.exist_possible_agent(ActionList,ActionList2) <-
	jia.update_action_list(possibleStartedActions,ActionList2);	
	jia.update_action_list(possibleProgressingActions,ActionList2);
	++possibleFinishedActions(ActionList2).
	
// wait for an effect after an observed movement finished
@unknownS
+possibleStartedActions(ActionList) :  true <-
		.wait(possibleProgressingActions(A) & .intersection(A,ActionList,I) & .length(I) >0) 
	||| .wait(possibleFinishedActions(A) & .intersection(A,ActionList,I)  & .length(I) >0)
	||| !timeoutMovement(ActionList).

+!timeoutMovement(ActionList) : true <-
	.wait(20000);
	// ne fonctionne pas sans le add_time !! pourquoi ?? parce qu'ajouté via code ??
	-possibleStartedActions(ActionList)[add_time(_)].
	
+possibleProgressingActions(ActionList) : true <-
	.wait(possibleFinishedActions(A) & .intersection(A,ActionList,I) & .length(I) >0)
	||| !timeoutProgressing(ActionList).

// TODO timeout should be action type dependent
+!timeoutProgressing(ActionList) : true <-
	.wait(20000);
	-possibleProgressingActions(ActionList)[add_time(_)];
	.findall(possibleStartedActions(A),possibleStartedActions(A) & .intersection(A,ActionList,I) & .length(I)>0,L);
	for(.member(M,L)){
		-M;
		+M;
	}.
	

	