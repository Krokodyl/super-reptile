package editor.table;

import editor.common.Pointer;
import editor.common.PointerType;
import editor.common.Range;
import editor.tree.Block;

import javax.swing.table.AbstractTableModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static resources.Hex.h2;

public class ByteDataModel extends AbstractTableModel implements Serializable {

    byte[] data = new byte[0];
    byte[] metadata = new byte[0];

    private String[] columnNames = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "0A", "0B", "0C", "0D", "0E", "0F"};

    public static int MASK_POINTER = 0x80;
    public static int MASK_DATA_FIXED_LENGTH = 0x40;
    public static int MASK_DATA_DELIMITED = 0x20;
    
    public void loadData(byte[] data) {
        this.data = data;
        metadata = new byte[data.length];
    }

    public void markPointer(Pointer pointer, Range range) {
        for (int i= range.getOffsetStart();i <= range.getOffsetEnd();i++) {
            metadata[i] |= MASK_POINTER;
            pointer.setMetadata(metadata[i]);
        }
        pointer.setMetadata(MASK_POINTER);
    }

    public int markData(Pointer pointer, int baseOffset) {
        int offset = pointer.getValue() - baseOffset;
        if (pointer.getType() == PointerType.FIXED) {
            pointer.setMetadata(MASK_DATA_FIXED_LENGTH);
            for (int i = 0;i<pointer.getExtra();i++) {
                metadata[offset + i] |= MASK_DATA_FIXED_LENGTH;
            }
            offset = offset + pointer.getExtra();
        } else if (pointer.getType() == PointerType.DELIMITED) {
            pointer.setMetadata(MASK_DATA_DELIMITED);
            int extra = pointer.getExtra();
            if (extra >= 0x100) {
                while ((data[offset] & 0xFF) != (extra % 0x100)
                        && (data[offset+1] & 0xFF) != (extra / 0x100)
                )
                    metadata[offset++] |= MASK_DATA_DELIMITED;
                metadata[offset++] |= MASK_DATA_DELIMITED;
                metadata[offset++] |= MASK_DATA_DELIMITED;
            } else {
                while ((data[offset] & 0xFF) != (extra)
                )
                    metadata[offset++] |= MASK_DATA_DELIMITED;
            }
            metadata[offset++] |= MASK_DATA_DELIMITED;
            
        }
        return offset;
    }
    
    public int getPointerValue(Range range) {
        int value = 0;
        value += (data[range.getOffsetStart()] & 0xFF);
        value += (data[range.getOffsetStart()+1] & 0xFF)*0x100;
        if (Math.abs(range.getOffsetEnd()-range.getOffsetStart())>1)
        value += (data[range.getOffsetStart()+2] & 0xFF)*0x10000;
        return value;
    }
    
    public int getMetaData(int offset) {
        if (offset>=metadata.length) return 0;
        return metadata[offset];
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public int getRowCount() {
        return (int) Math.ceil(data.length/16f);
    }

    @Override
    public int getColumnCount() {
        return 16;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex*16+columnIndex>=data.length) return null;
        return h2(data[rowIndex*16+columnIndex]);
    }

    public int getLength() {
        return data.length;
    }

    public void saveModel(ObjectOutputStream oos) throws IOException {
        oos.writeObject(data);
        oos.writeObject(metadata);
    }

    public void loadModel(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        data = (byte[]) ois.readObject();
        metadata = (byte[]) ois.readObject();
    }
}
