package ui.map.axis;


import ui.map.ExcelAdapter;
import ui.map.MapUtil;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MapAxis {

    private JTable table;
    private JScrollPane scrollPane;
    private DefaultTableModel tableModel;
    private Double[][] data;


    private MapAxis(Double[][] data) {
        this.table = this.createAxis(data);
        this.data = data;

        scrollPane = new JScrollPane(table);
    }


    public void setTableData(Double[][] data) {
        this.data = data;

        tableModel.setDataVector(this.data, new Double[data[0].length]);

        MapUtil.enforceTableColumnWidth(this.table);
    }

    private JTable createAxis(final Double[][] data) {
        tableModel = new DefaultTableModel(data, data[0]) {
            private static final long serialVersionUID = 1L;
            @Override
            public Class<?> getColumnClass(int column) {
                return Double.class;
            }
        };

        final JTable table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setGridColor(Color.BLACK);

        table.setTableHeader(null);

        MapUtil.enforceTableColumnWidth(table);

        // Handle Copy/Paste
        ExcelAdapter excelAdapter = new ExcelAdapter(table);

        table.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                Double[][] values = new Double[MapAxis.this.data.length][];

                for(int i = 0; i < MapAxis.this.data.length; i++) {
                    values[i] = new Double[MapAxis.this.data[i].length];
                    for(int j = 0; j < MapAxis.this.data[i].length; j++) {
                        values[i][j] = (Double) table.getModel().getValueAt(i, j);
                    }
                }

                MapAxis.this.data = values;
            }
        });

       return table;
    }

    public Double[][] getData() {
        return data;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public static MapAxis getMapAxis(final Double[][] data) {
        return new MapAxis(data);
    }
}
