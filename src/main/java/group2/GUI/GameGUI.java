package group2.GUI;

import org.jspace.Space;
import org.jspace.ActualField;
import org.jspace.FormalField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;;


public class GameGUI extends JFrame implements ActionListener, Runnable {

	private Space GUISpace;
	private String username;

	private JPanel titlePanel;
	private JPanel matchPanel;
	private JPanel infoPanel;

	private JLabel player1Name;
	private JLabel player2Name;
	private JLabel player1Score;
	private JLabel player2Score;
	private JLabel winnerText;
	private JLabel queue;
	private JLabel scoreboard;
	private JLabel title;


	private JList<String> chatList;
	private JList<String> scoreboardList;
	private JList<String> queueList;

	private DefaultListModel<String> chatModel;
	private DefaultListModel<String> scoreboardModel;
	private DefaultListModel<String> queueModel;

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

        chatModel = new DefaultListModel<String>();
        chatList.setModel(chatModel);
        jScrollPane1.setViewportView(chatList);

        scoreboardModel = new DefaultListModel<String>();
        scoreboardList.setModel(scoreboardModel);
        jScrollPane2.setViewportView(scoreboardList);

        queueModel = new DefaultListModel<String>();
        queueList.setModel(queueModel);
        jScrollPane3.setViewportView(queueList);

        rock.addActionListener(this);
        paper.addActionListener(this);
        scissors.addActionListener(this);
		chatField.addActionListener(this);

        rock.setVisible(false);
        paper.setVisible(false);
        scissors.setVisible(false);
        winnerText.setVisible(false);

