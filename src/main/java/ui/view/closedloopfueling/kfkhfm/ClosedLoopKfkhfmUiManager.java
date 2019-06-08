package ui.view.closedloopfueling.kfkhfm;

import ui.view.fkfhfm.KfkhfmUiManager;

import javax.swing.*;
import java.awt.*;

public class ClosedLoopKfkhfmUiManager {

    public JPanel getPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(getTabbedPane(), BorderLayout.CENTER);

        return panel;
    }

    private JTabbedPane getTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.LEFT);
        tabbedPane.addTab("KFKHFM", null, new KfkhfmUiManager(true).getPanel(), "Correction map for MAF");
        tabbedPane.addTab("ME7 Logs", null, new ClosedLoopKfkhfmMe7LogUiManager().getMe7LogPanel(), "ME7 Logging");
        tabbedPane.addTab("Correction", null, new ClosedLoopKfkhfmCorrectionUiManager().getCorrectionPanel(), "Corrected MLHFM");

        return tabbedPane;
    }
}