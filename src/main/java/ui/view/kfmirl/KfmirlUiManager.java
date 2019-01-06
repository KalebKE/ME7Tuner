package ui.view.kfmirl;

import model.kfmirl.Kfmirl;
import ui.map.map.MapTable;

import javax.swing.*;
import java.awt.*;


public class KfmirlUiManager {
    private MapTable kfmirl;
    private DesiredLoadCalculatorPanel desiredLoadCalculatorPanel;

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
        scrollPane.setPreferredSize(new Dimension(650, 272));

        panel.add(scrollPane ,c);

        return panel;
    }

    private JPanel getKmfirlGeneratorPanel() {

        desiredLoadCalculatorPanel = new DesiredLoadCalculatorPanel(new DesiredLoadCalculatorPanel.OnValueChangedListener() {
            @Override
            public void onValueChanged(DesiredLoadCalculatorPanel.FieldTitle fieldTitle) {

                switch (fieldTitle) {
                    case MAX_DESIRED_BOOST:
                        kfmirl.setTableData(Kfmirl.getScaledKfmirlMap(Double.parseDouble(desiredLoadCalculatorPanel.getFieldText(DesiredLoadCalculatorPanel.FieldTitle.MAX_DESIRED_LOAD))));
                        break;
                    case KFURL:
                        kfmirl.setTableData(Kfmirl.getScaledKfmirlMap(Double.parseDouble(desiredLoadCalculatorPanel.getFieldText(DesiredLoadCalculatorPanel.FieldTitle.MAX_DESIRED_LOAD))));
                        break;
                }
            }
        });

        return desiredLoadCalculatorPanel;
    }
}
