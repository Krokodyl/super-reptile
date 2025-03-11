package compression;

import resources.Hex;

public class RepeatCommand extends Command {
    
    int shift;
    int length;
    
    LzAlgorithm algorithm;
    //REPEAT_ALGORITHM algorithm;

    public RepeatCommand(int shift, int length, LzAlgorithm algorithm) {
        this.shift = shift;
        this.length = length;
        this.algorithm = algorithm;
    }

    /*public byte[] getBytes() {
        byte[] bytes = new byte[2];
        int a = shift;
        int b = 0;
        if (a>0xFF) {
            int aHigh = (a & 0xF00) >>> 8;
            b = aHigh << 4;
        }
        if (length-3<=0x7) {
            b = b + (length-3);
            bytes[0] = (byte) a;
            bytes[1] = (byte) b;
        }
        else {
            bytes = new byte[3];
            int c = (length-3) & 0xFF;
            int bLow = ((length-3) & 0xF00) >>> 8;
            b = b + bLow + 8;
            bytes[0] = (byte) a;
            bytes[1] = (byte) b;
            bytes[2] = (byte) c;
        }
        return bytes;
    }*/

    public int getShift() {
        return shift;
    }

    public void setShift(int shift) {
        this.shift = shift;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "RepeatCommand{" +
                "shift=" + shift +
                ", length=" + length +
                ", bytes=" + Hex.getHexString(getBytes()) +
                '}';
    }

    @Override
    public byte[] getBytes() {
        return algorithm.getBytes(this);
    }
}
