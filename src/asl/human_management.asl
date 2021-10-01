{ include("common.asl")}
{ include("plan_manager.asl")}
{ include("action_models.asl")}
{ register_function("rjs.function.length_allow_unground") } 

isActionMatching(Name,Agent,Params,Type,NewParams) :-
    Type &  Type=..[T,[A],[Source]] & .member(M,A) &
     M=..[Prop,RecoParamsWithAgent,[]] & .nth(0,RecoParamsWithAgent,Actor) & .delete(0,RecoParamsWithAgent,RecoParams) 
     & jia.is_same_action_class(Prop,Name)
     & (not .ground(Actor) | Actor==Agent  | agentXName(Agent))
     & rjs.function.length_allow_unground(RecoParams)==rjs.function.length_allow_unground(Params) 
     & jia.members_same_entity(RecoParams,Params,NewParams).
     
isMonitoredActionInPlan(Type,PlanActions) :-
	Type & Type=..[T,[A],[]] & .member(M,A) &
    M=..[Prop,RecoParamsWithAgent,[]] &
    .nth(0,RecoParamsWithAgent,Actor) &
    .delete(0,RecoParamsWithAgent,RecoParams) &
    .setof(action(ID,State,Name,Agent,Params,Preds,Decompo),
    	action(ID,State,Name,Agent,Params,Preds,Decompo)[source(_)] & not robotName(Agent) &( not .ground(Actor) | Actor==Agent  | agentXName(Agent)) & jia.is_same_action_class(Prop,Name) 
    			  & rjs.function.length_allow_unground(RecoParams)==rjs.function.length_allow_unground(Params) & jia.members_same_entity(RecoParams,Params),
    	PlanActions
    ).
    
isTodoAlreadyPerformed(Name,Agent,Params) :-
	.setof(ActionsList,possibleFinishedActions(ActionsList),PossibleFinishedActions)
	& jia.is_todo_act_already_performed(PossibleFinishedActions,Name,Agent,Params).

notAlreadyAsked(ID) :- 
	.findall(action(I), 
		asked(todo,ActionList)[source(communication)] 
		& .member(A,ActionList) 
		& A=..[Pred,[I,N,P],[]]
		& I==ID,
		L
	) & .empty(L) 
	| not asked(todo,ActionList)[source(communication)].
	
actionEffectsVisible(Name,Params) :- 
	jia.get_action_class(Name,Action,robot) 
	& Action =..[ActMod,[ActPred,Effects],S] 
	& ActPred =..[ActModName,[Agent,ParamsAction],[]]
	& jia.members_same_class(Params,ParamsAction)
	& .count(.member(M,Effects) & M=..[Pred,[P1,P2],[]] & jia.is_relation_in_onto(P1,Pred,P2,false,human),C)
	& rjs.function.length_allow_unground(Effects)==C.

!start.
	
+!start : true <-
    rjs.jia.log_beliefs;
    .verbose(2);
    T=[[a],[b],[c]];
    .concat(T,B);
    !initRosComponents;
    !getAgentNames;
    ?humanName(HName);
    ?robotName(Robot);
    mementarSubscribe("?",HName,isPerceiving,Robot,-1);
    mementarSubscribe("?",HName,isLookingAt,Robot,-1);
    mementarSubscribe("?",Robot,isPerceiving,HName,-1).
 
+!updatePlanTasksStart(Decompo) : true.

+!updatePlanTasksEnd(Decompo,State) : true. 

+goal(Name,active) : true <-
	!updatePlanActions.

//////////////////////////////////////////////////////////////////	 
////////////////// Action to perform by human ////////////////////    
//////////////////////////////////////////////////////////////////	
+action(ID,"todo",Name,Agent,Params,Preds,Decompo) : humanName(Agent) & jia.is_of_class(class,Name,"DefaultAction") <-
	-action(ID,"todo",Name,Agent,Params,Preds,Decompo);
	++action(ID,"executed",Name,Agent,Params,Preds,Decompo);
	.send(robot_management,tell,action(ID,"executed",Name,Agent,Params)).
	