        setVisible(true);

    }

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		try {
			if (source == chatField){//Pressed enter key
				String message = chatField.getText();
				if (!message.isEmpty()) {
					GUISpace.put("ToClient", "Send message", chatField.getText());
					chatField.setText("");
				}
				return;
			}
			JButton pressed = (JButton) e.getSource();
			GUISpace.put("ToClient", "Move", new String[]{pressed.getText()});
		} catch (InterruptedException ignored) {}
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
						} else {
							rock.setVisible(false);
							paper.setVisible(false);
							scissors.setVisible(false);
						}
						if (name1.equals("disconnected")) {
							player1Name.setText("");
							player2Name.setText("");
						}
						break;
					case "Current score":
						String score1 = ((String[])tuple[2])[0];
						String score2 = ((String[])tuple[2])[1];

						player1Score.setText(score1);
						player2Score.setText(score2);
						break;
					case "Scoreboard":
						String map = (String)tuple[2];

						HashMap<String, Integer> points = new HashMap<String, Integer>();

						map = map.substring(1, map.length()-1);
						for (String item : map.split(", ")) {
							String key = item.split("=")[0];
							String value = item.split("=")[1];
							points.put(key, Integer.parseInt(value));
						}

						System.out.println("Points " + points.entrySet());

						scoreboardModel.clear();

						Map<String, Integer> sorted = points.entrySet().stream()
				                .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
				                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

						sorted.forEach((k, v) -> scoreboardModel.addElement(k + ":" + v));

						System.out.println("Updated scoreboard");
						break;
					case "New message":
						System.out.println(scoreboardModel.toString());
						chatModel.addElement((String)tuple[2]);
						break;
					case "Spectators":
						ArrayList<String> spectators = (ArrayList<String>)tuple[2];
						queueModel.clear();
						for (String user : spectators) {
							queueModel.addElement(user);
						}
						break;
					default:
						System.out.println("[GUI]Unknown tuple " + tuple[1]);
						break;
				}
			} catch (InterruptedException e) {}
			infoPanel.repaint();
			infoPanel.revalidate();
			matchPanel.repaint();
			matchPanel.revalidate();
		}
	}
	// Generated with Netbeans GUI helper
	private void makeWindow() {

		infoPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        scoreboardList = new javax.swing.JList<>();
        scoreboard = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        queueList = new javax.swing.JList<>();
        queue = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatList = new javax.swing.JList<>();
        chatField = new javax.swing.JTextField();
        matchPanel = new javax.swing.JPanel();
        player2Name = new javax.swing.JLabel();
        player2Score = new javax.swing.JLabel();
        winnerText = new javax.swing.JLabel();
        player1Score = new javax.swing.JLabel();
        rock = new javax.swing.JButton();
        paper = new javax.swing.JButton();
        player1Name = new javax.swing.JLabel();
        scissors = new javax.swing.JButton();
        titlePanel = new javax.swing.JPanel();
        title = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(800, 2147483647));
        setPreferredSize(new java.awt.Dimension(800, 716));

        javax.swing.GroupLayout infoPanelLayout = new javax.swing.GroupLayout(infoPanel);
        infoPanel.setLayout(infoPanelLayout);
        infoPanelLayout.setHorizontalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(queue)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scoreboard)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chatField, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(62, Short.MAX_VALUE))
        );
        infoPanelLayout.setVerticalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addComponent(scoreboard)
                .addGap(11, 11, 11)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(queue, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 461, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chatField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        player2Name.setText("Opponent name");

        player2Score.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        player2Score.setText("0");

        winnerText.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        winnerText.setText("You win!");

        player1Score.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        player1Score.setText("0");
		queue.setText("Spectators Queue");

        rock.setText("Rock");

        paper.setText("Paper");

        player1Name.setText("My name");

        scissors.setText("Scissors");

        javax.swing.GroupLayout matchPanelLayout = new javax.swing.GroupLayout(matchPanel);
        matchPanel.setLayout(matchPanelLayout);
        matchPanelLayout.setHorizontalGroup(
            matchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, matchPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(matchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(matchPanelLayout.createSequentialGroup()
                        .addGap(89, 89, 89)
                        .addComponent(player1Name))
                    .addGroup(matchPanelLayout.createSequentialGroup()
                        .addGap(83, 83, 83)
                        .addComponent(player2Name))
                    .addGroup(matchPanelLayout.createSequentialGroup()
                        .addGap(103, 103, 103)
                        .addGroup(matchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(player1Score)
                            .addComponent(player2Score, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(matchPanelLayout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(winnerText))
                    .addGroup(matchPanelLayout.createSequentialGroup()
                        .addComponent(rock)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(paper)
                        .addGap(18, 18, 18)
                        .addComponent(scissors)))
                .addContainerGap())
        );
        matchPanelLayout.setVerticalGroup(
            matchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, matchPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(matchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(matchPanelLayout.createSequentialGroup()
                        .addGap(423, 423, 423)
                        .addComponent(player1Name))
                    .addGroup(matchPanelLayout.createSequentialGroup()
                        .addComponent(player2Name)
                        .addGap(51, 51, 51)
                        .addComponent(player2Score)
                        .addGap(52, 52, 52)
                        .addComponent(winnerText)
                        .addGap(37, 37, 37)
                        .addComponent(player1Score)
                        .addGap(42, 42, 42)
                        .addGroup(matchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rock)
                            .addComponent(paper)
                            .addComponent(scissors))
                        .addGap(43, 43, 43)))
                .addContainerGap())
        );

        title.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        title.setText("Rock Paper Scissors Arcade Game");

        javax.swing.GroupLayout titlePanelLayout = new javax.swing.GroupLayout(titlePanel);
        titlePanel.setLayout(titlePanelLayout);
        titlePanelLayout.setHorizontalGroup(
            titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(titlePanelLayout.createSequentialGroup()
                .addComponent(title)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        titlePanelLayout.setVerticalGroup(
            titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(titlePanelLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(title)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addComponent(matchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(96, 96, 96)
                        .addComponent(infoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(220, 220, 220)
                        .addComponent(titlePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(509, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titlePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(infoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(matchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(49, 49, 49))
        );
	}
}
