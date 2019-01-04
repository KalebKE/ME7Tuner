package ui.view.kfmirl;

import model.kfmirl.Kfmirl;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import ui.map.map.MapTable;
import util.Util;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

public class KfmirlUiManager {
    private MapTable kfmirl;

    private MapTable kfurl;
    private JFormattedTextField maxDesiredBoostTextArea;
    private JFormattedTextField maxDesiredLoadTextArea;


    public JPanel getKfmirlCalculationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.PAGE_START;

        panel.add(getKmfirlGeneratorPanel(), c);

        c.gridx = 0;
        c.gridy = 1;

        c.insets.top = 16;

        panel.add(getKfmirlMapPanel(), c);

        return panel;
    }

    private JPanel getKfmirlMapPanel() {

        JPanel panel = new JPanel();

        panel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        kfmirl = MapTable.getMapTable(Kfmirl.getStockKfmirlYAxis(), Kfmirl.getStockKfmirlXAxis(), Kfmirl.getStockKfmirlMap());

        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.CENTER;
        c.anchor = GridBagConstraints.CENTER;

        panel.add(new JLabel("KFMIRL"),c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 2;
        c.insets.top = 16;
        c.fill = GridBagConstraints.CENTER;
        c.anchor = GridBagConstraints.CENTER;

        JScrollPane scrollPane = kfmirl.getScrollPane();
        scrollPane.setPreferredSize(new Dimension(665, 272));

        panel.add(scrollPane ,c);

        c.gridx = 0;
        c.gridy = 3;

        Button button = new Button("Generate");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Mean mean = new Mean();
                double[]array = Util.toDoubleArray(kfurl.getData()[0]);
                double averageKfurl = mean.evaluate(array, 0, array.length);
                double maximumBoost = Integer.parseInt(maxDesiredBoostTextArea.getText());
                double maximumSpecifiedLoad = (maximumBoost - 300)*averageKfurl;
                maxDesiredLoadTextArea.setValue(maximumSpecifiedLoad);

                kfmirl.setTableData(Kfmirl.getScaledKfmirlMap(maximumSpecifiedLoad));
            }
        });

        panel.add(button, c);

        return panel;
    }

    private JPanel getKmfirlGeneratorPanel() {
        JPanel panel = new JPanel();

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Configure Maximum Boost"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        panel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        JPanel maxBoostPanel = new JPanel();
        maxBoostPanel.setLayout(new FlowLayout(0,5,0));

        NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        integerFormat.setGroupingUsed(false);
        NumberFormatter integerFormatter = new NumberFormatter(integerFormat);
        integerFormatter.setValueClass(Integer.class);
        integerFormatter.setAllowsInvalid(false);
        integerFormatter.setMinimum(0);

        JLabel maxDesiredBoostLabel = new JLabel("Max Desired Boost:");
        maxBoostPanel.add(maxDesiredBoostLabel);
        maxDesiredBoostTextArea = new JFormattedTextField(integerFormatter);
        maxDesiredBoostTextArea.setColumns(4);
        maxDesiredBoostTextArea.setValue(1700);
        maxBoostPanel.add(maxDesiredBoostTextArea);
        JLabel maxDesiredBoostUnitsLabel = new JLabel("MBAR");
        maxBoostPanel.add(maxDesiredBoostUnitsLabel);

        maxBoostPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;

        panel.add(maxBoostPanel, c);

        JPanel maxLoadPanel = new JPanel();
        maxLoadPanel.setLayout(new FlowLayout(0,5,0));

        JLabel maxDesiredLoadLabel = new JLabel("Max Desired Load:");
        maxLoadPanel.add(maxDesiredLoadLabel);
        maxDesiredLoadTextArea = new JFormattedTextField(integerFormatter);
        maxDesiredLoadTextArea.setEditable(false);
        maxDesiredLoadTextArea.setColumns(4);
        maxDesiredLoadTextArea.setValue(191);
        maxLoadPanel.add(maxDesiredLoadTextArea);
        JLabel maxDesiredLoadUnitsLabel = new JLabel("%");
        maxLoadPanel.add(maxDesiredLoadUnitsLabel);

        maxLoadPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 1;
        c.insets.top = 20;

        panel.add(maxLoadPanel, c);

        c.gridwidth = 1;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 2;

        panel.add(getKfurlPanel(), c);

        return panel;
    }

    private JPanel getKfurlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;

        panel.add(new JLabel("KFURL"),c);

        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 1;

        kfurl = MapTable.getMapTable(new Double[]{17.9}, new Double[] {680d, 1000d, 1280d, 1520d, 2000d, 2520d, 3000d, 3800d, 5000d, 6000d }, new Double[][]{{0.131076,0.125832,0.127140,0.127140,0.128454,0.125832,0.124524,0.111999,0.111999,0.111999}});

        JScrollPane scrollPane = kfurl.getScrollPane();
        scrollPane.setPreferredSize(new Dimension(550, 32));

        panel.add(scrollPane,c);

        return panel;
    }
}
