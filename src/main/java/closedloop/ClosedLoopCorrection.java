package closedloop;

import contract.Me7LogFileContract;
import contract.MlhfmFileContract;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.util.*;

public class ClosedLoopCorrection {

    private final int lambdaControlEnabled = 1;
    private final double minThrottleAngle = 0.1;
    private final double minRpm = 700;
    private final double maxStdDev = 0.025;

    private Map<String, List<Double>> correctedMlhfm = new HashMap<>();
    private Map<Double, List<Double>> rawVoltageStdDev = new HashMap<>();
    private Map<Double, List<Double>> filteredVoltageStdDev = new HashMap<>();

    public Map<String, List<Double>> getCorrectedMlhfm() {
        return correctedMlhfm;
    }

    public Map<Double, List<Double>> getRawVoltageStdDev() {
        return rawVoltageStdDev;
    }
    public Map<Double, List<Double>> getFilteredVoltageStdDev() {
        return filteredVoltageStdDev;
    }

    public void correct(Map<String, List<Double>> me7Logs, Map<String, List<Double>> mlhfm) {

        Map<Double, List<Double>> correctionErrorMap = new HashMap<>();

        for (Double voltage : mlhfm.get(MlhfmFileContract.MAF_VOLTAGE_HEADER)) {
            correctionErrorMap.put(voltage, new ArrayList<>());
            rawVoltageStdDev.put(voltage, new ArrayList<>());
            filteredVoltageStdDev.put(voltage, new ArrayList<>());
        }

        calculateCorrections(correctionErrorMap, me7Logs, mlhfm);

        List<Double> correctionErrorList = new ArrayList<>();

        int maxCorrectionIndex = processCorrections(correctionErrorList, correctionErrorMap, mlhfm);

        postProcessCorrections(correctionErrorList, maxCorrectionIndex);

        smooth(correctionErrorList);

        System.out.println(Arrays.toString(correctionErrorList.toArray()));

        applyCorrections(correctionErrorList, mlhfm);
    }

    private void populateRawVoltageStdDev(List<Double> me7voltageStdDev, List<Double> me7Voltages, Map<String, List<Double>> mlhfm) {
        for (int i = 0; i < me7Voltages.size(); i++) {
            double me7Voltage = me7Voltages.get(i);
            int mlhfmVoltageIndex = Math.abs(Collections.binarySearch(mlhfm.get(MlhfmFileContract.MAF_VOLTAGE_HEADER), me7Voltage));
            double mlhfmVoltageKey = mlhfm.get(MlhfmFileContract.MAF_VOLTAGE_HEADER).get(mlhfmVoltageIndex);
            rawVoltageStdDev.get(mlhfmVoltageKey).add(me7voltageStdDev.get(i));
        }
    }

    private void calculateCorrections(Map<Double, List<Double>> correctionError, Map<String, List<Double>> me7Logs, Map<String, List<Double>> mlhfm) {
        List<Double> me7Voltages = me7Logs.get(Me7LogFileContract.MAF_VOLTAGE_HEADER);
        List<Double> me7voltageStdDev = getStandardDeviation(me7Voltages);
        List<Double> stft = me7Logs.get(Me7LogFileContract.STFT_COLUMN_HEADER);
        List<Double> ltft = me7Logs.get(Me7LogFileContract.LTFT_COLUMN_HEADER);
        List<Double> lambdaControl = me7Logs.get(Me7LogFileContract.LAMBDA_CONTROL_ACTIVE_HEADER);
        List<Double> throttleAngle = me7Logs.get(Me7LogFileContract.THROTTLE_PLATE_ANGLE_HEADER);
        List<Double> rpm = me7Logs.get(Me7LogFileContract.RPM_COLUMN_HEADER);

        populateRawVoltageStdDev( me7voltageStdDev,  me7Voltages,  mlhfm);

        for (int i = 0; i < stft.size(); i++) {
            // Closed loop only and not idle
            if (lambdaControl.get(i) == lambdaControlEnabled && throttleAngle.get(i) > minThrottleAngle && rpm.get(i) > minRpm && me7voltageStdDev.get(i) < maxStdDev) {
                // Get every logged voltage
                double me7Voltage = me7Voltages.get(i);
                // Look up the corresponding voltage from MLHFM
                int mlhfmVoltageIndex = Math.abs(Collections.binarySearch(mlhfm.get(MlhfmFileContract.MAF_VOLTAGE_HEADER), me7Voltage));
                double mlhfmVoltageKey = mlhfm.get(MlhfmFileContract.MAF_VOLTAGE_HEADER).get(mlhfmVoltageIndex);

                // Calculate the error based on LTFT and STFT
                double stftValue = stft.get(i) - 1;
                double ltftValue = ltft.get(i) - 1;

                // Record the correction.
                correctionError.get(mlhfmVoltageKey).add(stftValue + ltftValue);

                // Keep track of the standard deviation of the logged voltages relative to the MLHFM voltages
                filteredVoltageStdDev.get(mlhfmVoltageKey).add(me7voltageStdDev.get(i));
            }
        }
    }

