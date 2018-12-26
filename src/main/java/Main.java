import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        LogParser logParser = new LogParser();
        Map<String, List<Double>> logMap = logParser.parseLogFile();

        MlhfmParser mlhfmParser = new MlhfmParser();
        Map<String, List<Double>> mlhfmMap = mlhfmParser.parse();

        ClosedLoopCorrection closedLoopCorrection = new ClosedLoopCorrection();
        Map<String, List<Double>> newMlhfmMap = closedLoopCorrection.correct(logMap, mlhfmMap);

        List<Double> voltagesOld = mlhfmMap.get(MlhfmFileContract.MAF_VOLTAGE_HEADER);
        List<Double> kgPerHourOld = mlhfmMap.get(MlhfmFileContract.KILOGRAM_PER_HOUR_HEADER);

        Coord3d[] points = new Coord3d[voltagesOld.size()];

        for(int i = 0; i < voltagesOld.size(); i++) {
            Coord3d coord = new Coord3d();
            points[i] = coord.set(kgPerHourOld.get(i).floatValue(), voltagesOld.get(i).floatValue(), 0);
        }

        Scatter scatterPlot = new Scatter(points, new org.jzy3d.colors.Color(
                238, 238, 238));

        List<AbstractDrawable> plots = new ArrayList<>();

        plots.add(scatterPlot);

        Chart chart = new Chart(Quality.Nicest);

        chart.getScene().add(plots);

        chart.getView().setViewPositionMode(ViewPositionMode.TOP);

        ChartLauncher.openChart(chart);
    }
}
