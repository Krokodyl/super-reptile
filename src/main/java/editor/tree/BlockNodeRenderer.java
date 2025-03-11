package editor.tree;

import editor.common.PointerType;
import editor.table.ByteDataModel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.Objects;

public class BlockNodeRenderer extends DefaultTreeCellRenderer {

    private static final String SPAN_FORMAT = "<span style='color:%s;'>%s</span>";

    /*private final ImageIcon icon = new ImageIcon(
            Objects.requireNonNull(BlockNodeRenderer.class.getResource("/images/employee.png")));*/

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = node.getUserObject();
        if (node instanceof Block) {
            Block b = (Block) node;
            byte metadata = b.getMetadata();
            String color = "";
            if ((metadata & ByteDataModel.MASK_POINTER) == ByteDataModel.MASK_POINTER) {
                color = "red";
            } 
            if ((metadata & ByteDataModel.MASK_DATA_FIXED_LENGTH) == ByteDataModel.MASK_DATA_FIXED_LENGTH) {
                color = "yellow";
            }
            if ((metadata & ByteDataModel.MASK_DATA_DELIMITED) == ByteDataModel.MASK_DATA_DELIMITED) {
                color = "green";
            }
            String text = String.format(SPAN_FORMAT, color, b);
            this.setText("<html>" + text + "</html>");
            //this.setIcon(icon);
        }
        return this;
    }
}
