package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

/**
 * The class for displaying a hash table
 *
 * @author darius
 */
public class Table extends JTable {

    public static final String ARROW = "\u2794";

    public void setModel(String[][] tableContent, String delimiter, int maxChainSize, int colWidth) {
        if (tableContent == null || delimiter == null) {
            throw new IllegalArgumentException("Table content or delimiter is null");
        }

        if (maxChainSize < 0 || colWidth < 0) {
            throw new IllegalArgumentException("Table column width or max chain size is <0: " + colWidth + ", " + maxChainSize);
        }

        setModel(new TableModel(tableContent, delimiter, maxChainSize));
        appearance(colWidth);
    }

    private void appearance(int colWidth) {
        setShowGrid(false);
        // Cell placement in the center
        DefaultTableCellRenderer toCenter = new DefaultTableCellRenderer();
        toCenter.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < getColumnCount(); i++) {
            if (i == 0) {
                getColumnModel().getColumn(i).setPreferredWidth(1);
                // Setting the style of zero-column cells
                getColumnModel().getColumn(i).setCellRenderer(toCenter);
            } else if (i % 2 != 0) {
                getColumnModel().getColumn(i).setPreferredWidth(30);
                // Setting the cell style for columns with arrows
                getColumnModel().getColumn(i).setCellRenderer(toCenter);
            } else {
                getColumnModel().getColumn(i).setMaxWidth(colWidth);
                getColumnModel().getColumn(i).setMinWidth(colWidth);
            }
        }

        // Table header
        getTableHeader().setResizingAllowed(false);
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        // Center the headers
        ((DefaultTableCellRenderer) getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        // Set the tooltips view
        String value = (String) getValueAt(row, column);
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            jc.setToolTipText(value);
        }
        // Non-empty Cells containing anything other than an arrow are coloured orange
        if (value != null && !value.equals("") && !value.equals(ARROW)) {
            c.setBackground(Color.ORANGE);
        } // Colour the remaining cells white
        else {
            c.setBackground(Color.WHITE);
        }

        int rendererWidth = c.getPreferredSize().width;
        TableColumn tableColumn = getColumnModel().getColumn(column);
        tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
        return c;
    }
}
