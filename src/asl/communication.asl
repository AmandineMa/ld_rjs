{ include("common.asl")}
!start.

+!start : true <-
	.verbose(2);
	 rjs.jia.log_beliefs;
	!getAgentNames.


@iae[atomic]
+?informActionExecuted(Name,Params)[source(ASLAgent)] : robotName(Robot) <-
	.concat("I did ",Name," with ",Params,Sentence);
	say(inform,["executed",Name,Params],Sentence);
	.send(ASLAgent,tell,informed(executed,Name,Params)).
	
@iat[atomic]
+?askActionsTodo(ActionList)[source(ASLAgent)] : robotName(Robot) <-
	for(.member(A,ActionList)){
		A =.. [P,[ID,Name,Params],[]];
		if(not rjs.jia.believes(sentenceToBuild(_))){
			.concat("Can you ",Name,SentenceIntermediate);
		}else{
			?sentenceToBuild(SentenceX);
			.concat(SentenceX," or ",Name,SentenceIntermediate);
		}
		if(not .empty(Params)){
			.concat(SentenceIntermediate," with ",Params,Sentence);
		}else{
			Sentence=SentenceIntermediate;	
		}
		-+sentenceToBuild(Sentence);
	}
	?sentenceToBuild(Sentence);
	-sentenceToBuild(Sentence);
	say(ask,["todo",ActionList],Sentence);
	.send(ASLAgent,tell,asked(todo,ActionList)).

@rc[atomic]	
+!robot_congratulate(Params): true <- say("bravo ! we did it !").