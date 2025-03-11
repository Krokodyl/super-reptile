package old;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static old.FontColor.*;

public class Palette2bpp extends Palette {

    Map<String, FontColor> mapGameColors = new HashMap<String, FontColor>();
    Map<FontColor, String> mapHexaValues = new HashMap<FontColor, String>();

    public Palette2bpp(String file) {
        try {
            BufferedImage image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(file)));
            loadPaletteImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Palette2bpp(BufferedImage image) throws IOException {
        loadPaletteImage(image);
    }
    
    public void loadPaletteImage(BufferedImage image) {
        int gap = image.getWidth()/4;
        //System.out.println("Loading Palette "+file);
        for (int index = 0;index<4;index++) {
            int color = image.getRGB(index * gap, 0);
            FontColor fc = null;
            switch (index) {
                case 0:fc = MAP_2BPP_COLOR_01;break;
                case 1:{fc = MAP_2BPP_COLOR_02;break;}
                case 2:{fc = MAP_2BPP_COLOR_03;break;}
                case 3:{fc = MAP_2BPP_COLOR_04;break;}
            }
            String colorAsHex = ImageParser.getColorAsHex(color);
            //System.out.println(colorAsHex +"\t"+fc.name());
            if (!mapGameColors.containsKey(colorAsHex)) {
                mapGameColors.put(colorAsHex.toLowerCase(), fc);
                mapHexaValues.put(fc, colorAsHex.toLowerCase());
            }
            
        }
    }

    @Override
    public FontColor getFontColor(String hexa) {
        return mapGameColors.get(hexa);
    }

    @Override
    public String getHexaValue(FontColor fontColor) {
        return mapHexaValues.get(fontColor);
    }

}
