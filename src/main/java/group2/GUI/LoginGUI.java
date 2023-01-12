package group2.GUI;
import org.jspace.Space;
import org.jspace.ActualField;
import org.jspace.FormalField;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;

public class LoginGUI extends JFrame implements ActionListener {
	// Spaces and swing components
	Space GUISpace;
	JTextField usernameTextField;
	JTextField ipTextField;
	JButton loginButton;
	Font  f1  = new Font(Font.SERIF, Font.PLAIN,  24);
	boolean hasIP = false;

	// Constructor for login GUI
	public LoginGUI(Space GUISpace) {
		this.GUISpace = GUISpace;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(102, 102, 102));
		setTitle("Rock Paper Scissors Arcade!");
        setSize(600, 400);
        setResizable(false);


		JLabel jLabel1 = new JLabel();
        ipTextField = new JTextField();
        usernameTextField = new JTextField();
        loginButton = new JButton();
        JLabel jLabel2 = new JLabel();
        JLabel jLabel3 = new JLabel();

        jLabel1.setFont(f1);
        jLabel1.setForeground(new java.awt.Color(255, 0, 255));
        jLabel1.setText("Rock Paper Scissors Arcade");


        loginButton.setText("Login");
        loginButton.addActionListener(this);

        jLabel2.setForeground(new Color(255, 0, 255));
        jLabel2.setText("IP:");

        jLabel3.setForeground(new Color(255, 0, 255));
        jLabel3.setText("Username:");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(loginButton, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE)
                .addGap(83, 83, 83))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(usernameTextField, GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                            .addComponent(ipTextField)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(149, 149, 149)
                        .addComponent(jLabel1)))
                .addContainerGap(165, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE)
                .addGap(111, 111, 111)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(ipTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(usernameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                .addComponent(loginButton, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String usernameInput = usernameTextField.getText();
		String ipInput = ipTextField.getText();
		try {
			if (!hasIP) {
				GUISpace.put("IP", ipInput);
				String res = (String)GUISpace.get(new ActualField("IP Response"), new FormalField(String.class))[1];
				if (res.equals("Fail")) {
					//CREATE DIALOGBOX
					System.out.println("Ip bad");
					return;
				}
				hasIP = true;
				System.out.println("Ip good");
			}
			
			GUISpace.put("Username", usernameInput);
			String res = (String)GUISpace.get(new ActualField("Name Response"), new FormalField(String.class))[1];
			if (res.equals("Fail")) {
				//CREATE DIALOGBOX
				System.out.println("Name bad");
				return;
			}

			System.out.println("Name good");
			//Open next GUI
			

		} catch (InterruptedException ie) {}
	}



}