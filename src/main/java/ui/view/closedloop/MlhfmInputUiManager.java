package ui.view.closedloop;


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
import ui.viewmodel.MlhfmViewModel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;


public class MlhfmInputUiManager {

    private JFreeChart chart;
    private JPanel mlhfmPanel;
    private JLabel fileLabel;
    private MlhfmViewModel mlhfmViewModel;

    public MlhfmInputUiManager() {
        mlhfmViewModel = MlhfmViewModel.getInstance();
        mlhfmViewModel.getPublishSubject().subscribe(new Observer<Map<String, List<Double>>>() {
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

    JPanel getMlhfmPanel() {
        initChart();
        mlhfmPanel = new JPanel();
        mlhfmPanel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1.0;
        c.gridheight = 1;

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.95;
        mlhfmPanel.add(getChartPanel(), c);

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.05;
        mlhfmPanel.add(getActionPanel(), c);

        return mlhfmPanel;
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

            int returnValue = fc.showOpenDialog(mlhfmPanel);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fc.getSelectedFile();
                mlhfmViewModel.loadFile(selectedFile);
                fileLabel.setText(selectedFile.getAbsolutePath());
            }
        });

        return button;
    }

    private void initChart() {
        XYSeriesCollection dataset = new XYSeriesCollection();

        chart = ChartFactory.createScatterPlot(
                "MHLFM",
                "Voltage", "kg/hr", dataset);

        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setBackgroundPaint(java.awt.Color.WHITE);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        plot.setRenderer(renderer);

        plot.getRenderer().setSeriesPaint(0, Color.BLUE);
        plot.getRenderer().setSeriesStroke(0, new BasicStroke(
                0.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1.0f, new float[] {1f}, 5.0f
        ));
    }

    private void drawChart(Map<String, List<Double>> mlhfmMap) {
        List<Double> voltage = mlhfmMap.get(MlhfmFileContract.MAF_VOLTAGE_HEADER);
        List<Double> kghr = mlhfmMap.get(MlhfmFileContract.KILOGRAM_PER_HOUR_HEADER);

        XYSeries series = new XYSeries("MLHFM");

        for (int i = 0; i < voltage.size(); i++) {
            series.add(voltage.get(i), kghr.get(i));
        }

        XYPlot plot = (XYPlot)chart.getPlot();
        ((XYSeriesCollection)plot.getDataset()).removeAllSeries();
        ((XYSeriesCollection)plot.getDataset()).addSeries(series);
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
