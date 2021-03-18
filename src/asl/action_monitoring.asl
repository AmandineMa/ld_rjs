{ include("human_actions.asl")}

handMovingToward(human_0,[cube_GGTB,obj2])[source(percept)].
handEmpty(human_0)[source(percept)].
isOn(cube_GGTB,table)[source(percept)].


possibleActions(NewPredicate,ActionsList) :-
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
	).
	
!start.

@startSM[atomic]
+NewPredicate[source(percept)] : possibleActions(NewPredicate,ActionsList) <-
	.print(ActionsList);
	+possibleOngoingActions(ActionsList).
	

@ongoingSM[atomic]
+NewPredicate[source(percept)] : possibleOngoingActions(_) & isProgressionEffect(NewPredicate,ActionsList) <-
	.print(ActionsList);
	+possibleProgressingActions(ActionsList).
	
+!start : true <-
	rjs.jia.log_beliefs;
	.wait(2000);
	+hasInHand(human_0,cube_BGCG)[source(percept)].
	
	