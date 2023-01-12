package group2.GUI;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Lobby {
	
	JFrame screen;
	Container con;
	JLabel textLabel;
	JPanel textPanel;
	JPanel inputPanel;
	JTextField tf;
	JButton button;

	
	Font  f1  = new Font(Font.SERIF, Font.PLAIN,  23);
	
	
	public static void main(String[] args) {
		new Lobby();
				
	}
	
	
	public Lobby() {
		
	
		screen = new JFrame();
		screen.setSize(700, 600);	
		screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		screen.getContentPane().setBackground(Color.black);
		screen.setLayout(null);
		
		con = screen.getContentPane(); 
		
		
		
		textPanel = new JPanel();
		textPanel.setBounds(140,240,500,100);
		textPanel.setBackground(Color.white);
		con.add(textPanel);
		
		
		//Only shows the black window with a label for entering the "username"
		
		textLabel = new JLabel("Please enter your username: ");
		textLabel.setForeground(Color.black);
		textLabel.setFont(f1);
		textPanel.add(textLabel);
		
		
		//Able to enter the username
		
		inputPanel = new JPanel();
		inputPanel.setBounds(150,420,500,50);
		inputPanel.setBackground(Color.black);
		inputPanel.setLayout(new GridLayout(1,2));
		
		tf = new JTextField();
		inputPanel.add(tf);
		
		//button for entering username
		button = new JButton("Enter");
		button.setForeground(Color.black);
		inputPanel.add(button);
		con.add(inputPanel);
		
		screen.setVisible(true);
		
		//TODO: create an input handler to go to next phase after entering the name
		
		
		
		
	}
}