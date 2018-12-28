package openloop;

import contract.AfrLogFileContract;
import contract.Me7LogFileContract;
import contract.MlhfmFileContract;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

import java.util.*;

public class OpenLoopCorrection {
    private final int minPointsMe7 = 75;
    private final int minPointsAfr = 150;
    private final double minThrottleAngle = 80;
    private final int lambdaControlEnabled = 0;
    private final double minRpm = 2000;
    private final double maxAfr = 15;

    private Map<String, List<Double>> correctedMlhfm = new HashMap<>();
    private Map<Double, List<Double>> correctedAfrMap = new HashMap<>();

    public void correct(Map<String, List<Double>> me7Log, Map<String, List<Double>> mlhfm, Map<String, List<Double>> afrLog) {

        List<Map<String, List<Double>>> me7LogList = findMe7Logs(me7Log);
        List<Map<String, List<Double>>> afrLogList = findAfrLogs(afrLog);

        generateMlhfm(mlhfm, me7LogList, afrLogList);
    }

    public Map<String, List<Double>> getCorrectedMlhfm() {
        return correctedMlhfm;
    }

    public Map<Double, List<Double>> getCorrectedAfrMap() {
        return correctedAfrMap;
    }

    private void generateMlhfm(Map<String, List<Double>> mlhfm, List<Map<String, List<Double>>> me7LogList, List<Map<String, List<Double>>> afrLogList) {
        if (me7LogList.size() != afrLogList.size()) {
            throw new IllegalArgumentException("ME7 Log size does not match AFR Log size! " + me7LogList.size() + " AFR: " + afrLogList.size());
        } else if (me7LogList.size() == 0) {
            throw new IllegalArgumentException("Not enough log files! " + "ME7: " + me7LogList.size() + " AFR: " + afrLogList.size());
        }

        List<Double> mlhfmVoltage = mlhfm.get(MlhfmFileContract.MAF_VOLTAGE_HEADER);

        // Calculate the initial corrections sets
        calculateCorrections(me7LogList, afrLogList, mlhfmVoltage);
        // Process the corrections sets into a single correction
        ArrayList<Double> correctedAfrList = processCorrections(mlhfmVoltage);
        // Clean up the corrections
        postProcessCorrections(correctedAfrList);
        // Smooth the corrections
        smooth(correctedAfrList, 5);
        System.out.println(Arrays.toString(correctedAfrList.toArray()));
        applyCorrections(mlhfm, correctedAfrList);
    }

