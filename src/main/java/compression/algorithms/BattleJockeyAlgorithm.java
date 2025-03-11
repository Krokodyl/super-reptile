package compression.algorithms;

import compression.*;

public class BattleJockeyAlgorithm extends LzAlgorithm {
    
    BattleJockeyAlgorithm() {
        super(0x00, 0x01, REPEAT_ALGORITHM.REPEAT_ALGORITHM_SIZE_4BITS);
        headerSize = 3;
        footerSize = 0;
    }

    @Override
    public RepeatCommand buildRepeatCommand(byte[] data, int offset) {
        byte a = data[offset++];
        byte b = data[offset++];
        byte c = data[offset];
        int length =  (b & 0xF0) >> repeatAlgorithm.getShift();
        length = length + 2;
        if ((b & 0xFF) < 0x0F) length = ((c & 0xFF) + 0x11) +1; 
        //int length = ((b & 0xFF) >>> algorithm.getShift()) + 3;
        //a = (byte) (a + ((b & 0xF0) * 0x100));
        int shift = ((b & repeatAlgorithm.getMask())*0x100) + (a & 0xFF);
        shift = shift + 1;
        RepeatCommand repeatCommand = new RepeatCommand(shift, length, this);
        return repeatCommand;
    }

    @Override
    public byte[] getBytes(RepeatCommand repeatCommand) {
        //return super.getBytes(repeatCommand);
        byte[] bytes = new byte[2];
        if (repeatCommand.getLength()>(0x0F+2)) bytes = new byte[3];
        byte a = (byte) ((repeatCommand.getShift()-1) & 0xFF);
        byte b = (byte) ((repeatCommand.getLength()-2 << repeatAlgorithm.getShift()) + (((repeatCommand.getShift()-1) >> 8) & repeatAlgorithm.getMask()));
        bytes[0] = a;
        bytes[1] = b;
        if (repeatCommand.getLength()>(0x0F+2)) {
            bytes[1] = (byte) ((repeatCommand.getShift()-1) / 0x100);
            bytes[2] = (byte) (repeatCommand.getLength() - 0x11 - 1);
        }
        return bytes;
    }

    public HeaderCommand buildHeaderCommand(byte[] data, int[] offsets) {
        int start = offsets[0];
        int valueHeader = (data[start+2] & 0xFF)*0x100+(data[start+1] & 0xFF);
        //int offsetFooter = start + 2 + valueHeader;
        //int compressedLength = (data[1] & 0xFF)*x("100")+(bytes[0] & 0xFF)+headerSize+2;
        HeaderCommand headerCommand = new HeaderCommand(this);
        headerCommand.setFlagCount(valueHeader >> 3);
        headerCommand.setFooterCommandCount(valueHeader & 0x07);
        return headerCommand;
    }

    @Override
    public FooterCommand buildFooterCommand(HeaderCommand headerCommand, byte[] data, int start, int offset) {
        int count = headerCommand.getFooterCommandCount();
        //int valueHeader = (data[offset+2] & 0xFF)*0x100+(data[offset+1] & 0xFF);
        FooterCommand footerCommand = new FooterCommand(count, 0, this);
        return footerCommand;
    }

    @Override
    public byte[] getBytes(FooterCommand footerCommand) {
        byte[] bytes = new byte[3];
        bytes[0] = (byte) (footerCommand.getCommandCount() + 0x40);
        bytes[1] = (byte) ((footerCommand.getOffset() % 0x100) & 0xFF);
        bytes[2] = (byte) ((footerCommand.getOffset() / 0x100) & 0xFF);
        return bytes;
    }

    @Override
    public byte[] getBytes(HeaderCommand headerCommand) {
        byte[] bytes = new byte[3];
        int a = headerCommand.getOffsetFooter() % 0x100;
        int b = headerCommand.getOffsetFooter() / 0x100;
        bytes[0] = (byte) 0xFE;
        bytes[1] = (byte) (a & 0xFF);
        bytes[2] = (byte) (b & 0xFF);
        return bytes;
    }

    @Override
    public boolean endDecompression(HeaderCommand headerCommand, int decomp, int comp, int flagCount) {
        if (headerCommand.getFlagCount()>0) return (flagCount>headerCommand.getFlagCount());
        else if (headerCommand.getDecompressedLength()>0) return (decomp<headerCommand.getDecompressedLength());
        else if (headerCommand.getCompressedLength()>0) return (comp>=headerCommand.getCompressedLength());
        return false;
    }

    @Override
    public boolean footerReached(HeaderCommand headerCommand, int startOffset, int offset, int flagCount) {
        if (headerCommand.getFlagCount()>0) return (flagCount>headerCommand.getFlagCount());
        return false;
    }
    
    public boolean hasTerminalByte() {
        return true;
    }

    public byte getTerminalByte() {
        return 0x40;
    }
}
