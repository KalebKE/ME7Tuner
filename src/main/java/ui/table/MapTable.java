package ui.table;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;

public class MapTable extends JList implements TableModelListener {
    private JTable table;
    private Double[] rowHeaders;
    private Object[] columnHeaders;
    private Double[][] data;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    @SuppressWarnings("unchecked")
    private MapTable(Double[] rowHeaders, Object[] columnHeaders, Double[][] data) {
        this.table = this.createTable(columnHeaders, data);
        this.rowHeaders = rowHeaders;
        this.columnHeaders = columnHeaders;
        this.data = data;

        setAutoscrolls(false);
        setCellRenderer(new RowHeaderRenderer());
        setFixedCellHeight(table.getRowHeight());
        setFixedCellWidth(50);
        setFocusable(false);
        setModel(new TableListModel());
        setOpaque(false);
        setSelectionModel(table.getSelectionModel());
        table.getModel().addTableModelListener(this);
        scrollPane = new JScrollPane(table);
        scrollPane.setRowHeaderView(this);
        scrollPane.setMinimumSize(new Dimension(120, 100));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
    }

    public void tableChanged(TableModelEvent e) {
        if (e.getType() == TableModelEvent.INSERT || e.getType() == TableModelEvent.DELETE) {
            repaint();
        }
    }

    public Double[][] getData() {
        return data;
    }

    public Object[] getColumnHeaders() {
        return columnHeaders;
    }

    public Double[] getRowHeaders() { return rowHeaders; }

    /*
     *  Use the table to implement the ListModel
     */
    class TableListModel extends AbstractListModel {
        public int getSize() {
            return table.getRowCount();
        }

        public Object getElementAt(int index) {
            return String.valueOf(index + 1);
        }
    }

    /*
     *  Use the table row header properties to render each cell
     */
    class RowHeaderRenderer extends DefaultListCellRenderer {
        private DecimalFormat decimalFormat = new DecimalFormat("#.####");

        RowHeaderRenderer() {
            setHorizontalAlignment(CENTER);
            setOpaque(true);
            setBorder(BorderFactory.createRaisedBevelBorder());
            setFont(table.getTableHeader().getFont());
            setBackground(table.getTableHeader().getBackground());
            setForeground(table.getTableHeader().getForeground());
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (isSelected) {
                setBackground(Color.YELLOW);
            } else {
                setBackground(table.getTableHeader().getBackground());
            }

            setText(decimalFormat.format(rowHeaders[index]));

            return this;
        }
    }

    public void setTableData(Double[][] data) {
        this.data = data;

        tableModel.setDataVector(this.data, this.columnHeaders);

        MapUtil.enforceTableColumnWidth(this.table);
    }

    public void setColumnHeaders(Double[] columnHeaders) {
        this.columnHeaders = columnHeaders;
        tableModel.setDataVector(this.data, this.columnHeaders);

        MapUtil.enforceTableColumnWidth(this.table);
    }

    public void setRowHeaders(Double[] rowHeaders) {
        this.rowHeaders = rowHeaders;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    private JTable createTable(Object[] columnHeaders, final Double[][] data) {
        tableModel = new DefaultTableModel(data, columnHeaders) {
            private static final long serialVersionUID = 1L;
            @Override
            public Class<?> getColumnClass(int column) {
                if(columnHeaders instanceof Double[]) {
                    return Double.class;
                } else if(columnHeaders instanceof String[]) {
                    return String.class;
                }

                return Double.class;
            }
        };

        final JTable table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setGridColor(Color.BLACK);

        MapUtil.enforceTableColumnWidth(table);

        // Handle Copy/Paste
        ExcelAdapter excelAdapter = new ExcelAdapter(table);

        table.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                Double[][] values = new Double[MapTable.this.data.length][];

                for(int i = 0; i < MapTable.this.data.length; i++) {
                    values[i] = new Double[MapTable.this.data[i].length];
                    for(int j = 0; j < MapTable.this.data[i].length; j++) {
                        values[i][j] = (Double) table.getModel().getValueAt(i, j);
                    }
                }

                MapTable.this.data = values;
            }
        });

        return table;
    }

    public static MapTable getMapTable(Double[] rowHeaders, Object[] columnHeaders, final Double[][] data) {
        return new MapTable(rowHeaders, columnHeaders, data);
    }
}
