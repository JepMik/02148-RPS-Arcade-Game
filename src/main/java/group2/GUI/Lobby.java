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
	JLabel InputLabel;
	JPanel InputPanel;

	
	Font  f1  = new Font(Font.SERIF, Font.PLAIN,  23);
	
	public static void main(String[] args) {
		new Lobby();
		
		
	}
	
	
	public Lobby() {
		


		
		
		screen = new JFrame();
		screen.setSize(800, 600);	
		screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		screen.getContentPane().setBackground(Color.black);
		screen.setLayout(null);
		
		con = screen.getContentPane(); 
		
		InputPanel = new JPanel();
		InputPanel.setBounds(150,250,500,100);
		InputPanel.setBackground(Color.black);
		con.add(InputPanel);
		
		
		InputLabel = new JLabel("Please enter your username: ");
		InputLabel.setForeground(Color.white);
		InputLabel.setFont(f1);
		InputPanel.add(InputLabel);
		screen.setVisible(true);
		

		
				

		
		
	}
}