package ui.view.closedloop;

import contract.MlhfmFileContract;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartScene;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;
import parser.mlhfm.MlhfmParser;
import ui.viewmodel.MlhfmViewModel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;


public class ClosedLoopUiManager {

    private Chart chart;
    private JPanel closeLoopPanel;
    private JLabel fileLabel;
    private MlhfmViewModel viewModel;

    public ClosedLoopUiManager() {
        viewModel = new MlhfmViewModel();
        viewModel.getPublishSubject().subscribe(new Observer<Map<String, List<Double>>>() {
            @Override
            public void onNext(Map<String, List<Double>> mlhfmMap) {
                drawChart(mlhfmMap);
            }

            @Override
            public void onSubscribe(Disposable disposable) {}

            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onComplete() {}
        });
    }

    public JPanel getClosedLoopPanel() {
        initChart();

        closeLoopPanel = new JPanel();
        closeLoopPanel.setLayout(new BorderLayout());
        closeLoopPanel.add(getTabbedPane(), BorderLayout.CENTER);

        closeLoopPanel.invalidate();

        return closeLoopPanel;
    }

    private JTabbedPane getTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.LEFT);
        tabbedPane.addTab("MLHFM", null, getMlhfmPanel(), "Voltage to Kg/Hr");
        tabbedPane.addTab("Logs", null, new JPanel(), "ME7 Logging");

        return tabbedPane;
    }

    private JPanel getMlhfmPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1.0;
        c.gridheight = 1;

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.95;
        panel.add(getChartPanel(), c);

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.05;
        panel.add(getFilePanel(), c);

        return panel;
    }

    private JPanel getChartPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;

        Coord3d[] points = new Coord3d[1];

        for (int i = 0; i < points.length; i++) {
            Coord3d coord = new Coord3d();
            points[i] = coord.set(0,0 , 0);

        }
        Scatter scatterPlot = new Scatter(points, Color.WHITE);

        ChartScene scene = chart.getScene();
        scene.add(scatterPlot);

        AWTCameraMouseController controller = new AWTCameraMouseController(chart);
        Component canvas = (Component) chart.getCanvas();
        canvas.addMouseListener(controller);
        canvas.addMouseMotionListener(controller);
        canvas.addMouseWheelListener(controller);

        panel.add(canvas, c);

        return panel;
    }

    private JPanel getFilePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;

        JButton button = getFileButton();
        panel.add(button, c);

        c.gridx = 0;
        c.gridy = 1;

        fileLabel = new JLabel("No File Selected");
        panel.add(fileLabel, c);

        return panel;
    }


    private JButton getFileButton() {
        JButton button = new JButton("Load MLHFM");

        button.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new CSVFileFilter());

            int returnValue = fc.showOpenDialog(closeLoopPanel);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fc.getSelectedFile();
                viewModel.loadFile(selectedFile);
                fileLabel.setText(selectedFile.getAbsolutePath());
            }
        });

        return button;
    }

    private void initChart() {
        chart = AWTChartComponentFactory.chart(Quality.Advanced, IChartComponentFactory.Toolkit.swing);
        chart.getAxeLayout().setXAxeLabel("Voltage");
        chart.getAxeLayout().setYAxeLabel("Airflow Kg/Hr");
        chart.getView().setViewPositionMode(ViewPositionMode.TOP);
    }

    private void drawChart(Map<String, List<Double>> mlhfmMap) {
        List<Double> voltage = mlhfmMap.get(MlhfmFileContract.MAF_VOLTAGE_HEADER);
        List<Double> kghr = mlhfmMap.get(MlhfmFileContract.KILOGRAM_PER_HOUR_HEADER);

        Coord3d[] points = new Coord3d[voltage.size()];

        for (int i = 0; i < voltage.size(); i++) {
            Coord3d coord = new Coord3d();
            points[i] = coord.set(voltage.get(i).floatValue(), kghr.get(i).floatValue() , 0);
        }

        Scatter scatterPlot = new Scatter(points, Color.RED);

        ChartScene scene = chart.getScene();
        scene.getGraph().getAll().clear();
        scene.add(scatterPlot, true);
    }

    private class CSVFileFilter extends FileFilter {
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String name = f.getName();
            String[] parts = name.split("\\.");
            if(parts.length > 0) {
                String ext = parts[parts.length - 1];
                return ext.trim().equalsIgnoreCase("csv");
            }

            return false;
        }

        @Override
        public String getDescription() {
            return "CSV";
        }
    }
}
