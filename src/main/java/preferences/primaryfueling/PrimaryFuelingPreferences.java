package preferences.primaryfueling;

import java.util.prefs.Preferences;

public class PrimaryFuelingPreferences {

    private static final String AIR_DENSITY_GRAMS_PER_CUBIC_DECIMETER = "air_density_grams_per_cubic_decimeter_preference";
    private static final String ENGINE_DISPLACEMENT_CUBIC_DECIMETER = "engine_displacement_cubic_decimeter_preference";
    private static final String ENGINE_NUM_CYLINDERS_PREFERENCE = "engine_num_cylinders_preference";
    private static final String STOICHIOMETRIC_AIR_FUEL_RATIO_PREFERENCE = "stoichiometric_air_fuel_ratio_preference";

    private static final String KRKTE_PREFERENCE = "krkte_preference";
    private static final String GASOLINE_GRAMS_PER_CUBIC_CENTIMETER = "gasoline_grams_per_cubic_centimeter_preference";
    private static final String METHANOL_GRAMS_PER_CUBIC_CENTIMETER = "methanol_grams_per_cubic_centimeter_preference";
    private static final String FUEL_INJECTOR_SIZE_PREFERENCE = "fuel_injector_size_preference";
    private static final String NUM_FUEL_INJECTOR_PREFERENCE = "num_fuel_injector_preference";
    private static final String METHANOL_NOZZLE_SIZE_PREFERENCE = "methanol_nozzle_size_preference";
    private static final String NUM_METHANOL_NOZZLE_PREFERENCE = "num_methanol_nozzle_preference";

    private static Preferences prefs = Preferences.userNodeForPackage(PrimaryFuelingPreferences.class);

    public static double getAirDensityGramsPerCubicDecimeterPreference() {
        return Double.valueOf(prefs.get(AIR_DENSITY_GRAMS_PER_CUBIC_DECIMETER, String.valueOf(1.293)));
    }

    public static double getEngineDisplacementCubicDecimeterPreference() {
        return Double.valueOf(prefs.get(ENGINE_DISPLACEMENT_CUBIC_DECIMETER, String.valueOf(2.7)));
    }

    public static void setEngineDisplacementCubicDecimeterPreference(double engineDisplacementCubicDecimeter) {
        prefs.put(ENGINE_DISPLACEMENT_CUBIC_DECIMETER, String.valueOf(engineDisplacementCubicDecimeter));
    }

    public static int getNumEngineCylindersPreference() {
        return Integer.valueOf(prefs.get(ENGINE_NUM_CYLINDERS_PREFERENCE , String.valueOf(6)));
    }

    public static void setNumEngineCylindersPreference(int numEngineCylinders) {
        prefs.put(ENGINE_NUM_CYLINDERS_PREFERENCE , String.valueOf(numEngineCylinders));
    }

    public static double getStoichiometricAirFuelRatioPreference() {
        return Double.valueOf(prefs.get(STOICHIOMETRIC_AIR_FUEL_RATIO_PREFERENCE, String.valueOf(14.7)));
    }

    public static void setStoichiometricAirFuelRatioPreference(double stoichiometricAirFuelRatio) {
        prefs.put(STOICHIOMETRIC_AIR_FUEL_RATIO_PREFERENCE, String.valueOf(stoichiometricAirFuelRatio));
    }

    public static double getGasolineGramsPerCubicCentimeterPreference() {
        return Double.valueOf(prefs.get(GASOLINE_GRAMS_PER_CUBIC_CENTIMETER, String.valueOf(0.71)));
    }

    public static void setGasolineGramsPerCubicCentimeterPreference(double gramsPerCubicCentimeter) {
        prefs.put(GASOLINE_GRAMS_PER_CUBIC_CENTIMETER, String.valueOf(gramsPerCubicCentimeter));
    }

    public static double getMethanolGramsPerCubicCentimeterPreference() {
        return Double.valueOf(prefs.get(METHANOL_GRAMS_PER_CUBIC_CENTIMETER , String.valueOf(0.75)));
    }

    public static void setMethanolGramsPerCubicCentimeterPreference(double gramsPerCubicCentimeter) {
        prefs.put(METHANOL_GRAMS_PER_CUBIC_CENTIMETER, String.valueOf(gramsPerCubicCentimeter));
    }

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
