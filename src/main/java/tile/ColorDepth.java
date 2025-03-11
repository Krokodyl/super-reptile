package tile;

public enum ColorDepth {
    
    _1BPP(1, 8, 2, 64),
    _2BPP(2,16, 4, 8),
    _4BPP(4, 32, 16, 16),
    _8BPP(8,64, 256, 1)
    ;

    private int depth;
    private int bytesPerTile;
    private int colorPerPalette;
    private int paletteCount;

    ColorDepth(int depth, int bytesPerTile, int colors, int paletteCount) {
        this.depth = depth;
        this.bytesPerTile = bytesPerTile;
        this.colorPerPalette = colors;
        this.paletteCount = paletteCount;
    }

    public int getBytesPerTile() {
        return bytesPerTile;
    }

    public int getColorPerPalette() {
        return colorPerPalette;
    }

    public int getPaletteCount() {
        return paletteCount;
    }

    public int getDepth() {
        return depth;
    }
}
