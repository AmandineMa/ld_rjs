package dir;

import java.util.logging.Level;
import java.util.logging.Logger;

import jason.asSemantics.Agent;
import jason.asSyntax.Literal;
import jason.asSyntax.Plan;
import jason.asSyntax.PlanBody;
import jason.asSyntax.PlanBodyImpl;
import jason.asSyntax.Pred;
import jason.asSyntax.PlanBody.BodyType;
import jason.asSyntax.directives.DefaultDirective;
import jason.asSyntax.directives.Directive;

public class HumanPresenceDir extends DefaultDirective implements Directive {

    static Logger logger = Logger.getLogger(HumanPresenceDir.class.getName());

    public Agent process(Pred directive, Agent outerContent, Agent innerContent) {
        try {
            Agent newAg = new Agent();
            newAg.initAg();
            PlanBody pb = outerContent.getPL().get("whls").getBody();
            for (Plan p: innerContent.getPL()) {
                p.getBody().add(0,pb.clonePB());
                
                Literal unsetHeadBuff = Literal.parseLiteral("setHMBuff([human_monitoring,normal])");
                PlanBody b2 = new PlanBodyImpl(BodyType.action, unsetHeadBuff);
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