+action(ID,"todo",Name,Agent,Params,Preds,Decompo) : humanName(Agent) & isTodoAlreadyPerformed(Name,Agent,Params) <-
	//TODO maybe find better place to remove these
	.findall(possibleFinishedActions(Actions)[source(S)],possibleFinishedActions(Actions)[source(S)],PFA);
	for(.member(A,PFA)){
		-A;
	}
	!finishedAction(ID,Name,Agent,Params,Preds,Decompo).
	
+action(ID,"todo",Name,Agent,Params,Preds,Decompo) : humanName(Agent) & jia.is_of_class(class,Name,"WaitAction") <-
	-action(ID,"todo",Name,Agent,Params,Preds,Decompo);
	++action(ID,"ongoing",Name,Agent,Params,Preds,Decompo);
	.send(robot_management,tell,action(ID,"ongoing",Name,Agent,Params));
	!waitToSeeIfHumanActs(ID,Name,Agent,Params,Preds,Decompo).
	
+!waitToSeeIfHumanActs(ID,Name,Agent,Params,Preds,Decompo) : true <-
	.wait(action(ID2,State,_,Agent,_,Preds,_) & State == "executed" & ID2 \== ID,5000).
	
+action(ID,"todo",Name,Agent,Params,Preds,Decompo) : humanName(Agent) | agentXName(Agent) <-
    !waitForActionToStart(ID,Name,Agent,Params,Preds,Decompo).
    
-!waitToSeeIfHumanActs(ID,Name,Agent,Params,Preds,Decompo) : true <-
	!finishedAction(ID,Name,Agent,Params,Preds,Decompo).
	
+!waitForActionToStart(ID,Name,Agent,Params,Preds,Decompo) : humanName(Human) <-
    .wait(isActionMatching(Name,Agent,Params,possibleStartedActions(X)[source(action_monitoring)],NewParams) | isActionMatching(Name,Agent,Params,possibleProgressingActions(X)[source(action_monitoring)],NewParams), 10000); 
    -action(ID,"todo",Name,Agent,Params,Preds,Decompo);
    ++action(ID,"ongoing",Name,Human,NewParams,Preds,Decompo);
    .send(robot_management,tell,action(ID,"ongoing",Name,Human,NewParams));
    !waitForActionToFinish(ID,Name,Human,NewParams,Preds,Decompo).
    
+!waitForActionToStart(ID,Name,Agent,Params,Preds,Decompo) : true.
	
+!suspendActionsWithSamePreds(ID,Agent,Preds) : true <-
	.findall(action(IDX,_,NameX,Agent,ParamsX,Preds,DecompoX),action(IDX,_,NameX,Agent,ParamsX,Preds,DecompoX) & IDX \== ID,ActionList);
    for(.member(A,ActionList)){
		A=..[action,[IDX,StateX,NameX,AgentX,ParamsX,Preds,DecompoX],[]];
		-A[source(_)];
		.drop_intention(waitForActionToStart(IDX,NameX,AgentX,ParamsX,Preds,DecompoX));
		.drop_intention(waitForActionToFinish(IDX,NameX,AgentX,ParamsX,Preds,DecompoX));
		++action(IDX,"suspended",NameX,AgentX,ParamsX,Preds,DecompoX);
		.send(robot_management,tell,action(IDX,"suspended",NameX,AgentX,ParamsX));
	}. 
	
-!waitForActionToStart(ID,Name,Agent,Params,Preds,Decompo)[code(_),code_line(_),code_src(_),error(wait_timeout),error_msg(_),source(self)] : agentXName(Agent) <- true.

