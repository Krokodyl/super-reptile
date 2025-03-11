package tile;

import map.TileMapValue;
import org.apache.commons.lang.ArrayUtils;
import palette.ColorGraphics;
import palette.Palette;
import palette.PaletteSelector;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class TileParser {

    public static boolean VERBOSE = false;
    
    // Map between Tile and their offset in VRAM
    Map<Tile, Integer> tilesOffset = new HashMap<>();// Tile Map
    List<Tile> tiles = new ArrayList<>();
    // Tile Map
    List<TileMapValue> tileMap = new ArrayList<>();
 
    private boolean allowDuplicatedTiles = false;
    private boolean addEmptyTile = false;
    private int blockHeightInTiles = 1;
    
    private List<TileComparison> allowedComparisons = new ArrayList<>(); 
    
    /**
     * Parse a regular image into tiles and a tile map
     * @param tileStartOffset When the first tile is not indexed by 0x0000
     *                        
     */
    public void parseImage(
            BufferedImage image, 
            ColorGraphics cgram, 
            ColorDepth depth,
            int tileStartOffset,
            int tileSize,
            int tilePriority,
            int paletteId
            ) {
        int tileOffset = tileStartOffset;
        int newTileId = tileOffset / depth.getBytesPerTile();
        int tileCount = 0;
        
        if (addEmptyTile) {
            Tile tile = new Tile();
            if (tileSize == 16) {
                tile = new Tile16();
            }
            tile.loadEmptyTile(depth);
            
            int currentPaletteId = paletteId;
            tilesOffset.put(tile, newTileId);
            tiles.add(tile);
            if (VERBOSE) {
                //System.out.format("New tile added: %s\n", Hex.getHexString(tile.getBytes()));
            }
            tileOffset += depth.getBytesPerTile();

            newTileId++;
            tileCount++;
            if (tileSize == 16) {
                newTileId++;
                tileCount++;
                if (tileCount == 16) {
                    newTileId += depth.getBytesPerTile();
                    tileCount = 0;
                }
            }
        }
        
        for (int y=0;y<image.getHeight();y=y+tileSize) {
            for (int x = 0; x < image.getWidth(); x = x + tileSize) {
                if (!isTransparent(image, x, y)) {
                int currentPaletteId = paletteId;
                if (VERBOSE) System.out.println("Tile " + x + "\t" + y);
                BufferedImage subimage = image.getSubimage(x, y, tileSize, tileSize);
                Tile tile = new Tile();
                if (tileSize == 16) tile = new Tile16();
                tile.loadFromImage(subimage, cgram, depth, currentPaletteId);
                int realOffset = tileOffset;
                ComparableTile existingTile = getComparableTile(subimage, cgram, depth, allowedComparisons);
                TileComparison comparison = TileComparison.IDENTICAL;
                if (!allowDuplicatedTiles && existingTile != null) {
                    //existingTile
                    realOffset = tilesOffset.get(existingTile.getTile());
                    comparison = existingTile.getComparison();

                    if (currentPaletteId < 0) {
                        Palette palette = PaletteSelector.selectBestPalette(subimage, cgram, depth);
                        if (palette != null) {
                            currentPaletteId = palette.getPaletteId();
                        }
                    }
                    TileMapValue tileMapValue = new TileMapValue(tilesOffset.get(existingTile.getTile()), currentPaletteId, comparison, tilePriority);
                    tileMap.add(tileMapValue);
                } else {
                    tilesOffset.put(tile, newTileId);
                    tiles.add(tile);
                    if (VERBOSE) {
                        //System.out.format("New tile added: %s\n", Hex.getHexString(tile.getBytes()));
                    }
                    tileOffset += depth.getBytesPerTile();

                    if (currentPaletteId < 0) {
                        Palette palette = PaletteSelector.selectBestPalette(subimage, cgram, depth);
                        if (palette != null) {
                            currentPaletteId = palette.getPaletteId();
                        }
                    }
                    TileMapValue tileMapValue = new TileMapValue(newTileId, currentPaletteId, comparison, tilePriority);
                    tileMap.add(tileMapValue);


                    newTileId++;
                    tileCount++;
                    if (tileSize == 16) {
                        newTileId++;
                        tileCount++;
                        if (tileCount == 16) {
                            newTileId += depth.getBytesPerTile();
                            tileCount = 0;
                        }
                    }
                }
            }
            }
        }
    }

    public void parseImage(
            BufferedImage image,
            ColorGraphics cgram,
            ColorDepth depth,
            int tileSize,
            int tilePriority
    ) {
        parseImage(image, cgram, depth, 0x0000, tileSize, tilePriority, 0);
    }

    public boolean isTransparent(BufferedImage img, int x, int y ) {
        int pixel = img.getRGB(x,y);
        if( (pixel>>24) == 0x00 ) {
            return true;
        }
        return false;
    }
    
    private ComparableTile getComparableTile(BufferedImage image, ColorGraphics cg, ColorDepth cd, List<TileComparison> allowedComparisons) {
        for (Tile t : tiles) {
            //TileComparison comparison = TileComparator.compare(t, image, cg, cd);
            TileComparison comparison = t.compare(image, cg, cd);
            if ((allowedComparisons.isEmpty() && comparison!=TileComparison.DIFFERENT) || allowedComparisons.contains(comparison)) {
                return new ComparableTile(t, comparison);
            }
        }
        return null;
    }
    
    class ComparableTile {
        
        Tile tile;
        TileComparison comparison;

        public ComparableTile(Tile tile, TileComparison comparison) {
            this.tile = tile;
            this.comparison = comparison;
        }

        public Tile getTile() {
            return tile;
        }

        public TileComparison getComparison() {
            return comparison;
        }
    }

    public Map<Tile, Integer> getTilesOffset() {
        return tilesOffset;
    }

    public List<TileMapValue> getTileMap() {
        return tileMap;
    }

    public List<Tile> getTiles() {
        return tiles;
    }
    
    /*public static void turnImageIntoTiles(
            int tileX, String s, String file, String outputTiles, String outputMap) throws IOException {
        BufferedImage image = ResourceLoader.loadImage(file);
        ByteArrayOutputStream outputMapBytes = new ByteArrayOutputStream();
        Map<Tile, String> tiles = new HashMap<>();
        BufferedImage out = new BufferedImage(8*16, 320, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) out.getGraphics();
        int code = Integer.parseInt(s,16);
        int tileY = 0;
        for (int y=0;y<image.getHeight();y=y+8) {
            for (int x=0;x<image.getWidth();x=x+8) {
                BufferedImage subimage = image.getSubimage(x, y, 8, 8);
                Tile tile = new Tile(subimage);
                String hexCode = Utils.toHexString(code, 4);
                if (!tiles.containsKey(tile)) {
                    tiles.put(tile, hexCode);
                    g.drawImage(subimage,tileX,tileY, null);
                    tileX += 8;
                    if (tileX%(8*16)==0) {
                        tileX=0;tileY+=8;
                    }
                    outputMapBytes.write(Utils.codeBytes(hexCode));
                    code += Integer.parseInt("0100",16);
                } else {
                    hexCode = tiles.get(tile);
                    outputMapBytes.write(Utils.codeBytes(hexCode));
                }
            }
        }
        try {
            Utils.saveData(outputMap, outputMapBytes.toByteArray());
            ImageIO.write(out, "png", new File(outputTiles));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    
    public byte[] getCharacterBytes() {
        byte[] bytes = new byte[0];
        for (Tile tile : tiles) {
            bytes = ArrayUtils.addAll(bytes, tile.getBytes());
        }
        return bytes;
    }
    
    public byte[] getTileMapBytes() {
        byte[] bytes = new byte[0];
        for (TileMapValue tileMapValue : tileMap) {
            bytes = ArrayUtils.addAll(bytes, tileMapValue.getBytes());
        }
        return bytes;
    }

    public boolean isAllowDuplicatedTiles() {
        return allowDuplicatedTiles;
    }

    public void setAllowDuplicatedTiles(boolean allowDuplicatedTiles) {
        this.allowDuplicatedTiles = allowDuplicatedTiles;
    }

    public void setAddEmptyTile(boolean addEmptyTile) {
        this.addEmptyTile = addEmptyTile;
    }
    
    public void addAllowedComparisons(TileComparison comparison) {
        allowedComparisons.add(comparison);
    }
}
