{ include("common.asl")}
{ include("plan_manager.asl")}
{ register_function("rjs.function.length_allow_unground") } 

isActionMatching(Name,Agent,Params,Type,NewParams) :-
    Type &  Type=..[T,[A],[Source]] & .member(M,A) &
     M=..[Prop,RecoParamsWithAgent,[]] & .nth(0,RecoParamsWithAgent,Actor) & .delete(0,RecoParamsWithAgent,RecoParams) 
     & jia.is_same_action_class(Prop,Name)
     & (Actor==Agent  | agentXName(Agent))
     & rjs.function.length_allow_unground(RecoParams)==rjs.function.length_allow_unground(Params) 
     & jia.members_same_entity(RecoParams,Params,NewParams).
     
isMonitoredActionInPlan(Type,PlanActions) :-
	Type & Type=..[T,[A],[]] & .member(M,A) &
    M=..[Prop,RecoParamsWithAgent,[]] &
    .nth(0,RecoParamsWithAgent,Actor) &
    .delete(0,RecoParamsWithAgent,RecoParams) &
    .setof(action(ID,State,Name,Agent,Params,Preds,Decompo),
    	action(ID,State,Name,Agent,Params,Preds,Decompo)[source(_)] & (Actor==Agent  | agentXName(Agent)) & jia.is_same_action_class(Prop,Name) 
    			  & rjs.function.length_allow_unground(RecoParams)==rjs.function.length_allow_unground(Params) & jia.members_same_entity(RecoParams,Params),
    	PlanActions
    ).

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
//    .verbose(2);
    !initRosComponents;
    !getAgentNames;
    ?humanName(HName);
    ?robotName(Robot);
    mementarSubscribe("?",HName,isInFoV,Robot,-1);
    mementarSubscribe("?",HName,isLookingAt,Robot,-1);
    mementarSubscribe("?",Robot,isPerceiving,HName,-1).
 
+!updatePlanTasksStart(Decompo) : true.

+!updatePlanTasksEnd(Decompo,State) : true. 

+goal(Name,active) : true <-
	!updatePlanActions.

//////////////////////////////////////////////////////////////////	 
////////////////// Action to perform by human ////////////////////    
//////////////////////////////////////////////////////////////////	

+action(ID,"todo",Name,Agent,Params,Preds,Decompo) : humanName(Agent) & Name == "IDLE" <-
	-action(ID,"todo",Name,Agent,Params,Preds,Decompo);
	++action(ID,"executed",Name,Agent,Params,Preds,Decompo);
	.send(robot_management,tell,action(ID,"executed",Name,Agent,Params)).
	
+action(ID,"todo",Name,Agent,Params,Preds,Decompo) : humanName(Agent) | agentXName(Agent) <-
    !waitForActionToStart(ID,Name,Agent,Params,Preds,Decompo).
    
+!waitForActionToStart(ID,Name,Agent,Params,Preds,Decompo) : not .intend(waitForActionToStart(_,Name,Agent,Params,_,_)) & humanName(Human) <-
    .wait(isActionMatching(Name,Agent,Params,possibleStartedActions(X)[source(action_monitoring)],NewParams), 30000);
    -action(ID,"todo",Name,Agent,Params,Preds,Decompo);
    ++action(ID,"ongoing",Name,Human,NewParams,Preds,Decompo);
    .send(robot_management,tell,action(ID,"ongoing",Name,Human,NewParams));
    !suspendActionsWithSamePreds(ID,Human,Preds);
    !waitForActionToFinish(ID,Name,Human,NewParams,Preds,Decompo).
    
+!waitForActionToStart(ID,Name,Agent,Params,Preds,Decompo) : true.
	
+!suspendActionsWithSamePreds(ID,Agent,Preds) : true <-
	.findall(action(IDX,"todo",NameX,Agent,ParamsX,Preds,DecompoX),action(IDX,"todo",NameX,Agent,ParamsX,Preds,DecompoX) & IDX \== ID,ActionList);
    for(.member(A,ActionList)){
		A=..[action,[IDX,StateX,NameX,AgentX,ParamsX,Preds,DecompoX],[]];
		-A;
		.drop_intention(waitForActionToStart(IDX,NameX,AgentX,ParamsX,Preds,DecompoX));
		++action(IDX,"suspended",NameX,AgentX,ParamsX,Preds,DecompoX);
	}.
	
-!waitForActionToStart(ID,Name,Agent,Params,Preds,Decompo)[code(_),code_line(_),code_src(_),error(wait_timeout),error_msg(_),source(self)] : agentXName(Agent) <- true.

-!waitForActionToStart(ID,Name,Agent,Params,Preds,Decompo)[code(_),code_line(_),code_src(_),error(wait_timeout),error_msg(_),source(self)] : true <-
	-action(ID,"todo",Name,Agent,Params,Preds,Decompo);
    ++action(ID,"not_starting",Name,Agent,Params,Preds,Decompo).
    
 +!waitForActionToFinish(ID,Name,Agent,Params,Preds,Decompo) : humanName(Human) <-
    .wait(isActionMatching(Name,Agent,Params,possibleFinishedActions(A)[source(action_monitoring)],NewParams), 30000);
    -action(ID,_,Name,Agent,Params,Preds,Decompo);
    +action(ID,"executed",Name,Human,NewParams,Preds,Decompo);
    .send(robot_management,tell,action(ID,"executed",Name,Human,NewParams));
    !removeParallelStreams(Agent,Preds);
    !updatePlanActions.
    
 -!waitForActionToFinish(ID,Name,Agent,Params,Preds,Decompo)[code(_),code_line(_),code_src(_),error(wait_timeout),error_msg(_),source(self)] : true <-
 	-action(ID,"ongoing",Name,Agent,Params,Preds,Decompo);
 	.send(robot_management,tell,action(ID,"todo",Name,Agent,Params));
    ++action(ID,"not_finished",Name,Agent,Params,Preds,Decompo).

