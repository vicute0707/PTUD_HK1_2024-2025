package component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {
	 private Image image;
     private int width;
     private int height;

     public ImagePanel(String imagePath, int width, int height) {
         this.width = width;
         this.height = height;
         try {
             image = new ImageIcon(getClass().getResource(imagePath))
                     .getImage()
                     .getScaledInstance(width, height, Image.SCALE_SMOOTH);
         } catch (Exception e) {
             image = createDefaultImage(width, height);
         }
         setPreferredSize(new Dimension(width, height));
     }

     private Image createDefaultImage(int width, int height) {
         BufferedImage defaultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
         Graphics2D g2d = defaultImage.createGraphics();
         g2d.setColor(new Color(240, 240, 240));
         g2d.fillRect(0, 0, width, height);
         g2d.setColor(Color.GRAY);
         g2d.drawRect(0, 0, width-1, height-1);
         g2d.drawString("No Image", width/4, height/2);
         g2d.dispose();
         return defaultImage;
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
