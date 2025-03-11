package compression;

public class HeaderCommand extends Command {

    LzAlgorithm algorithm;
    
    int compressedLength;
    int decompressedLength;
    int offsetFooter;
    int footerCommandCount;
    int flagCount;

    public HeaderCommand(LzAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public int getCompressedLength() {
        return compressedLength;
    }

    public void setCompressedLength(int compressedLength) {
        this.compressedLength = compressedLength;
    }

    public int getDecompressedLength() {
        return decompressedLength;
    }

    public void setDecompressedLength(int decompressedLength) {
        this.decompressedLength = decompressedLength;
    }

    public int getOffsetFooter() {
        return offsetFooter;
    }

    public void setOffsetFooter(int offsetFooter) {
        this.offsetFooter = offsetFooter;
    }

    public int getFlagCount() {
        return flagCount;
    }

    public void setFlagCount(int flagCount) {
        this.flagCount = flagCount;
    }

    public boolean hasFooter() {
        return offsetFooter>0;
    }

    public int getFooterCommandCount() {
        return footerCommandCount;
    }

    public void setFooterCommandCount(int footerCommandCount) {
        this.footerCommandCount = footerCommandCount;
    }
    
    /*public HeaderCommand(byte a, byte b) {
        decompressedLength = (b & 0xFF)*x("100")+(a & 0xFF);
    }*/
    
    /*public HeaderCommand(int decompressedLength) {
        this.decompressedLength = decompressedLength;
    }*/
    
    /*public REPEAT_ALGORITHM getRepeatAlgorithm() {
        return REPEAT_ALGORITHM.REPEAT_ALGORITHM_SIZE_5BITS;
    }*/
    
    @Override
    public byte[] getBytes() {
        /*byte[] bytes = new byte[2];
        int a = decompressedLength % x("100");
        int b = decompressedLength / x("100");
        bytes[0] = (byte) (a & 0xFF);
        bytes[1] = (byte) (b & 0xFF);
        return bytes;*/
        return algorithm.getBytes(this);
    }

    @Override
    public String toString() {
        return "HeaderCommand{" +
                "algorithm=" + algorithm +
                ", compressedLength=" + compressedLength +
                ", decompressedLength=" + decompressedLength +
                ", offsetFooter=" + offsetFooter +
                ", flagCount=" + flagCount +
                '}';
    }
}
