package editor.tree;

import editor.common.Range;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static resources.Hex.h4;

public class Block extends DefaultMutableTreeNode implements Comparable<Block>, Serializable {
    
    int start;
    int length;
    byte metadata;
    
    Block parent = null;
    List<Block> children = new ArrayList<>();

    public Block(int start, int end, byte metadata) {
        this.start = start;
        this.length = end-start;
        this.metadata = metadata;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte getMetadata() {
        return metadata;
    }

    public void setMetadata(byte metadata) {
        this.metadata = metadata;
    }

    public void addChildren(Block b) {
        children.add(b);
        b.setParent(this);
    }

    private void setParent(Block block) {
        parent = block;
    }

    public boolean contains(Range range) {
        return start <= range.getOffsetStart() && start+length>=range.getOffsetEnd();
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return children.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return children.size();
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        return children.indexOf(node);
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public String toString() {
        if (parent == null) return "Root";
        else return String.format("%s - %s", h4(start), h4(start+length));
    }

    public void sortChildren() {
        Collections.sort(children);
    }

    @Override
    public int compareTo(Block o) {
        return start-o.getStart();
    }
}