-!waitForActionToStart(ID,Name,Agent,Params,Preds,Decompo)[code(_),code_line(_),code_src(_),error(wait_timeout),error_msg(_),source(self)] : true <-
	-action(ID,"todo",Name,Agent,Params,Preds,Decompo);
    ++action(ID,"not_starting",Name,Agent,Params,Preds,Decompo).
    
 +!waitForActionToFinish(ID,Name,Agent,Params,Preds,Decompo) : humanName(Human) <-
    .wait(isActionMatching(Name,Agent,Params,possibleFinishedActions(A)[source(action_monitoring)],NewParams), 10000);
    !finishedAction(ID,Name,Human,NewParams,Preds,Decompo).
    
+!finishedAction(ID,Name,Agent,Params,Preds,Decompo) : humanName(Human) <-
	-action(ID,_,Name,Agent,_,Preds,Decompo)[source(_)];
    +action(ID,"executed",Name,Human,Params,Preds,Decompo);
	!suspendActionsWithSamePreds(ID,Human,Preds);
    .send(robot_management,tell,action(ID,"executed",Name,Human,Params));
    !removeParallelStreams(Agent,Preds);
    !updatePlanActions.
    
 -!waitForActionToFinish(ID,Name,Agent,Params,Preds,Decompo)[code(_),code_line(_),code_src(_),error(wait_timeout),error_msg(_),source(self)] : true <-
 	-action(ID,"ongoing",Name,Agent,Params,Preds,Decompo);
// 	.send(robot_management,tell,action(ID,"todo",Name,Agent,Params));
    ++action(ID,"not_finished",Name,Agent,Params,Preds,Decompo).
    
@onga[atomic]
+action(ID,"ongoing",Name,Agent,Params,Preds,Decompo)[add_time(T)] : humanName(Agent) <-
	.random(X);
	Y=math.round(X*1000000);
	.concat(Name,"_",Y, NameX);
	+action(ID,nameX,NameX);
	jia.insert_task_mementar(NameX,start,T).
	
+action(ID,"executed",Name,Agent,Params,Preds,Decompo)[add_time(T)] : humanName(Agent) & action(ID,nameX,NameX) <-
	jia.insert_task_mementar(NameX,end,T);
	-action(ID,nameX,NameX).
	
+action(ID,"executed",Name,Agent,Params,Preds,Decompo)[add_time(T)] : humanName(Agent) <-
	.random(X);
	Y=math.round(X*1000000);
	.concat(Name,"_",Y, NameX);
	jia.insert_task_mementar(NameX,end,T).
	
+action(ID,"suspended",Name,Agent,Params,Preds,Decompo) : humanName(Agent) <-
	.random(X);
	Y=math.round(X*1000000);
	.concat(Name,"_",Y, NameX);
	jia.remove_task_mementar(NameX).

+action(ID,"not_starting",Name,Agent,Params,Preds,Decompo) : notAlreadyAsked(ID) <-
	!handleNotStarting(ID,"not_starting",Name,Agent,Params,Preds,Decompo).

+action(ID,"not_starting",Name,Agent,Params,Preds,Decompo) : true <-
	!askStop.

//TODO send only action state and not sentence
+action(ID,"not_finished",Name,Agent,Params,Preds,Decompo) : notAlreadyAsked(ID) <-
 	.send(communication,askOne,askActionsTodo([action(ID,Name,Params)],_),Answer);
 	.send(communication,askOne,talk("I think you started to do it but did not finish it."),Answer2);
 	-action(ID,_,Name,Agent,Params,Preds,Decompo);
	++action(ID,"todo",Name,Agent,Params,Preds,Decompo).

+action(ID,"not_finished",Name,Agent,Params,Preds,Decompo) : true <-
	!askStop.
	
+!askStop(ID,_,Name,Agent,Params,Preds,Decompo) : true <-
	.send(communication,askOne,askStop(Answer),A);
	if(A=..[askStop,["yes"],[S]]){
		?goal(GName,GState)[source(_)];
		.broadcast(tell,drop(GName));
		!reset;
	}else{
		.send(communication,askOne,askActionsTodo([action(ID,Name,Params)],_),Answer);
	}.

