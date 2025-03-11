package tile;

import java.util.HashMap;
import java.util.Map;

public enum ColorMask {

    _1BPP(ColorDepth._1BPP),
    _2BPP(ColorDepth._2BPP),
    _4BPP(ColorDepth._4BPP);
    //_8BPP(ColorDepth._8BPP);
    
    Map<Integer, Long> masks = new HashMap<>();
    
    ColorMask(ColorDepth depth) {
        if (depth==ColorDepth._2BPP) {
            masks.put(0, Long.parseLong("0000000000000000",2));
            masks.put(1, Long.parseLong("1000000000000000",2));
            masks.put(2, Long.parseLong("0000000010000000",2));
            masks.put(3, Long.parseLong("1000000010000000",2));
        }
        if (depth==ColorDepth._4BPP) {
            masks.put(0, Long.parseLong("00000000000000000000000000000000",2));
            masks.put(1, Long.parseLong("10000000000000000000000000000000",2));
            masks.put(2, Long.parseLong("00000000100000000000000000000000",2));
            masks.put(3, Long.parseLong("10000000100000000000000000000000",2));

            masks.put(4, Long.parseLong("00000000000000001000000000000000",2));
            masks.put(5, Long.parseLong("10000000000000001000000000000000",2));
            masks.put(6, Long.parseLong("00000000100000001000000000000000",2));
            masks.put(7, Long.parseLong("10000000100000001000000000000000",2));

            masks.put(8, Long.parseLong("00000000000000000000000010000000",2));
            masks.put(9, Long.parseLong("10000000000000000000000010000000",2));
            masks.put(10, Long.parseLong("00000000100000000000000010000000",2));
            masks.put(11, Long.parseLong("10000000100000000000000010000000",2));

            masks.put(12, Long.parseLong("00000000000000001000000010000000",2));
            masks.put(13, Long.parseLong("10000000000000001000000010000000",2));
            masks.put(14, Long.parseLong("00000000100000001000000010000000",2));
            masks.put(15, Long.parseLong("10000000100000001000000010000000",2));
        }
        /*if (depth==ColorDepth._8BPP) {
            masks.put(15, Long.parseLong("1000000010000000100000001000000010000000100000001000000010000000",2));
        }*/
    }

    public Long getMask(int colorId) {
        return masks.get(colorId);
    }
    
    public static ColorMask getColorMask(ColorDepth depth) {
        switch (depth) {
            case _1BPP:
                return ColorMask._1BPP;
            case _2BPP:
                return ColorMask._2BPP;
            case _4BPP:
                return ColorMask._4BPP;
        }
        return ColorMask._4BPP;
    }
}
