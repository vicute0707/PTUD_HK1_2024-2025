package style;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import javax.swing.border.AbstractBorder;

public class ShadowBorder extends AbstractBorder {
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Color shadowColor = new Color(0, 0, 0, 20);
        g2.setColor(shadowColor);
        g2.fill(new RoundRectangle2D.Double(x+2, y+2, width-4, height-4, 15, 15));
        
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(4, 4, 4, 4);
    }
}
