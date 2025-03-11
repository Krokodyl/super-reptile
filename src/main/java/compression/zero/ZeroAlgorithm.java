package compression.zero;

public class ZeroAlgorithm {


    public HeaderCommand buildHeaderCommand(byte[] data, int start) {
        int decompressedLength = (data[start+1] & 0xFF)*0x100+(data[0] & 0xFF);
        HeaderCommand headerCommand = new HeaderCommand(this);
       // headerCommand.setDecompressedLength(decompressedLength);
        return headerCommand;
    }
    
}
