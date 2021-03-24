{ include("human_actions.asl")}

handEmpty(human_0)[source(percept)].
isOn(cube_GGTB,table_1)[source(percept)].

isMovementRelatedToActions(NewPredicate,ActionsList) :-
	.findall(
		Name,
		action(Name,Preconditions,Movement,ProgressionEffects,NecessaryEffect) &
		.member(NewPredicate, Movement) &
		arePredicatesInListTrue(Preconditions),
		ActionsList
	)
	& .length(ActionsList) > 0.

arePredicatesInListTrue(List) :- 
	.count(
		.member(Predicate,List) & 
		Predicate, 
		Count
	) &
	.length(List) == Count.

isProgressionEffect(Predicate,ActionsList) :-
	.findall(
		Name,
		possibleOngoingActions(PossibleActionsList) &
		action(Name,Preconditions,Movement,ProgressionEffects,NecessaryEffect) &
		jia.member_same_type(Predicate,ProgressionEffects) &
		.member(Name,PossibleActionsList),
		ActionsList
	)
	& .length(ActionsList) > 0.
	
isNecessaryEffect(Predicate,ActionsList) :-
	.findall(
		Name,
		possibleProgressingActions(ProgressingActionsList) &
		action(Name,Preconditions,Movement,ProgressionEffects,NecessaryEffect) &
		jia.member_same_type(Predicate,NecessaryEffect) &
		.member(Name,ProgressingActionsList),
		ActionsList
	)
	& .length(ActionsList) > 0.		
	
existPossibleHumanAgent(NewPredicate) :-
	rjs.jia.exist_possible_agent.

!start.

@startSM[atomic]
+NewPredicate[source(percept)] :  isMovementRelatedToActions(NewPredicate,ActionsList) 
								& not possibleOngoingActions(_) 
								& not (possibleProgressingActions(_) | possibleFinishedActions(_)) <-
	+possibleOngoingActions(ActionsList).	


@ongoingSM[atomic]
+NewPredicate[source(percept)] : possibleOngoingActions(_) & isProgressionEffect(NewPredicate,ActionsList) <-
	+possibleProgressingActions(ActionsList).
	
@overSM[atomic]
+NewPredicate[source(percept)] : (possibleProgressingActions(_) | possibleOngoingActions(_)) & isNecessaryEffect(NewPredicate,ActionsList) <-
	+possibleFinishedActions(ActionsList).
	
@ongoingSM2[atomic]
+NewPredicate[source(percept)] : isProgressionEffect(NewPredicate,ActionsList) <-
	+possibleProgressingActions(ActionsList).

@overSM2[atomic]
+NewPredicate[source(percept)] : isNecessaryEffect(NewPredicate,ActionsList) & existPossibleHumanAgent(NewPredicate) <-
	+possibleFinishedActions(ActionsList).
	
	
	
+!start : true <-
	.wait(500);
	rjs.jia.log_beliefs;
	.verbose(2);
	+handMovingToward(human_0,[cube_GGTB,obj2])[source(percept)];
	.wait(2000);
	+hasInHand(human_0,cube_GGTB)[source(percept)];
	.wait(2000);
	-isOn(cube_GGTB,table_1)[source(percept)];
	+~isOn(cube_GGTB,table_1)[source(percept)].
	
	