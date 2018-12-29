package ui.view.closedloop;

import closedloop.ClosedLoopCorrection;
import contract.MlhfmFileContract;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import preferences.ClosedLoopLogFilterPreferences;
import stddev.StandardDeviation;
import ui.viewmodel.ClosedLoopCorrectionViewModel;
import writer.MlhfmWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.util.List;
import java.util.Map;

public class ClosedLoopCorrectionUiManager {

    private JFreeChart mlfhmChart;
    private JFreeChart stdDevChart;
    private JPanel correctionPanel;
    private ClosedLoopCorrection closedLoopCorrection;

    public ClosedLoopCorrectionUiManager() {
        ClosedLoopCorrectionViewModel closedLoopCorrectionViewModel = ClosedLoopCorrectionViewModel.getInstance();
        closedLoopCorrectionViewModel.getPublishSubject().subscribe(new Observer<ClosedLoopCorrection>() {

            @Override
            public void onNext(ClosedLoopCorrection closedLoopCorrection) {
                ClosedLoopCorrectionUiManager.this.closedLoopCorrection = closedLoopCorrection;
                drawMlhfmChart(closedLoopCorrection.inputMlhfm, closedLoopCorrection.correctedMlhfm);
                drawStdDevChart(closedLoopCorrection.filteredVoltageStdDev, closedLoopCorrection.correctedMlhfm);
            }

            @Override
            public void onSubscribe(Disposable disposable) {}

            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onComplete() { }
        });
    }

    JPanel getCorrectionPanel() {
        initMlhfmChart();
        correctionPanel = new JPanel();
        correctionPanel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1.0;
        c.gridheight = 1;

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.95;
        correctionPanel.add(getTabbedPane(), c);

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.05;
        correctionPanel.add(getActionPanel(), c);

        return correctionPanel;
    }

    private JTabbedPane getTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
        tabbedPane.addTab("MLHFM", null, getMlhfmChartPanel(), "Voltage to Kg/Hr");
        tabbedPane.addTab("Std Dev", null, getStdDevChartPanel(), "Standard Deviation");

        return tabbedPane;
    }

    private JPanel getMlhfmChartPanel() {
        initStdDevChart();
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

        panel.add(new ChartPanel(mlfhmChart), c);

        return panel;
    }

    private JPanel getStdDevChartPanel() {
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

        panel.add(new ChartPanel(stdDevChart), c);

        return panel;
    }

    private JPanel getActionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;

        JButton button = getFileButton();
        panel.add(button, c);

        return panel;
    }

    private void initMlhfmChart() {
        XYSeriesCollection dataset = new XYSeriesCollection();

        mlfhmChart = ChartFactory.createScatterPlot(
                "MHLFM",
                "Voltage", "kg/hr", dataset);

        XYPlot plot = (XYPlot) mlfhmChart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        plot.setRenderer(renderer);

        plot.getRenderer().setSeriesPaint(0, Color.BLUE);
        plot.getRenderer().setSeriesStroke(0, new BasicStroke(
                0.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1.0f, new float[] {1f}, 5.0f
        ));

        plot.getRenderer().setSeriesPaint(1, Color.RED);
        plot.getRenderer().setSeriesStroke(1, new BasicStroke(
                0.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1.0f, new float[] {1f}, 5.0f
        ));
    }

    private void initStdDevChart() {
        XYSeriesCollection dataset = new XYSeriesCollection();

        stdDevChart = ChartFactory.createScatterPlot(
                "Standard Deviation",
                "Voltage", "Std Dev", dataset);

        XYPlot plot = (XYPlot)stdDevChart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
        plot.setRenderer(renderer);

        plot.getRenderer().setSeriesShape(0, new Ellipse2D.Double(0,0,1,1));
        plot.getRenderer().setSeriesPaint(0, Color.BLUE);
    }

    private JButton getFileButton() {
        JButton button = new JButton("Save MLHFM");

        button.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser();

            int returnValue = fc.showOpenDialog(correctionPanel);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fc.getSelectedFile();

                MlhfmWriter mlhfmWriter = new MlhfmWriter();
                mlhfmWriter.write(selectedFile, closedLoopCorrection.correctedMlhfm);
            }
        });

        return button;
    }

    private void drawMlhfmChart(Map<String, List<Double>> inputMlhfmMap, Map<String, List<Double>> correctedMlhfmMap) {
        List<Double> voltage = inputMlhfmMap.get(MlhfmFileContract.MAF_VOLTAGE_HEADER);
        List<Double> kghr = inputMlhfmMap.get(MlhfmFileContract.KILOGRAM_PER_HOUR_HEADER);

        XYSeries inputMlhfmSeries = new XYSeries("MLHFM");

        for (int i = 0; i < voltage.size(); i++) {
            inputMlhfmSeries.add(voltage.get(i), kghr.get(i));
        }

       voltage = correctedMlhfmMap.get(MlhfmFileContract.MAF_VOLTAGE_HEADER);
         kghr = correctedMlhfmMap.get(MlhfmFileContract.KILOGRAM_PER_HOUR_HEADER);

        XYSeries correctedMlhfmSeries = new XYSeries("Corrected MLHFM");

        for (int i = 0; i < voltage.size(); i++) {
            correctedMlhfmSeries.add(voltage.get(i), kghr.get(i));
        }

        XYPlot plot = (XYPlot) mlfhmChart.getPlot();
        ((XYSeriesCollection)plot.getDataset()).removeAllSeries();
        ((XYSeriesCollection)plot.getDataset()).addSeries(inputMlhfmSeries);
        ((XYSeriesCollection)plot.getDataset()).addSeries(correctedMlhfmSeries);
    }

    private void drawStdDevChart(Map<Double, List<Double>> stdDev, Map<String, List<Double>> mlhfmMap) {

        List<Double> voltages = mlhfmMap.get(MlhfmFileContract.MAF_VOLTAGE_HEADER);

        XYSeries series = new XYSeries("Std Dev");

        for (Double voltage : voltages) {
            List<Double> values = stdDev.get(voltage);

            for (Double value : values) {
                series.add(voltage, value);
            }
        }

        XYPlot plot = (XYPlot)stdDevChart.getPlot();
        ((XYSeriesCollection)plot.getDataset()).removeAllSeries();
        ((XYSeriesCollection)plot.getDataset()).addSeries(series);
    }
}