//only one plan as this one at a time
+!handleNotStarting(ID,"not_starting",Name,Agent,Params,Preds,Decompo) : not .intend(handleNotStarting(_,_,_,_,_,_,_)) <-
	.wait(100);
	.findall(action(IDX,NameX,ParamsX),action(IDX,"not_starting",NameX,Agent,ParamsX,PredsX,DecompoX),ActionList);
	.send(communication,askOne,askActionsTodo(ActionList,or),Answer);
	for(.member(A,ActionList)){
		A =.. [P,[IDX,NameX,ParamsX],[]];
		?action(IDX,_,NameX,AgentX,ParamsX,PredsX,DecompoX);
		-action(IDX,_,NameX,AgentX,ParamsX,PredsX,DecompoX);
		++action(IDX,"todo",NameX,AgentX,ParamsX,PredsX,DecompoX);
	}.
//everything is handled with the plan above	
+!handleNotStarting(ID,"not_starting",Name,Agent,Params,Preds,Decompo) : true.
	
+possibleFinishedActions(ActionList)[source(action_monitoring)] : isMonitoredActionInPlan(possibleFinishedActions(ActionList),PlanActions)  & goal(GName,GState)[source(_)]<-
	jia.choose_most_probable_action(PlanActions,A);
	A =.. [P,[ID,State,Name,Agent,Params,Preds,Decompo],[]];
	.concat("I think you ",Name," ",Sentence);
//	.send(communication,askOne,talk(Sentence),Answer1);
	if(State=="todo"){
		.drop_desire(waitForActionToStart(ID,Name,Agent,Params,Preds,Decompo));
		-possibleFinishedActions(ActionList)[source(action_monitoring)];
		!suspendActionsWithSamePreds(ID,Agent,Preds);
		!finishedAction(ID,Name,Agent,Params,Preds,Decompo);
	}elif(State=="planned"){
		?robotName(Robot);
		if((action(IDX,"ongoing",NameX,Robot,ParamsX,PredsX,DecompoX) | action(IDX,"todo",NameX,Robot,ParamsX,PredsX,DecompoX)) & .member(IDX,Preds)){
//			.send(communication,askOne,talk("You are fast ! I was supposed to finish before you"),Answer);
			-possibleFinishedActions(ActionList)[source(action_monitoring)];
			!finishedAction(ID,Name,Agent,Params,Preds,Decompo);
		}elif(jia.are_planned_preds_only_wait(Agent,Name,Preds)){
				-possibleFinishedActions(ActionList)[source(action_monitoring)];
				!finishedAction(ID,Name,Agent,Params,Preds,Decompo);
//			else{
//				.send(robot_management,tell,replan(GName));
//				.send(communication,askOne,talk("I replan"),Answer);
//			}
		}
	}elif(State=="unplanned"){
//			.send(robot_management,tell,replan(GName));
//			.send(communication,askOne,talk("I replan"),Answer);
	}elif(State=="not_starting" | State=="not_finished"){
		-possibleFinishedActions(ActionList)[source(action_monitoring)];
		!finishedAction(ID,Name,Agent,Params,Preds,Decompo);
	}.
	
//TODO only during plan, maybe too many actions detected
//+possibleFinishedActions(A)[source(action_monitoring)] : true <-
//	.send(communication,askOne,talk("I think you made a mistake, it was not part of the plan"),Answer).
	
+said(cannot_do)[source(communication)] : action(ID,State,Name,Robot,Params,Preds,Decompo)  
	& (State == "not_finished" | State == "not_starting" | State == "todo") & goal(GName,GState)[source(_)] <-
	.drop_all_intentions;
	.send(robot_management,tell,replan(GName)).

