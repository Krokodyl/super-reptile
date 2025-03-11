package compression.algorithms;

import asm.Env;
import compression.*;
import resources.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class TadaimaAlgorithm extends LzAlgorithm {
    
    byte[] referenceTable83C5 = Hex.parseHex(
        "FF 1F FF 3F FF 7F FF FF 08 09 0A 0B 0C 0D 0E 0F 04 04 04 04 05 05 05 05 06 06 06 06 07 07 07 07 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 03 03 03 03 03 03 03 03 03 03 03 03 03 03 03 03 1A 18 14 14 10 10 10 10 0C 0C 0C 0C 0C 0C 0C 0C 08 08 08 08 08 08 08 08 08 08 08 08 08 08 08 08 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04"
    );
    byte[] referenceTable8405 = Hex.parseHex(
    "1A 18 14 14 10 10 10 10 0C 0C 0C 0C 0C 0C 0C 0C 08 08 08 08 08 08 08 08 08 08 08 08 08 08 08 08 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 04 08 08 0C 0C 0C 0C 00 00 00 00 00 00 00 00 01 01 01 01 01 01 01 01 02 02 02 02 02 02 02 02 03 03 03 03 03 03 03 03 04 04 04 04 05 05 05 05 06 06 06 06 07 07 07 07 08 08 09 09 0A 0A 0B 0B 0C"
    );
    
    TadaimaAlgorithm() {
        super(0x00, 0x01, REPEAT_ALGORITHM.REPEAT_ALGORITHM_SIZE_5BITS);
        headerSize = 2;
        footerSize = 0;
        
    }

    @Override
    public RepeatCommand buildRepeatCommand(byte[] data, int offset) {
        byte a = data[offset++];
        byte b = data[offset];
        int length =  (b & 0xFF) >> repeatAlgorithm.getShift();
        //int length = ((b & 0xFF) >>> algorithm.getShift()) + 3;
        //a = (byte) (a + ((b & 0xF0) * 0x100));
        int shift = ((b & repeatAlgorithm.getMask())*0x100) + (a & 0xFF);
        length = length + 3;
        RepeatCommand repeatCommand = new RepeatCommand(shift, length, this);
        return repeatCommand;
    }

    @Override
    public byte[] getBytes(RepeatCommand repeatCommand) {
        //return super.getBytes(repeatCommand);
        byte a = (byte) (repeatCommand.getShift() & 0xFF);
        byte b = (byte) ((repeatCommand.getLength()-3 << repeatAlgorithm.getShift()) + ((repeatCommand.getShift() >> 8) & repeatAlgorithm.getMask()));
        byte[] bytes = new byte[2];
        bytes[0] = a;
        bytes[1] = b;
        return bytes;
    }

    public HeaderCommand buildHeaderCommand(byte[] data, int[] offsets) {
        //int start = offsets[0];
        int valueHeader = (data[offsets[1]+4] & 0xFF)*0x100+(data[offsets[1]+3] & 0xFF);
        //int offsetFooter = start + 2 + valueHeader;
        //int compressedLength = (data[1] & 0xFF)*x("100")+(bytes[0] & 0xFF)+headerSize+2;
        HeaderCommand headerCommand = new HeaderCommand(this);
        headerCommand.setDecompressedLength(valueHeader);
        //headerCommand.setFlagCount(valueHeader >> 3);
        //headerCommand.setFooterCommandCount(valueHeader & 0x07);
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
        if (headerCommand.getDecompressedLength()>0) return (decomp>=headerCommand.getDecompressedLength());
        return false;
    }

    @Override
    public boolean footerReached(HeaderCommand headerCommand, int startOffset, int offset, int flagCount) {
        return false;
    }
    
    public boolean hasTerminalByte() {
        return false;
    }

    public byte getTerminalByte() {
        return 0x40;
    }

    @Override
    public int getCommandBit(FlagCommand flagCommand, int offset) {
        byte tmp = flagCommand.getFlags();
        tmp = (byte) ((flagCommand.getFlags() >> (7-offset)) & 0x01);
        return tmp;
    }

    public void processCommands(List<Command> commands, ByteArrayOutputStream decompressedData, boolean verbose) throws IOException {
        Env env = new Env();
        if (verbose) System.out.println("processCommands");
        for (Command command : commands) {
            if (verbose) System.out.println("process "+command);
            if (command instanceof WriteCommand) {
                WriteCommand writeCommand = (WriteCommand) command;
                decompressedData.write(writeCommand.getBytes());
            }
            if (command instanceof RepeatCommand) {
                /**
                 * 8282FF  A2 20          LDX #$20
                 * 828301  38             SEC
                 * 828302  8A             TXA
                 * 828303  E5 B9          SBC $B9
                 * 828305  85 B7          STA $B7
                 * 828307  AA             TAX
                 * 828308  A5 B5          LDA $B5
                 * 82830A  FC 5F 83       JSR ($835F,X)
                 * 82830D  A6 B9          LDX $B9
                 * 82830F  3D AB 83       AND $83AB,X
                 * 828312  05 B3          ORA $B3
                 * 828314  85 B3          STA $B3
                 * 828316  60             RTS
                 *                      ----------------
                 */
                env.ldx((byte) 0x20);
                RepeatCommand repeatCommand = (RepeatCommand) command;
                /*int shift = repeatCommand.getShift();
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
                */
                }
            }
        }
}
