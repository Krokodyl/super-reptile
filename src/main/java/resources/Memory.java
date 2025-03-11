package resources;

import static resources.Hex.h4;

public class Memory {
    
    public static byte[] getBank(int value) {
        byte[] data = new byte[1];
        int a = value / 0x8000;
        data[0] = (byte) a;
        return data;
    }

    public static byte[] getPointer(int value) {
        String code = h4(value);
        byte[] data = new byte[2];
        int a = Integer.parseInt(code.substring(0, 2), 16);
        int b = Integer.parseInt(code.substring(2, 4), 16);
        if (a<0x80) a+=0x80;
        data[0] = (byte) b;
        data[1] = (byte) a;
        return data;
    }

    public static byte[] getPointerLow(int value) {
        String code = h4(value);
        byte[] data = new byte[2];
        int a = Integer.parseInt(code.substring(0, 2), 16);
        int b = Integer.parseInt(code.substring(2, 4), 16);
        //if (a<0x80) a+=0x80;
        data[0] = (byte) b;
        data[1] = (byte) a;
        return data;
    }

    public static byte[] getPointerLong(int value) {
        String code = h4(value);
        byte[] data = new byte[3];
        int a = Integer.parseInt(code.substring(0, 2), 16);
        int b = Integer.parseInt(code.substring(2, 4), 16);
        if (a<0x80) a+=0x80;
        data[0] = (byte) b;
        data[1] = (byte) a;

        int bank = value / 0x8000;
        data[2] = (byte) (bank + 0x80);
        
        return data;
    }
}