@onga[atomic]
+action(ID,"ongoing",Name,Agent,Params,Preds,Decompo) : humanName(Agent) <-
	.random(X);
	Y=math.round(X*1000000);
	.concat(Name,"_",Y, NameX);
	+action(ID,nameX,NameX);
	jia.insert_task_mementar(NameX,start).
	
+action(ID,"executed",Name,Agent,Params,Preds,Decompo) : humanName(Agent) & action(ID,nameX,NameX) <-
	jia.insert_task_mementar(NameX,end);
	-action(ID,nameX,NameX).
	
+action(ID,"executed",Name,Agent,Params,Preds,Decompo) : humanName(Agent) <-
	.random(X);
	Y=math.round(X*1000000);
	.concat(Name,"_",Y, NameX);
	jia.insert_task_mementar(NameX,end).

+action(ID,"not_starting",Name,Agent,Params,Preds,Decompo) : notAlreadyAsked(ID) <-
	!handleNotStarting(ID,"not_starting",Name,Agent,Params,Preds,Decompo).

+action(ID,"not_starting",Name,Agent,Params,Preds,Decompo) : true <-
	!askStop.

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
	for(.member(A,PlanActions)){
		A =.. [P,[ID,State,Name,Agent,Params,Preds,Decompo],[]];
		if(State=="todo"){
			.drop_desire(waitForActionToStart(ID,Name,Agent,Params,Preds,Decompo));
			!suspendActionsWithSamePreds(ID,Agent,Preds);
    		!waitForActionToFinish(ID,Name,Agent,Params,Preds,Decompo);
		}elif(State=="planned"){
			?robotName(Robot);
			if(action(IDX,"ongoing",NameX,Robot,ParamsX,PredsX,DecompoX) & .member(IDX,Preds)){
				.send(communication,askOne,talk("You are fast ! I was supposed to finish before you"),Answer);
				!waitForActionToFinish(ID,Name,Agent,Params,Preds,Decompo);
			}else{
				.send(robot_management,tell,replan(GName));
				.send(communication,askOne,talk("I replan"),Answer);
			}
		}elif(State=="unplanned"){
			.send(robot_management,tell,replan(GName));
			.send(communication,askOne,talk("I replan"),Answer);
		}elif(State=="not_starting" | State=="not_finished"){
			!waitForActionToFinish(ID,Name,Agent,Params,Preds,Decompo);
		}
	}.
	
+possibleFinishedActions(A)[source(action_monitoring)] : true <-
	.send(communication,askOne,talk("I think you made a mistake, it was not part of the plan"),Answer).
	
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
  
+action(ID,"ongoing",Name,Robot,Params)[source(_)] : humanName(HName) & jia.is_relation_in_onto(Robot,isInFoV,HName,true,robot) <-
	-action(ID,"ongoing",Name,Robot,Params)[source(_)];
	!waitHumanToSeeAction(ID,Name,Robot,Params,HName).

+action(ID,"ongoing",Name,Robot,Params)[source(_)] : humanName(HName) <-
	-action(ID,"ongoing",Name,Robot,Params)[source(_)];
//	mementarSubscribe("?",HName,isInFoV,Robot,-1);
	!waitFoV(ID,Name,Robot,Params,HName) ||| .wait(action(ID,"executed",Name,Robot,Params)[source(robot_executor)]).

+!waitHumanToSeeAction(ID,Name,Robot,Params,HName) : true <-
	if(not jia.is_relation_in_onto(HName,isLookingAt,Robot,true,robot)){
//		mementarSubscribe("?",HName,isLookingAt,Robot,-1);
	}else{
		+isLookingAt(HName,Robot);
	}
	!waitLookingAt(ID,Name,Robot,Params,HName) ||| .wait(action(ID,"executed",Name,Robot,Params)[source(robot_executor)]).

+!waitFoV(ID,Name,Robot,Params,HName) : true <-
	.wait(isInFoV(Robot,HName));
	!waitHumanToSeeAction(ID,Name,Robot,Params,HName).
	
+!waitLookingAt(ID,Name,Robot,Params,HName) : true <-
	.wait(isLookingAt(HName,Robot));
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
	
+!handleActionNotSeen(ID,"not_seen",Name,Robot,Params,Preds,Decompo) : humanName(HName) <-
	if(not jia.is_relation_in_onto(Robot,isPerceiving,HName,false,robot)){
//		mementarSubscribe("?",Robot,isPerceiving,HName,-1);
	}else{
		+isPerceiving(Robot,HName);
	};
	.wait(isPerceiving(Robot,HName));
	!checkActionEffects(Name,Params);
	-action(ID,"not_seen",Name,Robot,Params,Preds,Decompo);
	+action(ID,"executed",Name,Robot,Params,Preds,Decompo);
	!updatePlanActions.
	
+!checkActionEffects(Name,Params) : not actionEffectsVisible(Name,Params) <-
	.send(communication,askOne,informActionExecuted(Name,Params),Answer).
	
+!checkActionEffects(Name,Params) : true.
	