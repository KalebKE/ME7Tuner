package closedloop;

import java.util.List;
import java.util.Map;

public class ClosedLoopCorrection {
    public final Map<String, List<Double>> inputMlhfm;
    public final Map<String, List<Double>> correctedMlhfm;
    public final Map<Double, List<Double>> filteredVoltageStdDev;

    public ClosedLoopCorrection(Map<String, List<Double>> inputMlhfm, Map<String, List<Double>> correctedMlhfm, Map<Double, List<Double>> filteredVoltageStdDev) {
        this.inputMlhfm = inputMlhfm;
        this.correctedMlhfm = correctedMlhfm;
        this.filteredVoltageStdDev = filteredVoltageStdDev;
    }
}
