package resources;

public class Hex {

    public static int x(String s) {
        return Integer.parseInt(s,16);
    }

    public static String h2(int i) {
        return padLeft(Integer.toHexString(i & 0xFF).toUpperCase(), '0',2);
    }
    
    public static String h4(int i) {
        return padLeft(Integer.toHexString(i & 0xFFFF).toUpperCase(), '0',4);
    }

    public static String h(int i) {
        return padLeft(Integer.toHexString(i).toUpperCase(), '0',5);
    }

    public static String h6(int i) {
        return padLeft(Integer.toHexString(i & 0xFFFFFF).toUpperCase(), '0',6);
    }

    public static String h8(int i) {
        return padLeft(Integer.toHexString(i & 0xFFFFFF).toUpperCase(), '0',8);
    }

    public static byte b(String s) {
        return (byte) Integer.parseInt(s,16);
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String getHexString(byte[] bytes) {
        if (bytes==null) return "";
        char[] hexChars = new char[bytes.length * 3];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = HEX_ARRAY[v >>> 4];
            hexChars[j * 3 + 1] = HEX_ARRAY[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars);
    }

    public static String padLeft(String s, char c, int length) {
        while (s.length()<length) {
            s=c+s;
        }
        return s;
    }

    /**
     * Parses a space separated hexadecimal representation of a byte array
     * @param hexValues
     * @return
     */
    public static byte[] parseHex(String hexValues) {
        String[] s1 = hexValues.split(" ");
        byte[] bytes = new byte[s1.length];
        for (int i = 0; i < s1.length; i++) {
            bytes[i] = (byte) (Integer.parseInt(s1[i],16) & 0xFF);
        }
        return bytes;
    }
}
