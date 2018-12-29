package ui.view.closedloop;

import preferences.ClosedLoopLogFilterPreferences;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class ClosedLoopMe7LogFilterConfigPanel extends JPanel {
    private static final Insets WEST_INSETS = new Insets(5, 0, 5, 5);
    private static final Insets EAST_INSETS = new Insets(5, 5, 5, 0);

    enum FieldTitle {
        MIN_THROTTLE_ANGLE("Minimum Throttle Angle"), MIN_RPM("Minimum RPM"), MAX_STD_DEV("Max Std Dev"), STD_DEV_SAMPLE_WINDOW("Std Dev Window Size");
        private String title;

        private FieldTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    private Map<FieldTitle, JTextField> fieldMap = new HashMap<FieldTitle, JTextField>();

    ClosedLoopMe7LogFilterConfigPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Configure Closed Loop Filter"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        GridBagConstraints gbc;
        for (int i = 0; i < FieldTitle.values().length; i++) {
            FieldTitle fieldTitle = FieldTitle.values()[i];
            gbc = createGbc(0, i);
            add(new JLabel(fieldTitle.getTitle() + ":", JLabel.LEFT), gbc);
            gbc = createGbc(1, i);

            NumberFormat integerFormat = NumberFormat.getIntegerInstance();
            NumberFormatter integerFormatter = new NumberFormatter(integerFormat);
            integerFormatter.setValueClass(Integer.class);
            integerFormatter.setAllowsInvalid(false);
            integerFormatter.setMinimum(0);

            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            NumberFormatter decimalFormatter = new NumberFormatter(decimalFormat);
            decimalFormatter.setOverwriteMode(true);
            decimalFormatter.setAllowsInvalid(true);
            decimalFormatter.setMinimum(0d);

            JFormattedTextField textField = null;

            switch (fieldTitle) {
                case MIN_THROTTLE_ANGLE:
                    textField = new JFormattedTextField(integerFormatter);
                    textField.setValue(ClosedLoopLogFilterPreferences.getMinThrottleAnglePreference());
                    break;
                case MIN_RPM:
                    textField = new JFormattedTextField(integerFormatter);
                    textField.setValue(ClosedLoopLogFilterPreferences.getMinRpmPreference());
                    break;
                case MAX_STD_DEV:
                    textField = new JFormattedTextField(decimalFormatter);
                    textField.setValue(ClosedLoopLogFilterPreferences.getMaxStdDevPreference());
                    break;
                case STD_DEV_SAMPLE_WINDOW:
                    textField = new JFormattedTextField(integerFormatter);
                    textField.setValue(ClosedLoopLogFilterPreferences.getStdDevSampleWindowPreference());
                    break;
            }

            textField.setColumns(5);
            add(textField, gbc);

            fieldMap.put(fieldTitle, textField);
        }
    }


    private GridBagConstraints createGbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;

        gbc.anchor = (x == 0) ? GridBagConstraints.WEST : GridBagConstraints.EAST;
        gbc.fill = (x == 0) ? GridBagConstraints.BOTH
                : GridBagConstraints.HORIZONTAL;

        gbc.insets = (x == 0) ? WEST_INSETS : EAST_INSETS;
        gbc.weightx = (x == 0) ? 0.1 : 1.0;
        gbc.weighty = 1.0;
        return gbc;
    }

    public String getFieldText(FieldTitle fieldTitle) {
        return fieldMap.get(fieldTitle).getText();
    }
}
