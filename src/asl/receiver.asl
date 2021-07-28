// Agent receiver in project director_task
/* Test with dt_setup_1
BGTB "take the green block with a triangle"
GGCB "take the green block with a green border"
BGCB "take the green block with a circle"
     -> the one with a green circle or with a blue circle ?"
          -> "with a green circle" */
          
actionCounter(0).

+goal(Name, State) : State == received & .substring("dtRR",Name) <- 
	.concat("plan_manager/goals/",Name,"/container",Container);
	rjs.jia.get_param(Container, "String", C);
	setHMBuff([environment_monitoring,human_monitoring],[normal,normal]);
	setHMAtemp("",environment_monitoring,void);
	+container(C);
	+receiver;
	.send(robot_executor, tell, container(C));
	.send(communication, tell, receiver).
	
+sentence(Sentence) : receiver <-
	!analyzeSentence(Sentence);
	if(rjs.jia.believes(match(_))){
		disambiSentence;
	}.
	
+!analyzeSentence(Sentence) : mergedQuery(Query) <-
	analyzeSentence(Sentence, Query).

+!analyzeSentence(Sentence) : true <-
	analyzeSentence(Sentence, ["?0 isAbove table_1"]).
	
+verba(disambi_objects_sentence, DisambiSentence): true <-
	.concat("Do you mean ", DisambiSentence, "?", Sentence);
	 .send(communication,askOne,talk(Sentence),Answer).
	
	
+verba(not_understood, NUSentence) : true <-
	.concat("I did not understand ", NUSentence, Sentence);
	.send(communication,askOne,talk(Sentence),Answer).
	
+action("todo",Name,Agent,Params) : robotName(Agent) & .length(Name) > 0 <-
	?actionCounter(I);
	-+actionCounter(I+1);
	.send(robot_executor, tell, action(I,Name,Agent,Params)).
	
+action("todo",Name,Agent,Params) : .length(Name) == 0 <-
	.nth(0, Params, Object);
	.concat("I don't know what to do with ", Object, Sentence);
	.send(communication,askOne,talk(Sentence),Answer).
