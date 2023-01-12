package group2.GUI;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Lobby {
	
	JFrame screen;
	Container con;
	JLabel textLabel;
	JPanel textPanel;

	
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
		textPanel.setBackground(Color.blue);
		con.add(textPanel);
		
		
		//Only shows the black window with a label for entering the "username"
		
		textLabel = new JLabel("Please enter your username: ");
		textLabel.setForeground(Color.black);
		textLabel.setFont(f1);
		textPanel.add(textLabel);
		screen.setVisible(true);
		
		//TODO: Able to enter the username
		
		
	}
}