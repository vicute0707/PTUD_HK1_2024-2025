package style;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.border.LineBorder;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

public class StyleButton {
	  private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
	    private static final Color CONTENT_COLOR = new Color(255, 242, 242);
	    private static final Color HOVER_COLOR = new Color(252, 231, 243);
	    private static final Font HEADER_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 14);
	    private static final Font CONTENT_FONT = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12);
	    private static final Font TITLE_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 16);

	  public void styleButton(JButton button, boolean isPrimary) {
	        button.setFont(CONTENT_FONT);
	        button.setPreferredSize(new Dimension(100, 35));
	        
	        if (isPrimary) {
	            button.setBackground(PRIMARY_COLOR);
	            button.setForeground(Color.WHITE);
	            button.setBorder(new LineBorder(PRIMARY_COLOR));
	        } else {
	            button.setBackground(Color.WHITE);
	            button.setForeground(Color.BLACK);
	            button.setBorder(new LineBorder(new Color(230, 230, 230)));
	        }
	        
	        button.setFocusPainted(false);

	        // Thêm hiệu ứng hover
	        button.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseEntered(MouseEvent e) {
	                if (isPrimary) {
	                    button.setBackground(PRIMARY_COLOR.darker());
	                } else {
	                    button.setBackground(HOVER_COLOR);
	                }
	            }

	            @Override
	            public void mouseExited(MouseEvent e) {
	                if (isPrimary) {
	                    button.setBackground(PRIMARY_COLOR);
	                } else {
	                    button.setBackground(Color.WHITE);
	                }
	            }
	        });
	    }

}
