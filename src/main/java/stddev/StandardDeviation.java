package stddev;

import contract.Me7LogFileContract;
import contract.MlhfmFileContract;
import util.Util;

import java.util.*;

public class StandardDeviation {

    public static Map<Double, List<Double>> getStandDeviationMap(Map<String, List<Double>> me7Logs, Map<String, List<Double>> mlhfm, int numSampleWindow) {
        Map<Double, List<Double>> rawVoltageStdDev = new HashMap<>();

        for (Double voltage : mlhfm.get(MlhfmFileContract.MAF_VOLTAGE_HEADER)) {
            rawVoltageStdDev.put(voltage, new ArrayList<>());
        }

        List<Double> me7Voltages = me7Logs.get(Me7LogFileContract.MAF_VOLTAGE_HEADER);
        List<Double> me7voltageStdDev = getStandardDeviation(me7Voltages, numSampleWindow);

        for (int i = 0; i < me7Voltages.size(); i++) {
            double me7Voltage = me7Voltages.get(i);
            int mlhfmVoltageIndex = Math.abs(Collections.binarySearch(mlhfm.get(MlhfmFileContract.MAF_VOLTAGE_HEADER), me7Voltage));
            double mlhfmVoltageKey = mlhfm.get(MlhfmFileContract.MAF_VOLTAGE_HEADER).get(mlhfmVoltageIndex);
            rawVoltageStdDev.get(mlhfmVoltageKey).add(me7voltageStdDev.get(i));
        }

        return rawVoltageStdDev;
    }

    public static ArrayList<Double> getStandardDeviation(List<Double> values, int numSampleWindow) {
        org.apache.commons.math3.stat.descriptive.moment.StandardDeviation standardDeviation = new org.apache.commons.math3.stat.descriptive.moment.StandardDeviation();
        double[] inputValues = Util.toDoubleArray(values.toArray(new Double[0]));
        ArrayList<Double> result = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            if (i < 20 || i >= values.size() - 20) {
                result.add(i, 0d);
            } else {
                double stdDev = standardDeviation.evaluate(inputValues, i, numSampleWindow);
                result.add(i, stdDev);
            }
        }

        return result;
    }
}
