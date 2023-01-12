package group2.GUI;
import org.jspace.Space;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameGUI extends JFrame implements ActionListener {
    Space GUISpace;

    GameGUI(Space GameGUI){
        this.GUISpace = GameGUI;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Takes playing input of the playing players.
        // Updates GUI when winner is found

    }
}
