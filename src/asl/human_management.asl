{ include("common.asl")}
{ include("plan_manager.asl")}

isActionMatching(Name,Agent,Params,Type) :-
    Type & Type=..[T,[A],[Source]] & .member(M,A)
    & (   M=..[Prop,[T1],[]] & .empty(Params)
    	| M=..[Prop,[T1,T2],[]] & .member(T2,Params)
    	| M=..[Prop,[T1,T2,T3],[]] & (.member(T2,Params) | .member(T3,Params))
    )
    & jia.is_same_action_type(Prop,Name) & T1==Agent.
    
!start.

+!start : true <-
    rjs.jia.log_beliefs;
    .verbose(2);
	!initRosComponents;
    !getAgentNames;
    !initRosComponents;
    ?humanName(HName);
    ?robotName(Robot);
    mementarSubscribe("?",HName,isInFoV,Robot,-1);
    mementarSubscribe("?",HName,isLookingAt,Robot,-1);
    mementarSubscribe("?",Robot,isPerceiving,HName,-1).
//    .wait(5000);
//    +possibleStartedActions([human_get_water("human")])[source(action_monitoring)];
//    .wait(10000);
//    +possibleFinishedActions([human_get_water("human")])[source(action_monitoring)].
 
+!updatePlanTasksStart(Decompo) : true.

+!updatePlanTasksEnd(Decompo,State) : true. 
    
////////////////// Action to perform by human //////////////////    

+action(ID,"todo",Name,Agent,Params,Preds,Decompo) : humanName(Agent) & Name == "IDLE" <-
	-action(ID,"todo",Name,Agent,Params,Preds,Decompo);
	+action(ID,"executed",Name,Agent,Params,Preds,Decompo);
	.send(robot_management,tell,action(ID,"executed",Name,Agent,Params)).
	
+action(ID,"todo",Name,Agent,Params,Preds,Decompo) : humanName(Agent) <-
    !waitForActionToStart(ID,Name,Agent,Params,Preds,Decompo).
    
 +!waitForActionToStart(ID,Name,Agent,Params,Preds,Decompo) : true <-
    .wait(isActionMatching(Name,Agent,Params,possibleStartedActions(X)[source(action_monitoring)]), 30000);
    -action(ID,"todo",Name,Agent,Params,Preds,Decompo);
    +action(ID,"ongoing",Name,Agent,Params,Preds,Decompo);
    .send(robot_management,tell,action(ID,"ongoing",Name,Agent,Params));
    !suspendActionsWithSamePreds(Preds);
    !waitForActionToFinish(ID,Name,Agent,Params,Preds,Decompo).
	
+!suspendActionsWithSamePreds(Preds) : true <-
	.findall(action(IDX,"todo",NameX,Agent,ParamsX,Preds,DecompoX),action(IDX,"todo",NameX,Agent,ParamsX,Preds,DecompoX),ActionList);
    for(.member(A,ActionList)){
		A=..[action,[IDX,StateX,NameX,AgentX,ParamsX,Preds,DecompoX],[]];
		-A;
		.drop_intention(waitForActionToStart(IDX,NameX,AgentX,ParamsX,Preds,DecompoX));
		+action(IDX,"suspended",NameX,AgentX,ParamsX,Preds,DecompoX);
	}.

-!waitForActionToStart(ID,Name,Agent,Params,Preds,Decompo)[code(_),code_line(_),code_src(_),error(wait_timeout),error_msg(_),source(self)] : true <-
	-action(ID,"todo",Name,Agent,Params,Preds,Decompo);
    ++action(ID,"not_starting",Name,Agent,Params,Preds,Decompo).
    
 +!waitForActionToFinish(ID,Name,Agent,Params,Preds,Decompo) : true <-
    .wait(isActionMatching(Name,Agent,Params,possibleFinishedActions(A)[source(action_monitoring)]), 30000);
    -action(ID,_,Name,Agent,Params,Preds,Decompo);
    +action(ID,"executed",Name,Agent,Params,Preds,Decompo);
    .send(robot_management,tell,action(ID,"executed",Name,Agent,Params));
    !removeParallelStreams(Agent,Preds);
    !updatePlanActions.
    
 -!waitForActionToFinish(ID,Name,Agent,Params,Preds,Decompo)[code(_),code_line(_),code_src(_),error(wait_timeout),error_msg(_),source(self)] : true <-
 	-action(ID,"ongoing",Name,Agent,Params,Preds,Decompo);
 	.send(robot_management,tell,action(ID,"todo",Name,Agent,Params));
    +action(ID,"not_finished",Name,Agent,Params,Preds,Decompo).

