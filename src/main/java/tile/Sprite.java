package tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sprite {
    
    int tilesPerRow = 2;
    SpriteOrientation orientation = SpriteOrientation.LEFT_RIGHT;
    List<Tile> tiles = new ArrayList<>();
    
    public Sprite(byte[] data, ColorDepth bpp, int tilesPerRow) {
        this.tilesPerRow = tilesPerRow;
        int bytesPerTile = bpp.getBytesPerTile();
        for(int i=0;i<data.length;i+= bytesPerTile) {
            byte[] bytes = Arrays.copyOfRange(data, i, Math.min(data.length, i + bytesPerTile));
            Tile tile = new Tile(bytes);
            tiles.add(tile);
            /*try {
                BufferedImage imageTile = SpriteReader.getImage(tile, new Palette2bpp("/palettes/satellaview.png"));
                ImageIO.write(image, "png", new File("src/main/resources/gen/"+i+".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
        
    }

    public SpriteOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(SpriteOrientation orientation) {
        this.orientation = orientation;
    }

    public int getTilesPerRow() {
        return tilesPerRow;
    }

    public List<Tile> getTiles() {
        return tiles;
    }
}
