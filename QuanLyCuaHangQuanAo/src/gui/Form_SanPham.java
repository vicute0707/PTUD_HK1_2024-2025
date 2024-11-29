package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

import bus.SanPhamBUS;
import dialog.ChiTietSanPham;
import dialog.EditSanPham;
import dialog.ThemSanPham;

import entity.SanPham;
import entity.UserSession;
import service.PermissionChecker;
import style.CustomScrollBarUI;

public class Form_SanPham extends JPanel {

	private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
	private static final Color CONTENT_COLOR = new Color(255, 192, 203);
	private static final Color HOVER_COLOR = new Color(252, 231, 243);
	private static final Font HEADER_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12);
	private static final Font CONTENT_FONT = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12);
	private int selectedRow = -1; // Thêm biến để lưu hàng được chọn
	private JTable table;
	private DefaultTableModel tableModel;
	private JTextField searchField;
	SanPhamBUS sanPhamBUS;
	private static final Logger LOGGER = Logger.getLogger(Form_SanPham.class.getName());

	public Form_SanPham() {
		sanPhamBUS = new SanPhamBUS();
		table = new JTable();
		tableModel = new DefaultTableModel();
		initComponents();
		loadInitialData();
	}

	private void initComponents() {
		setLayout(new BorderLayout(0, 20));
		setBackground(Color.WHITE);
		setBorder(new EmptyBorder(30, 30, 30, 30)); // Tăng padding cho toàn bộ form

		// Top Panel
		add(createTopPanel(), BorderLayout.NORTH);
		// Table Panel
		add(createTablePanel(), BorderLayout.CENTER);
	}

	public JPanel createTablePanel() {
		JPanel tablePanel = new JPanel(new BorderLayout(0, 15));
		tablePanel.setBackground(Color.WHITE);
		tablePanel.setBorder(new EmptyBorder(10, 0, 0, 0));

		// Khởi tạo columns
		String[] columns = { "Mã SP", "Tên sản phẩm", "Danh mục", "Số lượng tồn", "Giá nhập", "Giá bán", "Thương hiệu",
				"Tình trạng" };

		// Khởi tạo tableModel
		this.tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		// Khởi tạo table với model vừa tạo
		this.table = new JTable(this.tableModel);
		setupTable();

		// Thêm dữ liệu mẫu
		loadDataFromDB();
		// Setup scrollPane
		JScrollPane scrollPane = setupScrollPane();
		tablePanel.add(scrollPane, BorderLayout.CENTER);

		return tablePanel;
	}

	private void setupTable() {
		table.setFont(CONTENT_FONT);
		table.setRowHeight(32);
		table.setGridColor(new Color(245, 245, 245));
		table.setSelectionBackground(HOVER_COLOR);
		table.setSelectionForeground(Color.BLACK);
		table.setIntercellSpacing(new Dimension(10, 10));
		table.setPreferredScrollableViewportSize(new Dimension(800, 400));
		table.setFillsViewportHeight(true);

		// Setup header
		JTableHeader header = table.getTableHeader();
		header.setFont(HEADER_FONT);
		header.setBackground(Color.WHITE);
		header.setForeground(Color.BLACK);
		header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
		header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

		// Set status column renderer
		setupStatusColumnRenderer();
	}

	private JScrollPane setupScrollPane() {
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.getViewport().setBackground(Color.WHITE);
		scrollPane.setBackground(Color.WHITE);
		scrollPane.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(10, 10, 10, 10),
				BorderFactory.createLineBorder(new Color(245, 245, 245))));

		scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
		scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI());

		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		return scrollPane;
	}

	private void setupStatusColumnRenderer() {
		table.getColumnModel().getColumn(7).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
			JLabel label = new JLabel(value.toString());
			label.setOpaque(true);
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setFont(CONTENT_FONT);
			label.setBorder(new EmptyBorder(0, 5, 0, 5));

			switch (value.toString()) {
			case "Còn hàng":
				label.setForeground(new Color(0, 128, 0));
				break;
			case "Sắp hết":
				label.setForeground(new Color(255, 140, 0));
				break;
			case "Hết hàng":
				label.setForeground(Color.RED);
				break;
			}

			if (isSelected) {
				label.setBackground(HOVER_COLOR);
			} else {
				label.setBackground(table.getBackground());
			}

			return label;
		});
	}

	public void loadDataFromDB() {
		tableModel.setRowCount(0); // Clear existing data

		try {
			// Use a database connection and execute a query to fetch the product data
			// Assuming you have a SanPhamBUS class that handles the database operations
			SanPhamBUS sanPhamBUS = new SanPhamBUS();
			java.util.List<SanPham> dsSanPham = sanPhamBUS.getAllSanPham();

			// Format the data and add it to the table model
			NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
			for (SanPham sp : dsSanPham) {
				String giaNhap = currencyFormat.format(sp.getGiaNhap());
				String giaBan = currencyFormat.format(sp.getGiaBan());
				String tinhTrang = getTinhTrangText(sp.getSoLuongTonKho());

				Object[] row = { sp.getMaSP(), sp.getTenSP(), sp.getDanhmuc().getTenDM(), sp.getSoLuongTonKho(),
						giaNhap, giaBan, sp.getThuongHieu(), tinhTrang };
				tableModel.addRow(row);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private String getTinhTrangText(int soLuongTonKho) {
		if (soLuongTonKho <= 0) {
			return "Hết hàng";
		} else if (soLuongTonKho <= 10) {
			return "Sắp hết";
		} else {
			return "Còn hàng";
		}
	}

	public JTable getTable() {
		return this.table;
	}

	public DefaultTableModel getTableModel() {
		return this.tableModel;
	}

	private void loadInitialData() {
		try {
			ArrayList<SanPham> danhSachSP = sanPhamBUS.getAllSanPham();
			tableModel.setRowCount(0);

			for (SanPham sp : danhSachSP) {
				NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
				String giaNhap = currencyFormat.format(sp.getGiaNhap());
				String giaBan = currencyFormat.format(sp.getGiaBan());

				Object[] row = { sp.getMaSP(), sp.getTenSP(), sp.getDanhmuc().getTenDM(), sp.getSoLuongTonKho(),
						giaNhap, giaBan, sp.getThuongHieu(), getTinhTrangText(sp.getSoLuongTonKho()) };
				tableModel.addRow(row);
			}
		} catch (Exception e) {
			LOGGER.severe("Lỗi tải dữ liệu: " + e.getMessage());
			JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private JPanel createTopPanel() {
		JPanel panel = new JPanel(new BorderLayout(20, 0));
		panel.setBackground(Color.WHITE);

		// Get current user for permission checking
		String currentUserId = UserSession.getInstance().getCurrentUser().getUserID();

		// Create search panel with proper alignment
		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
		searchPanel.setBackground(Color.WHITE);

		// Custom ComboBox for filtering
		JComboBox<String> filterCombo = new JComboBox<>(new String[] { "Theo Mã", "Tên SP", "Tên DM", "Thương Hiệu" });
		filterCombo.setPreferredSize(new Dimension(120, 35));
		filterCombo.setFont(CONTENT_FONT);
		filterCombo.setBorder(BorderFactory.createEmptyBorder());
		filterCombo.setBackground(Color.WHITE);

		// Style the combo box
		filterCombo.setUI(new BasicComboBoxUI() {
			@Override
			protected void installDefaults() {
				super.installDefaults();
				LookAndFeel.installProperty(comboBox, "opaque", false);
			}

			@Override
			protected JButton createArrowButton() {
				JButton button = new JButton();
				button.setBorder(BorderFactory.createEmptyBorder());
				button.setContentAreaFilled(false);
				button.setIcon(new ImageIcon(getClass().getResource("/icon/arrow-down.png")));
				return button;
			}

		});

		// Search field setup
		searchField = new JTextField();
		searchField.setPreferredSize(new Dimension(220, 35));
		searchField.setFont(CONTENT_FONT);

		// Create and configure search button
		JButton searchButton = createRoundedButton("", "/icon/search.png", false);
		configureButtonWithPermission(searchButton, currentUserId, PermissionChecker.PERM_PRODUCT_MANAGEMENT, () -> {
			String keyword = searchField.getText().trim();

			if (keyword.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm!", "Thông báo",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			// Implement search logic here

		});

		// Add components to search panel
		searchPanel.add(filterCombo);
		searchPanel.add(searchField);
		searchPanel.add(searchButton);

		// Create action buttons panel
		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
		actionPanel.setBackground(Color.WHITE);

		// Add Product Button
		JButton addButton = createRoundedButton("Thêm sản phẩm", "/icon/circle-plus.png", true);
		addButton.setBackground(PRIMARY_COLOR);
		addButton.setForeground(Color.WHITE);
		addButton.setPreferredSize(new Dimension(160, 38));
		configureButtonWithPermission(addButton, currentUserId, PermissionChecker.PERM_PRODUCT_MANAGEMENT, () -> {
			ThemSanPham dialog = new ThemSanPham((Frame) SwingUtilities.getWindowAncestor(Form_SanPham.this));
			dialog.setVisible(true);
			if (dialog.isConfirmed()) {
				handleAddProduct(dialog);
			}
		});

		// Edit Button
		JButton editButton = createRoundedButton("Edit", "/icon/pencil.png", true);
		configureButtonWithPermission(editButton, currentUserId, PermissionChecker.PERM_PRODUCT_MANAGEMENT, () -> {
			handleEditProduct();
		});

		// Delete Button
		JButton deleteButton = createRoundedButton("Xóa", "/icon/trash.png", true);
		configureButtonWithPermission(deleteButton, currentUserId, PermissionChecker.PERM_PRODUCT_MANAGEMENT, () -> {
			handleDeleteProduct();
		});

		// Info Button
		JButton infoButton = createRoundedButton("About", "/icon/info.png", true);
		configureButtonWithPermission(infoButton, currentUserId, PermissionChecker.PERM_PRODUCT_MANAGEMENT, () -> {
			handleProductInfo();
		});

		// Export Button
		JButton exportButton = createRoundedButton("Xuất Excel", "/icon/printer.png", true);
		exportButton.setPreferredSize(new Dimension(160, 38));
		configureButtonWithPermission(exportButton, currentUserId, PermissionChecker.PERM_REPORT, () -> {
			handleExportToExcel();
		});

		// Add buttons to action panel
		actionPanel.add(addButton);
		actionPanel.add(editButton);
		actionPanel.add(deleteButton);
		actionPanel.add(infoButton);
		actionPanel.add(exportButton);

		// Add panels to the main panel
		panel.add(searchPanel, BorderLayout.WEST);
		panel.add(actionPanel, BorderLayout.EAST);

		return panel;
	}

	// Helper methods for button actions
	private void handleAddProduct(ThemSanPham dialog) {
		Object[] rowData = { "PRD" + (tableModel.getRowCount() + 1), dialog.getTenSP(), dialog.getDanhMuc(),
				dialog.getTonKho(), dialog.getGiaNhap(), dialog.getGiaBan(), dialog.getThuongHieu(), "Còn hàng" };
		tableModel.addRow(rowData);
	}

	private void handleEditProduct() {
		selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm để chỉnh sửa.", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		EditSanPham dialog = new EditSanPham((Frame) SwingUtilities.getWindowAncestor(this), tableModel, selectedRow);
		dialog.setVisible(true);
	}

	private void handleDeleteProduct() {
		selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm để xóa.", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		showDeleteConfirmDialog();
	}

	private void handleProductInfo() {
		String maSP = table.getValueAt(selectedRow, 1).toString();
		selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm để xem chi tiết.", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		ChiTietSanPham dialog = new ChiTietSanPham((Frame) SwingUtilities.getWindowAncestor(this), maSP);
		dialog.setVisible(true);
	}

	private void handleExportToExcel() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Chọn vị trí lưu file");
		fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xlsx"));

		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			String filePath = fileChooser.getSelectedFile().getAbsolutePath();
			if (!filePath.endsWith(".xlsx")) {
				filePath += ".xlsx";
			}
			exportToHTMLWithFont(table, filePath);
		}
	}

	private void showDeleteConfirmDialog() {
		String maSP = table.getValueAt(selectedRow, 0).toString();
		int confirm = JOptionPane.showConfirmDialog(this,
				"Bạn có chắc muốn xóa sản phẩm " + maSP + "?\n" + "Hành động này không thể hoàn tác!", "Xác nhận xóa",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (confirm == JOptionPane.YES_OPTION) {
			tableModel.removeRow(selectedRow);
			updateTable();
		}
	}

	private void updateTable() {
		// Update STT column and any other necessary updates
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			tableModel.setValueAt(i + 1, i, 0);
		}
	}

	private void configureButtonWithPermission(JButton button, String userId, String permission, Runnable action) {
		LOGGER.info("Configuring button for user: " + userId + " permission: " + permission);

		if (PermissionChecker.hasPermission(userId, permission)) {
			LOGGER.info("Permission granted");
			button.setEnabled(true);
			button.addActionListener(e -> {
				if (button.isEnabled()) {
					action.run();
				}
			});
		} else {
			LOGGER.warning("Permission denied");
			button.setEnabled(false);
			String permissionDesc = PermissionChecker.getPermissionDescription(permission);
			button.setToolTipText("Bạn không có quyền " + permissionDesc);
		}
	}

	private class CustomScrollBarUI extends BasicScrollBarUI {
		@Override
		protected void configureScrollBarColors() {
			this.thumbColor = CONTENT_COLOR;
			this.trackColor = Color.WHITE;
		}

		@Override
		protected JButton createDecreaseButton(int orientation) {
			return createZeroButton();
		}

		@Override
		protected JButton createIncreaseButton(int orientation) {
			return createZeroButton();
		}

		@Override
		protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setPaint(thumbColor);
			g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10); // Bo tròn
																											// góc
			g2.dispose();
		}

		@Override
		protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setPaint(trackColor);
			g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
			g2.dispose();
		}

		private JButton createZeroButton() {
			JButton button = new JButton();
			button.setPreferredSize(new Dimension(0, 0));
			button.setMinimumSize(new Dimension(0, 0));
			button.setMaximumSize(new Dimension(0, 0));
			return button;
		}
	}

	private JButton createRoundedButton(String text, String iconPath, boolean isRounded) {
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
		button.setBackground(Color.WHITE);
		button.setPreferredSize(new Dimension(text.isEmpty() ? 38 : 130, 38));

		// Add hover effect
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(HOVER_COLOR);
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

	// phương thức xuất file Excel
	public void exportToHTMLWithFont(JTable table, String filePath) {
		try (FileWriter htmlWriter = new FileWriter(filePath)) {
			// Bắt đầu file HTML và thêm CSS cho kiểu chữ
			htmlWriter.write("<html><head><meta charset='UTF-8'>\n");
			htmlWriter.write("<style>\n");
			htmlWriter.write("body { font-family: 'Times New Roman', serif; }\n"); // Thiết lập font Times New Roman
			htmlWriter.write("table { border-collapse: collapse; width: 100%; }\n");
			htmlWriter.write("th, td { border: 0.5px solid black; padding: 16px; text-align: left; }\n");
			htmlWriter.write("th { background-color: #f2f2f2; }\n");
			htmlWriter.write("</style>\n</head><body>\n");

			// Bắt đầu bảng
			htmlWriter.write("<table>\n");

			// Lấy mô hình bảng
			TableModel model = table.getModel();

			// Tạo tiêu đề bảng (header)
			htmlWriter.write("<tr>\n");
			for (int col = 0; col < model.getColumnCount(); col++) {
				htmlWriter.write("<th>" + model.getColumnName(col) + "</th>");
			}
			htmlWriter.write("</tr>\n");

			// Ghi dữ liệu từng hàng
			for (int row = 0; row < model.getRowCount(); row++) {
				htmlWriter.write("<tr>\n");
				for (int col = 0; col < model.getColumnCount(); col++) {
					Object value = model.getValueAt(row, col);
					if (value != null) {
						htmlWriter.write("<td>" + value.toString() + "</td>");
					} else {
						htmlWriter.write("<td></td>");
					}
				}
				htmlWriter.write("</tr>\n");
			}

			// Kết thúc bảng và file HTML
			htmlWriter.write("</table>\n</body></html>");

			JOptionPane.showMessageDialog(null, "Xuất file Excel thành công!", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Lỗi khi xuất file: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

}