@onga[atomic]
+action(ID,"ongoing",Name,Agent,Params,Preds,Decompo) : humanName(Agent) <-
	.random(X);
	Y=math.round(X*1000000);
	.concat(Name,"_",Y, NameX);
	+action(ID,nameX,NameX);
	jia.insert_task_mementar(NameX,start).
	
+action(ID,"executed",Name,Agent,Params,Preds,Decompo) : humanName(Agent) <-
	?action(ID,nameX,NameX);
	jia.insert_task_mementar(NameX,end);
	-action(ID,nameX,NameX).

+action(ID,"not_starting",Name,Agent,Params,Preds,Decompo) : 
	.findall(action(N,P), 
		asked(todo,ActionList)[source(communication)] 
		& .member(A,ActionList) 
		& A=..[Pred,[I,N,P],[]]
		& N==Name,
		L
	) & .empty(L)
	| not asked(todo,ActionList)[source(communication)] 
	<-
	!handleNotStarting(ID,"not_starting",Name,Agent,Params,Preds,Decompo).

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
	
+action(ID,"not_starting",Name,Agent,Params,Preds,Decompo) : true <-
	//TODO to define-
	.print("nothing to do").
	  
//////////////////// Action being performed by robot ///////////// 
+action(ID,"ongoing",Name,Robot,Params)[source(_)] : jia.is_of_class(class,Name,"CommunicateAction") <-	
	-action(_,"ongoing",Name,Robot,Params)[source(_)];
	-action(ID,_,Name,Robot,Params,Preds,Decompo)[source(_)];
	+action(ID,"ongoing",Name,Robot,Params,Preds,Decompo).
  
+action(ID,"ongoing",Name,Robot,Params)[source(_)] : humanName(HName) & jia.is_relation_in_onto(Robot,isInFoV,HName,true) <-
	-action(ID,"ongoing",Name,Robot,Params)[source(_)];
	!waitHumanToSeeAction(ID,Name,Robot,Params,HName).

+action(ID,"ongoing",Name,Robot,Params)[source(_)] : humanName(HName) <-
	-action(ID,"ongoing",Name,Robot,Params)[source(_)];
//	mementarSubscribe("?",HName,isInFoV,Robot,-1);
	!waitFoV(ID,Name,Robot,Params,HName) ||| .wait(action(ID,"executed",Name,Robot,Params)[source(robot_executor)]).

+!waitHumanToSeeAction(ID,Name,Robot,Params,HName) : true <-
	if(not jia.is_relation_in_onto(HName,isLookingAt,Robot,true)){
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
	-action(ID,_,Name,Robot,Params,Preds,Decompo)[source(_)];
	+action(ID,"ongoing",Name,Robot,Params,Preds,Decompo).
	
+action(ID,"executed",Name,Robot,Params)[source(robot_executor)] :  
		action(ID,"ongoing",Name,Robot,Params,Preds,Decompo) | not jia.is_of_class(class,Name,"PhysicalAction") <-
	-action(_,"executed",Name,Robot,Params)[source(_)];
	-+action(ID,"executed",Name,Robot,Params,Preds,Decompo)[source(_)];
	!updatePlanActions.

+action(ID,"executed",Name,Robot,Params)[source(robot_executor)] : true <-
	-action(ID,"executed",Name,Robot,Params)[source(robot_executor)];
	-action(ID,_,Name,Robot,Params,Preds,Decompo)[source(_)];
	+action(ID,"not_seen",Name,Robot,Params,Preds,Decompo).
	
+action(ID,"not_seen",Name,Robot,Params,Preds,Decompo) : true <-
	!handleActionNotSeen(ID,"not_seen",Name,Robot,Params,Preds,Decompo).
	
+!handleActionNotSeen(ID,"not_seen",Name,Robot,Params,Preds,Decompo) : humanName(HName) <-
	if(not jia.is_relation_in_onto(Robot,isPerceiving,HName,false)){
//		mementarSubscribe("?",Robot,isPerceiving,HName,-1);
	}else{
		+isPerceiving(Robot,HName);
	};
	.wait(isPerceiving(Robot,HName));
	.send(communication,askOne,informActionExecuted(Name,Params),Answer);
	-action(ID,"not_seen",Name,Robot,Params,Preds,Decompo);
	+action(ID,"executed",Name,Robot,Params,Preds,Decompo);
	!updatePlanActions.
	
	
	
	