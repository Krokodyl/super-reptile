package editor.common;

public class Pointer {
    
    int offset;
    int value;
    PointerType type;
    int extra;
    byte metadata;

    public Pointer(int offset, int value, PointerType type, int extra) {
        this.offset = offset;
        this.value = value;
        this.type = type;
        this.extra = extra;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public PointerType getType() {
        return type;
    }

    public void setType(PointerType type) {
        this.type = type;
    }

    public int getExtra() {
        return extra;
    }

    public void setExtra(int extra) {
        this.extra = extra;
    }

    public byte getMetadata() {
        return metadata;
    }

    public void setMetadata(int metadata) {
        this.metadata |= metadata;
    }
}
