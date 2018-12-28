package ui.view;

import ui.view.closedloop.ClosedLoopUiManager;

import javax.swing.*;

public class MainManager {
    public void start() {
        JFrame frame = new JFrame();
        frame.setTitle("MAF Scaler");
        frame.setSize(800, 800);
        frame.add(getTabbedPane());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private JTabbedPane getTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Closed Loop", null, new ClosedLoopUiManager().getClosedLoopPanel(), "Closed Loop Fueling");
        tabbedPane.addTab("Open Loop", null, new JPanel(), "Open Loop Fueling");

        return tabbedPane;
    }


}