//////////////////////////////////////////////////////////////////	  
//////////////////// Action being performed by robot ///////////// 
//////////////////////////////////////////////////////////////////	

+action(ID,"ongoing",Name,Robot,Params)[source(_)] : jia.is_of_class(class,Name,"CommunicateAction") <-	
	-action(_,"ongoing",Name,Robot,Params)[source(_)];
	-action(ID,_,Name,_,_,Preds,Decompo)[source(_)];
	+action(ID,"ongoing",Name,Robot,Params,Preds,Decompo).
  
+action(ID,"ongoing",Name,Robot,Params)[source(_)] : humanName(HName) &  jia.is_relation_in_onto(Robot,isPerceiving,HName,false,robot) <-
	-action(ID,"ongoing",Name,Robot,Params)[source(_)];
	!waitHumanToSeeAction(ID,Name,Robot,Params,HName).

+action(ID,"ongoing",Name,Robot,Params)[source(_)] : humanName(HName) <-
	-action(ID,"ongoing",Name,Robot,Params)[source(_)];
	!waitFoV(ID,Name,Robot,Params,HName) ||| .wait(action(ID,"executed",Name,Robot,Params)[source(robot_executor)]).

+!waitHumanToSeeAction(ID,Name,Robot,Params,HName) : true <-
	!waitLookingAt(ID,Name,Robot,Params,HName) ||| .wait(action(ID,"executed",Name,Robot,Params)[source(robot_executor)]).

+!waitFoV(ID,Name,Robot,Params,HName) : true <-
	!waitFact(isPerceiving(HName,Robot));
	!waitHumanToSeeAction(ID,Name,Robot,Params,HName).
	
+!waitLookingAt(ID,Name,Robot,Params,HName) : true <-
	//TODO select proper parameter 
	.nth(0,Params,P);
	!waitFact(isLookingAt(HName,Robot)) ||| !waitFact(isLookingAt(HName,P));
	-action(_,"ongoing",Name,Robot,Params)[source(_)];
	-action(ID,_,Name,_,_,Preds,Decompo)[source(_)];
	+action(ID,"ongoing",Name,Robot,Params,Preds,Decompo).

+action(ID,"executed",Name,Robot,Params)[source(robot_executor)] :  
		action(ID,"ongoing",Name,Robot,Params,Preds,Decompo) | not jia.is_of_class(class,Name,"PhysicalAction") <-
	-action(_,"executed",Name,_,_)[source(_)];
	-action(ID,_,Name,_,_,Preds,Decompo)[source(_)];
	+action(ID,"executed",Name,Robot,Params,Preds,Decompo)[source(_)];
	!updatePlanActions.

+action(ID,"executed",Name,Robot,Params)[source(robot_executor)] : true <-
	-action(ID,"executed",Name,Robot,Params)[source(robot_executor)];
	-action(ID,_,Name,_,_,Preds,Decompo)[source(_)];
	+action(ID,"not_seen",Name,Robot,Params,Preds,Decompo).
	
+action(ID,"not_seen",Name,Robot,Params,Preds,Decompo) : true <-
	!handleActionNotSeen(ID,"not_seen",Name,Robot,Params,Preds,Decompo).
	
//TODO add non-observable action here and in onto
	
+!handleActionNotSeen(ID,"not_seen",Name,Robot,Params,Preds,Decompo) : humanName(HName) <-
//	!waitFact(isPerceiving(Robot,HName));
	!checkActionEffects(Name,Params);
	-action(ID,"not_seen",Name,Robot,Params,Preds,Decompo);
	+action(ID,"executed",Name,Robot,Params,Preds,Decompo);
	!updatePlanActions.
	
+!checkActionEffects(Name,Params) : not actionEffectsVisible(Name,Params) <-
	.send(communication,askOne,informActionExecuted(Name,Params),Answer).
	
+!checkActionEffects(Name,Params) : true.
	