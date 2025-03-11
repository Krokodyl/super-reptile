package tile;

import org.apache.commons.lang.ArrayUtils;
import palette.BGRColor;
import palette.ColorGraphics;
import palette.Palette;
import palette.PaletteSelector;
import resources.Images;
import resources.ResourceLoader;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Represents a tile
 * 4bpp = 32 bytes
 */
public class Tile {
    
    byte[] data;
    
    public Tile() {}
    
    public Tile(byte[] data) {
        this.data = ArrayUtils.clone(data);
    }
    
    public void loadEmptyTile(ColorDepth depth) {
        if (data == null) data = new byte[depth.getBytesPerTile()];
    }
    
    public void loadFromImageFile(String name, ColorGraphics cgram, ColorDepth depth) {
        BufferedImage image = ResourceLoader.loadImage(name);
        loadFromImage(image, cgram, depth);
    }

    public void loadFromImage(BufferedImage image, ColorGraphics cgram, ColorDepth depth) {
        loadFromImage(image, cgram, depth, -1);
    }
    public void loadFromImage(BufferedImage image, ColorGraphics cgram, ColorDepth depth, int paletteId) {
        Palette palette = null;
        if (paletteId>=0) {
            palette = new Palette();
            palette.loadPalette(cgram, paletteId, depth);
        } else  palette = PaletteSelector.selectBestPalette(image, cgram, depth);
        int row = 0;
        int indexData = 0;
        while (row<image.getHeight()) {
            int col = 0;
            if (depth == ColorDepth._8BPP) {
                int tileBitIndex = 7;
                while (col < image.getWidth()) {
                    int rgb = image.getRGB(col, row);
                    BGRColor bgrColor = new BGRColor(rgb);
                    if (palette == null) System.out.println(col + " " + row);
                    Integer colorId = palette.getColorId(bgrColor);
                    
                    
                    
                    int colorBit = 0x1;
                    int colorBitIndex = 0x0;
                    
                    
                    if (data == null) data = new byte[depth.getBytesPerTile()];
                    int[] adds = new int[] {0, 1, 16, 17, 32, 33, 48, 49};
                    for (int add : adds) {
                        int i = (colorBit & colorId) >> (colorBitIndex++);
                        i = i << (tileBitIndex);
                        data[indexData + add] |= i;
                        colorBit = colorBit << 1;
                    }
                    tileBitIndex--;

                    /*
                    data[indexData + 1] |= tileBitIndex & (colorBit & colorId); colorBit = colorBit >> 1;
                    data[indexData + 16] |= tileBitIndex & (colorBit & colorId); colorBit = colorBit >> 1;
                    data[indexData + 17] |= tileBitIndex & (colorBit & colorId); colorBit = colorBit >> 1;
                    data[indexData + 32] |= tileBitIndex & (colorBit & colorId); colorBit = colorBit >> 1;
                    data[indexData + 33] |= tileBitIndex & (colorBit & colorId); colorBit = colorBit >> 1;
                    data[indexData + 48] |= tileBitIndex & (colorBit & colorId); colorBit = colorBit >> 1;
                    data[indexData + 49] |= tileBitIndex & (colorBit & colorId); colorBit = colorBit >> 1;
*/
                    col++;
                }
                indexData += 2;
            }
            else {
                long encodedLine = 0;
                while (col < image.getWidth()) {
                    int rgb = image.getRGB(col, row);
                    BGRColor bgrColor = new BGRColor(rgb);
                    if (palette == null) System.out.println(col + " " + row);
                    Integer colorId = palette.getColorId(bgrColor);
                    ColorMask colorMask = ColorMask.getColorMask(depth);
                    Long mask = colorMask.getMask(colorId);
                    mask = mask >> (col);
                    encodedLine = encodedLine | mask;
                    col++;
                }
                if (depth == ColorDepth._2BPP) {
                    if (data == null) data = new byte[depth.getBytesPerTile()];
                    long byte1 = (encodedLine >> 8) & 0x00FF;
                    long byte2 = (encodedLine) & 0x00FF;

                    data[indexData] = (byte) ((byte1) & 0xFF);
                    data[indexData + 1] = (byte) ((byte2) & 0xFF);
                    indexData += 2;
                }
                if (depth == ColorDepth._4BPP) {
                    if (data == null) data = new byte[depth.getBytesPerTile()];
                    long byte1 = encodedLine >> 24;
                    long byte2 = (encodedLine >> 16) & 0x00FF;
                    long byte3 = (encodedLine >> 8) & 0x00FF;
                    long byte4 = (encodedLine) & 0x00FF;

                    data[indexData] = (byte) ((byte1) & 0xFF);
                    data[indexData + 1] = (byte) ((byte2) & 0xFF);
                    data[indexData + 16] = (byte) ((byte3) & 0xFF);
                    data[indexData + 17] = (byte) ((byte4) & 0xFF);
                    indexData += 2;
                }
            }

            /*data = ArrayUtils.addAll(data, new byte[] {
                    (byte) (byte1 & 0xFF), (byte) (byte2 & 0xFF), (byte) (byte3 & 0xFF), (byte) (byte4 & 0xFF),
            });*/
            
            row++;
        }
    }

    public TileComparison compare(BufferedImage image, ColorGraphics cgram, ColorDepth depth) {
        BufferedImage normal = image;
        BufferedImage hFlip = Images.flipImage(image, true, false);
        BufferedImage vFlip = Images.flipImage(image, false, true);
        BufferedImage hvFlip = Images.flipImage(image, true, true);
        Tile tile2 = new Tile();
        tile2.loadFromImage(normal, cgram, depth);
        if (this.equals(tile2)) return TileComparison.IDENTICAL;
        tile2.loadFromImage(hFlip, cgram, depth);
        if (this.equals(tile2)) return TileComparison.IDENTICAL_H_FLIP;
        tile2.loadFromImage(vFlip, cgram, depth);
        if (this.equals(tile2)) return TileComparison.IDENTICAL_V_FLIP;
        tile2.loadFromImage(hvFlip, cgram, depth);
        if (this.equals(tile2)) return TileComparison.IDENTICAL_HV_FLIP;
        return TileComparison.DIFFERENT;
    }

    public byte[] getBytes() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return Arrays.equals(data, tile.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }
}
