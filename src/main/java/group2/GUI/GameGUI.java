package group2.GUI;

import org.jspace.Space;
import org.jspace.ActualField;
import org.jspace.FormalField;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.Color;

public class GameGUI extends JFrame implements ActionListener, Runnable {

	private Space GUISpace;
	private String username;

	private JLabel player1Name;
	private JLabel player2Name;
	private JLabel player1Score;
	private JLabel player2Score;
	private JLabel winnerText;
	private JList<String> chatWindow;
	private JList<String> scoreboardWindow;
	private JList<String> queueWindow;

	private JScrollPane jScrollPane1;
	private JScrollPane jScrollPane2;
	private JScrollPane jScrollPane3;

	private JTextField chatField;
	private JButton rock;
	private JButton paper;
	private JButton scissors;

	private boolean inGame = false;

    @SuppressWarnings("unchecked")
    public GameGUI(Space GUISpace, String username) {
		this.GUISpace = GUISpace;
		this.username = username;


        setSize(800, 800);
        setResizable(false);
        setTitle("Game on!");
        getContentPane().setBackground(new Color(102, 102, 102));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ImageIcon img = new ImageIcon("resources\rps_logo.jpeg");
        setIconImage(img.getImage());

		makeWindow();

        player2Name.setText("Player1");
        player1Name.setText("Player2");

        rock.setText("Rock");
        paper.setText("Paper");
        scissors.setText("Scissors");

        rock.addActionListener(this);
        paper.addActionListener(this);
        scissors.addActionListener(this);

        rock.setVisible(false);
        paper.setVisible(false);
        scissors.setVisible(false);
        winnerText.setVisible(false);


        setVisible(true);
    }

	public void actionPerformed(ActionEvent e)  {
		JButton pressed = (JButton)e.getSource();
		try {
			GUISpace.put("ToClient", "Move", new String[]{pressed.getText()});
		} catch (InterruptedException ex) {}
	}

	public void run() {
		while (true) {
			try {
				Object[] tuple = GUISpace.get(new ActualField("ToGui"), new FormalField(String.class), new FormalField(Object.class));
				switch ((String)tuple[1]) {
					case "Playing against":
						String name1 = ((String[])tuple[2])[0];
						String name2 = ((String[])tuple[2])[1];
						// update myName and opponentName fields in JFrame
						player1Name.setText(name1);
						player2Name.setText(name2);
						if (name1.equals(username)) {
							rock.setVisible(true);
							paper.setVisible(true);
							scissors.setVisible(true);
						}
						break;
					case "Current score":
						int score1 = ((int[])tuple[2])[0];
						int score2 = ((int[])tuple[2])[1];
						// TODO: update myScore and opponentScore fields in JFrame
						player1Score.setText("" + score1);
						player2Score.setText("" + score2);
                    default:
						System.out.println("Unknown tuple " + tuple[1]);
						break;
				}
			} catch (InterruptedException e) {}

		}
	}

	private void makeWindow() {
        jScrollPane1 = new JScrollPane();
		chatWindow = new JList<>();
		jScrollPane2 = new JScrollPane();
		scoreboardWindow = new JList<>();
		jScrollPane3 = new JScrollPane();
		queueWindow = new JList<>();
		JLabel scoreboard = new JLabel();
		JLabel queue = new JLabel();
		chatField = new JTextField();
		JLabel title = new JLabel();
		player2Name = new JLabel();
		player1Name = new JLabel();
		rock = new JButton();
		paper = new JButton();
		scissors = new JButton();
		player1Score = new JLabel();
		player2Score = new JLabel();
		winnerText = new JLabel();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		scoreboard.setText("Scoreboard");

		queue.setText("Queue");

		title.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
		title.setText("Rock Paper Scissors Arcade Game");


		player1Score.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
		player1Score.setText("0");

		player2Score.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
		player2Score.setText("0");

		winnerText.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
		winnerText.setText("You win!");

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
		    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		    .addGroup(layout.createSequentialGroup()
		        .addContainerGap(195, Short.MAX_VALUE)
		        .addComponent(title)
		        .addGap(192, 192, 192))
		    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
		        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		            .addGroup(layout.createSequentialGroup()
		                .addGap(159, 159, 159)
		                .addComponent(player1Name))
		            .addGroup(layout.createSequentialGroup()
		                .addGap(91, 91, 91)
		                .addComponent(rock)
		                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		                    .addComponent(player2Name)
		                    .addGroup(layout.createSequentialGroup()
		                        .addComponent(paper)
		                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
		                        .addComponent(scissors))))
		            .addGroup(layout.createSequentialGroup()
		                .addGap(173, 173, 173)
		                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		                    .addComponent(player1Score)
		                    .addComponent(player2Score, GroupLayout.Alignment.TRAILING)))
		            .addGroup(layout.createSequentialGroup()
		                .addGap(119, 119, 119)
		                .addComponent(winnerText)))
		        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
		                .addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
		                .addComponent(jScrollPane2))
		            .addComponent(scoreboard)
		            .addComponent(queue))
		        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
		            .addComponent(jScrollPane1)
		            .addComponent(chatField, GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE))
		        .addGap(40, 40, 40))
		);
		layout.setVerticalGroup(
		    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		    .addGroup(layout.createSequentialGroup()
		        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		            .addGroup(layout.createSequentialGroup()
		                .addComponent(title)
		                .addGap(141, 141, 141)
		                .addComponent(player2Name)
		                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		                    .addGroup(layout.createSequentialGroup()
		                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 461, GroupLayout.PREFERRED_SIZE)
		                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                        .addComponent(chatField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		                    .addGroup(layout.createSequentialGroup()
		                        .addGap(51, 51, 51)
		                        .addComponent(player2Score)
		                        .addGap(52, 52, 52)
		                        .addComponent(winnerText)
		                        .addGap(37, 37, 37)
		                        .addComponent(player1Score)
		                        .addGap(0, 0, Short.MAX_VALUE))))
		            .addGroup(layout.createSequentialGroup()
		                .addGap(195, 195, 195)
		                .addComponent(scoreboard)
		                .addGap(18, 18, 18)
		                .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 194, GroupLayout.PREFERRED_SIZE)
		                .addGap(18, 18, 18)
		                .addComponent(queue)
		                .addGap(12, 12, 12)
		                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
		                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		                            .addComponent(rock)
		                            .addComponent(paper)
		                            .addComponent(scissors))
		                        .addGap(18, 18, 18)
		                        .addComponent(player1Name)
		                        .addGap(66, 66, 66))
		                    .addComponent(jScrollPane3, GroupLayout.PREFERRED_SIZE, 209, GroupLayout.PREFERRED_SIZE))))
		        .addContainerGap(32, Short.MAX_VALUE))
		);
	}
}