    private void applyCorrections(Map<String, List<Double>> mlhfm, ArrayList<Double> correctedAfrList) {
        Map<Double, Double> totalCorrectionError = new HashMap<>();
        List<Double> voltage = mlhfm.get(MlhfmFileContract.MAF_VOLTAGE_HEADER);

        for (int i = 0; i < voltage.size(); i++) {
            totalCorrectionError.put(voltage.get(i), correctedAfrList.get(i));
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

    private void smooth(ArrayList<Double> correctedAfrList, int window) {
        int halfWindow = window / 2;

        // Smooth
        double[] meanTempCorrections = toDoubleArray(correctedAfrList.toArray(new Double[0]));
        Mean mean = new Mean();
        for (int i = 0; i < meanTempCorrections.length; i++) {
            if (i > halfWindow && i < meanTempCorrections.length - halfWindow) {
                correctedAfrList.set(i, mean.evaluate(meanTempCorrections, i - halfWindow, window));
            }
        }
    }

    private void postProcessCorrections(ArrayList<Double> correctedAfrList) {

        // From the last known correction, fills out the list with the last known correction or 0 if a correction has not yet been found
        boolean foundStart = false;
        int lastValidCorrectionIndex = -1;
        for (int i = 0; i < correctedAfrList.size(); i++) {
            Double value = correctedAfrList.get(i);
            if (value.isNaN() && !foundStart) {
                correctedAfrList.set(i, 0d);
            } else if (!value.isNaN() && !foundStart) {
                foundStart = true;
                lastValidCorrectionIndex = i;
            } else if (value.isNaN()) {
                correctedAfrList.set(i, correctedAfrList.get(lastValidCorrectionIndex));
            } else {
                lastValidCorrectionIndex = i;
            }
        }
    }

    private ArrayList<Double> processCorrections(List<Double> mlhfmVoltage) {
        ArrayList<Double> correctedAfrList = new ArrayList<>();

        Mean mean = new Mean();
        for (Double voltage : mlhfmVoltage) {
            List<Double> corrections = correctedAfrMap.get(voltage);
            // Get the mean of the correction set
            double meanValue = mean.evaluate(toDoubleArray(corrections.toArray(new Double[0])), 0, corrections.size());
            // Get the mode of the correction set
            double[] mode = StatUtils.mode(toDoubleArray(corrections.toArray(new Double[0])));

            // Get the average of the mean and the mode
            double correction = meanValue;
            for (double v : mode) {
                correction += v;
            }
            correction /= 1 + mode.length;

            correctedAfrList.add(correction);
        }

        return correctedAfrList;
    }

    private void calculateCorrections(List<Map<String, List<Double>>> me7LogList, List<Map<String, List<Double>>> afrLogList, List<Double> mlhfmVoltageList) {
        // Loop over each log
        for (int i = 0; i < me7LogList.size(); i++) {
            Map<String, List<Double>> me7Log = me7LogList.get(i);
            Map<String, List<Double>> afrLog = afrLogList.get(i);

            // For each log, loop over the voltages in MLHFM and attempt to calculate a correction
            for (int j = 0; j < mlhfmVoltageList.size(); j++) {

                double mlhfmVoltage = mlhfmVoltageList.get(j);

                correctedAfrMap.put(mlhfmVoltage, new ArrayList<>());

                // Get the measured MAF voltages in the log
                List<Double> me7VoltageList = me7Log.get(Me7LogFileContract.MAF_VOLTAGE_HEADER);

                // Attempt to find the mlhfm voltages in the log.
                List<Integer> me7VoltageIndices = getVoltageToMatchIndices(j, mlhfmVoltageList, me7VoltageList);

                // Calculate a corrected AFR for each index that is found
                for (int me7Index : me7VoltageIndices) {
                    if (me7Index != 1 && me7Index != me7VoltageList.size() - 1) {
                        double stft = me7Log.get(Me7LogFileContract.STFT_COLUMN_HEADER).get(me7Index) - 1;
                        double ltft = me7Log.get(Me7LogFileContract.LTFT_COLUMN_HEADER).get(me7Index) - 1;
                        double rpm = me7Log.get(Me7LogFileContract.RPM_COLUMN_HEADER).get(me7Index);
                        double targetAfr = me7Log.get(Me7LogFileContract.REQUESTED_LAMBDA_HEADER).get(me7Index);

                        // Find the RPM from the ME7 log in the AFR log
                        int afrIndex = Math.abs(Collections.binarySearch(afrLog.get(AfrLogFileContract.RPM_HEADER), rpm));

                        List<Double> afrList = afrLog.get(AfrLogFileContract.AFR_HEADER);

                        // Now find the AFR that corresponds to the RPM in the AFR Log
                        double afr = afrList.get(Math.min(afrIndex, afrList.size() - 1)) / 14.7;

                        // Calculate a correction accounting for STFT and LTFT
                        double rawAfr = afr / (1 - (stft + ltft));
                        double afrCorrection = (rawAfr / targetAfr) - 1;

                        correctedAfrMap.get(mlhfmVoltage).add(afrCorrection);
                    } else {
                        correctedAfrMap.get(mlhfmVoltage).add(Double.NaN);
                    }
                }
            }
        }
    }

    private List<Integer> getVoltageToMatchIndices(int mlhfmVoltageToMatchIndex, List<Double> mlhfmVoltageList, List<Double> me7VoltageList) {
        int previousIndex = mlhfmVoltageToMatchIndex - 1;
        int nextIndex = mlhfmVoltageToMatchIndex + 1;

        double lowValue = previousIndex >= 0 ? mlhfmVoltageList.get(previousIndex) : mlhfmVoltageList.get(mlhfmVoltageToMatchIndex) - 0.0001;
        double highValue = nextIndex <= mlhfmVoltageList.size() - 1 ? mlhfmVoltageList.get(nextIndex) : mlhfmVoltageList.get(mlhfmVoltageToMatchIndex) + 0.0001;

        ArrayList<Integer> indices = new ArrayList<>();

        for (int i = 0; i < me7VoltageList.size(); i++) {
            double voltage = me7VoltageList.get(i);

            if (voltage > lowValue && voltage < highValue) {
                indices.add(i);
            }
        }

        return indices;
    }

    private List<Map<String, List<Double>>> findAfrLogs(Map<String, List<Double>> afrLog) {
        ArrayList<Map<String, List<Double>>> logList = new ArrayList<>();

        List<Double> throttleAngle = afrLog.get(AfrLogFileContract.TPS_HEADER);
        List<Double> rpm = afrLog.get(AfrLogFileContract.RPM_HEADER);
        List<Double> afr = afrLog.get(AfrLogFileContract.AFR_HEADER);

        for (int i = 0; i < throttleAngle.size(); i++) {

            if (throttleAngle.get(i) >= minThrottleAngle && rpm.get(i) >= minRpm && afr.get(i) < maxAfr) {
                if (isValidLogLength(i, minPointsAfr, throttleAngle)) {
                    int endOfLog = findEndOfLog(i, throttleAngle);
                    logList.add(getAfrLog(i, endOfLog, afrLog));
                    i = endOfLog + 1;
                }
            }
        }

        return logList;
    }

    private Map<String, List<Double>> getAfrLog(int start, int end, Map<String, List<Double>> afrLog) {
        List<Double> rpm = afrLog.get(AfrLogFileContract.RPM_HEADER);
        List<Double> afr = afrLog.get(AfrLogFileContract.AFR_HEADER);
        List<Double> tps = afrLog.get(AfrLogFileContract.TPS_HEADER);

        Map<String, List<Double>> log = new HashMap<>();
        log.put(AfrLogFileContract.RPM_HEADER, rpm.subList(start, end));
        log.put(AfrLogFileContract.AFR_HEADER, afr.subList(start, end));
        log.put(AfrLogFileContract.TPS_HEADER, tps.subList(start, end));

        return log;
    }

    private List<Map<String, List<Double>>> findMe7Logs(Map<String, List<Double>> me7Log) {
        ArrayList<Map<String, List<Double>>> logList = new ArrayList<>();

        List<Double> lambdaControl = me7Log.get(Me7LogFileContract.LAMBDA_CONTROL_ACTIVE_HEADER);
        List<Double> throttleAngle = me7Log.get(Me7LogFileContract.THROTTLE_PLATE_ANGLE_HEADER);
        List<Double> rpm = me7Log.get(Me7LogFileContract.RPM_COLUMN_HEADER);

        for (int i = 0; i < throttleAngle.size(); i++) {
            if (throttleAngle.get(i) >= minThrottleAngle && lambdaControl.get(i) == lambdaControlEnabled && rpm.get(i) >= minRpm) {
                if (isValidLogLength(i, minPointsMe7, throttleAngle)) {
                    int endOfLog = findEndOfLog(i, throttleAngle);
                    logList.add(getMe7Log(i, endOfLog, me7Log));
                    i = endOfLog + 1;
                }
            }
        }

        return logList;
    }

    private Map<String, List<Double>> getMe7Log(int start, int end, Map<String, List<Double>> me7Log) {
        List<Double> voltages = me7Log.get(Me7LogFileContract.MAF_VOLTAGE_HEADER);
        List<Double> requestedLambda = me7Log.get(Me7LogFileContract.REQUESTED_LAMBDA_HEADER);
        List<Double> stft = me7Log.get(Me7LogFileContract.STFT_COLUMN_HEADER);
        List<Double> ltft = me7Log.get(Me7LogFileContract.LTFT_COLUMN_HEADER);
        List<Double> lambdaControl = me7Log.get(Me7LogFileContract.LAMBDA_CONTROL_ACTIVE_HEADER);
        List<Double> throttleAngle = me7Log.get(Me7LogFileContract.THROTTLE_PLATE_ANGLE_HEADER);
        List<Double> rpm = me7Log.get(Me7LogFileContract.RPM_COLUMN_HEADER);

        Map<String, List<Double>> log = new HashMap<>();
        log.put(Me7LogFileContract.MAF_VOLTAGE_HEADER, voltages.subList(start, end));
        log.put(Me7LogFileContract.REQUESTED_LAMBDA_HEADER, requestedLambda.subList(start, end));
        log.put(Me7LogFileContract.STFT_COLUMN_HEADER, stft.subList(start, end));
        log.put(Me7LogFileContract.LTFT_COLUMN_HEADER, ltft.subList(start, end));
        log.put(Me7LogFileContract.LAMBDA_CONTROL_ACTIVE_HEADER, lambdaControl.subList(start, end));
        log.put(Me7LogFileContract.THROTTLE_PLATE_ANGLE_HEADER, throttleAngle.subList(start, end));
        log.put(Me7LogFileContract.RPM_COLUMN_HEADER, rpm.subList(start, end));

        return log;
    }

    private boolean isValidLogLength(int start, int minPoints, List<Double> throttleAngle) {
        int minValidIndex = start + minPoints;

        if (minValidIndex < throttleAngle.size()) {
            for (int i = start; i < minValidIndex; i++) {
                if (throttleAngle.get(i) <= minThrottleAngle) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    private int findEndOfLog(int start, List<Double> thottleAngle) {

        for (int i = start; i < thottleAngle.size(); i++) {
            if (thottleAngle.get(i) < minThrottleAngle) {
                return i;
            }
        }

        return thottleAngle.size() - 1;
    }

    private double[] toDoubleArray(Double[] array) {
        double[] result = new double[array.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = array[i];
        }

        return result;
    }
}
