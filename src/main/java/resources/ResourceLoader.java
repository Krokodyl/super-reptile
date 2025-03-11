package resources;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResourceLoader {

    public static BufferedImage loadImage(String name) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(Objects.requireNonNull(ResourceLoader.class.getClassLoader().getResource(name)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static byte[] loadBinaryFile(String name) {
        byte[] bytes = null;
        try {
            bytes = Objects.requireNonNull(ResourceLoader.class.getClassLoader().getResourceAsStream(name)).readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static List<String> loadTextFile(String filename) {
        List<String> lines = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            Objects.requireNonNull(ResourceLoader.class.getClassLoader().getResourceAsStream(filename)), StandardCharsets.UTF_8));
            String line = br.readLine();
            while (line!=null) {
                lines.add(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
