// Agent receiver in project director_task


+goal(Name, State) : State == received & .substring("dtRR",Name) <- 
	+receiver.
	
+sentence(Sentence) : receiver <-
	analyzeSentence(Sentence);
	disambiSentence.