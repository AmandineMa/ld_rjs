// Agent receiver in project director_task
actionCounter(0).

+goal(Name, State) : State == received & .substring("dtRR",Name) <- 
	+receiver.
	
+sentence(Sentence) : receiver <-
	!analyzeSentence(Sentence);
	if(rjs.jia.believes(match(_))){
		disambiSentence;
	}.
	
+!analyzeSentence(Sentence) : mergedQuery(Query) <-
	analyzeSentence(Sentence, Query).

+!analyzeSentence(Sentence) : true <-
	analyzeSentence(Sentence, ["?0 isAbove table_1"]).
	
+!removeBeliefs : true <-
	-mergedQuery(_);
	-sparql_input(_);
	-match(_);
	-sentence(_);
	-verba(disambi_objects_sentence,_).
	
+verba(disambi_objects_sentence, DisambiSentence): true <-
	.concat("Do you mean ", DisambiSentence, "?", Sentence);
	say(Sentence).
	
	
+verba(not_understood, NUSentence) : true <-
	.concat("I did not understand ", NUSentence, Sentence);
	say(Sentence).
	
+action("todo",Name,Agent,Params) : robotName(Agent) & .length(Name) > 0 <-
	?actionCounter(I);
	-+actionCounter(I+1);
	.send(robot_executor, tell, action(I,Name,Agent,Params)).
	
+action("todo",Name,Agent,Params) : .length(Name) == 0 <-
	.nth(0, Params, Object);
	.concat("I don't know what to do with ", Object, Sentence);
	say(Sentence).
