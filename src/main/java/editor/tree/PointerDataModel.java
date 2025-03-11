package editor.tree;

import editor.common.Pointer;
import editor.common.Range;
import editor.table.ByteDataModel;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PointerDataModel implements TreeModel, Serializable {

    Block root = new Block(0, 0, (byte)0);
    
    List<TreeModelListener> listeners = new ArrayList<>();

    public void setRoot(Block block) {
        root = block;
    }
    
    Map<Integer, Pointer> pointerMap = new HashMap<>();

    public void addPointer(Range selection, Pointer pointer, int baseOffset, int endOffset) {
        Block bestBlock = findBestBlock(root, selection);
        bestBlock.addChildren(new Block(selection.getOffsetStart(), selection.getOffsetEnd(), (byte) ByteDataModel.MASK_POINTER));
        bestBlock.sortChildren();

        int offset = pointer.getValue() - baseOffset;
        Block dataBlock = new Block(offset, endOffset, pointer.getMetadata());
        
        bestBlock = findBestBlock(root, new Range(offset, endOffset));
       
        bestBlock.addChildren(dataBlock);
        bestBlock.sortChildren();
        
        
        notifyListeners();
    }

    private void notifyListeners() {
        for (TreeModelListener listener : listeners) {
            listener.treeStructureChanged(new TreeModelEvent(this,
                    new Object[] {root}));
        }

    }

    public Block findBestBlock(Block b, Range range) {
        Block best = null;
        if (b.contains(range)) best = b;
        List<Block> children = b.children;
        boolean foundBetterChild = false;
        boolean end = false;
        int i = 0;
        while (!foundBetterChild && !end) {
            if (i<children.size()) {
                Block child = children.get(i);
                if (child.contains(range)) {
                    best = child;
                    foundBetterChild = true;
                }
            } else end = true;
            i++;
        }
        if (foundBetterChild) return findBestBlock(best, range);
        return best;
    }
    
    /*public Pointer getPointer(int offset) {
        return pointerMap.get(offset);
    }*/

    @Override
    public Object getRoot() {
        return root;
    }

    public Object getChild(Object parent, int index) {
        Block b = (Block) parent;
        return b.getChildAt(index);
        /*Block block = findBlock((Block) parent);
        return block.getChildAt(index);*/
    }

    @Override
    public int getChildCount(Object parent) {
        Block b = (Block) parent;
        return b.getChildCount();
        /*Block block = findBlock((Block) parent);
        if (block!=null) return block.getChildCount();
        else return 0;*/
    }

    @Override
    public boolean isLeaf(Object node) {
        Block b = (Block) node;
        return b.isLeaf();
        /*Block block = findBlock((Block) node);
        if (block!=null) return block.getChildCount()==0;
        else return false;*/
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {

    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        Block b = (Block) parent;
        return b.getIndex((Block) child);
        /*Block block = findBlock((Block) parent);
        if (block!=null) return block.getIndex((Block) child);
        else return -1;*/
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    private Block findBlock(Block b) {
        Block res = null;
        if (root == b) {
            res = root;
        } else {
            for (Block child : b.children) {
                if (res == null) res = findBlock(child);
                else continue;
            }
        }
        return res;
    }
    
    public void saveModel(ObjectOutputStream oos) throws IOException {
        oos.writeObject(root);
    }
    
    public void loadModel(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        root = (Block) ois.readObject();
        notifyListeners();
    }
}
