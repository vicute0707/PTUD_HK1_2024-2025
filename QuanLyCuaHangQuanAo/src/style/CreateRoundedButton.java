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

public class CreateRoundedButton {
	private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
	private static final Color CONTENT_COLOR = new Color(255, 192, 203);
	private static final Color HOVER_COLOR = new Color(252, 231, 243);
	private static final Font HEADER_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12);
	private static final Font CONTENT_FONT = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12);
	public JButton createRoundedButton(String text, String iconPath, boolean isRounded) {
		JButton button = new JButton(text);
		button.setFont(CONTENT_FONT);
		if (iconPath != null && !iconPath.isEmpty()) {
			button.setIcon(new ImageIcon(getClass().getResource(iconPath)));
		}
		if (isRounded) {
			button.setBorder(new LineBorder(new Color(230, 230, 230), 1, true));
		} else {
			button.setBorder(BorderFactory.createEmptyBorder());
		}

		button.setFocusPainted(false);
		button.setContentAreaFilled(true);
		button.setBackground(Color.WHITE);
		button.setPreferredSize(new Dimension(text.isEmpty() ? 38 : 130, 38));

		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (!button.getForeground().equals(Color.WHITE)) {
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

		return button;
	}

}
