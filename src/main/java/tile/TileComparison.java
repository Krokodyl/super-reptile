package tile;

public enum TileComparison {

    DIFFERENT,
    IDENTICAL,
    IDENTICAL_H_FLIP,
    IDENTICAL_V_FLIP,
    IDENTICAL_HV_FLIP;
    
    public boolean isIdentical() {
        return this!=DIFFERENT;
    }
    
}
