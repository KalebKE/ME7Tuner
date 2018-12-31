package ui.view;

import ui.view.closedloopfueling.ClosedLoopFuelingUiManager;
import ui.view.openloopfueling.OpenLoopFuelingUiManager;
import ui.view.primaryfueling.PrimaryFuelingUiManager;

import javax.swing.*;

public class MainManager {
    public void start() {
        JFrame frame = new JFrame();
        frame.setTitle("MAF Scaler");
        frame.setSize(1280, 800);
        frame.add(getTabbedPane());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private JTabbedPane getTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Closed Loop Fueling", null, new ClosedLoopFuelingUiManager().getClosedLoopPanel(), "Closed Loop Fueling");
        tabbedPane.addTab("Open Loop Fueling", null, new OpenLoopFuelingUiManager().getOpenLoopPanel(), "Open Loop Fueling");
        tabbedPane.addTab("Primary Fueling", null, new PrimaryFuelingUiManager().getPrimaryFuelingPanel(), "Primary Fueling");

        return tabbedPane;
    }


}
