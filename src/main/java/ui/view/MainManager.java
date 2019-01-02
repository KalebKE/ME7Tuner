package ui.view;

import ui.view.closedloopfueling.ClosedLoopFuelingUiManager;
import ui.view.krkte.KrkteCalculationManager;
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
        tabbedPane.addTab("Closed Loop Fueling", null, new ClosedLoopFuelingUiManager().getClosedLoopPanel(), "Closed Loop Fueling Compensation");
        tabbedPane.addTab("Open Loop Fueling", null, new OpenLoopFuelingUiManager().getOpenLoopPanel(), "Open Loop Fueling Compensation");
        tabbedPane.addTab("Primary Fueling", null, new PrimaryFuelingUiManager().getPrimaryFuelingPanel(), "Primary Fueling Compensation");
        tabbedPane.addTab("KRKTE", null, new KrkteCalculationManager().getKrkteCalculationPanel(), "KRKTE Calculator");

        return tabbedPane;
    }


}
