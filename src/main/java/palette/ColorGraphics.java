package palette;

import resources.Bytes;
import resources.ResourceLoader;
import tile.ColorDepth;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * CGRAM
 *
 * "Color Graphics RAM"
 * Holds color information for palettes
 * Holds a total of 256 ($FF) color palette entries.
 * Each graphics mode divides the 256 palette entries into sub palettes, each with N colors
 * "N" depends on the video mode
 * By default, each subpalette is 16 color entries (256 colors/16 color for subpalette = 16 subpalettes)
 * By default, the first 8 subpalettes (subpalette IDs 0-7/$00-$07, palette entry IDs 0-63/$00-$3F) are used for FG tiles, while the last 8 subpalettes (subpalette IDs 8-16/$08-$10, palette entry IDs 64-127 or $40-$80) are for Sprite objects
 * First color entry in a subpalette will be rendered transparent for tiles/sprite tiles that use the subpalette
 *
 * 15-bit BGR color format
 * Each color entry in CGRAM is in the format of 2 bytes (a "word") of format 0BBBBBGG GGGRRRRR (B=Blue bits, G=Green bits, R=Red bits)
 * 16-bit, 1st bit is unused, so 15-bit color range
 * Each color component (B, G, R) ranges from values of 0-31 (decimal) or $00-$1F (hexadecimal)
 */
public class ColorGraphics {
    
    byte[] data = new byte[0x200];


    /**
     * Image representing 8 or 16 palettes in a uniform grid.
     * Only the top left pixel of each section is read when parsing the colors.
     * Image width must be a multiple of 16
     * Image height must be a multiple of @paletteCount
     * 
     * @param name - File name of the image
     * @param paletteCount - 8 or 16
     */
    public void loadFromImageFile(String name, int paletteCount) {
        BufferedImage image = ResourceLoader.loadImage(name);
        data = new byte[0x200];
        final int COLUMN_COUNT = 16;
        int hGap = image.getWidth()/COLUMN_COUNT;
        int vGap = image.getHeight()/paletteCount;
        int col = 0;
        int row = 0;
        int offset = 0;
        while (row<paletteCount) {
            int rgb = image.getRGB(col * hGap, row * vGap);
            Color color = new Color(rgb);
            byte[] colorBytes = new BGRColor(rgb).getBytes();
            Bytes.writeBytes(colorBytes, data, offset);
            offset += colorBytes.length;
            col++;
            if (col==COLUMN_COUNT) {
                col = 0;
                row++;
            }
        }
    }

    public void loadFromDataFile(String name) {
        data = ResourceLoader.loadBinaryFile(name);
    }

    public void setTransparentColor(ColorDepth depth, int color24Bits) {
        int offset = 0;
        byte[] colorBytes = new BGRColor(color24Bits).getBytes();
        while (offset < data.length) {
            Bytes.writeBytes(colorBytes, data, offset);
            offset += depth.getBytesPerTile();
        }
    }
    
    public BGRColor getColor(int paletteId, int colorId, ColorDepth depth) {
        int offset = paletteId * (depth.getColorPerPalette()*2) + colorId * 2;
        return new BGRColor(new byte[]{
                data[offset],
                data[offset+1]
        });
    }
    
    public Palette getPalette(int paletteId, ColorDepth depth) {
        Palette palette = new Palette();
        palette.loadPalette(this, paletteId, depth);
        return palette;
    }

    public byte[] getBytes() {
        return data;
    }
}
