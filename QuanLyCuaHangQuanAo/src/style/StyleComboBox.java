package style;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

public class StyleComboBox {
	   private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
	    private static final Color CONTENT_COLOR = new Color(255, 242, 242);
	    private static final Color HOVER_COLOR = new Color(252, 231, 243);
	    private static final Font HEADER_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 14);
	    private static final Font CONTENT_FONT = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12);
	    private static final Font TITLE_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 16);

    public void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setPreferredSize(new Dimension(300, 35));
        comboBox.setMaximumSize(new Dimension(300, 35));
        comboBox.setFont(CONTENT_FONT);
        comboBox.setBackground(Color.WHITE);
        
        comboBox.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = super.createArrowButton();
                button.setBackground(Color.WHITE);
                button.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                return button;
            }
        });
        
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(0, 5, 0, 5)
        ));

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(CONTENT_FONT);
                setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
                
                if (isSelected) {
                    setBackground(HOVER_COLOR);
                    setForeground(Color.BLACK);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }
                return this;
            }
        });

        comboBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (comboBox.isEnabled()) {
                    comboBox.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(PRIMARY_COLOR, 1),
                        BorderFactory.createEmptyBorder(0, 5, 0, 5)
                    ));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!comboBox.hasFocus()) {
                    comboBox.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(230, 230, 230), 1),
                        BorderFactory.createEmptyBorder(0, 5, 0, 5)
                    ));
                }
            }
        });
    }

}
