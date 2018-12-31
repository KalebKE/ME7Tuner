package preferences.closedloopfueling;

import java.util.prefs.Preferences;

public class ClosedLoopFuelingLogFilterPreferences {

    private static final String MIN_THROTTLE_ANGLE_PREFERENCE = "min_throttle_angle_preference";
    private static final String MIN_RPM_PREFERENCE = "min_rpm_preference";
    private static final String MAX_STD_DEV_PREFERENCE = "max_std_dev_preference";
    private static final String STD_DEV_SAMPLE_WINDOW_PREFERENCE = "std_dev_sample_window_preference";

    private static Preferences prefs = Preferences.userNodeForPackage(ClosedLoopFuelingLogFilterPreferences.class);

    public static double getMinThrottleAnglePreference() {
        return Double.valueOf(prefs.get(MIN_THROTTLE_ANGLE_PREFERENCE, String.valueOf(0)));
    }

    public static void setMinThrottleAnglePreference(double minThrottleAngle) {
        prefs.put(MIN_THROTTLE_ANGLE_PREFERENCE, String.valueOf(minThrottleAngle));
    }

    public static double getMinRpmPreference() {
        return Double.valueOf(prefs.get(MIN_RPM_PREFERENCE, String.valueOf(0)));
    }

    public static void setMinRpmPreference(double minRpm) {
        prefs.put(MIN_RPM_PREFERENCE, String.valueOf(minRpm));
    }

    public static double getMaxStdDevPreference() {
        return Double.valueOf(prefs.get(MAX_STD_DEV_PREFERENCE, String.valueOf(1)));
    }

    public static void setMaxStdDevPreference(double maxStdDev) {
        prefs.put(MAX_STD_DEV_PREFERENCE, String.valueOf(maxStdDev));
    }

    public static int getStdDevSampleWindowPreference() {
        return Integer.valueOf(prefs.get(STD_DEV_SAMPLE_WINDOW_PREFERENCE, String.valueOf(20)));
    }

    public static void setStdDevSampleWindowPreference(int stdDevSampleWindow) {
        prefs.put(STD_DEV_SAMPLE_WINDOW_PREFERENCE, String.valueOf(stdDevSampleWindow));
    }
}
