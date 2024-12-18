package component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {
	private Image image;
    private int width;
    private int height;
    private static final Logger LOGGER = Logger.getLogger(ImagePanel.class.getName());
    private static final String PROJECT_ROOT = System.getProperty("user.dir");
    private static final String RESOURCES_PATH = "src/main/resources";
    
	    public ImagePanel(String imagePath, int width, int height) {
	        this.width = width;
	        this.height = height;
	        
	        try {
	            // Clean and normalize the image path
	            String normalizedPath = normalizeImagePath(imagePath);
	            
	            // Construct the full system path
	            Path fullPath = Paths.get(PROJECT_ROOT, RESOURCES_PATH, normalizedPath);
	            File imageFile = fullPath.toFile();
	            
	            LOGGER.info("Attempting to load image from: " + fullPath);
	            
	            if (imageFile.exists()) {
	                image = new ImageIcon(imageFile.getPath()).getImage();
	                image = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	            } else {
	                // Try loading default image
	                String defaultPath = normalizeImagePath("/images/products/default-product.png");
	                Path defaultFullPath = Paths.get(PROJECT_ROOT, RESOURCES_PATH, defaultPath);
	                File defaultFile = defaultFullPath.toFile();
	                
	                if (defaultFile.exists()) {
	                    image = new ImageIcon(defaultFile.getPath()).getImage();
	                    image = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	                } else {
	                    LOGGER.warning("Default image not found, creating placeholder");
	                    image = createPlaceholderImage();
	                }
	            }
	            
	        } catch (Exception e) {
	            LOGGER.warning("Failed to load image: " + e.getMessage());
	            image = createPlaceholderImage();
	        }
	        
	        setPreferredSize(new Dimension(width, height));
	    }
	    
	    private String normalizeImagePath(String path) {
	        if (path == null || path.trim().isEmpty()) {
	            return "images/products/default-product.png";
	        }
	        
	        // Remove any leading or trailing whitespace
	        path = path.trim();
	        
	        // Convert backslashes to forward slashes
	        path = path.replace('\\', '/');
	        
	        // Remove drive letter and absolute path if present (e.g., C:/Users/...)
	        if (path.matches("^[A-Za-z]:/.*")) {
	            path = path.substring(path.indexOf("/") + 1);
	        }
	        
	        // Remove leading slash if present
	        if (path.startsWith("/")) {
	            path = path.substring(1);
	        }
	        
	        // Remove duplicate /images prefix if present
	        if (path.startsWith("images/images/")) {
	            path = path.substring(7);
	        }
	        
	        // Remove duplicate slashes
	        path = path.replaceAll("//+", "/");
	        
	        // Ensure path starts with images/
	        if (!path.startsWith("images/")) {
	            path = "images/" + path;
	        }
	        
	        LOGGER.info("Normalized path: " + path);
	        return path;
	    }
    
    // The createPlaceholderImage and paintComponent methods remain the same
    private Image createPlaceholderImage() {
        BufferedImage placeholder = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = placeholder.createGraphics();
        
        g2d.setColor(new Color(245, 245, 245));
        g2d.fillRect(0, 0, width, height);
        
        g2d.setColor(Color.GRAY);
        g2d.drawRect(0, 0, width - 1, height - 1);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "No Image";
        int textX = (width - fm.stringWidth(text)) / 2;
        int textY = (height + fm.getAscent()) / 2;
        g2d.drawString(text, textX, textY);
        
        g2d.dispose();
        return placeholder;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            int x = (getWidth() - width) / 2;
            int y = (getHeight() - height) / 2;
            g.drawImage(image, x, y, null);
        }
    }
}