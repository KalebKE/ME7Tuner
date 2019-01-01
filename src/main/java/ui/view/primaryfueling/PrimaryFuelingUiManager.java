package ui.view.primaryfueling;

import ui.view.primaryfueling.krkte.KrkteInputUiManager;
import ui.view.mlhfm.MlhfmUiManager;
import ui.view.openloopfueling.OpenLoopFuelingMe7LogUiManager;

import javax.swing.*;
import java.awt.*;

public class PrimaryFuelingUiManager {

    public JPanel getPrimaryFuelingPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(getTabbedPane(), BorderLayout.CENTER);

        return panel;
    }

    private JTabbedPane getTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.LEFT);
        tabbedPane.addTab("KRKTE", null, new KrkteInputUiManager().getKrktePanel(), "Primary Fueling");
        tabbedPane.addTab("MLHFM", null, new MlhfmUiManager().getMlhfmPanel(), "Voltage to Kg/Hr");
        tabbedPane.addTab("ME7 Logs", null, new PrimaryFuelingMe7LogUiManager().getMe7LogPanel(), "ME7 Logging");
        tabbedPane.addTab("Correction", null, new PrimaryFuelingCorrectionUiManager().getCorrectionPanel(), "Primary Fueling Correction");

        return tabbedPane;
    }
}
