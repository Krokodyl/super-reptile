package palette;

import tile.ColorDepth;

import java.awt.image.IndexColorModel;
import java.util.HashMap;
import java.util.Map;

public class Palette {

    /**
     * key: colorId of the palette (0 to 15)
     * value: the color
     */
    Map<Integer, BGRColor> colors = new HashMap<>();
    BGRColor transparentColor = new BGRColor(0xFF00FF);
    
    int paletteId;
    ColorDepth depth;
    
    public void loadPalette(ColorGraphics cgram, int id, ColorDepth depth) {
        int colorId = 0;
        this.paletteId = id;
        while (colorId<depth.getColorPerPalette()) {
            BGRColor color = cgram.getColor(id, colorId, depth);
            colors.put(colorId, color);
            //if (colorId == 0) transparentColor = color;
            colorId++;
        }
        this.depth = depth;
    }
    
    public boolean containsColor(BGRColor color) {
        return color.equals(transparentColor) ||
                (colors.values().contains(color)
                && getColorId(color)!=null && getColorId(color)>=0
                );
    }


    public Integer getColorId(BGRColor bgrColor) {
        if (bgrColor.equals(transparentColor)) return 0;
        Integer key = null;
        for (Map.Entry<Integer, BGRColor> e : colors.entrySet()) {
            if (e.getKey()>=0 && e.getValue().equals(bgrColor)) key = e.getKey();
        }
        return key;
    }
    
    public IndexColorModel getIndexColorModel() {
        byte[] r = new byte[getDepth().getColorPerPalette()];
        byte[] g = new byte[getDepth().getColorPerPalette()];
        byte[] b = new byte[getDepth().getColorPerPalette()];

        Integer key = null;
        for (Map.Entry<Integer, BGRColor> e : colors.entrySet()) {
            key = e.getKey();
            BGRColor color = e.getValue();
            int rgb = color.getColor24Bits() | 0xFF000000;
            r[key] = (byte) ((rgb >> 16) & 0xFF);
            g[key] = (byte) ((rgb >> 8) & 0xFF);
            b[key] = (byte) (rgb & 0xFF);
        }
        
        IndexColorModel colorModel = new IndexColorModel(
                8, getDepth().getColorPerPalette(), r, g, b 
        );
        return colorModel;
    }
    
    public BGRColor getColor(int id) {
        return colors.get(id);
    }

    public int getPaletteId() {
        return paletteId;
    }

    public ColorDepth getDepth() {
        return depth;
    }

    public void setDepth(ColorDepth depth) {
        this.depth = depth;
    }
}
