package component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.toedter.calendar.JDateChooser;

public class CreateDateChooser {
    private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
    private static final Color CONTENT_COLOR = new Color(255, 242, 242);
    private static final Color HOVER_COLOR = new Color(252, 231, 243);
    private static final Font HEADER_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 14);
    private static final Font CONTENT_FONT = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12);
    private static final Font TITLE_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 16);
	  public JDateChooser createDateChooser() {
	    	JDateChooser chooser = new JDateChooser();
	        chooser.setPreferredSize(new Dimension(200, 30)); // Giảm chiều cao xuống 25
	        chooser.setMaximumSize(new Dimension(200, 35));  // Thêm maxSize
	        chooser.setMinimumSize(new Dimension(200, 30));  // Thêm minSize
	        chooser.setFont(CONTENT_FONT);
	        chooser.setDateFormatString("dd/MM/yyyy");
	        chooser.setBackground(Color.WHITE);
	        
	        // Tùy chỉnh calendar button và textfield container
	        for (Component comp : chooser.getComponents()) {
	            if (comp instanceof JButton) {
	                JButton calendarButton = (JButton) comp;
	                calendarButton.setPreferredSize(new Dimension(20, 23));
	                calendarButton.setBackground(Color.WHITE);
	                calendarButton.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
	                
	                calendarButton.addMouseListener(new MouseAdapter() {
	                    @Override
	                    public void mouseEntered(MouseEvent e) {
	                        calendarButton.setBackground(HOVER_COLOR);
	                    }
	                    
	                    @Override
	                    public void mouseExited(MouseEvent e) {
	                        calendarButton.setBackground(Color.WHITE);
	                    }
	                });
	            }
	        }
	        
	        // Tùy chỉnh text field
	        JTextField textField = ((JTextField)chooser.getDateEditor().getUiComponent());
	        textField.setPreferredSize(new Dimension(160, 23));
	        textField.setBackground(Color.WHITE);
	        textField.setBorder(new LineBorder(new Color(230, 230, 230), 1));
	        textField.setFont(new Font(CONTENT_FONT.getFamily(), Font.PLAIN, 11)); // Giảm font size
	        
	        // Set size cho container của textfield
	        Component dateEditor = (Component) chooser.getDateEditor();
	        if (dateEditor instanceof JComponent) {
	            ((JComponent) dateEditor).setPreferredSize(new Dimension(160, 23));
	        }
	        
	        // Thêm hiệu ứng hover và focus
	        textField.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseEntered(MouseEvent e) {
	                if (textField.isEnabled()) {
	                    textField.setBorder(new LineBorder(PRIMARY_COLOR, 1));
	                }
	            }
	            
	            @Override
	            public void mouseExited(MouseEvent e) {
	                if (!textField.hasFocus()) {
	                    textField.setBorder(new LineBorder(new Color(230, 230, 230), 1));
	                }
	            }
	        });
	        
	        textField.addFocusListener(new FocusAdapter() {
	            @Override
	            public void focusGained(FocusEvent e) {
	                textField.setBorder(new LineBorder(PRIMARY_COLOR, 1));
	            }
	            
	            @Override
	            public void focusLost(FocusEvent e) {
	                textField.setBorder(new LineBorder(new Color(230, 230, 230), 1));
	            }
	        });
	        
	        return chooser;

	    }

}
