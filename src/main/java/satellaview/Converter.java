package satellaview;

import checksum.ChecksumCalculator;
import resources.Bytes;
import resources.Hex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Converter {
    
    public void sfc2bs(
            String title,
            String inputRom,
            String outputRom
    ) {
        byte[] data = new byte[0];
        try {
            data = Files.readAllBytes(new File(inputRom).toPath());
        } catch (IOException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        int[] header = {
                0x30, 0x31,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x42, 0x53, 0xCF, 0xB2, 0xC3, 0xA8, 0xCE, 0xDF, // title
                0xB9, 0xAF, 0xC2, 0x31, 0x77, 0x65, 0x65, 0x6B, // title
                0x0F, 0x00, 0x00, 0x00, // Block Allocation Flags
                0x00, 0x00, // Limited Starts
                0xFF, // Date - Month
                0xFF, // Date - Day
                0x20, // ROM Speed (unconfirmed) & Map Mode
                0x20, // File/Execution Type
                0x33, // Fixed (0x33)
                0x02  // Version Number (unconfirmed)
        };
        int offset = 16;
        for (byte b : getTitle(title)) {
            header[offset++] = b;
        }
        byte blockFlag = getBlockFlag(data.length);
        header[32] = blockFlag;
        //header[32] = 0x7;
        
        offset = 0x7FB0;
        for (int i : header) {
            data[offset++] = (byte) (i & 0xFF);
        }

        // JUMP to reboot bsx
        //Bytes.writeBytes(Hex.parseHex("5C 3C 5C 10"), data, 0xF1E);

        // JUMP to custom code
        //Bytes.writeBytes(Hex.parseHex("5C 00 94 80"), data, 0xF1E);
        //Bytes.writeBytes(Hex.parseHex("E2 20 C2 30 A9 80 80 8F 00 50 08 8F 00 50 0E"), data, 0x1200);

        // Exit to BIOS routine
        //String routine = "5C 04 94 00 E2 30 A9 80 48 AB 8F 00 50 08 8F 00 50 0E 22 32 D7 99 E2 10 C2 20 22 10 C2 81 22 9A C2 81 22 7F 93 80 8E B2 13 F0 10 4B F4 38 94 8B F4 00 7E AB AB 5C 99 EB 80 E2 20 C2 30 9C DE 0C A9 99 00 85 BE A9 9A D6 22 B0 C2 81 E2 20 A9 81 8D 00 42 A9 80 8F 00 50 07 A2 7C 1E 9A 5C 27 BC 80";
        //Bytes.writeBytes(Hex.parseHex(routine), data, 0x1400);
        
        
        // RESET Vector
        //Bytes.writeBytes(Hex.parseHex("CC 80"), data, 0x7FFC);
        
        ChecksumCalculator.updateChecksumLowRomBS(data, ChecksumCalculator.LOW_ROM_DEFAULT_HEADER_OFFSET);
        saveData(outputRom, data);
    }
    
    public byte[] getTitle(String title) {
        byte[] res = new byte[16];
        char[] charArray = title.toCharArray();
        for (int i = 0, charArrayLength = charArray.length; i < charArrayLength; i++) {
            if (i<16) {
                char c = charArray[i];
                res[i] = (byte)c;
            }
        }
        return res;
    }
    
    public byte getBlockFlag(int romSize) {
        int blockCount = romSize/0x20000;
        if (blockCount*0x20000<romSize) blockCount++;
        byte res = 1;
        while (--blockCount>0) {
            res = (byte) ((res << 1) + 1);
        }
        return res;
    }

    static void saveData(String output, byte[] data) {
        System.out.println("Saving data : "+output);
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(output);
            stream.write(data);
            stream.flush();
            stream.close();
        } catch (IOException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (stream != null) {
                try {
                    stream.flush();
                    stream.close();
                } catch (IOException ex) {
                    Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
