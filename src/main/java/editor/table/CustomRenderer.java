package editor.table;

import editor.Voodoo;
import editor.common.Range;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CustomRenderer extends DefaultTableCellRenderer {

    boolean selectionEnabled = false;
    Range selection;

    public CustomRenderer(Range rs) {
        super();
        selection = rs;
    }

    public boolean isSelectionEnabled() {
        return selectionEnabled;
    }

    public void setSelectionEnabled(boolean selectionEnabled) {
        this.selectionEnabled = selectionEnabled;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JComponent  component = (JComponent)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        //Object valueAt = table.getValueAt(row, column);
        //System.out.println(valueAt);
        /*
        double valueAt = (double) table.getValueAt(row, column);
        double compareValue = (double) table.getValueAt(row, column - 1);
        */

        ByteDataModel model = (ByteDataModel) table.getModel();
        
        
        int offset = Voodoo.getOffset(row, column);
        int metaData = model.getMetaData(offset);
        if ((metaData & ByteDataModel.MASK_POINTER) == ByteDataModel.MASK_POINTER) {
            component.setForeground(Color.RED);
        } else {
            component.setForeground(Color.BLACK);
        }
        if ((metaData & ByteDataModel.MASK_DATA_FIXED_LENGTH) == ByteDataModel.MASK_DATA_FIXED_LENGTH) {
            component.setBackground(Color.YELLOW);
        } else  if ((metaData & ByteDataModel.MASK_DATA_DELIMITED) == ByteDataModel.MASK_DATA_DELIMITED) {
            component.setBackground(Color.GREEN);
        } else {
            component.setBackground(Color.WHITE);
        }
        

        if (selectionEnabled && selection.inRange(offset)) {
            Border border = BorderFactory.createCompoundBorder();
            if (column==0 || !selection.inRange(offset-1)) {
                border = BorderFactory.createCompoundBorder(border, BorderFactory.createMatteBorder(0,1,0,0, Color.BLACK));
            }
            if (!selection.inRange(offset-16)) {
                border = BorderFactory.createCompoundBorder(border, BorderFactory.createMatteBorder(1,0,0,0, Color.BLACK));
            }
            if (column==15 || !selection.inRange(offset+1)) {
                border = BorderFactory.createCompoundBorder(border, BorderFactory.createMatteBorder(0,0,0,1, Color.BLACK));
            }
            if (!selection.inRange(offset+16)) {
                border = BorderFactory.createCompoundBorder(border, BorderFactory.createMatteBorder(0,0,1,0, Color.BLACK));
            }
            
            component.setBorder(border);
            
            //cellComponent.setBackground(Color.green);
            System.out.println(row+" "+column);
        } else {
            component.setBorder(null);
        }

        return component;
    }
}