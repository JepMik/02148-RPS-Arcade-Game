package group2.GUI;
import org.jspace.Space;
import org.jspace.ActualField;
import org.jspace.FormalField;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.*;

public class LoginGUI extends JFrame implements ActionListener {
	// Spaces and swing components
	Space GUISpace;
	JTextField usernameTextField;
	JTextField ipTextField;
	JButton loginButton;
	Font  f1  = new Font(Font.SERIF, Font.PLAIN,  24);
	boolean hasIP = false;
	Image icon;

	// Constructor for login GUI
	public LoginGUI(Space GUISpace) {
		this.GUISpace = GUISpace;

		// Frame init
		Font font = new java.awt.Font("Segoe UI", 0, 34);
		Font font1 = new java.awt.Font("Segoe UI", 0, 26);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(102, 102, 102));
		setTitle("Rock Paper Scissors Arcade!");
        setSize(600, 400);
        setResizable(false);

		ImageIcon img = new ImageIcon("src/resources/rps_logo.jpeg");
		setIconImage(img.getImage());

		// Labels, Buttons and Text fields
		JLabel jLabel1 = new JLabel();
		jLabel1.setFont(f1);
		jLabel1.setForeground(new Color(255, 0, 255));
		jLabel1.setText("Rock Paper Scissors Arcade");
		jLabel1.setFont(font);

		JLabel jLabel2 = new JLabel();
		jLabel2.setForeground(new Color(255, 0, 255));
		jLabel2.setText("IP:");
		jLabel2.setFont(font1);

		JLabel jLabel3 = new JLabel();
		jLabel3.setForeground(new Color(255, 0, 255));
		jLabel3.setText("Username:");
		jLabel3.setFont(font1);

		ipTextField = new JTextField();
        usernameTextField = new JTextField();
        loginButton = new JButton();
		loginButton.setText("Login");
        loginButton.addActionListener(this);


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
		pack();
		setLocationRelativeTo(null);

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
					showErrIp();
					return;
				}
				hasIP = true;
				ipTextField.setEnabled(false);
			}

			GUISpace.put("Username", usernameInput);
			String res = (String)GUISpace.get(new ActualField("Name Response"), new FormalField(String.class))[1];
			if (res.equals("Fail")) {
				//CREATE DIALOGBOX
				showErrName();
				return;
			}
			//Close winddow
			dispose();
			

		} catch (InterruptedException ie) {}
	}
	// Methods for JOptionPane, shows errors
	private void showErrName(){
		JOptionPane.showMessageDialog(null,"All names must be different!","Invalid names",JOptionPane.ERROR_MESSAGE);
	}
	private void showErrIp(){
		JOptionPane.showMessageDialog(null,"Ip is not valid","Invalid Ip",JOptionPane.ERROR_MESSAGE);
	}
}



