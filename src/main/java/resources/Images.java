package resources;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Images {

    /**
     * Flips an image horizontally and/or vertically.
     *
     * @param image      The image to be flipped.
     * @param horizontal Whether the image should be flipped horizontally.
     * @param vertical   Whether the image should be flipped vertically.
     * @return           The given image, flipped horizontally and/or vertically.
     */
    public static BufferedImage flipImage(final BufferedImage image, final boolean horizontal,
                                          final boolean vertical) {
        int x = 0;
        int y = 0;
        int w = image.getWidth();
        int h = image.getHeight();

        final BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = out.createGraphics();

        if (horizontal) {
            x = w;
            w *= -1;
        }

        if (vertical) {
            y = h;
            h *= -1;
        }

        g2d.drawImage(image, x, y, w, h, null);
        g2d.dispose();

        return out;
    }
    
}
