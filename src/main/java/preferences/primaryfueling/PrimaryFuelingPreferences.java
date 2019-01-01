package preferences.primaryfueling;

import java.util.prefs.Preferences;

public class PrimaryFuelingPreferences {

    private static final String KRKTE_PREFERENCE = "krkte_preference";
    private static final String FUEL_INJECTOR_SIZE_PREFERENCE = "fuel_injector_size_preference";
    private static final String NUM_FUEL_INJECTOR_PREFERENCE = "num_fuel_injector_preference";
    private static final String METHANOL_NOZZLE_SIZE_PREFERENCE = "methanol_nozzle_size_preference";
    private static final String NUM_METHANOL_NOZZLE_PREFERENCE = "num_methanol_nozzle_preference";

    private static Preferences prefs = Preferences.userNodeForPackage(PrimaryFuelingPreferences.class);

    public static double getKrktePreference() {
        return Double.valueOf(prefs.get(KRKTE_PREFERENCE, String.valueOf(0.09777)));
    }

    public static void setKrktePreference(double krkte) {
        prefs.put(KRKTE_PREFERENCE, String.valueOf(krkte));
    }

    public static int getFuelInjectorSizePreference() {
        return Integer.valueOf(prefs.get(FUEL_INJECTOR_SIZE_PREFERENCE, String.valueOf(349)));
    }

    public static void setFuelInjectorSizePreference(int fuelInjectorSizeCcMin) {
        prefs.put(FUEL_INJECTOR_SIZE_PREFERENCE, String.valueOf(fuelInjectorSizeCcMin));
    }

    public static int getNumFuelInjectorPreference() {
        return Integer.valueOf(prefs.get(NUM_FUEL_INJECTOR_PREFERENCE, String.valueOf(6)));
    }

    public static void setNumFuelInjectorPreference(int numFuelInjectorPreference) {
        prefs.put(NUM_FUEL_INJECTOR_PREFERENCE, String.valueOf(numFuelInjectorPreference));
    }

    public static int getMethanolNozzleSizePreference() {
        return Integer.valueOf(prefs.get(METHANOL_NOZZLE_SIZE_PREFERENCE, String.valueOf(0)));
    }

    public static void setMethanolNozzleSizePreference(int methanolNozzleSizeCcMin) {
        prefs.put(METHANOL_NOZZLE_SIZE_PREFERENCE, String.valueOf(methanolNozzleSizeCcMin));
    }

    public static int getNumMethanolNozzlePreference() {
        return Integer.valueOf(prefs.get(NUM_METHANOL_NOZZLE_PREFERENCE, String.valueOf(0)));
    }

    public static void setNumMethanolNozzlePreference(int numMethanolPreference) {
        prefs.put(NUM_METHANOL_NOZZLE_PREFERENCE, String.valueOf(numMethanolPreference));
    }
}