    private int processCorrections(List<Double> correctionErrorList, Map<Double, List<Double>> correctionErrorMap, Map<String, List<Double>> mlhfm) {
        int maxCorrectionIndex = 0;
        int index = 0;
        Mean mean = new Mean();
        for (Double voltage : mlhfm.get(MlhfmFileContract.MAF_VOLTAGE_HEADER)) {
            List<Double> corrections = correctionErrorMap.get(voltage);
            // Get the mean of the correction set
            double meanValue = mean.evaluate(toDoubleArray(corrections.toArray(new Double[0])), 0, corrections.size());
            // Get the mode of the correction set
            double[] mode = StatUtils.mode(toDoubleArray(corrections.toArray(new Double[0])));

            double correction = meanValue;

            for (double v : mode) {
                correction += v;
            }

            // Get the average of the mean and the mode
            correction /= 1 + mode.length;

            // Keep track of the largest index a correction was made at
            if (!Double.isNaN(correction)) {
                maxCorrectionIndex = index;
            }

            correctionErrorList.add(correction);

            index++;
        }

        return maxCorrectionIndex;
    }

    private void postProcessCorrections(List<Double> correctionErrorList, int maxCorrectionIndex) {
        boolean foundStart = false;
        int lastValidCorrectionIndex = -1;
        // Fill in any missing corrections with 0 if it occurs before the first correction or last correction. Fill in
        // many missing corrections between the first and last correction with the last known correction.
        for (int i = 0; i < correctionErrorList.size(); i++) {
            Double value = correctionErrorList.get(i);
            if (value.isNaN() && !foundStart) {
                correctionErrorList.set(i, 0d);
            } else if (!value.isNaN() && !foundStart) {
                foundStart = true;
                lastValidCorrectionIndex = i;
            } else if (value.isNaN()) {
                if (i < maxCorrectionIndex) {
                    correctionErrorList.set(i, correctionErrorList.get(lastValidCorrectionIndex));
                } else {
                    correctionErrorList.set(i, 0d);
                }
            } else {
                lastValidCorrectionIndex = i;
            }
        }
    }

    private void smooth(List<Double> correctionErrorList) {
        // Smooth
        Mean mean = new Mean();
        double[] meanTempCorrections = toDoubleArray(correctionErrorList.toArray(new Double[0]));
        for(int i = 0; i < meanTempCorrections.length; i++) {
            if(i > 2 && i < meanTempCorrections.length - 2) {
                correctionErrorList.set(i, mean.evaluate(meanTempCorrections, i - 2, 5));
            }
        }
    }

    private void applyCorrections(List<Double> correctionErrorList, Map<String, List<Double>> mlhfm) {
        Map<Double, Double> totalCorrectionError = new HashMap<>();
        List<Double> voltage = mlhfm.get(MlhfmFileContract.MAF_VOLTAGE_HEADER);

        for (int i = 0; i < voltage.size(); i++) {
            totalCorrectionError.put(voltage.get(i), correctionErrorList.get(i));
        }

        correctedMlhfm.put(MlhfmFileContract.MAF_VOLTAGE_HEADER, voltage);

        List<Double> oldKgPerHour = mlhfm.get(MlhfmFileContract.KILOGRAM_PER_HOUR_HEADER);
        List<Double> newKgPerHour = new ArrayList<>();

        for (int i = 0; i < voltage.size(); i++) {
            double oldKgPerHourValue = oldKgPerHour.get(i);
            double totalCorrectionErrorValue = totalCorrectionError.get(voltage.get(i));

            newKgPerHour.add(i, oldKgPerHourValue * ((totalCorrectionErrorValue) + 1));
        }

        correctedMlhfm.put(MlhfmFileContract.KILOGRAM_PER_HOUR_HEADER, newKgPerHour);
    }

    private double[] toDoubleArray(Double[] array) {
        double[] result = new double[array.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = array[i];
        }

        return result;
    }

    private ArrayList<Double> getStandardDeviation(List<Double> values) {
        int window = 20;
        StandardDeviation standardDeviation = new StandardDeviation();
        double[] inputValues = toDoubleArray(values.toArray(new Double[0]));
        ArrayList<Double> result = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            if (i < 20 || i >= values.size() - 20) {
                result.add(i, 0d);
            } else {
                double stdDev = standardDeviation.evaluate(inputValues, i, window);
                result.add(i, stdDev);
            }
        }

        return result;
    }
}
