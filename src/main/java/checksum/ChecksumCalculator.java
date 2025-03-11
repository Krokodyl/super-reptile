package checksum;

import resources.Hex;

public class ChecksumCalculator {

    public final static int LOW_ROM_DEFAULT_HEADER_OFFSET = 0x7FB0;
    public final static int HIGH_ROM_DEFAULT_HEADER_OFFSET = 0xFFB0;

    /**
     * From header offset
     */
    public final static int LOW_ROM_CHECKSUM_OFFSET = 0x2C;
    public final static int LOW_ROM_BLOCK_FLAGS_OFFSET = 0x20;

    public static void updateChecksumLowRom(byte[] data) {
        updateChecksumLowRom(data, LOW_ROM_DEFAULT_HEADER_OFFSET);
    }

    public static void updateChecksumLowRomBS(byte[] data) {
        updateChecksumLowRomBS(data, LOW_ROM_DEFAULT_HEADER_OFFSET);
    }

    public static void updateChecksumHighRomBS(byte[] data) {
        updateChecksumLowRomBS(data, HIGH_ROM_DEFAULT_HEADER_OFFSET);
    }

    public static void updateChecksumHighRom(byte[] data) {

        int headerOffset = HIGH_ROM_DEFAULT_HEADER_OFFSET;
        int checksum = 0;

        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET] = (byte) 0x00;
        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET+1] = (byte) 0x00;
        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET+2] = (byte) 0xFF;
        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET+3] = (byte) 0xFF;

        for (int i = 0; i < data.length; i = i+1) {
            if ((i<headerOffset || i>(headerOffset+0x2F)) && i<0x80000)
                checksum += (data[i] & 0xFF);
            if (checksum>=0x10000) checksum-=0x10000;
        }

        int complement = 0xFFFF-checksum;
        //System.out.println("Expected: BD3A");
        //System.out.println(Integer.toHexString(checksum));
        //System.out.println(Integer.toHexString(complement));

        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET] = (byte) (complement%0x100);
        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET+1] = (byte) (complement/0x100);
        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET+2] = (byte) (checksum%0x100);
        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET+3] = (byte) (checksum/0x100);

    }
    
    public static void updateChecksumLowRom(byte[] data, int headerOffset) {

        int checksum = 0;

        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET] = (byte) 0x00;
        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET+1] = (byte) 0x00;
        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET+2] = (byte) 0xFF;
        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET+3] = (byte) 0xFF;

        for (int i = 0; i < data.length; i = i+1) {
            if ((i<headerOffset || i>(headerOffset+0x2F)) && i<0x80000)
                checksum += (data[i] & 0xFF);
            if (checksum>=0x10000) checksum-=0x10000;
        }

        int complement = 0xFFFF-checksum;
        //System.out.println("Expected: BD3A");
        //System.out.println(Integer.toHexString(checksum));
        //System.out.println(Integer.toHexString(complement));

        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET] = (byte) (complement%0x100);
        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET+1] = (byte) (complement/0x100);
        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET+2] = (byte) (checksum%0x100);
        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET+3] = (byte) (checksum/0x100);

    }

    /**
     * LowRom Satellaview Checksum Calculator
     */
    public static void updateChecksumLowRomBS(byte[] data, int headerOffset) {

        int checksum = 0;

        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET] = (byte) 0x00;
        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET+1] = (byte) 0x00;
        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET+2] = (byte) 0xFF;
        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET+3] = (byte) 0xFF;

        int bits = 8;
        byte allocationBlocks = data[headerOffset+LOW_ROM_BLOCK_FLAGS_OFFSET];
        
        int offsetBlock = 0;
        while (bits > 0) {
            if ((allocationBlocks & 0x01) == 0x01) {
                //System.out.println("Block "+ Hex.h(offsetBlock));
                for (int i = offsetBlock; i < offsetBlock + 0x20000; i = i+1) {
                    if ((i<headerOffset || i>(headerOffset+0x2F)))
                        checksum += (data[i] & 0xFF);
                    if (checksum>=0x10000) checksum-=0x10000;
                }
            }
            allocationBlocks = (byte) ((allocationBlocks & 0xFF) >>> 1);
            offsetBlock += 0x20000;
            bits--;
        }
        
        /*for (int i = 0; i < data.length; i = i+1) {
            if ((i<0x7FB0 || i>0x7FDF) && i<0x80000)
                checksum += (data[i] & 0xFF);
            if (checksum>=0x10000) checksum-=0x10000;
        }*/

        int complement = 0xFFFF-checksum;
        //System.out.println("Expected: BD3A");
        //System.out.println(Integer.toHexString(checksum));
        //System.out.println(Integer.toHexString(complement));

        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET] = (byte) (complement%0x100);
        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET+1] = (byte) (complement/0x100);
        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET+2] = (byte) (checksum%0x100);
        data[headerOffset+LOW_ROM_CHECKSUM_OFFSET+3] = (byte) (checksum/0x100);

    }

    public static void updateChecksumLowRomBSFlash(byte[] data, int headerOffset) {
        int checksum = 0;
        data[headerOffset + 44] = 0;
        data[headerOffset + 44 + 1] = 0;
        data[headerOffset + 44 + 2] = -1;
        data[headerOffset + 44 + 3] = -1;
        int bits = 8;
        byte allocationBlocks = data[headerOffset + 32];

        for (int i = 0; i < data.length; i++) {
            if ((i<headerOffset || i>(headerOffset+0x2F)))
            checksum += data[i] & 255;
            if (checksum >= 65536) {
                checksum -= 65536;
            }
        }


        int i;
        i = '\uffff' - checksum;
        data[headerOffset + 44] = (byte)(i % 256);
        data[headerOffset + 44 + 1] = (byte)(i / 256);
        data[headerOffset + 44 + 2] = (byte)(checksum % 256);
        data[headerOffset + 44 + 3] = (byte)(checksum / 256);
    }
}
