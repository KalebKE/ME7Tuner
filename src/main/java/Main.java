import closedloop.ClosedLoopCorrection;
import openloop.OpenLoopCorrection;
import org.jzy3d.chart.Chart;
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
import parser.afrLog.AfrLogParser;
import parser.me7log.Me7LogParser;
import contract.MlhfmFileContract;
import parser.mlhfm.MlhfmParser;
import writer.MlhfmWriter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        Me7LogParser me7LogParser = new Me7LogParser();
        Map<String, List<Double>> me7Log = me7LogParser.parseLogFile();

        MlhfmParser mlhfmParser = new MlhfmParser();
        Map<String, List<Double>> oldMlhfmMap = mlhfmParser.parse();

        AfrLogParser afrLogParser = new AfrLogParser();
        Map<String, List<Double>> afrLog = afrLogParser.parse();

        OpenLoopCorrection openLoopCorrection = new OpenLoopCorrection();
        openLoopCorrection.correct(me7Log, oldMlhfmMap, afrLog);
        Map<String, List<Double>> newMlhfmMap = openLoopCorrection.getNewMlhfm();

        plotMlhfm(oldMlhfmMap, newMlhfmMap);
        plotVoltageStdDev(oldMlhfmMap, openLoopCorrection.getCorrectedAfrMap());

//        ClosedLoopCorrection closedLoopCorrection = new ClosedLoopCorrection();
//        closedLoopCorrection.correct(me7Log, oldMlhfmMap);
//        Map<String, List<Double>> newMlhfmMap = closedLoopCorrection.getNewMlhfm();
//
//        MlhfmWriter mlhfmWriter = new MlhfmWriter();
//        mlhfmWriter.write(newMlhfmMap);
//
//        plotMlhfm(oldMlhfmMap, newMlhfmMap);
//        plotVoltageStdDev(oldMlhfmMap, closedLoopCorrection.getStdDev());
    }

    private static void plotMlhfm(Map<String, List<Double>> oldMlhfmMap, Map<String, List<Double>> newMlhfmMap) {

        List<Double> voltagesOld = oldMlhfmMap.get(MlhfmFileContract.MAF_VOLTAGE_HEADER);
        List<Double> kgPerHourOld = oldMlhfmMap.get(MlhfmFileContract.KILOGRAM_PER_HOUR_HEADER);

        List<Double> voltagesNew = newMlhfmMap.get(MlhfmFileContract.MAF_VOLTAGE_HEADER);
        List<Double> kgPerHourNew = newMlhfmMap.get(MlhfmFileContract.KILOGRAM_PER_HOUR_HEADER);

        Coord3d[] oldPoints = new Coord3d[voltagesOld.size()];

        for(int i = 0; i < voltagesOld.size(); i++) {
            Coord3d coord = new Coord3d();
            oldPoints[i] = coord.set(voltagesOld.get(i).floatValue(), kgPerHourOld.get(i).floatValue(), 0);
        }

        Coord3d[] newPoints = new Coord3d[voltagesOld.size()];

        for(int i = 0; i < voltagesOld.size(); i++) {
            Coord3d coord = new Coord3d();
            newPoints[i] = coord.set(voltagesNew.get(i).floatValue(), kgPerHourNew.get(i).floatValue(), 0);
        }

        Scatter scatterPlotOld = new Scatter(oldPoints, Color.RED);
        Scatter scatterPlotNew = new Scatter(newPoints, Color.BLUE);

        List<AbstractDrawable> plots = new ArrayList<>();

        plots.add(scatterPlotOld);
        plots.add(scatterPlotNew);

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

    private static void plotVoltageStdDev(Map<String, List<Double>> mlhfm, Map<Double, List<Double>> stdDev) {

        List<Double> voltages = mlhfm.get(MlhfmFileContract.MAF_VOLTAGE_HEADER);
        List<Coord3d> points = new ArrayList<>();

        for (Double voltage : voltages) {
            List<Double> values = stdDev.get(voltage);

            for (Double value : values) {
                Coord3d coord = new Coord3d();
                points.add(coord.set(voltage.floatValue(), value.floatValue(), 0));
            }
        }

        Scatter scatterPlot = new Scatter(points.toArray(new Coord3d[0]), Color.RED);

        Chart chart = AWTChartComponentFactory.chart(Quality.Advanced, IChartComponentFactory.Toolkit.swing);
        ChartScene scene = chart.getScene();
        scene.add(scatterPlot);

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
