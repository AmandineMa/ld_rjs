package dir;

import java.util.logging.Level;
import java.util.logging.Logger;

import jason.asSemantics.Agent;
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
            	Literal nth = Literal.parseLiteral(".nth(0, Params, O)");
                PlanBody b1 = new PlanBodyImpl(BodyType.internalAction, nth);
                p.getBody().add(0,b1);
                
                Literal setHead = Literal.parseLiteral("setHMAtemp(O,environment_monitoring,urgent)");
                PlanBody b1bis = new PlanBodyImpl(BodyType.action, setHead);
                p.getBody().add(1,b1bis);

                Literal unsetHead = Literal.parseLiteral("setHMAtemp(O,environment_monitoring,void)");
                PlanBody b2 = new PlanBodyImpl(BodyType.action, unsetHead);
                p.getBody().add(b2);

                newAg.getPL().add(p);
            }
            return newAg;
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Directive error.", e);
        }
        return null;
    }
}
