package style;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class CustomScrollBarUI extends BasicScrollBarUI{
    private static final Color CONTENT_COLOR = new Color(255, 192, 203);

	 @Override
     protected void configureScrollBarColors() {
         this.thumbColor = CONTENT_COLOR;
         this.trackColor = Color.WHITE;
     }
     
     @Override
     protected JButton createDecreaseButton(int orientation) {
         return createZeroButton();
     }
     
     @Override
     protected JButton createIncreaseButton(int orientation) {
         return createZeroButton();
     }
     
     @Override
     protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
         Graphics2D g2 = (Graphics2D) g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
         g2.setPaint(thumbColor);
         g2.fillRoundRect(thumbBounds.x, thumbBounds.y,
                        thumbBounds.width, thumbBounds.height,
                        10, 10);
         g2.dispose();
     }
     
     @Override
     protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
         Graphics2D g2 = (Graphics2D) g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
         g2.setPaint(trackColor);
         if (this.scrollbar.getOrientation() == JScrollBar.VERTICAL) {
             g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
         } else {
             g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
         }
         g2.dispose();
     }
     
     private JButton createZeroButton() {
         JButton button = new JButton();
         button.setPreferredSize(new Dimension(0, 0));
         button.setMinimumSize(new Dimension(0, 0));
         button.setMaximumSize(new Dimension(0, 0));
         return button;
     }

}
