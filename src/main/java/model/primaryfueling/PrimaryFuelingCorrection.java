package model.primaryfueling;

import java.util.List;
import java.util.Map;

public class PrimaryFuelingCorrection {

    public final Map<String, List<Double>> inputMlhfm;
    public final Map<String, List<Double>> correctedMlhfm;
    public final double correctedKrkte;
    public final double correction;

    public PrimaryFuelingCorrection(double correction, Map<String, List<Double>> inputMlhfm, Map<String, List<Double>> correctedMlhfm, double correctedKrkte) {
        this.correction = correction;
        this.inputMlhfm = inputMlhfm;
        this.correctedMlhfm = correctedMlhfm;
        this.correctedKrkte = correctedKrkte;
    }
}
