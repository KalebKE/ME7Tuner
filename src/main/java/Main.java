import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.ChartScene;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

import javax.swing.*;
import java.awt.*;
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
            points[i] = coord.set(voltagesOld.get(i).floatValue(), kgPerHourOld.get(i).floatValue(), 0);
        }

        Scatter scatterPlot = new Scatter(points, Color.RED);

        List<AbstractDrawable> plots = new ArrayList<>();

        plots.add(scatterPlot);

        Chart chart = AWTChartComponentFactory.chart(Quality.Advanced, IChartComponentFactory.Toolkit.swing);
        ChartScene scene = chart.getScene();
        scene.add(plots);

        AWTCameraMouseController controller = new AWTCameraMouseController(chart);
        Component canvas = (Component) chart.getCanvas();
        canvas.addMouseListener(controller);
        canvas.addMouseMotionListener(controller);
        canvas.addMouseWheelListener(controller);

        chart.getView().setViewPositionMode(ViewPositionMode.TOP);

        JFrame frame = new JFrame();
        frame.setTitle("MAF Scaler");
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(canvas);
       
        frame.setVisible(true);
    }
}
