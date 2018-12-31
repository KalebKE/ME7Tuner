package ui.view.openloop;

import contract.MlhfmFileContract;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import openloop.OpenLoopCorrection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import ui.table.MapTable;
import ui.viewmodel.openloop.OpenLoopCorrectionViewModel;
import writer.MlhfmWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.util.List;
import java.util.Map;

public class OpenLoopCorrectionUiManager {

    private JFreeChart mlfhmChart;
    private JFreeChart afrCorrectionChart;
    private JPanel correctionPanel;
    private OpenLoopCorrection openLoopCorrection;
    private MapTable mapTable;

    public OpenLoopCorrectionUiManager() {
        OpenLoopCorrectionViewModel.getInstance().getPublishSubject().subscribe(new Observer<OpenLoopCorrection>() {
            @Override
            public void onNext(OpenLoopCorrection openLoopCorrection) {
                OpenLoopCorrectionUiManager.this.openLoopCorrection = openLoopCorrection;
                drawMlhfmChart(openLoopCorrection.inputMlhfm, openLoopCorrection.correctedMlhfm);
                drawMapTable(openLoopCorrection.correctedMlhfm);
                drawAfrCorrectionChart(openLoopCorrection.correctionsAfrMap);
            }

            @Override
            public void onSubscribe(Disposable disposable) {
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    JPanel getCorrectionPanel() {
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
        tabbedPane.addTab("AFR Correction %", null, getAfrCorrectionChartPanel(), "AFR Corrections");

        return tabbedPane;
    }

    private JPanel getMapTablePanel() {
        initMapTable();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLUE);

        panel.add(mapTable.getScrollPane());

        return panel;
    }

    private JPanel getMlhfmChartPanel() {
        initMlhfmChart();
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.gridheight = 1;

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0.95;
        panel.add(new ChartPanel(mlfhmChart), c);

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0.95;
        c.insets = new Insets(0, 8, 0, 0);
        panel.add(getMapTablePanel(), c);

        return panel;
    }

    private JPanel getAfrCorrectionChartPanel() {
        initAfrCorrectionChart();
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

        panel.add(new ChartPanel(afrCorrectionChart), c);

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
        plot.setDomainGridlinePaint(Color.BLACK);
        plot.setRangeGridlinePaint(Color.BLACK);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        plot.setRenderer(renderer);

        plot.getRenderer().setSeriesPaint(0, Color.BLUE);
        plot.getRenderer().setSeriesPaint(1, Color.RED);
    }

    private void initAfrCorrectionChart() {
        XYSeriesCollection dataset = new XYSeriesCollection();

        afrCorrectionChart = ChartFactory.createScatterPlot(
                "AFR Correction %",
                "Voltage", "Correction %", dataset);

        XYPlot plot = (XYPlot) afrCorrectionChart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.BLACK);
        plot.setRangeGridlinePaint(Color.BLACK);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
        plot.setRenderer(renderer);
        plot.getRenderer().setSeriesShape(0, new Ellipse2D.Double(0, 0, 1, 1));
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
                mlhfmWriter.write(selectedFile, openLoopCorrection.correctedMlhfm);
            }
        });

        return button;
    }

    private void initMapTable() {
        mapTable = MapTable.getMapTable(new Double[0], new String[]{"kg/hr"}, new Double[0][]);
    }

    private void drawMapTable(Map<String, List<Double>> mlhfmMap) {
        List<Double> voltage = mlhfmMap.get(MlhfmFileContract.MAF_VOLTAGE_HEADER);
        List<Double> kghr = mlhfmMap.get(MlhfmFileContract.KILOGRAM_PER_HOUR_HEADER);
        Double[][] data = new Double[kghr.size()][1];

        for (int i = 0; i < data.length; i++) {
            data[i][0] = kghr.get(i);
        }

        mapTable.setRowHeaders(voltage.toArray(new Double[0]));
        mapTable.setTableData(data);
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
        ((XYSeriesCollection) plot.getDataset()).removeAllSeries();
        ((XYSeriesCollection) plot.getDataset()).addSeries(inputMlhfmSeries);
        ((XYSeriesCollection) plot.getDataset()).addSeries(correctedMlhfmSeries);
    }

    private void drawAfrCorrectionChart(Map<Double, List<Double>> correctedAfrMap) {

        XYSeries series = new XYSeries("Raw AFR Correction %");

        for (Double voltage : correctedAfrMap.keySet()) {
            List<Double> values = correctedAfrMap.get(voltage);

            for (Double value : values) {
                series.add(voltage, value);
            }
        }

        XYPlot plot = (XYPlot) afrCorrectionChart.getPlot();
        ((XYSeriesCollection) plot.getDataset()).removeAllSeries();
        ((XYSeriesCollection) plot.getDataset()).addSeries(series);
    }
}
