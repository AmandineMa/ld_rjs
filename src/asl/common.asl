rmMsgPrio(vital,4)[ground].
rmMsgPrio(urgent,3)[ground].
rmMsgPrio(high,2)[ground].
rmMsgPrio(standard,1)[ground].
rmMsgPrio(low,0)[ground].
rmMsgPrio(void,-1)[ground].

rmBuffPrio(atomic,4)[ground].
rmBuffPrio(prioritize,3)[ground].
rmBuffPrio(normal,2)[ground].
rmBuffPrio(secondary,1)[ground].
rmBuffPrio(background,0)[ground].
rmBuffPrio(inhibit,-1)[ground].


+!getAgentNames : true <-
	!getRobotName;
	!getHumanName;
	!getAgentXName.

+!getRobotName : true <- 
	rjs.jia.get_param("/supervisor/robot_name", "String", Name);
	+robotName(Name)[ground].
	
+!getHumanName : true <-
	rjs.jia.get_param("/supervisor/human_name", "String", Name);
	+humanName(Name)[ground].
	
+!getAgentXName : true <-
	rjs.jia.get_param("/supervisor/agentX_name", "String", Name);
	+agentXName(Name)[ground].

+!initRosComponents : true <- 
	!init_services;
	!init_sub.
	
+!init_services : true <-
	initServices.

-!init_services: true <-
	.wait(3000);
	!init_services.

+!init_sub : true <-
	initSub.
	
-!init_sub : true <-
	.wait(3000);
	!init_sub.

+!head_scan : true <-
	.send(robot_executor, tell, action(_,"head_scan","robot",[])).
	
+!send_goal(Goal) : true <-
	.send(robot_management, tell, goal(Goal,received)).
	
+!waitFact(Fact) : Fact=..[Pred,[P1,P2],[]] & jia.is_relation_in_onto(P1,Pred,P2,false,robot) <-
	true.
	
+!waitFact(Fact) :  
	Fact=..[Pred,[P1,P2],[]] 
	& .term2string(Pred,PredS)
	& monitoring(ID,Function,Subj,Prop,Obj,-1) 
	& (.substring("add",Function) | .substring("?",Function))  
	& (Subj==P1 | jia.is_of_class(individual,P1,Subj)) 
	& (PredS==Prop | jia.is_of_class(individual,PredS,Prop))
	& (P2==Obj | jia.is_of_class(individual,P2,Obj)) <-
	.wait(Fact).

+!waitFact(Fact) : Fact=..[Pred,[P1,P2],[]]  <-
	-toRemoveAfterwards(_);
	mementarSubscribe("?",P1,Pred,P2,-1);
	.term2string(Pred,PredS);
	?monitoring(ID,Function,P1,PredS,P2,-1);
	+toRemoveAfterwards(ID);
	.wait(Fact);
	-toRemoveAfterwards(ID);
	mementarUnsubscribe(ID).	
	
//TODO not dropped if comes from fork	
^!waitFact(Fact)[state(dropped)] : toRemoveAfterwards(ID) <- 
	-toRemoveAfterwards(ID);
	mementarUnsubscribe(ID).
	
//TODO idle human not removed
	
+!reset : true <-
	.drop_all_desires;
	rjs.jia.abolish_all_except_ground.
	
	