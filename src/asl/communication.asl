{ include("common.asl")}
!start.


+!start : true <-
	.verbose(2);
	rjs.jia.log_beliefs;
	!initRosComponents;
	!getAgentNames;
	?humanName(HName);
    ?robotName(Robot);
	mementarSubscribe("?",Robot,isPerceiving,HName,-1).

// Plan added at the beginning of each plan of hpd	
@whls
+!waitHumanListeningState : true <-
	!wait.	
	
+!wait : humanName(HName) & robotName(Robot)  <-
//	.wait(isPerceiving(Robot,HName));
	setHMBuff([human_monitoring,prioritize]);
	!dispatchAction.

+!wait : true <- !dispatchAction.

+!dispatchAction : action(ID,"todo",Name,Agent,Params)[source(_)] <-
	-action(ID,"todo",Name,Agent,Params)[source(_)];
	+action(ID,"ongoing",Name,Agent,Params);
	.send([robot_executor,human_management], tell, action(ID,"ongoing",Name,Agent,Params)).
	
+!dispatchAction : true.	

+!drop(G) : true <-
	!reset.

//plans with directive hdp which adds the plan @whls at the beginning of each inner plan and also lower human_monitoring at the end
{begin hpd}
@iae[atomic]
+?informActionExecuted(Name,Params)[source(ASLAgent)] : robotName(Robot) <-
	jia.action_verbalization(Robot,Name,Params,"SimplePast",ActionVerba);
	say(inform,["executed",Name,Params],ActionVerba);
	.send(ASLAgent,tell,informed(executed,Name,Params));
	//TODO robot_congratulate ongoing is not removed
	-action(ID,"ongoing",Name,Agent,Params).
	
@iat[atomic]
+?askActionsTodo(ActionList,Operator)[source(ASLAgent)] : humanName(Human) <-
	for(.member(A,ActionList)){
		A =.. [P,[ID,Name,Params],[]];
		jia.action_verbalization(Human,Name,Params,"SimplePresent",ActionVerba);
		if(not rjs.jia.believes(sentenceToBuild(_))){
			.concat("Can ",ActionVerba,Sentence);
		}else{
			?sentenceToBuild(SentenceX);
			.concat(SentenceX," ",Operator," ",ActionVerba,Sentence);
		}
		-+sentenceToBuild(Sentence);
	}
	?sentenceToBuild(Sentence);
	-sentenceToBuild(Sentence);
	say(ask,["todo",ActionList,Operator],Sentence);
	.send(ASLAgent,tell,asked(todo,ActionList));
	-action(ID,"ongoing",Name,Agent,Params).

@as[atomic]	
+?askStop(Answer)[source(ASLAgent)] : true <-
	say(ask,[stop_task],"You're not acting, should we stop the task ?");
	.wait(sentence(Answer));
	.send(ASLAgent,tell,asked(stop)).
	
@talk[atomic]	
+?talk(Sentence)[source(ASLAgent)]: true <- 
	say(inform,[],Sentence);
	.send(ASLAgent,tell,informed(Sentence)).
{end}

+sentence(Sentence) : receiver[source(_)] <-
	-sentence(_);
	.send(robot_management,tell,sentence(Sentence)).

+sentence(Sentence) : .substring("cannot",Sentence) <-
	.send(human_management,tell,said(cannot_do)).
	
	