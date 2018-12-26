import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

import java.util.*;

public class ClosedLoopCorrection {
    public Map<String, List<Double>> correct(Map<String, List<Double>> log, Map<String, List<Double>> mlhfm) {

        Map<Double, List<Double>> correctionError = new HashMap<>();

        for(Double voltage:mlhfm.get(MlhfmFileContract.MAF_VOLTAGE_HEADER)) {
            correctionError.put(voltage, new ArrayList<>());
        }

        List<Double> voltages = log.get(LogFileContract.MAF_VOLTAGE_HEADER);
        List<Double> stft = log.get(LogFileContract.STFT_COLUMN_HEADER);
        List<Double> ltft = log.get(LogFileContract.LTFT_COLUMN_HEADER);
        List<Double> lambdaControl = log.get(LogFileContract.LAMBDA_CONTROL_ACTIVE_HEADER);
        List<Double> throttleAngle = log.get(LogFileContract.THROTTLE_PLATE_ANGLE_HEADER);
        List<Double> rpm = log.get(LogFileContract.RPM_COLUMN_HEADER);

        for(int i = 0; i < stft.size(); i++) {
            // Closed loop only and not idle
            if(lambdaControl.get(i) == 1 && throttleAngle.get(i) > 5 && rpm.get(i) > 1000) {
                double inputVoltage = voltages.get(i);
                int index = Math.abs(Collections.binarySearch(mlhfm.get(MlhfmFileContract.MAF_VOLTAGE_HEADER), inputVoltage));
                double voltageKey = mlhfm.get(MlhfmFileContract.MAF_VOLTAGE_HEADER).get(index);
                double stftValue = stft.get(i) - 1;
                double ltftValue = ltft.get(i) - 1;
                correctionError.get(voltageKey).add(stftValue + ltftValue);
            }
        }

        List<Double> tempCorrections = new ArrayList<>();
        int maxCorrectionIndex = 0;
        int index = 0;
        Mean mean = new Mean();
        for(Double voltage:mlhfm.get(MlhfmFileContract.MAF_VOLTAGE_HEADER)) {
            List<Double> corrections = correctionError.get(voltage);
            double meanValue = mean.evaluate(toDoubleArray(corrections.toArray(new Double[0])), 0, corrections.size());
            double[] mode = StatUtils.mode(toDoubleArray(corrections.toArray(new Double[0])));

            double correction = meanValue;

            for(int i = 0; i < mode.length; i++) {
                correction += mode[i];
            }

            correction /= 1 + mode.length;

            if(!Double.isNaN(correction)) {
                maxCorrectionIndex = index;
            }

            tempCorrections.add(correction);

            index++;
        }

        boolean foundStart = false;
        int lastValidCorrectionIndex = -1;
        for(int i = 0; i < tempCorrections.size(); i++) {
            Double value = tempCorrections.get(i);

            if(value.isNaN() && !foundStart) {
                tempCorrections.set(i, 0d);
            }else if(!value.isNaN() && !foundStart) {
                foundStart = true;
                lastValidCorrectionIndex = i;
            } else if(value.isNaN()) {
                if(i < maxCorrectionIndex) {
                    tempCorrections.set(i, tempCorrections.get(lastValidCorrectionIndex));
                } else {
                    tempCorrections.set(i, 0d);
                }
            } else {
                lastValidCorrectionIndex = i;
            }
        }

        System.out.println(Arrays.toString(tempCorrections.toArray()));

        Map<Double, Double> totalCorrectionError = new HashMap<>();
        List<Double> voltage = mlhfm.get(MlhfmFileContract.MAF_VOLTAGE_HEADER);

        for(int i = 0; i < voltage.size(); i++) {
            totalCorrectionError.put(voltage.get(i), tempCorrections.get(i));
        }

        Map<String, List<Double>> newMlhfm = new HashMap<>();
        newMlhfm.put(MlhfmFileContract.MAF_VOLTAGE_HEADER, voltage);

        List<Double> oldKgPerHour = mlhfm.get(MlhfmFileContract.KILOGRAM_PER_HOUR_HEADER);
        List<Double> newKgPerHour = new ArrayList<>();

        for(int i = 0; i < voltage.size(); i++) {
            double oldKgPerHourValue = oldKgPerHour.get(i);
            double totalCorrectionErrorValue = totalCorrectionError.get(voltage.get(i));

            newKgPerHour.add(i, oldKgPerHourValue *((totalCorrectionErrorValue/100) + 1));
        }

        newMlhfm.put(MlhfmFileContract.KILOGRAM_PER_HOUR_HEADER, newKgPerHour);

        return newMlhfm;
    }

    private double[] toDoubleArray(Double[] array) {
        double[] result = new double[array.length];

        for(int i = 0; i < result.length; i++) {
            result[i] = array[i];
        }

        return result;
    }
}
