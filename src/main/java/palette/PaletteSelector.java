package palette;

import resources.Hex;
import tile.ColorDepth;
import tile.TileParser;

import java.awt.image.BufferedImage;

public class PaletteSelector {
    
    public static boolean VERBOSE = false;
    
    public static Palette selectBestPalette(BufferedImage image, ColorGraphics cgram, ColorDepth depth) {
        int paletteId = 0;
        Palette palette = new Palette();
        palette.loadPalette(cgram, paletteId, depth);
        
        StringBuilder sb = new StringBuilder();
        
        int row = 0;
        while (row<image.getHeight()) {
            int col = 0;
            while (col<image.getWidth()) {
                int rgb = image.getRGB(col, row);
                //Color color = new Color(rgb);
                if (VERBOSE) {
                    System.out.print(String.format("PaletteSelector palette(%s), pixel(%s,%s), color(%s)\n", paletteId, row, col, Hex.h6(rgb)));
                }
                BGRColor bgrColor = new BGRColor(rgb);
                while (!palette.containsColor(bgrColor)) {
                    if (VERBOSE)
                        System.out.print(String.format("PaletteSelector color not found (%d,%d) paletteId=%d bgrColor=%s\n", col, row, paletteId, bgrColor));
                    paletteId++;
                    row=-1;
                    col=100;
                    if (paletteId==depth.getPaletteCount()) {
                        if (VERBOSE) {
                            System.out.print(sb);
                        }
                        return null;
                    } else sb = new StringBuilder();
                    palette.loadPalette(cgram, paletteId, depth);
                }
                col++;
            }
            row++;
        }
        //if (TileParser.VERBOSE) System.out.print(String.format("PaletteSelector palette found "+palette.getPaletteId()+"\n"));

        //System.out.println(sb);
        return palette;
    }
}
