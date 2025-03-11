package tile;

import palette.BGRColor;
import palette.Palette;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;

public class SpriteReader {

    public static void saveSprite(Sprite sprite, String file, Palette palette ) {
        try {
            BufferedImage image = getImage(sprite, palette);
            ImageIO.write(image, "png", new File(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage getImage(Sprite sprite, Palette palette) {
        int width = 8*sprite.getTilesPerRow();
        int rows = sprite.getTiles().size() / sprite.getTilesPerRow();
        if (rows*sprite.getTilesPerRow()<sprite.getTiles().size()) rows++;
        int height = 8*rows;
        //BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED, palette.getIndexColorModel());
        
        int line = 0;
        int col = 0;
        
        for (Tile tile : sprite.getTiles()) {
            if (sprite.orientation == SpriteOrientation.LEFT_RIGHT) {
                if (col==width) {
                    col = 0;
                    line += 8;
                }
            }
            else if (sprite.orientation == SpriteOrientation.TOP_DOWN) {
                if (line==height) {
                    col += 8;
                    line = 0;
                }
            }
            
            BufferedImage image = getImage(tile, palette);
            addImage(out, image, 1, col, line);
            if (sprite.orientation == SpriteOrientation.LEFT_RIGHT) col += 8;
            else if (sprite.orientation == SpriteOrientation.TOP_DOWN) line += 8;
        }
        return out;
    }

    public static BufferedImage getImage(Tile tile, Palette palette) {
        //BufferedImage out = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        BufferedImage out = new BufferedImage(8, 8, BufferedImage.TYPE_BYTE_INDEXED, palette.getIndexColorModel());

        Graphics2D g = (Graphics2D) out.getGraphics();
        byte[] bytes = tile.getBytes();
        for (int y=0;y<8;y++) {
            for (int x = 0; x <8; x++) {
                int colorId = 0;
                if (palette.getDepth() == ColorDepth._2BPP) {
                    byte a = bytes[y * 2];
                    byte b = bytes[y * 2 + 1];
                    a = (byte) (((a << x) & 0x80) & 0xFF);
                    b = (byte) (((b << x) & 0x80) & 0xFF);
                    a = (byte) (((a & 0x80) >> 7) & 0xFF);
                    b = (byte) (((b & 0x80) >> 7) & 0xFF);
                    colorId = a + b * 2;
                } else if (palette.getDepth() == ColorDepth._4BPP) {
                    byte a = bytes[y * 2];
                    byte b = bytes[y * 2 + 1];
                    byte c = bytes[y * 2 + 16];
                    byte d = bytes[y * 2 + 17];
                    a = (byte) (((a << x) & 0x80) & 0xFF);
                    b = (byte) (((b << x) & 0x80) & 0xFF);
                    c = (byte) (((c << x) & 0x80) & 0xFF);
                    d = (byte) (((d << x) & 0x80) & 0xFF);
                    a = (byte) (((a & 0x80) >> 7) & 0xFF);
                    b = (byte) (((b & 0x80) >> 7) & 0xFF);
                    c = (byte) (((c & 0x80) >> 7) & 0xFF);
                    d = (byte) (((d & 0x80) >> 7) & 0xFF);
                    colorId = a + b * 2 + c * 4 + d * 8;
                    
                }
                BGRColor color = palette.getColor(colorId);
                //out.setRGB(x,y, color.getColor24Bits() | 0xFF000000);
                out.getRaster().setPixel(x, y, new int[]{colorId});
            }
        }
        return out;
    }

    /**
     * prints the contents of buff2 on buff1 with the given opaque value.
     */
    public static void addImage(BufferedImage buff1, BufferedImage buff2,
                                float opaque, int x, int y) {
        Graphics2D g2d = buff1.createGraphics();
        g2d.setComposite(
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opaque));
        g2d.drawImage(buff2, x, y, null);
        g2d.dispose();
    }
    
}
