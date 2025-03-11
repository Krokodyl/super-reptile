package tile;

import palette.ColorGraphics;
import resources.Images;

import java.awt.image.BufferedImage;

public class TileComparator {

    public static TileComparison compare(Tile tile, BufferedImage image, ColorGraphics cgram, ColorDepth depth) {
        BufferedImage normal = image;
        BufferedImage hFlip = Images.flipImage(image, true, false);
        BufferedImage vFlip = Images.flipImage(image, false, true);
        BufferedImage hvFlip = Images.flipImage(image, true, true);
        Tile tile2 = new Tile();
        tile2.loadFromImage(normal, cgram, depth);
        if (tile.equals(tile2)) return TileComparison.IDENTICAL;
        tile2.loadFromImage(hFlip, cgram, depth);
        if (tile.equals(tile2)) return TileComparison.IDENTICAL_H_FLIP;
        tile2.loadFromImage(vFlip, cgram, depth);
        if (tile.equals(tile2)) return TileComparison.IDENTICAL_V_FLIP;
        tile2.loadFromImage(hvFlip, cgram, depth);
        if (tile.equals(tile2)) return TileComparison.IDENTICAL_HV_FLIP;
        return TileComparison.DIFFERENT;
    }

    public static TileComparison compare(Tile16 tile, BufferedImage image, ColorGraphics cgram, ColorDepth depth) {
        BufferedImage normal = image;
        BufferedImage hFlip = Images.flipImage(image, true, false);
        BufferedImage vFlip = Images.flipImage(image, false, true);
        BufferedImage hvFlip = Images.flipImage(image, true, true);
        Tile16 tile2 = new Tile16();
        tile2.loadFromImage(normal, cgram, depth);
        if (tile.equals(tile2)) return TileComparison.IDENTICAL;
        tile2.loadFromImage(hFlip, cgram, depth);
        if (tile.equals(tile2)) return TileComparison.IDENTICAL_H_FLIP;
        tile2.loadFromImage(vFlip, cgram, depth);
        if (tile.equals(tile2)) return TileComparison.IDENTICAL_V_FLIP;
        tile2.loadFromImage(hvFlip, cgram, depth);
        if (tile.equals(tile2)) return TileComparison.IDENTICAL_HV_FLIP;
        return TileComparison.DIFFERENT;
    }
    
}
