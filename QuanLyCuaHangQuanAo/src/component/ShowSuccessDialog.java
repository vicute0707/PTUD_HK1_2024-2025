package component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

public class ShowSuccessDialog {
    private static final Font CONTENT_FONT = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12);

	 public void showSuccessDialog(String message) {
	        JDialog successDialog = new JDialog();
	        successDialog.setLayout(new BorderLayout());
	        successDialog.setBackground(Color.WHITE);
	        JPanel panel = new JPanel();
	        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	        panel.setBackground(Color.WHITE);
	        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
	        JLabel messageLabel = new JLabel(message);
	        messageLabel.setFont(CONTENT_FONT);
	        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	        panel.add(Box.createVerticalStrut(15));
	        panel.add(messageLabel);
	        successDialog.add(panel);
	        successDialog.pack();

	        // Tự động đóng dialog sau 2 giây
	        Timer timer = new Timer(2000, e -> successDialog.dispose());
	        timer.setRepeats(false);
	        timer.start();

	        successDialog.setVisible(true);
	    }

}
