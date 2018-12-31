package preferences.primaryfueling;

import java.util.prefs.Preferences;

public class PrimaryFuelingPreferences {

    private static final String KRKTE_PREFERENCE = "krkte_preference";


    private static Preferences prefs = Preferences.userNodeForPackage(PrimaryFuelingPreferences.class);

    public static double getKrktePreference() {
        return Double.valueOf(prefs.get(KRKTE_PREFERENCE, String.valueOf(0.09777)));
    }

    public static void setKrktePreference(double krkte) {
        prefs.put(KRKTE_PREFERENCE, String.valueOf(krkte));
    }
}
