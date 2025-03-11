package compression;

import resources.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static resources.Hex.h;

public class LzDecompressor {

    ByteArrayOutputStream decompressedData = new ByteArrayOutputStream();
    String compressedBytes = "";
    
    //final static int REPEAT_BIT = 0x00;
    //final static int WRITE_BIT = 0x01;
    
    int end = 0;
    boolean verbose = false;
    boolean writeData = true;
    
    private LzAlgorithm algorithm;

    public LzDecompressor(LzAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void decompressData(byte[] input, int start, String output, boolean verbose) {
        this.verbose = verbose;
        decompressData(input, start, output);
    }

    public void decompressData(byte[] input, int start) {
        decompressData(input, new int[]{start}, "src/main/resources/trash/" + h(start) + ".data");
    }

    public void decompressData(byte[] input, int start, String output) {
        decompressData(input, new int[]{start}, "src/main/resources/trash/" + h(start) + ".data");
    }

    public void decompressData(byte[] input, int[] offsets) {
        decompressData(input, offsets, "src/main/resources/trash/" + h(offsets[0]) + ".data");
    }
    
    public void decompressData(byte[] input, int[] offsets, String output) {
        decompressedData = new ByteArrayOutputStream();
        List<Command> commands = buildCommands(input, offsets);
        /*System.out.print("Compressed bytes:\t\t");
        for (Command command : commands) {
            byte[] bytes = command.getBytes();
            compressedBytes += bytesToHex(bytes);
            System.out.print(bytesToHex(bytes));
        }
        System.out.println();*/
        try {
            processCommands(commands);
            byte[] bytes = decompressedData.toByteArray();
            byte[] first = Arrays.copyOfRange(bytes, 0, 20*16);
            if (writeData && output!=null) {
                Utils.saveData(output, bytes);
            }
            System.out.println(h(offsets[0]) + "-" + h(end) + "\t\t" + h(bytes.length) + "\t" + Hex.getHexString(bytes));
            //System.out.println(h(start)+"-"+h(end)+"\t\t"+h(bytes.length)+"\t");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public List<Command> buildCommands(byte[] input, int[] offsets) {
        return buildCommands(input, offsets, 10);
    }
    
    public List<Command> buildCommands(byte[] input, int[] offsets, int commandCount) {
        int start = offsets[0];
        int offset = start;
        if (verbose) System.out.println("buildCommands");
        List<Command> commands = new ArrayList<>();
        //int offset = start;

        /*byte[] bytes = Arrays.copyOfRange(input, offset, offset + algorithm.headerSize);
        offset += bytes.length;*/
        HeaderCommand headerCommand = algorithm.buildHeaderCommand(input, offsets);
        offset += algorithm.headerSize;
        /*HeaderCommand headerCommand = new HeaderCommand(
                bytes
        );*/
        commands.add(headerCommand);
        if (verbose) System.out.println(headerCommand);
        int decompressedLength = 0;
        int compressedLength = 0;
        int flagCount = 0;
        //int offsetEnd = start+4+headerCommand.getCompressedLength();
        //this.end = offsetEnd+2;
        //System.out.println("input[offsetEnd+2] = "+h(input[offsetEnd+2]));
        if (algorithm==null) {
            //int a =  ((input[offsetEnd+2] & 0xC0) == 0) ? 4 : 3;
            //if (a == 4) algorithm = REPEAT_ALGORITHM.REPEAT_ALGORITHM_SIZE_4BITS;
            //else algorithm = REPEAT_ALGORITHM.REPEAT_ALGORITHM_SIZE_3BITS;
            //algorithm = headerCommand.getRepeatAlgorithm();
            System.out.println("Algorithm: "+algorithm);
        }
        boolean end = false;
        int count = 0;
        while (!end) {
            FlagCommand flagCommand = new FlagCommand(input[offset++]);
            commands.add(flagCommand);
            flagCount++;
            if (verbose) System.out.println(flagCommand);
            count = 0;
            while (count<8 
                    && count<commandCount 
                    && !algorithm.endDecompression(headerCommand, decompressedLength, compressedLength, flagCount)
                    && !algorithm.footerReached(headerCommand, start, offset, flagCount)
            ) {
                //int bit = flagCommand.getBit(count++);
                int bit = algorithm.getCommandBit(flagCommand, (count++));
                if (bit==algorithm.writeBit) {
                    WriteCommand writeCommand = new WriteCommand(input[offset++]);
                    if (verbose) System.out.println(decompressedLength+"\t"+writeCommand);
                    commands.add(writeCommand);
                    decompressedLength++;
                    compressedLength++;
                }
                else {
                    //byte a = input[offset++];
                    //byte b = input[offset++];
                    //Byte c = null;
                    //RepeatCommand repeatCommand = null;
                    /*if ((b & 0x08) == 0x08) {
                        c = input[offset++];
                    }*/
                    RepeatCommand repeatCommand = algorithm.buildRepeatCommand(input, offset);
                    offset += algorithm.byteCount(repeatCommand);
                    
                    //if (decompressedLength<repeatCommand.getLength()) repeatCommand.setLength(decompressedLength);
                    if (verbose) System.out.println(h(offset)+"\t"+decompressedLength+"\t"+repeatCommand);
                    commands.add(repeatCommand);
                    decompressedLength+=repeatCommand.getLength();
                    compressedLength++;
                    compressedLength++;
                }
            }
            if (algorithm.endDecompression(headerCommand, decompressedLength, compressedLength, flagCount)
                    || algorithm.footerReached(headerCommand, start, offset, flagCount)
                    || count==commandCount
            ) end = true;
            //end = true;
        }
        if (algorithm.footerReached(headerCommand, start, offset, flagCount)) {
            FooterCommand footerCommand = algorithm.buildFooterCommand(headerCommand, input, start, offset);
            commands.add(footerCommand);
            if (verbose) System.out.println(footerCommand);
            offset += algorithm.footerSize;
            count = 0;
            FlagCommand flagCommand = null;
            if (algorithm.footerSize>0) {
                flagCommand = new FlagCommand(input[offset++]);
                commands.add(flagCommand);
            } else {
                offset--;
                flagCommand = new FlagCommand(input[offset++]);
            }
            if (verbose) System.out.println(flagCommand);
            while (count<footerCommand.getCommandCount()) {
                int bit = flagCommand.getBit(count++);
                if (bit==algorithm.writeBit) {
                    WriteCommand writeCommand = new WriteCommand(input[offset++]);
                    if (verbose) System.out.println(decompressedLength+"\t"+writeCommand);
                    commands.add(writeCommand);
                    decompressedLength++;
                    compressedLength++;
                }
                else {
                    /*byte a = input[offset++];
                    byte b = input[offset++];
                    Byte c = null;*/
                    //RepeatCommand repeatCommand = null;
                    /*if ((b & 0x08) == 0x08) {
                        c = input[offset++];
                    }*/
                    //repeatCommand = algorithm.buildRepeatCommand(a, b, c);
                    RepeatCommand repeatCommand = algorithm.buildRepeatCommand(input, offset);
                    offset += algorithm.byteCount(repeatCommand);
                    
                    //if (decompressedLength<repeatCommand.getLength()) repeatCommand.setLength(decompressedLength);
                    if (verbose) System.out.println(h(offset)+"\t"+decompressedLength+"\t"+repeatCommand);
                    commands.add(repeatCommand);
                    decompressedLength+=repeatCommand.getLength();
                    compressedLength++;
                    compressedLength++;
                }
            }
        }
        this.end = offset;
        /*if (offset<input.length) {
            int endCommandCount = input[offset++];
            if ((endCommandCount & 0x3F) > 0) {
                commands.addAll(buildCommands(input, start, offset, endCommandCount & 0x3F, algorithm));
            }
        }*/

        //System.out.println("\t\t"+h(start)+"\t"+h(offsetEnd-2)+"\t\t");
        //if (verbose) System.out.println("end\t"+h(offsetEnd-2));
        if (verbose) System.out.println(h(offset));
        
        return commands;
    }
    
    public void processCommands(List<Command> commands) throws IOException {
        algorithm.processCommands(commands, decompressedData, verbose);
        /*if (verbose) System.out.println("processCommands");
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
        }*/
    }

    
/*
    public RepeatCommand buildRepeatCommand4(byte a,byte b) {
        int length = ((b & 0xFF) >>> 4) + 3;
        int shift = ((b & 0x0F) * x("100")) + (a & 0xFF);
        RepeatCommand repeatCommand = new RepeatCommand(shift, length);
        return repeatCommand;
    }

    public RepeatCommand buildRepeatCommand3(byte a,byte b) {
        int length = ((b & 0xFF) >>> 3) + 3;
        int shift = ((b & 0x07) * x("100")) + (a & 0xFF);
        RepeatCommand repeatCommand = new RepeatCommand(shift, length);
        return repeatCommand;
    }*/

    public byte[] getDecompressedData() {
        return decompressedData.toByteArray();
    }

    public String getCompressedBytes() {
        return compressedBytes;
    }
}
