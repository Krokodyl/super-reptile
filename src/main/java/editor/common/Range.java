package editor.common;

public class Range {
    
    int offsetStart, offsetEnd;

    public Range() {
    }

    public Range(int offsetStart, int offsetEnd) {
        this.offsetStart = offsetStart;
        this.offsetEnd = offsetEnd;
    }

    public boolean inRange(int offset) {
        if (offsetStart == offsetEnd) return offset == offsetStart;
        else if (offsetStart < offsetEnd) return (offset>=offsetStart && offset<=offsetEnd);
        else return (offset>=offsetEnd && offset<=offsetStart);
    }
    
    public int getOffsetStart() {
        return offsetStart;
    }

    public void setOffsetStart(int offsetStart) {
        this.offsetStart = offsetStart;
    }

    public int getOffsetEnd() {
        return offsetEnd;
    }

    public void setOffsetEnd(int offsetEnd) {
        this.offsetEnd = offsetEnd;
    }
}
