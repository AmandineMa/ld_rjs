package jia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import arch.agarch.LAASAgArch;
import jason.asSemantics.ActionExec;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Literal;
import jason.asSyntax.StringTermImpl;
import jason.asSyntax.Term;
import rjs.arch.actions.AbstractAction;
import rjs.arch.agarch.AbstractROSAgArch;
import rjs.utils.Tools;

public class action_verbalization extends DefaultInternalAction {

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		String humanName = Tools.removeQuotes(ts.getAg().findBel(Literal.parseLiteral("humanName(_))"),un).getTerm(0).toString());
		String robotName = Tools.removeQuotes(ts.getAg().findBel(Literal.parseLiteral("robotName(_))"),un).getTerm(0).toString());
		String actionAgent = Tools.removeQuotes(args[0].toString()); 
		String action = Tools.removeQuotes(args[1].toString()); 
		List<String> params = Tools.removeQuotes((ListTermImpl) args[2]); 
		String tense = Tools.removeQuotes(args[3].toString()); 

		List<String> actionVerbaList = classVerbalization(ts, action, "HtnAction");
		if(!actionVerbaList.isEmpty()) {
			String actionVerba = fillParameters(ts, actionVerbaList, params, humanName, robotName);
			String person = "ThirdSingularPersonalForm";
			String pronoun = "";
			if(actionAgent.equals(robotName)) {
				person = "FirstSingularPersonalForm";
				pronoun = "I";
			} else if(actionAgent.equals(humanName)) {
				person = "SecondSingularPersonalForm";
				pronoun = "you";
			}
			int verbPose = actionVerba.indexOf("@")+1;
			String verb = actionVerba.substring(verbPose, actionVerba.indexOf(" ", verbPose));
			String verbTense = verb + tense;

			List<String> verbConjugationNameList = ((LAASAgArch) ts.getAgArch()).callOntoClass("getDown",verbTense+" -s "+person).getValues();
			String verbConjugation;
			if(!verbConjugationNameList.isEmpty()) {
				verbConjugation = classVerbalization(ts, verbConjugationNameList.get(0), "LanguageVerb").get(0);
			} else {
				verbConjugation = verb;
			}
			actionVerba = actionVerba.replace("{Agent}", pronoun).replace("@"+verb, verbConjugation);
			return un.unifies(args[4], new StringTermImpl(actionVerba));
		}
		return false;
	}

	private List<String> classVerbalization(TransitionSystem ts, String className, String type) {
		List<String> classNameList = ((LAASAgArch) ts.getAgArch()).callOntoClass("getNames",className+" -i false").getValues();
		while(classNameList.isEmpty()) {
			List<String> upList = ((LAASAgArch) ts.getAgArch()).callOntoClass("getUp",className+" -d 1 -s "+type).getValues();

			if(upList.size() <= 1)
				break;

			for(String up : upList) {
				if(!up.equals(className)) {
					className = up;
					break;
				}
			}
			classNameList = ((LAASAgArch) ts.getAgArch()).callOntoClass("getNames",className+" -i false").getValues();
		}
		return classNameList;
	}
	
	private String fillParameters(TransitionSystem ts, List<String> actionVerbaList, List<String> params, String humanName, String robotName) {
		String actionVerba = "";
		for(int i = 0; i < actionVerbaList.size() && actionVerba.isBlank(); i++) {
			Pattern p = Pattern.compile("\\{(?!Agent)(.*?)\\}");
			Matcher m = p.matcher(actionVerbaList.get(i));
			ArrayList<String> regexGroups = new ArrayList<String>();
			while(m.find()) {
				regexGroups.add(m.group(1));
			}
			if(regexGroups.size() == params.size()) {
				for(String param : params) {
					for(String className : regexGroups) {
						List<String> sameClass = ((LAASAgArch) ts.getAgArch()).callOntoIndiv("getUp",param+" -s "+className).getValues();
						if(!sameClass.isEmpty()) {
							if(actionVerba.isBlank())
								actionVerba = actionVerbaList.get(i);
							actionVerba = actionVerba.replace("{"+className+"}",entityVerbalization(ts,param,robotName,humanName));
							break;
						}
					}
				}
			}
		}
		return actionVerba;
	}
	

	private String entityVerbalization(TransitionSystem ts, String entity, String robotName, String humanName) {
		List<String> paramSparql = ((LAASAgArch) ts.getAgArch()).getEntitySparql(Tools.removeQuotes(entity), robotName, false);
		return ((LAASAgArch) ts.getAgArch()).sparqlToVerba(paramSparql,humanName);
	}

}
