{ include("human_actions.asl")}

isMovementRelatedToActions(Predicate,ActionList) :-
	jia.findall(
		ActPred,
		action(ActPred,Preconditions,Movement,ProgressionEffects,NecessaryEffect) &
		jia.member_same_type(Predicate,Movement) &
		arePredicatesInListTrue(Preconditions),
		ActionList
	)
	& .length(ActionList) > 0.

arePredicatesInListTrue(List) :- 
	.count(
		.member(Predicate,List) & 
		Predicate, 
		Count
	) &
	.length(List) == Count.

isProgressionEffect(Predicate,ActionList) :-
	.findall(
		ActPred,
		action(ActPred,Preconditions,Movement,ProgressionEffects,NecessaryEffect) &
		jia.member_same_type(Predicate,ProgressionEffects),
		ActionList
	)
	& .length(ActionList) > 0.
	
matchingOngoingActions(Predicate,ActionList,ActionList2) :-
	.findall(
		ActPred,
		possibleOngoingActions(PossibleActionList) &
		.member(ActPred,ActionList) &
		.member(ActPred,PossibleActionList),
		ActionList2
	)
	& .length(ActionList2) > 0.
	
isNecessaryEffect(Predicate,ActionList) :-
	.findall(
		ActPred,
		action(ActPred,Preconditions,Movement,ProgressionEffects,NecessaryEffect) &
		jia.member_same_type(Predicate,NecessaryEffect),
		ActionList
	)
	& .length(ActionList) > 0.		
	
matchingProgressingActions(Predicate,ActionList,ActionList2) :-
	.findall(
		ActPred,
		possibleProgressingActions(ProgressingActionList) &
		.member(ActPred,ActionList) &
		.member(ActPred,ProgressingActionList),
		ActionList2
	)
	& .length(ActionList2) > 0.
	
existPossibleHumanAgent(NewPredicate,Human) :-
	jia.exist_possible_agent(NewPredicate,Human).

!start.

// trigger with an action -> action ongoing
@ongoingS[atomic]
+NewPredicate[source(percept)] :  isMovementRelatedToActions(NewPredicate,ActionList) 
								& not possibleOngoingActions(_) 
								& not (possibleProgressingActions(_) | possibleFinishedActions(_)) <-
	+possibleOngoingActions(ActionList).	

// from action ongoing -> action progressing
@progressS1[atomic]
+NewPredicate[source(percept)] :isProgressionEffect(NewPredicate,ActionList) 
							   & matchingOngoingActions(Predicate,ActionList,ActionList2) <-
	+possibleProgressingActions(ActionList2).

// from action ongoing or action progressing -> action over	
@overS[atomic]
+NewPredicate[source(percept)] : isNecessaryEffect(NewPredicate,ActionList) 
							   & (matchingProgressingActions(Predicate,ActionList,ActionList2) | matchingOngoingActions(Predicate,ActionList,ActionList2))<-
	+possibleFinishedActions(ActionList2).

// trigger with a progression effect -> action progressing
// no check to see if it exists a possible human agent has all progression effect predicates have a human as object or subject
@progressS2[atomic]
+NewPredicate[source(percept)] : isProgressionEffect(NewPredicate,ActionList) <-
	+possibleProgressingActions(ActionList).
	
// trigger with a necessary effect -> action over
@overS2[atomic]
+NewPredicate[source(percept)] : isNecessaryEffect(NewPredicate,ActionList) & existPossibleHumanAgent(NewPredicate,Human) <-
	+possibleFinishedActions(ActionList).
	
// wait for an effect after an observed movement
@unknownS
-NewPredicate[source(percept)] :  isMovementRelatedToActions(NewPredicate,ActionList) 
								& possibleOngoingActions(ActionList) <-
		.wait(possibleProgressingActions(A) & .sublist(A,ActionList)) 
	||| .wait(possibleFinishedActions(A) & .sublist(A,ActionList))
	||| !timeout.

+!timeout : true <-
	.wait(5000);
	-possibleOngoingActions(_).

//tests
//+!start : true <-
//	rjs.jia.log_beliefs;
//	.verbose(2);
//	+handEmpty("human_0")[source(percept)];
//	+isOn("cube_GGTB","table_1")[source(percept)];
//	.wait(1000);
//	++handMovingToward("human_0",["cube_GGTB","obj2","cube_BGTB"])[source(percept)].
//	.wait(2000);
//	--handMovingToward("human_0",["cube_GGTB","obj2","cube_BGTB"])[source(percept)];
//	.wait(2000);
//	++hasInHand("human_0","cube_GGTB")[source(percept)];
//	.wait(2000);
//	--isOn("cube_GGTB","table_1")[source(percept)];
//	++~isOn("cube_GGTB","table_1")[source(percept)].
	
	