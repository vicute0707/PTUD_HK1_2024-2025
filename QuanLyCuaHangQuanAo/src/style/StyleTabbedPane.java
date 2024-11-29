package style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class StyleTabbedPane {
	 private JTabbedPane tabbedPane;
	    private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
	public void styleTabbedPane(JTabbedPane tabbedPane) {
        tabbedPane.setUI(new BasicTabbedPaneUI() {
            private final Insets borderInsets = new Insets(0, 0, 0, 0);
            private final Color selectedColor = PRIMARY_COLOR;
            private final int TAB_HEIGHT = 45;
            
            @Override
            protected void installDefaults() {
                super.installDefaults();
                highlight = Color.WHITE;
                lightHighlight = Color.WHITE;
                shadow = Color.WHITE;
                darkShadow = Color.WHITE;
                focus = PRIMARY_COLOR;
            }

            @Override
            protected Insets getContentBorderInsets(int tabPlacement) {
                return borderInsets;
            }

            @Override
            protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                return TAB_HEIGHT;
            }
            
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement,
                                           int tabIndex, int x, int y, int w, int h, 
                                           boolean isSelected) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                   RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (!isSelected) {
                    GradientPaint gp = new GradientPaint(
                        x, y, new Color(252, 251, 251),
                        x, y + h, new Color(250, 250, 250));
                    g2d.setPaint(gp);
                } else {
                    g2d.setColor(Color.WHITE);
                }
                g2d.fillRect(x, y, w, h);
            }

            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                        int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                   RenderingHints.VALUE_ANTIALIAS_ON);
                                   
                if (isSelected) {
                    g2d.setColor(selectedColor);
                    g2d.setStroke(new BasicStroke(3));
                    g2d.drawLine(x, y + h - 2, x + w, y + h - 2);
                } else {
                    g2d.setColor(new Color(240, 240, 240));
                    g2d.setStroke(new BasicStroke(1));
                    g2d.drawLine(x, y + h - 1, x + w, y + h - 1);
                }
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                // Không vẽ border cho content area
            }

            @Override
            protected void paintFocusIndicator(Graphics g, int tabPlacement,
                                             Rectangle[] rects, int tabIndex,
                                             Rectangle iconRect, Rectangle textRect,
                                             boolean isSelected) {
                // Không vẽ focus indicator
            }
            
            @Override
            protected void layoutLabel(int tabPlacement, FontMetrics metrics,
                                     int tabIndex, String title, Icon icon,
                                     Rectangle tabRect, Rectangle iconRect,
                                     Rectangle textRect, boolean isSelected) {
                super.layoutLabel(tabPlacement, metrics, tabIndex, title, icon, 
                                tabRect, iconRect, textRect, isSelected);
                
                // Căn giữa text theo chiều ngang
                textRect.x = tabRect.x + (tabRect.width - textRect.width) / 2;
                
                // Căn giữa text theo chiều dọc
                textRect.y = tabRect.y + (tabRect.height - textRect.height) / 2 + 2;
            }

            @Override
            protected int getTabLabelShiftY(int tabPlacement, int tabIndex, boolean isSelected) {
                return 0;
            }

            @Override
            protected int getTabLabelShiftX(int tabPlacement, int tabIndex, boolean isSelected) {
                return 0;
            }
        });
        
        tabbedPane.setBorder(new EmptyBorder(10, 15, 0, 15));
    }

}
