package ui.view.openloop;


import contract.AfrLogFileContract;
import contract.Me7LogFileContract;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import openloop.util.AfrLogUtil;
import openloop.util.Me7LogUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import preferences.openloop.OpenLoopLogFilterPreferences;
import ui.viewmodel.openloop.OpenLoopAfrLogViewModel;
import ui.viewmodel.openloop.OpenLoopCorrectionViewModel;
import ui.viewmodel.openloop.OpenLoopMe7LogViewModel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;


public class OpenLoopMe7LogUiManager {

    private JFreeChart chart;
    private JPanel openLoopLogPanel;
    private JLabel me7FileLabel;
    private JLabel afrFileLabel;
    private OpenLoopMe7LogViewModel openLoopMe7LogViewModel;
    private OpenLoopAfrLogViewModel openLoopAfrLogViewModel;
    private  XYSeriesCollection me7Dataset;
    private  XYSeriesCollection afrDataset;

    private File me7LogFile;
    private File afrLogFile;

    public OpenLoopMe7LogUiManager() {

        me7Dataset = new XYSeriesCollection();
        afrDataset = new XYSeriesCollection();

        openLoopMe7LogViewModel = OpenLoopMe7LogViewModel.getInstance();
        openLoopMe7LogViewModel.getPublishSubject().subscribe(new Observer<Map<String, List<Double>>>() {
            @Override
            public void onNext(Map<String, List<Double>> me7LogMap) {
                drawMe7LogChart(me7LogMap);
            }

            @Override
            public void onSubscribe(Disposable disposable) {}

            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onComplete() {}
        });

        openLoopAfrLogViewModel = OpenLoopAfrLogViewModel.getInstance();
        openLoopAfrLogViewModel.getPublishSubject().subscribe(new Observer<Map<String, List<Double>>>() {
            @Override
            public void onNext(Map<String, List<Double>> afrLogMap) {
                drawAfrLogChart(afrLogMap);
            }

            @Override
            public void onSubscribe(Disposable disposable) {}

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    JPanel getMe7LogPanel() {
        initChart();
        openLoopLogPanel = new JPanel();
        openLoopLogPanel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1.0;
        c.gridheight = 1;

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.95;
        openLoopLogPanel.add(getChartPanel(), c);

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.05;
        openLoopLogPanel.add(getActionPanel(), c);

        return openLoopLogPanel;
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

        panel.add(new ChartPanel(chart), c);

        return panel;
    }

    private JPanel getActionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1;

        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;

        JButton button = getConfigureFilterButton();
        panel.add(button, c);

        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;

        button = getMe7FileButton();
        panel.add(button, c);

        c.gridx = 1;
        c.gridy = 1;

        me7FileLabel = new JLabel("No File Selected");
        panel.add(me7FileLabel, c);

        c.gridx = 2;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;

        button = getAfrFileButton();
        panel.add(button, c);

        c.gridx = 2;
        c.gridy = 1;

        afrFileLabel = new JLabel("No File Selected");
        panel.add(afrFileLabel, c);

        c.gridx = 3;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_END;

        button = getApplyCorrectionButton();
        panel.add(button, c);

        return panel;
    }

    private JButton getApplyCorrectionButton() {
        JButton button = new JButton("Generate Correction");

        button.addActionListener(e -> {
            OpenLoopCorrectionViewModel.getInstance().generateCorrection();
        });

        return button;
    }

    private JButton getConfigureFilterButton() {
        JButton button = new JButton("Configure Filter");

        button.addActionListener(e -> {
            OpenLoopMe7LogFilterConfigPanel filterConfigPane = new OpenLoopMe7LogFilterConfigPanel();

            int result = JOptionPane.showConfirmDialog(openLoopLogPanel, filterConfigPane, "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                for (OpenLoopMe7LogFilterConfigPanel.FieldTitle fieldTitle : OpenLoopMe7LogFilterConfigPanel.FieldTitle.values()) {
                    switch (fieldTitle) {
                        case MIN_THROTTLE_ANGLE:
                           OpenLoopLogFilterPreferences.setMinThrottleAnglePreference(Double.valueOf(filterConfigPane.getFieldText(fieldTitle)));
                            break;
                        case MIN_RPM:
                            OpenLoopLogFilterPreferences.setMinRpmPreference(Double.valueOf(filterConfigPane.getFieldText(fieldTitle)));
                            break;
                        case MIN_ME7_POINTS:
                            OpenLoopLogFilterPreferences.setMinMe7PointsPreference(Integer.valueOf(filterConfigPane.getFieldText(fieldTitle)));
                            break;
                        case MIN_AFR_POINTS:
                            OpenLoopLogFilterPreferences.setMinAfrPointsPreference(Integer.valueOf(filterConfigPane.getFieldText(fieldTitle)));
                            break;
                        case MAX_AFR:
                            OpenLoopLogFilterPreferences.setMaxAfrPreference(Double.valueOf(filterConfigPane.getFieldText(fieldTitle)));
                    }
                }

                if(this.me7LogFile != null) {
                    loadMe7File(this.me7LogFile);
                }

                if(this.afrLogFile != null) {
                    loadAfrFile(this.afrLogFile);
                }
            }
        });

        return button;
    }

    private JButton getMe7FileButton() {
        JButton button = new JButton("Load ME7 Logs");

        button.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new CSVFileFilter());

            int returnValue = fc.showOpenDialog(openLoopLogPanel);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                this.me7LogFile = fc.getSelectedFile();
                loadMe7File(this.me7LogFile);
            }
        });

        return button;
    }

    private void loadMe7File(File file) {
        openLoopMe7LogViewModel.loadFile(file);
        me7FileLabel.setText(file.getName());
    }

    private JButton getAfrFileButton() {
        JButton button = new JButton("Load AFR Logs");

        button.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new CSVFileFilter());

            int returnValue = fc.showOpenDialog(openLoopLogPanel);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                this.afrLogFile = fc.getSelectedFile();
                loadAfrFile(this.afrLogFile);
            }
        });

        return button;
    }

    private void loadAfrFile(File file) {
        openLoopAfrLogViewModel.loadFile(file);
        afrFileLabel.setText(file.getName());
    }

    private void initChart() {
        XYSeriesCollection dataset = new XYSeriesCollection();

        chart = ChartFactory.createScatterPlot(
                "Fueling",
                "RPM", "AFR", dataset);

        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.black);
        plot.setRangeGridlinePaint(Color.black);

        XYLineAndShapeRenderer rendererMe7 = new XYLineAndShapeRenderer(true, false);
        rendererMe7.setAutoPopulateSeriesPaint(false);
        rendererMe7.setDefaultPaint(Color.BLUE);
        plot.setRenderer(0, rendererMe7);

        XYLineAndShapeRenderer rendererAfr = new XYLineAndShapeRenderer(true, false);
        rendererAfr.setAutoPopulateSeriesPaint(false);
        rendererAfr.setDefaultPaint(Color.RED);
        plot.setRenderer(1, rendererAfr);
    }

    private void drawMe7LogChart(Map<String, List<Double>> me7LogMap) {
        me7Dataset.removeAllSeries();

        List<Map<String, List<Double>>> me7LogList = Me7LogUtil.findMe7Logs(me7LogMap, 80, 0, 2000, 75);

        System.out.println(me7LogList.size());

        int logCount = 1;
        for (Map<String, List<Double>> map : me7LogList) {
            XYSeries series = new XYSeries("Desired AFR " + logCount++);

            List<Double> requestedAfr = map.get(Me7LogFileContract.REQUESTED_LAMBDA_HEADER);
            List<Double> rpm = map.get(Me7LogFileContract.RPM_COLUMN_HEADER);

            for (int i = 0; i < rpm.size(); i++) {
                series.add(rpm.get(i).doubleValue(), requestedAfr.get(i)*14.7);
            }

            me7Dataset.addSeries(series);
        }

        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setDataset(0, me7Dataset);
    }

    private void drawAfrLogChart(Map<String, List<Double>> afrLogMap) {
        afrDataset.removeAllSeries();

        List<Map<String, List<Double>>> afrLogList = AfrLogUtil.findAfrLogs(afrLogMap, 80, 2000, 15, 150);

        int logCount = 1;
        for (Map<String, List<Double>> map : afrLogList) {
            XYSeries series = new XYSeries("Actual AFR " + logCount++);

            List<Double> actualAfr = map.get(AfrLogFileContract.AFR_HEADER);
            List<Double> rpm = map.get(AfrLogFileContract.RPM_HEADER);

            for (int i = 0; i < rpm.size(); i++) {
                series.add(rpm.get(i).doubleValue(), actualAfr.get(i).doubleValue());
            }

            afrDataset.addSeries(series);
        }

        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setDataset(1, afrDataset);
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
