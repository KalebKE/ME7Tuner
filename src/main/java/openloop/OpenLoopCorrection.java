package openloop;

import java.util.List;
import java.util.Map;

public class OpenLoopCorrection {
    public final Map<String, List<Double>> inputMlhfm;
    public final Map<String, List<Double>> correctedMlhfm;
    public final Map<Double, List<Double>> correctionsAfrMap;

    public OpenLoopCorrection(Map<String, List<Double>> inputMlhfm, Map<String, List<Double>> correctedMlhfm, Map<Double, List<Double>> correctionsAfrMap) {
        this.inputMlhfm = inputMlhfm;
        this.correctedMlhfm = correctedMlhfm;
        this.correctionsAfrMap = correctionsAfrMap;
    }
}
