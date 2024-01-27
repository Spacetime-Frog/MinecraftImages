import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MinecraftImage {
    private final BufferedImage bufferedImage;

    private DisplaySpecifications specifications;
    private List<List<PixelDisplay>> dustOptionsList;

    public static List<MinecraftImage> fromGIFURL(URL url, DisplaySpecifications specifications) throws IOException {
        ImageInputStream stream = ImageIO.createImageInputStream(url.openStream());
        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        reader.setInput(stream);

        int num = reader.getNumImages(true);

        List<MinecraftImage> images = new ArrayList<>();

        for (int i = 0; i < num; i++) {
            images.add(fromBufferedImage(reader.read(i), specifications));
        }

        return images;
    }

    public static MinecraftImage fromURL(URL url, DisplaySpecifications specifications) throws IOException {
        return fromBufferedImage(ImageIO.read(url.openStream()), specifications);
    }

    public static MinecraftImage fromBufferedImage(BufferedImage bufferedImage, DisplaySpecifications specifications) {
        MinecraftImage minecraftImage = new MinecraftImage(bufferedImage);

        if (specifications != null) {
            minecraftImage.cacheSpecifications(specifications);
        }

        return minecraftImage;
    }

    public MinecraftImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public void cacheSpecifications(DisplaySpecifications specifications) {
        this.specifications = specifications;

        List<List<PixelDisplay>> display = new ArrayList<>();

        int bufferedHeightIncrement = bufferedImage.getHeight() / specifications.yIncrements();
        int bufferedWidthIncrement = bufferedImage.getWidth() / specifications.xIncrements();

        for (int y = 0; y < specifications.yIncrements(); y++) {
            List<PixelDisplay> row = new ArrayList<>();

            for (int x = 0; x < specifications.xIncrements(); x++) {
                int rgb = bufferedImage.getRGB(x * bufferedWidthIncrement, y * bufferedHeightIncrement);
                int alpha = (rgb >> 24) & 0xFF;

                if (alpha == 0) {
                    continue;
                }

                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                PixelDisplay pixelDisplay = new PixelDisplay(
                        red,
                        green,
                        blue,
                        new Vector(
                                specifications.basis().getX() * (specifications.xIncrements() - x),
                                specifications.basis().getY() * (specifications.yIncrements() - y),
                                specifications.basis().getZ() * (specifications.xIncrements() - x)
                        )
                );

                row.add(pixelDisplay);
            }

            display.add(row);
        }

        this.dustOptionsList = display;
    }

    public void display(Location location) {
        for (List<PixelDisplay> row : dustOptionsList) {
            for (PixelDisplay pixelDisplay : row) {
                pixelDisplay.display(location);
            }
        }
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public DisplaySpecifications getSpecifications() {
        return specifications;
    }

    public record DisplaySpecifications(
            Vector basis,
            int xIncrements,
            int yIncrements
    ) {
    }

    public record PixelDisplay(int r, int g, int b, Vector offset) {
        public void display(Location location) {
            location.getWorld().spawnParticle(
                    Particle.REDSTONE,
                    location.clone().add(offset),
                    1,
                    new Particle.DustOptions(
                            org.bukkit.Color.fromRGB(r, g, b),
                            1
                    )
            );
        }
    }
