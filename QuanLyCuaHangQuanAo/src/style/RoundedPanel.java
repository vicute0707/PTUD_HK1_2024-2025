package style;

import java.awt.*;

import javax.swing.JPanel;

public class RoundedPanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int radius;

    public RoundedPanel(int radius) {
        super();
        this.radius = radius;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
        g2.dispose();
    }
}