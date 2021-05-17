package jia;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import rjs.arch.agarch.AbstractROSAgArch;

public class createGUI extends DefaultInternalAction {

	@SuppressWarnings("unchecked")
	@Override
	public Object execute(final TransitionSystem ts, Unifier un, Term[] args) throws Exception {

		final JButton scan = new JButton("Head Scan");
		JPanel buttons = new JPanel();
		buttons.add(scan);
		buttons.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Head scan"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

		// add the event listeners
		scan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// creates a new event +!head_scan so that the agent can react to the button
				ts.getC().addAchvGoal(Literal.parseLiteral("head_scan"), null);
			}
		});
		
		final JButton sendGoal = new JButton("Send goal");
		
		Map<?,?> goalsMap = ((AbstractROSAgArch) ts.getAgArch()).getRosnode().getParameters().getMap("plan_manager/goals");
		final DefaultListModel<String> goalsList = new DefaultListModel<>();  
		goalsList.addAll((Collection<? extends String>) goalsMap.keySet());
		final JList<String> list = new JList<>(goalsList);  
		
		// add the event listeners
		sendGoal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(list.getSelectedIndex() != -1) {
					// creates a new event +!send_goal so that the agent can react to the button
					ts.getC().addAchvGoal(Literal.parseLiteral("send_goal("+list.getSelectedValue()+")"), null);
				}
			}
		});
		
	
		JPanel goal = new JPanel();
		goal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Goal management"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
		goal.setLayout(new BoxLayout(goal, BoxLayout.LINE_AXIS));
		goal.add(list);
		goal.add(sendGoal);
		
		final JButton reset = new JButton("Reset");
		JPanel resetP = new JPanel();
		resetP.add(reset);
		resetP.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Reset managers"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

		// add the event listeners
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// creates a new event +!head_scan so that the agent can react to the button
				ts.getC().addAchvGoal(Literal.parseLiteral("reset"), null);
			}
		});
		
		JPanel mainPane = new JPanel();
		mainPane.add(buttons);
		mainPane.add(goal);
		mainPane.add(resetP);
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.PAGE_AXIS));
		mainPane.setOpaque(true);
		
		JFrame frame = new JFrame("Supervisor controller");
		frame.setContentPane(mainPane);
		frame.pack();
		frame.setVisible(true);

		return true;
	}
}
