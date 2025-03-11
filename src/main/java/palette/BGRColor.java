package palette;

import resources.Hex;

import java.awt.*;
import java.util.Objects;

/**
 * 15-bit BGR color format
 * Each color entry in CGRAM is in the format of 2 bytes (a "word") of format 0BBBBBGG GGGRRRRR (B=Blue bits, G=Green bits, R=Red bits)
 * 16-bit, 1st bit is unused, so 15-bit color range
 * Each color component (B, G, R) ranges from values of 0-31 (decimal) or $00-$1F (hexadecimal)
 */
public class BGRColor {
    
    int color15Bits;
    /*int blue;
    int green;
    int red;*/
    
    public BGRColor(int color24Bits) {
        int r = (color24Bits & 0xF80000) >>> 19;
        int g = (color24Bits & 0x00F800) >>>  6;
        int b = (color24Bits & 0x0000F8) <<  7;
        color15Bits = b | g | r;
    }

    /**
     * 
     * @param bytes Little Endian - 15 bits color
     */
    BGRColor(byte[] bytes) {
        color15Bits = (bytes[1] & 0xFF)*0x100 + (bytes[0] & 0xFF);
        /*red = ((color       ) % 32) * 8;
        green = ((color /   32) % 32) * 8;
        blue = ((color / 1024) % 32) * 8;*/
    }

    /**
     * Little Endian
     * @return
     */
    public byte[] getBytes() {
        //int value = blue * 1024 + green * 32 + red;
        return new byte[]{
                (byte) (color15Bits & 0xFF),
                (byte) ((color15Bits & 0xFF00) >>> 8)
        };
    }

    public int getColor15Bits() {
        return color15Bits;
    }
    
    public int getColor24Bits() {
        int r = ((color15Bits       ) % 32) * 8 + (((color15Bits       ) % 32) * 8 / 32);
        int g = ((color15Bits /   32) % 32) * 8 + (((color15Bits /   32) % 32) * 8 / 32);
        int b = ((color15Bits / 1024) % 32) * 8 + (((color15Bits / 1024) % 32) * 8 / 32);
        return r*0x10000+g*0x100+b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BGRColor bgrColor = (BGRColor) o;
        return color15Bits == bgrColor.color15Bits;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color15Bits);
    }

    @Override
    public String toString() {
        byte[] bytes = getBytes();
        return "BGRColor{" +
                Hex.getHexString(bytes) +
                '}';
    }
}
