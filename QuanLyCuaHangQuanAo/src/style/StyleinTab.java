package style;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class StyleinTab {
    private static final Color CONTENT_COLOR = new Color(255, 192, 203);
    private static final Color HOVER_COLOR = new Color(252, 231, 243);
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 12);
    private static final Font CONTENT_FONT = new Font("Arial", Font.PLAIN, 12);
    private static final Font TAB_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Color PRIMARY_COLOR = new Color(219, 39, 119);

    public void styleTextField(JTextField field) {
        field.setPreferredSize(new Dimension(200, 38));
        field.setFont(CONTENT_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(240, 240, 240)),
            new EmptyBorder(5, 10, 5, 10)
        ));
    }
    public JButton createIconButton(String iconPath) {
        JButton button = new JButton(new ImageIcon(getClass().getResource(iconPath)));
        button.setPreferredSize(new Dimension(38, 38));
        button.setBackground(Color.WHITE);
        button.setBorder(new LineBorder(new Color(240, 240, 240)));
        button.setFocusPainted(false);
        styleButtonHover(button);
        return button;
    }
    private void styleButtonHover(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!button.getBackground().equals(PRIMARY_COLOR)) {
                    button.setBackground(HOVER_COLOR);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!button.getBackground().equals(PRIMARY_COLOR)) {
                    button.setBackground(Color.WHITE);
                }
            }
        });
    }
    public JButton createActionButton(String text, String iconPath, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(CONTENT_FONT);
        button.setIcon(new ImageIcon(getClass().getResource(iconPath)));
        button.setIconTextGap(10);
        button.setPreferredSize(new Dimension(text.length() > 10 ? 150 : 120, 38));
        button.setFocusPainted(false);
        
        if (isPrimary) {
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.WHITE);
            button.setBorder(new LineBorder(PRIMARY_COLOR));
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(Color.BLACK);
            button.setBorder(new LineBorder(new Color(240, 240, 240)));
            styleButtonHover(button);
        }
        
        return button;
    }
    public void styleTable(JTable table) {
        table.setFont(CONTENT_FONT);
        table.setRowHeight(40);
        table.setGridColor(new Color(240, 240, 240));
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setSelectionBackground(HOVER_COLOR);
        table.setSelectionForeground(Color.BLACK);
        table.setBackground(Color.WHITE);
        
        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(new Color(255, 240, 245));
        header.setForeground(Color.BLACK);
        header.setBorder(new LineBorder(new Color(240, 240, 240)));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        
        // Center align for first column and status/gender columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        
        // Additional center alignment for specific columns based on table type
        if (table.getColumnCount() > 2) {
            if (table.getColumnName(2).equals("Giới tính")) {
                table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
            }
            if (table.getColumnName(3).equals("Trạng thái")) {
                table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
            }
        }
    }


}
