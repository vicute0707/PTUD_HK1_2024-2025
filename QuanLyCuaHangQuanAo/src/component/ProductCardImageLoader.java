package component;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ProductCardImageLoader {
    private static final String IMAGE_DIRECTORY = "src/img_product";
    private static final Logger LOGGER = Logger.getLogger(ProductCardImageLoader.class.getName());

    public static ImageIcon loadProductImage(String imagePath, int width, int height) {
        try {
            // Extract just the filename from the full path
            String fileName = new File(imagePath).getName();
            
            // Create proper path to image in our application directory
            Path targetPath = Paths.get(IMAGE_DIRECTORY, fileName);
            
            if (Files.exists(targetPath)) {
                BufferedImage img = ImageIO.read(targetPath.toFile());
                Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
            return null;
        } catch (IOException e) {
            LOGGER.warning("Error loading image: " + e.getMessage());
            return null;
        }
    }
}