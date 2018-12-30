package ui.view.openloop;

import ui.view.MlhfmUiManager;
import ui.view.closedloop.ClosedLoopCorrectionUiManager;
import ui.view.closedloop.ClosedLoopMe7LogUiManager;

import javax.swing.*;
import java.awt.*;

public class OpenLoopUiManager {

    public JPanel getOpenLoopPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(getTabbedPane(), BorderLayout.CENTER);

        return panel;
    }

    private JTabbedPane getTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.LEFT);
        tabbedPane.addTab("MLHFM", null, new MlhfmUiManager().getMlhfmPanel(), "Voltage to Kg/Hr");
        tabbedPane.addTab("ME7 Logs", null, new OpenLoopMe7LogUiManager().getMe7LogPanel(), "ME7 Logging");
        tabbedPane.addTab("Correction", null, new OpenLoopCorrectionUiManager().getCorrectionPanel(), "Corrected MLHFM");

        return tabbedPane;
    }
}
