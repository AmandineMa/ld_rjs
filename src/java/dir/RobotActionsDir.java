package dir;

import java.util.logging.Level;
import java.util.logging.Logger;

import jason.asSemantics.Agent;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.Plan;
import jason.asSyntax.PlanBody;
import jason.asSyntax.PlanBody.BodyType;
import jason.asSyntax.PlanBodyImpl;
import jason.asSyntax.Pred;
import jason.asSyntax.directives.DefaultDirective;
import jason.asSyntax.directives.Directive;

public class RobotActionsDir extends DefaultDirective implements Directive {

    static Logger logger = Logger.getLogger(RobotActionsDir.class.getName());

    public Agent process(Pred directive, Agent outerContent, Agent innerContent) {
        try {
            Agent newAg = new Agent();
            newAg.initAg();
            for (Plan p: innerContent.getPL()) {
            	//TODO for now always first argument of Params but it might change, be careful
            	// Add at the beginning
            	Literal nth;
            	if(p.getLabel().getFunctor().contains("place"))
            		nth = Literal.parseLiteral(".nth(1, Params, Obj)");
            	else
            		nth = Literal.parseLiteral(".nth(0, Params, Obj)");
                PlanBody b1 = new PlanBodyImpl(BodyType.internalAction, nth);
                p.getBody().add(0,b1);
                
                Literal setHead = Literal.parseLiteral("setHMAtemp(Obj,environment_monitoring,urgent)");
                PlanBody b1bis = new PlanBodyImpl(BodyType.action, setHead);
                p.getBody().add(1,b1bis);
                
                Literal setHeadBuff = Literal.parseLiteral("setHMBuff([environment_monitoring,prioritize]);");
                PlanBody b1ter = new PlanBodyImpl(BodyType.action, setHeadBuff);
                p.getBody().add(2,b1ter);
                
                Literal setWait = Literal.parseLiteral(".wait(1000);");
                PlanBody b1quar = new PlanBodyImpl(BodyType.internalAction, setWait);
                p.getBody().add(2,b1quar);
                
                // Add at the end
//                Literal unsetHead = Literal.parseLiteral("setHMAtemp(Obj,environment_monitoring,void)");
//                PlanBody b2 = new PlanBodyImpl(BodyType.action, unsetHead);
//                p.getBody().add(b2);
//                
//                Literal unsetHeadBuff = Literal.parseLiteral("setHMBuff([environment_monitoring,normal])");
//                PlanBody b2bis = new PlanBodyImpl(BodyType.action, unsetHeadBuff);
//                p.getBody().add(b2bis);

                newAg.getPL().add(p);
                
                Literal planL = p.getTrigger().getLiteral();
                newAg.getPL().add(ASSyntax.parsePlan("-!"+planL
                		+"[Failure, code(Code),code_line(_),code_src(_),error(Error),error_msg(Msg)]: .substring(plan,Code) <- +error_msg(Msg);!"+planL+"."))
                	.setSourceFile(outerContent.getASLSrc());
                
                newAg.getPL().add(ASSyntax.parsePlan("-!"+planL +": true <-"
                		+ " ?error_msg(Msg);"
                		+ "-error_msg(Msg);"
                		+ "rjs.jia.fail_goal_with_annots(executeAction(_,_,_,_),[error_msg(Msg)])."))
                	.setSourceFile(outerContent.getASLSrc());
                
            }
            return newAg;
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Directive error.", e);
        }
        return null;
    }
}
