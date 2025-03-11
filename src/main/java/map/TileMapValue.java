package map;

import palette.Palette;
import tile.Tile;
import tile.TileComparison;

public class TileMapValue {
    
    int tileOffset;
    int paletteId;
    TileComparison comparison;
    int priority; // 0 or 1

    public TileMapValue(int tileOffset, int paletteId, TileComparison comparison, int priority) {
        this.tileOffset = tileOffset;
        this.paletteId = paletteId;
        this.comparison = comparison;
        this.priority = priority;
    }

    /**
     * Represents a tile on a tile map
     * 2 bytes - Little Endian
     * @return
     */
    public byte[] getBytes() {
        byte high = (byte) (tileOffset / 0x100);
        byte low = (byte) (tileOffset % 0x100);
        if (comparison==TileComparison.IDENTICAL_V_FLIP) {
            high = (byte) ((high & 0xFF) | 0x80);
        } else if (comparison==TileComparison.IDENTICAL_H_FLIP) {
            high = (byte) ((high & 0xFF) | 0x40);
        } else if (comparison==TileComparison.IDENTICAL_HV_FLIP) {
            high = (byte) ((high & 0xFF) | 0xC0);
        }
        if (priority==1) {
            high = (byte) ((high & 0xFF) | 0x20);
        }
        byte palette = (byte) (getPalette() & 0x07);
        high = (byte) (high | (palette << 2));
        return new byte[] { low, high};
    }

    public int getTileOffset() {
        return tileOffset;
    }

    public void setTileOffset(int tileOffset) {
        this.tileOffset = tileOffset;
    }

    public int getPalette() {
        return paletteId;
    }

    public void setPalette(int palette) {
        this.paletteId = palette;
    }

    public TileComparison getComparison() {
        return comparison;
    }

    public void setComparison(TileComparison comparison) {
        this.comparison = comparison;
    }
}
