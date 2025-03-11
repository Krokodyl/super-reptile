package editor.table;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class RowHeaderRenderer extends JLabel implements ListCellRenderer {

    public RowHeaderRenderer(JTable table) {
        JTableHeader header = table.getTableHeader();
        setOpaque(true);
        //setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        setHorizontalAlignment(LEFT);
        setForeground(Color.BLUE);
        setBackground(table.getBackground());
        setFont(table.getFont());
    }

    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        setText((value == null) ? "" : value.toString());
        return this;
    }
}