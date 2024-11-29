package style;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.border.LineBorder;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

public class StyleFormattedTextField {
	private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
	private static final Color CONTENT_COLOR = new Color(255, 242, 242);
    private static final Font CONTENT_FONT = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12);

	public void styleFormattedTextField(JFormattedTextField textField) {
		textField.setPreferredSize(new Dimension(70, 30));
		textField.setFont(CONTENT_FONT);
		textField.setBackground(Color.WHITE);
		textField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(230, 230, 230), 1),
				BorderFactory.createEmptyBorder(0, 8, 0, 8)));

		// Add hover and focus effects
		textField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (textField.isEnabled()) {
					textField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(PRIMARY_COLOR, 1),
							BorderFactory.createEmptyBorder(0, 8, 0, 8)));
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (!textField.hasFocus()) {
					textField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(230, 230, 230), 1),
							BorderFactory.createEmptyBorder(0, 8, 0, 8)));
				}
			}
		});

		textField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(PRIMARY_COLOR, 1),
						BorderFactory.createEmptyBorder(0, 8, 0, 8)));
			}

			@Override
			public void focusLost(FocusEvent e) {
				textField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(230, 230, 230), 1),
						BorderFactory.createEmptyBorder(0, 8, 0, 8)));
			}
		});
	}

}
