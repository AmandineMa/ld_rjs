{ include("action_monitoring.asl")}

!start.

+!start : true <-
	rjs.jia.log_beliefs;
	.verbose(2);
//	++action(1, "pick", "Helmet_2", ["cube_BBCG"]);
//	.wait(2000);
//	+possibleFinishedActions([pick("Helmet_2","cube_BBCG")]).
	!setActionsMementarMonitoring.

+!setActionsMementarMonitoring : true <-
	.findall(X,action(_,_,Y,_,_) & .member(X,Y),L);
	!setMementarMonitoring(L);
	.findall(X,action(_,_,_,Z,_) & .member(X,Z),M);
	!setMementarMonitoring(M);
	.findall(X,action(_,_,_,_,W) & .member(X,W),N);
	!setMementarMonitoring(N).

+!setMementarMonitoring(BelList) : true <-
	for(.member(B,BelList)){
		B=..[Pred,[T1,T2],[]];
		if(rjs.jia.unnamedvar2string(T2,T) & .substring("List",T)){
			Function="?";
		}elif(rjs.jia.negated_literal(Pred)){
			Function="del";
		}else{
			Function="add";
		}
		.term2string(Pred,PredS);
		.delete("~",PredS,PredF)
		if(not rjs.jia.believes(monitoring(_,Function,"?",PredF,"?",_))){
			mementarSubscribe(Function,"?",PredF,"?",-1);
		};
	}.
	
+action(ID,Name,Agent,Params) : true <-
	.wait(possibleFinishedActions(A) & .member(M,A) & M=..[Pred,[T1,T2],[]] & .term2string(N,Name) & Pred==N & T1==Agent & .nth(0,Params,P) & T2==P);
	.print(hey).
	