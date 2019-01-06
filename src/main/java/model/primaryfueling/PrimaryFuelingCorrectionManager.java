package model.primaryfueling;

import contract.MlhfmFileContract;
import model.airflow.AirflowEstimation;
import org.apache.commons.math3.stat.StatUtils;
import util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrimaryFuelingCorrectionManager {

    private PrimaryFuelingCorrection primaryFuelingCorrection;

    public PrimaryFuelingCorrection getPrimaryFuelingCorrection() {
        return primaryFuelingCorrection;
    }

    public void correct(double krkte, Map<String, List<Double>> mlhfm, AirflowEstimation airflowEstimation) {
        Map<String, List<Double>> correctedMlhfm = new HashMap<>();

        correctedMlhfm.put(MlhfmFileContract.MAF_VOLTAGE_HEADER, mlhfm.get(MlhfmFileContract.MAF_VOLTAGE_HEADER));

        List<List<Double>> correctionErrors = new ArrayList<>();

        List<List<Double>> estimatedAirflowGramsPerSecondLogs  = airflowEstimation.estimatedAirflowGramsPerSecondLogs;
        List<List<Double>> measuredAirflowGramsPerSecondLogs = airflowEstimation.measuredAirflowGramsPerSecondLogs;

        for(int i = 0; i < estimatedAirflowGramsPerSecondLogs.size(); i++) {
            List<Double> estimatedAirflowGramsPerSecondLog = estimatedAirflowGramsPerSecondLogs.get(i);
            List<Double>measuredAirflowGramsPerSecondLog = measuredAirflowGramsPerSecondLogs.get(i);
            correctionErrors.add(i, new ArrayList<>());

            for(int j = 0; j < estimatedAirflowGramsPerSecondLog.size(); j++) {
                double correctionError = estimatedAirflowGramsPerSecondLog.get(j)/measuredAirflowGramsPerSecondLog.get(j);
                correctionErrors.get(i).add(j, correctionError);
            }
        }

        List<double[]> modes = new ArrayList<>();

        for(List<Double> correctionError:correctionErrors) {
            modes.add(StatUtils.mode(Util.toDoubleArray(correctionError.toArray(new Double[0]))));
        }

        double numSamples = 0;
        double sum = 0;

        for(double[] mode:modes) {
            numSamples += mode.length;
            for(double value: mode) {
                sum += value;
            }
        }

        double mode = sum/numSamples;

        numSamples = 0;
        sum = 0;

        for(List<Double> correctionError:correctionErrors) {
            numSamples += correctionError.size();

            for(Double value: correctionError) {
                sum += value;
            }
        }

        double mean = sum/numSamples;

        double finalCorrectionError = (mode+mean)/2;

        List<Double> airflowKgHr = mlhfm.get(MlhfmFileContract.KILOGRAM_PER_HOUR_HEADER);
        List<Double> correctedAirflowKgHr = new ArrayList<>();

        for(int i = 0; i < airflowKgHr.size(); i++) {
            correctedAirflowKgHr.add(i, airflowKgHr.get(i)* finalCorrectionError);
        }

        correctedMlhfm.put(MlhfmFileContract.KILOGRAM_PER_HOUR_HEADER, correctedAirflowKgHr);

        // Inverse of mlhfm
        double correctedKrkte = krkte * (2 - finalCorrectionError);

        primaryFuelingCorrection = new PrimaryFuelingCorrection(finalCorrectionError, mlhfm, correctedMlhfm, correctedKrkte);
    }
}
