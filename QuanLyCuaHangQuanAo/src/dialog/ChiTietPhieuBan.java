package dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

import style.CreateFilter;

public class ChiTietPhieuBan {
    private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
    private static final Color CONTENT_COLOR = new Color(255, 242, 242);
    private static final Color HOVER_COLOR = new Color(252, 231, 243);
    private static final Font HEADER_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 14);
    private static final Font CONTENT_FONT = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12);
    private static final Font TITLE_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 16);
    CreateFilter createFilter;
	public void viewDetail(JTable table) {
		createFilter = new CreateFilter();
		int selectedRow = table.getSelectedRow();
		table.setPreferredSize(new Dimension(400,400)); 
		if (selectedRow != -1) {
			String maDonBan = table.getValueAt(selectedRow, 1).toString();
			String nhanVien = table.getValueAt(selectedRow, 2).toString();
			String thoiGian = table.getValueAt(selectedRow, 3).toString();
			String tongTien = table.getValueAt(selectedRow, 4).toString();

			// Tạo dialog
			JDialog dialog = new JDialog();
			dialog.setLayout(new BorderLayout(10, 10));
			dialog.setBackground(Color.WHITE);

			// Panel chứa thông tin chung
			JPanel infoPanel = new JPanel(new GridBagLayout());
			infoPanel.setBackground(Color.WHITE);
			infoPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
					new EmptyBorder(15, 15, 15, 15)));

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(5, 5, 5, 15);

			// Thêm thông tin chung
			addDetailField(infoPanel, "Mã đơn bán:", maDonBan, gbc, 0);
			addDetailField(infoPanel, "Nhân viên:", nhanVien, gbc, 1);
			addDetailField(infoPanel, "Thời gian:", thoiGian, gbc, 2);
			addDetailField(infoPanel, "Tổng tiền:", tongTien, gbc, 3);

			// Tạo bảng chi tiết sản phẩm
			String[] columns = { "STT", "Mã SP", "Tên sản phẩm", "Màu sắc", "Kích cỡ", "Đơn giá", "Số lượng",
					"Thành tiền" };
			DefaultTableModel detailModel = new DefaultTableModel(columns, 0) {
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};

			JTable detailTable = new JTable(detailModel);
			setupDetailTable(detailTable);

			// Thêm dữ liệu mẫu vào bảng chi tiết
			Object[][] sampleData = { { "1", "SP001", "Váy hoa", "Đỏ", "M", "350,000", "2", "700,000" },
					{ "2", "SP002", "Áo sơ mi", "Trắng", "L", "280,000", "1", "280,000" },
					{ "3", "SP003", "Quần jean", "Xanh", "29", "520,000", "1", "520,000" } };

			for (Object[] row : sampleData) {
				detailModel.addRow(row);
			}

			JScrollPane scrollPane = new JScrollPane(detailTable);
			scrollPane.setBorder(new EmptyBorder(10, 15, 10, 15));

			// Panel chứa nút
			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			buttonPanel.setBackground(Color.WHITE);
			buttonPanel.setBorder(new EmptyBorder(0, 15, 15, 15));

			JButton printButton = createFilter.createFilterButton("In đơn bán", true);
			JButton closeButton = createFilter.createDetailButton("Đóng", null, false);

			buttonPanel.add(printButton);
			buttonPanel.add(Box.createHorizontalStrut(10));
			buttonPanel.add(closeButton);

			// Thêm các panel vào dialog
			dialog.add(infoPanel, BorderLayout.NORTH);
			dialog.add(scrollPane, BorderLayout.CENTER);
			dialog.add(buttonPanel, BorderLayout.SOUTH);

			// Xử lý sự kiện nút
			printButton.addActionListener(e -> {
				JOptionPane.showMessageDialog(dialog, "Đang in đơn bán " + maDonBan, "Thông báo",
						JOptionPane.INFORMATION_MESSAGE);
			});

			closeButton.addActionListener(e -> dialog.dispose());

			// Hiển thị dialog
			dialog.setSize(900, 600);
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
		}
	}

	private void addDetailField(JPanel panel, String label, String value, GridBagConstraints gbc, int row) {
		gbc.gridx = 0;
		gbc.gridy = row;
		JLabel lblTitle = new JLabel(label);
		lblTitle.setFont(new Font(CONTENT_FONT.getFamily(), Font.BOLD, 13));
		panel.add(lblTitle, gbc);

		gbc.gridx = 1;
		JLabel lblValue = new JLabel(value);
		lblValue.setFont(CONTENT_FONT);
		panel.add(lblValue, gbc);
	}
	  
    private void setupDetailTable(JTable table) {
        table.setFont(CONTENT_FONT);
        table.setRowHeight(35);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(Color.WHITE);
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

        // Center align for specific columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // STT
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Mã SP
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // Số lượng
        table.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);  // Đơn giá
        table.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);  // Thành tiền
    }

}
