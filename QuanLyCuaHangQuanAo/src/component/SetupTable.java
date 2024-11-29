package component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

public class SetupTable {
	private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
	private static final Color CONTENT_COLOR = new Color(255, 242, 242);
	private static final Color HOVER_COLOR = new Color(252, 231, 243);
	private static final Font HEADER_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 14);
	private static final Font CONTENT_FONT = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12);
	private static final Font TITLE_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 16);
	public void setupTable(JTable table) {
		table.setFont(CONTENT_FONT);
		table.setRowHeight(40);
		table.setGridColor(new Color(245, 245, 245));
		table.setSelectionBackground(HOVER_COLOR);
		table.setSelectionForeground(Color.BLACK);
		table.setShowVerticalLines(true);
		table.setShowHorizontalLines(true);

		// Set column widths
		int[] columnWidths = { 100, 250, 200, 150, 200 };
		for (int i = 0; i < columnWidths.length; i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
		}

		// Setup header
		JTableHeader header = table.getTableHeader();
		header.setFont(HEADER_FONT);
		header.setBackground(Color.WHITE);
		header.setForeground(Color.BLACK);
		header.setPreferredSize(new Dimension(header.getPreferredSize().width, 45));
		header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));

		// Setup cell renderers
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
		table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
	}

}
