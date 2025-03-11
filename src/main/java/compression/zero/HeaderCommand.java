package compression.zero;

import compression.Command;
import compression.LzAlgorithm;

public class HeaderCommand extends Command {

    ZeroAlgorithm algorithm;
    
    

    public HeaderCommand(ZeroAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
/*
    public int getCompressedLength() {
        return compressedLength;
    }

    public void setCompressedLength(int compressedLength) {
        this.compressedLength = compressedLength;
    }

    @Override
    public byte[] getBytes() {
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
    }*/
}
