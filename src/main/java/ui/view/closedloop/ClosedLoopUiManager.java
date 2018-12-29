package ui.view.closedloop;

import javax.swing.*;
import java.awt.*;

public class ClosedLoopUiManager {

    private JPanel closeLoopPanel;

    public JPanel getClosedLoopPanel() {
        closeLoopPanel = new JPanel();
        closeLoopPanel.setLayout(new BorderLayout());
        closeLoopPanel.add(getTabbedPane(), BorderLayout.CENTER);

        closeLoopPanel.invalidate();

        return closeLoopPanel;
    }

    private JTabbedPane getTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.LEFT);
        tabbedPane.addTab("MLHFM", null, new MlhfmInputUiManager().getMlhfmPanel(), "Voltage to Kg/Hr");
        tabbedPane.addTab("Logs", null, new ClosedLoopLogUiManager().getMe7LogPanel(), "ME7 Logging");

        return tabbedPane;
    }
}
