package compression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class LzAlgorithm {
    
    /*ALGORITHM_01(0x00, 0x01, REPEAT_ALGORITHM.REPEAT_ALGORITHM_SIZE_5BITS),
    ALGORITHM_02(0x01, 0x00, REPEAT_ALGORITHM.REPEAT_ALGORITHM_SIZE_5BITS);*/

    public int headerSize;
    public int footerSize;
    public int repeatBit;
    public int writeBit;
    public REPEAT_ALGORITHM repeatAlgorithm;

    public LzAlgorithm(int repeatBit, int writeBit, REPEAT_ALGORITHM repeatAlgorithm) {
        this.repeatBit = repeatBit;
        this.writeBit = writeBit;
        this.repeatAlgorithm = repeatAlgorithm;
    }

    /*public RepeatCommand buildRepeatCommand(byte a, byte b, Byte c) {
        return repeatAlgorithm.buildRepeatCommand(a, b, c);
    }*/
    public RepeatCommand buildRepeatCommand(byte[] data, int offset) {
        return repeatAlgorithm.buildRepeatCommand(data, offset);
    }

    public int byteCount(RepeatCommand repeatCommand) {
        return getBytes(repeatCommand).length;
    }

    public HeaderCommand buildHeaderCommand(byte[] data, int[] offsets) {
        int decompressedLength = (data[offsets[0]+1] & 0xFF)*0x100+(data[offsets[0]] & 0xFF);
        HeaderCommand headerCommand = new HeaderCommand(this);
        headerCommand.setDecompressedLength(decompressedLength);
        return headerCommand;
    }

    public byte[] getBytes(RepeatCommand repeatCommand) {
        int shift = repeatCommand.getShift();
        int length = repeatCommand.getLength();
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
    }

    public byte[] getBytes(HeaderCommand headerCommand) {
        byte[] bytes = new byte[2];
        int a = headerCommand.getDecompressedLength() % 0x100;
        int b = headerCommand.getDecompressedLength() / 0x100;
        bytes[0] = (byte) (a & 0xFF);
        bytes[1] = (byte) (b & 0xFF);
        return bytes;
    }

    public FooterCommand buildFooterCommand(HeaderCommand headerCommand, byte[] input, int start, int offset) {

        return null;
    }

    public byte[] getBytes(FooterCommand footerCommand) {
        return new byte[0];
    }

    public boolean endDecompression(HeaderCommand headerCommand, int decompressedLength, int compressedLength, int flagCount) {
        return false;
    }

    public boolean footerReached(HeaderCommand headerCommand, int start, int offset, int flagCount) {
        return false;
    }

    public boolean hasTerminalByte() {
        return false;
    }
    
    public byte getTerminalByte() {
        return 0;
    }

    public int getCommandBit(FlagCommand flagCommand, int offset) {
        byte tmp = flagCommand.flags;
        tmp = (byte) ((flagCommand.flags >> (offset)) & 0x01);
        return tmp;
    }

    public void processCommands(List<Command> commands, ByteArrayOutputStream decompressedData, boolean verbose) throws IOException {
        if (verbose) System.out.println("processCommands");
        for (Command command : commands) {
            if (verbose) System.out.println("process "+command);
            if (command instanceof WriteCommand) {
                WriteCommand writeCommand = (WriteCommand) command;
                decompressedData.write(writeCommand.getBytes());
            }
            if (command instanceof RepeatCommand) {
                RepeatCommand repeatCommand = (RepeatCommand) command;
                int shift = repeatCommand.getShift();
                int length = repeatCommand.getLength();
                byte[] output = decompressedData.toByteArray();
                int repeatStart = (output.length)-shift;
                //if (repeatStart<0) repeatStart=0;
                int repeatIndex = repeatStart;
                while (length>0) {
                    byte data = 0;
                    if (repeatIndex>=0 && output.length!=0) {
                        if (repeatIndex == output.length) data = 0;
                        else data = output[repeatIndex];
                    }
                    repeatIndex++;
                    if (repeatIndex>output.length-1) repeatIndex=repeatStart;
                    decompressedData.write(data);
                    length--;
                }
            }
        }
    }
}
