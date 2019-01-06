package preferences.kfmirl;

import java.util.prefs.Preferences;

public class KfmirlPreferences {

    private static final String MAX_DESIRED_BOOST_PREFERENCE = "max_desired_boost_preference";
    private static final String KFURL_PREFERENCE = "kfurl_preference";

    private static Preferences prefs = Preferences.userNodeForPackage(KfmirlPreferences.class);

    public static double getMaxDesiredBoostPreference() {
        return Double.valueOf(prefs.get(MAX_DESIRED_BOOST_PREFERENCE, String.valueOf(1620)));
    }

    public static void setMaxDesiredBoostPreference(double maxDesiredBoost) {
        prefs.put(MAX_DESIRED_BOOST_PREFERENCE, String.valueOf(maxDesiredBoost));
    }

    public static double getKfurlPreference() {
        return Double.valueOf(prefs.get(KFURL_PREFERENCE, String.valueOf(0.1105)));
    }

    public static void setKfurlPreference(double kfurl) {
        prefs.put(KFURL_PREFERENCE, String.valueOf(kfurl));
    }
}
