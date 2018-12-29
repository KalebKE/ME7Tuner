package ui.view.closedloop;

import ui.view.MlhfmUiManager;

import javax.swing.*;
import java.awt.*;

public class ClosedLoopUiManager {

    private JPanel closeLoopPanel;

    public JPanel getClosedLoopPanel() {
        closeLoopPanel = new JPanel();
        closeLoopPanel.setLayout(new BorderLayout());
        closeLoopPanel.add(getTabbedPane(), BorderLayout.CENTER);

        return closeLoopPanel;
    }

    private JTabbedPane getTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.LEFT);
        tabbedPane.addTab("MLHFM", null, new MlhfmUiManager().getMlhfmPanel(), "Voltage to Kg/Hr");
        tabbedPane.addTab("ME7 Logs", null, new ClosedLoopMe7LogUiManager().getMe7LogPanel(), "ME7 Logging");
        tabbedPane.addTab("Correction", null, new ClosedLoopCorrectionUiManager().getCorrectionPanel(), "Corrected MLHFM");


        return tabbedPane;
    }
}
