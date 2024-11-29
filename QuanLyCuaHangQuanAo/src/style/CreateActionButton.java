package style;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.LineBorder;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

public class CreateActionButton {
    private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
    private static final Color CONTENT_COLOR = new Color(255, 242, 242);
    private static final Color HOVER_COLOR = new Color(252, 231, 243);
    private static final Font HEADER_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 14);
    private static final Font CONTENT_FONT = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12);
    private static final Font TITLE_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 16);
    public JButton createActionButton(String text, String iconPath, boolean isRounded, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(CONTENT_FONT);
        button.setIcon(new ImageIcon(getClass().getResource(iconPath)));
        button.setFocusPainted(false);

        if (isPrimary) {
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(Color.BLACK);
        }

        if (isRounded) {
            button.setBorder(new LineBorder(isPrimary ? PRIMARY_COLOR : new Color(230, 230, 230), 1, true));
        } else {
            button.setBorder(BorderFactory.createEmptyBorder());
        }

        button.setPreferredSize(new Dimension(130, 38));
        addButtonHoverEffect(button);
        
        return button;
    }
    public void addButtonHoverEffect(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.getBackground().equals(PRIMARY_COLOR)) {
                    button.setBackground(PRIMARY_COLOR.darker());
                } else {
                    button.setBackground(HOVER_COLOR);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.getForeground().equals(Color.WHITE)) {
                    button.setBackground(PRIMARY_COLOR);
                } else {
                    button.setBackground(Color.WHITE);
                }
            }
        });
    }
    

}
