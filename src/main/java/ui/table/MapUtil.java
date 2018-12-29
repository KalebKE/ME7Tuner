package ui.table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.text.DecimalFormat;

class MapUtil {
    static void enforceTableColumnWidth(JTable table) {
        TableColumn column;
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(50);
            column.setMaxWidth(50);

            DefaultTableCellRenderer centerRenderer = new DecimalFormatRenderer();
            centerRenderer.setHorizontalAlignment( JLabel.CENTER );
            table.getColumnModel().getColumn(i).setCellRenderer( centerRenderer );
        }
    }

    static class DecimalFormatRenderer extends DefaultTableCellRenderer {

        private final DecimalFormat formatter = new DecimalFormat( "#.##" );

        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if(value != null) {
                value = formatter.format(value);
            } else {
                value = 0.0;
            }

            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column );
        }
    }

}
