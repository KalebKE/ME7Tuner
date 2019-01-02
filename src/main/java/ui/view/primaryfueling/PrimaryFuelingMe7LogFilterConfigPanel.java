package ui.view.primaryfueling;

import preferences.primaryfueling.PrimaryFuelingPreferences;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

class PrimaryFuelingMe7LogFilterConfigPanel extends JPanel {
    private static final Insets WEST_INSETS = new Insets(5, 0, 5, 5);
    private static final Insets EAST_INSETS = new Insets(5, 5, 5, 0);

    enum FieldTitle {
        GASOLINE_GRAMS_PER_CUBIC_CENTIMETER("Gasoline Grams per Cubic Centimeter"), FUEL_INJECTOR_CC_MIN("Fuel Injector cc/min"), NUM_FUEL_INJECTORS("Fuel Injectors"), METHANOL_GRAMS_PER_CUBIC_CENTIMETER("Methanol Grams per Cubic Centimeter"), METHANOL_NOZZLE_CC_MIN("Methanol Nozzle cc/min"), NUM_METHANOL_NOZZLES("Methanol Nozzles");
        private String title;

        FieldTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    private Map<FieldTitle, JTextField> fieldMap = new HashMap<FieldTitle, JTextField>();

    PrimaryFuelingMe7LogFilterConfigPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Configure Open Loop Filter"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        GridBagConstraints gbc;

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

        for (int i = 0; i < FieldTitle.values().length; i++) {
            FieldTitle fieldTitle = FieldTitle.values()[i];
            gbc = createGbc(0, i);
            add(new JLabel(fieldTitle.getTitle() + ":", JLabel.LEFT), gbc);
            gbc = createGbc(1, i);

            JFormattedTextField textField = null;

            switch (fieldTitle) {
                case GASOLINE_GRAMS_PER_CUBIC_CENTIMETER:
                    textField = new JFormattedTextField(decimalFormat);
                    textField.setValue(PrimaryFuelingPreferences.getGasolineGramsPerCubicCentimeterPreference());
                    break;
                case FUEL_INJECTOR_CC_MIN:
                    textField = new JFormattedTextField(integerFormatter);
                    textField.setValue(PrimaryFuelingPreferences.getFuelInjectorSizePreference());
                    break;
                case NUM_FUEL_INJECTORS:
                    textField = new JFormattedTextField(integerFormatter);
                    textField.setValue(PrimaryFuelingPreferences.getNumFuelInjectorPreference());
                    break;
                case METHANOL_GRAMS_PER_CUBIC_CENTIMETER:
                    textField = new JFormattedTextField(decimalFormat);
                    textField.setValue(PrimaryFuelingPreferences.getMethanolGramsPerCubicCentimeterPreference());
                    break;
                case METHANOL_NOZZLE_CC_MIN:
                    textField = new JFormattedTextField(integerFormat);
                    textField.setValue(PrimaryFuelingPreferences.getMethanolNozzleSizePreference());
                    break;
                case NUM_METHANOL_NOZZLES:
                    textField = new JFormattedTextField(integerFormatter);
                    textField.setValue(PrimaryFuelingPreferences.getNumMethanolNozzlePreference());
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
