package tile;

import org.apache.commons.lang.ArrayUtils;
import palette.ColorGraphics;
import resources.Images;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Tile16 extends Tile {
    
    Tile[] topTiles = new Tile[2];
    Tile[] botTiles = new Tile[2];

    @Override
    public void loadFromImage(BufferedImage image, ColorGraphics cgram, ColorDepth depth) {
        BufferedImage subimage = image.getSubimage(0, 0, 8, 8);
        Tile tile = new Tile();
        tile.loadFromImage(subimage, cgram, depth);
        topTiles[0] = tile;
        subimage = image.getSubimage(8, 0, 8, 8);
        tile = new Tile();
        tile.loadFromImage(subimage, cgram, depth);
        topTiles[1] = tile;
        subimage = image.getSubimage(0, 8, 8, 8);
        tile = new Tile();
        tile.loadFromImage(subimage, cgram, depth);
        botTiles[0] = tile;
        subimage = image.getSubimage(8, 8, 8, 8);
        tile = new Tile();
        tile.loadFromImage(subimage, cgram, depth);
        botTiles[1] = tile;
        data = ArrayUtils.addAll(getTopBytes(), getBotBytes());
    }

    public TileComparison compare(BufferedImage image, ColorGraphics cgram, ColorDepth depth) {
        BufferedImage normal = image;
        BufferedImage hFlip = Images.flipImage(image, true, false);
        BufferedImage vFlip = Images.flipImage(image, false, true);
        BufferedImage hvFlip = Images.flipImage(image, true, true);
        Tile16 tile2 = new Tile16();
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

    public byte[] getTopBytes()
    {
        return ArrayUtils.addAll(topTiles[0].getBytes(), topTiles[1].getBytes());
    }

    public byte[] getBotBytes()
    {
        return ArrayUtils.addAll(botTiles[0].getBytes(), botTiles[1].getBytes());
    }

}
