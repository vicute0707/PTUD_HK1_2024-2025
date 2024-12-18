package gui;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.toedter.calendar.JDateChooser;
import bus.ImportBUS;
import component.*;
import dialog.ChiTietPhieuNhap;
import entity.*;
import export.ExcelExporterDonNhap;
import service.PermissionChecker;
import style.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.*;
import java.util.*;
import java.util.List;
import java.util.logging.*;

public class QuanLyNhapHang extends JPanel {
	// Constants for styling and configuration
	private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
	private static final Color CONTENT_COLOR = new Color(255, 242, 242);
	private static final Color HOVER_COLOR = new Color(252, 231, 243);
	private static final Font HEADER_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12);
	private static final Font CONTENT_FONT = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12);
	private static final int BUTTON_HEIGHT = 38;
	private static final int SEARCH_FIELD_WIDTH = 220;

	// Core components
	private final JTable table;
	private final DefaultTableModel tableModel;
	private final JTextField searchField;
	private final JLabel totalRecordsValue;
	private final JLabel totalAmountValue;

	// Business logic and utilities
	private final ImportBUS importBUS;
	private final SimpleDateFormat dateFormatter;
	private final NumberFormat currencyFormatter;
	private static final Logger LOGGER = Logger.getLogger(QuanLyNhapHang.class.getName());

	public QuanLyNhapHang() {
		importBUS = new ImportBUS();
		dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

		// Initialize components
		tableModel = createTableModel();
		table = createTable();
		searchField = createSearchField();
		totalRecordsValue = new JLabel("0", SwingConstants.RIGHT);
		totalAmountValue = new JLabel("0 VND", SwingConstants.RIGHT);

		setupPanel();
		loadData();
		setupTableListeners();
	}

	private DefaultTableModel createTableModel() {
		return new DefaultTableModel(
				new String[] { "Mã phiếu", "Nhà cung cấp", "Nhân viên nhập hàng", "Thời gian", "Tổng tiền" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
	}

	private JTable createTable() {
		JTable newTable = new JTable(tableModel);
		SetupTable setupTable = new SetupTable();
		setupTable.setupTable(newTable);

		// Enhanced table styling
		newTable.setSelectionBackground(HOVER_COLOR);
		newTable.setSelectionForeground(PRIMARY_COLOR);
		newTable.setIntercellSpacing(new Dimension(10, 10));

		// Configure header
		JTableHeader header = newTable.getTableHeader();
		header.setFont(HEADER_FONT);
		header.setBackground(Color.WHITE);
		header.setForeground(Color.BLACK);
		header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

		setupTableRenderers(newTable);
		return newTable;
	}

	private void setupTableRenderers(JTable table) {
		// Center renderer for ID and quantity columns
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);

		// Right renderer for amount columns
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

		// Apply renderers
		table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
		table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer); // Amount
	}

	private JTextField createSearchField() {
		JTextField field = new JTextField();
		field.setPreferredSize(new Dimension(SEARCH_FIELD_WIDTH, 35));
		field.setFont(CONTENT_FONT);

		// Add search on Enter key
		field.addActionListener(e -> performSearch());

		// Add placeholder text
		field.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (field.getText().equals("Tìm kiếm...")) {
					field.setText("");
					field.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (field.getText().isEmpty()) {
					field.setText("Tìm kiếm...");
					field.setForeground(Color.GRAY);
				}
			}
		});

		return field;
	}

	private void setupPanel() {
		setLayout(new BorderLayout(0, 20));
		setBackground(Color.WHITE);
		setBorder(new EmptyBorder(30, 30, 30, 30));

		add(createTopPanel(), BorderLayout.NORTH);
		add(createMainPanel(), BorderLayout.CENTER);
	}

	private JPanel createTopPanel() {
		JPanel panel = new JPanel(new BorderLayout(20, 0));
		panel.setBackground(Color.WHITE);

		String currentUserId = UserSession.getInstance().getCurrentUser().getUserID();

		panel.add(createSearchPanel(currentUserId), BorderLayout.WEST);
		panel.add(createActionPanel(currentUserId), BorderLayout.EAST);

		return panel;
	}

	private JPanel createSearchPanel(String userId) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
		panel.setBackground(Color.WHITE);

		// Add search components
		panel.add(searchField);

		// Search button
		JButton searchButton = new CreateFilter().createSearchButton();
		configureButtonWithPermission(searchButton, userId, PermissionChecker.PERM_ORDER_MANAGEMENT,
				this::performSearch);
		panel.add(searchButton);

		// Refresh button
		JButton refreshButton = createStyledButton("Làm mới", 100);
		configureButtonWithPermission(refreshButton, userId, PermissionChecker.PERM_ORDER_MANAGEMENT,
				this::refreshData);
		panel.add(refreshButton);

		return panel;
	}

	private JButton createStyledButton(String text, int width) {
		JButton button = new JButton(text);
		button.setFont(CONTENT_FONT);
		button.setBackground(Color.WHITE);
		button.setForeground(PRIMARY_COLOR);
		button.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
		button.setPreferredSize(new Dimension(width, 35));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));

		button.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				button.setBackground(HOVER_COLOR);
			}

			public void mouseExited(MouseEvent e) {
				button.setBackground(Color.WHITE);
			}
		});

		return button;
	}

	private JPanel createActionPanel(String userId) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
		panel.setBackground(Color.WHITE);

		CreateActionButton actionButtonCreator = new CreateActionButton();

		// Add Import Button
		JButton addButton = actionButtonCreator.createActionButton("Thêm phiếu nhập", "/icon/circle-plus.png", true,
				true);
		configureButtonWithPermission(addButton, userId, PermissionChecker.PERM_ORDER_MANAGEMENT,
				() -> showAddImportPanel());
		panel.add(addButton);

		// Delete Button
		JButton deleteButton = actionButtonCreator.createActionButton("Xóa", "/icon/trash.png", true, false);
		configureButtonWithPermission(deleteButton, userId, PermissionChecker.PERM_ORDER_MANAGEMENT,
				this::handleDelete);
		panel.add(deleteButton);

		// Detail Button
		JButton detailButton = actionButtonCreator.createActionButton("Chi tiết", "/icon/info.png", true, false);
		configureButtonWithPermission(detailButton, userId, PermissionChecker.PERM_ORDER_MANAGEMENT,
				this::showImportDetails);
		panel.add(detailButton);

		// Export Button
		JButton exportButton = actionButtonCreator.createActionButton("Xuất Excel", "/icon/printer.png", true, false);
		configureButtonWithPermission(exportButton, userId, PermissionChecker.PERM_REPORT, this::handleExport);
		panel.add(exportButton);

		return panel;
	}

	private void showAddImportPanel() {
		Container mainContent = this.getParent();
		mainContent.removeAll();
		
		mainContent.add(new ThemDonNhap(), BorderLayout.CENTER);
		mainContent.revalidate();
		mainContent.repaint();
	}

	private void showImportDetails() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			showWarningMessage("Vui lòng chọn phiếu nhập để xem chi tiết");
			return;
		}

		ChiTietPhieuNhap detailDialog = new ChiTietPhieuNhap();
		detailDialog.showImportDetails(table);
	}

	private void handleExport() {
		try {
			// Create and configure file chooser
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Xuất danh sách phiếu nhập");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setSelectedFile(new File("PhieuNhap.xls")); // Changed to .xls for HSSFWorkbook

			// Show save dialog and process result
			if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				String filePath = file.getAbsolutePath();

				// Ensure correct file extension
				if (!filePath.toLowerCase().endsWith(".xls")) {
					filePath += ".xls";
				}

				// Check if file exists and confirm overwrite
				File exportFile = new File(filePath);
				if (exportFile.exists()) {
					int response = JOptionPane.showConfirmDialog(this, "File đã tồn tại. Bạn có muốn ghi đè không?",
							"Xác nhận ghi đè", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

					if (response != JOptionPane.YES_OPTION) {
						return;
					}
				}

				// Create exporter and export data
				ExcelExporterDonNhap exporter = new ExcelExporterDonNhap();
				exporter.exportToExcel(table, createDetailTable(), filePath);

				// Show success message and log
				JOptionPane.showMessageDialog(this, "Xuất file thành công: " + filePath, "Thông báo",
						JOptionPane.INFORMATION_MESSAGE);

				LOGGER.info("Successfully exported import data to: " + filePath);

				// Open the containing folder if on Windows
				if (System.getProperty("os.name").toLowerCase().contains("windows")) {
					Runtime.getRuntime().exec("explorer.exe /select," + filePath);
				}
			}
		} catch (Exception e) {
			LOGGER.severe("Error exporting data: " + e.getMessage());
			JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private JTable createDetailTable() {
		String[] columns = { "Mã phiếu nhập", "Mã sản phẩm", "Tên sản phẩm", "Phân loại", "Đơn giá", "Số lượng",
				"Thành tiền" };

		DefaultTableModel detailModel = new DefaultTableModel(columns, 0);

		try {
			// Get all imports and their details
			List<Import> imports = importBUS.getAllImports();
			for (Import importObj : imports) {
				List<ImportDetail> details = importBUS.getImportDetailsByImportId(importObj.getImportID());

				// Add each detail to the model
				for (ImportDetail detail : details) {
					Object[] row = { importObj.getImportID(), detail.getVariantID(), detail.getProductName(),
							detail.getVariantName(), String.format("%,d VND", Math.round(detail.getPrice())),
							detail.getQuantity(),
							String.format("%,d VND", Math.round(detail.getPrice() * detail.getQuantity())) };
					detailModel.addRow(row);
				}
			}
		} catch (Exception e) {
			LOGGER.severe("Error creating detail table: " + e.getMessage());
		}

		return new JTable(detailModel);
	}

	private void handleDelete() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			showWarningMessage("Vui lòng chọn phiếu nhập để xóa");
			return;
		}

		String importId = table.getValueAt(selectedRow, 0).toString();

		if (showConfirmDialog("Bạn có chắc chắn muốn xóa phiếu nhập " + importId + "?")) {
			try {
				if (importBUS.deleteImport(importId)) {
					tableModel.removeRow(selectedRow);
					loadData(); // Refresh summary data
					showInfoMessage("Đã xóa phiếu nhập thành công");
				} else {
					showErrorMessage("Không thể xóa phiếu nhập");
				}
			} catch (Exception e) {
				LOGGER.severe("Error deleting import: " + e.getMessage());
				showErrorMessage("Lỗi khi xóa phiếu nhập: " + e.getMessage());
			}
		}
	}

	private void performSearch() {
		String keyword = searchField.getText().trim();

		if (keyword.isEmpty() || keyword.equals("Tìm kiếm...")) {
			showWarningMessage("Vui lòng nhập từ khóa tìm kiếm!");
			return;
		}

		try {
			List<Import> imports = importBUS.getAllImports();
			updateTableWithFilteredData(imports, keyword.toLowerCase());
		} catch (Exception e) {
			LOGGER.severe("Error during search: " + e.getMessage());
			showErrorMessage("Lỗi khi tìm kiếm: " + e.getMessage());
		}
	}

	private void updateTableWithFilteredData(List<Import> imports, String keyword) {
		tableModel.setRowCount(0);
		double totalAmount = 0;
		int matchCount = 0;

		for (Import importObj : imports) {
			if (matchesKeyword(importObj, keyword)) {
				addImportToTable(importObj);
				totalAmount += importObj.getTotalAmount();
				matchCount++;
			}
		}

		updateSummaryInfo(matchCount, totalAmount);

		if (matchCount == 0) {
			showInfoMessage("Không tìm thấy kết quả nào phù hợp với từ khóa: " + keyword);
		}
	}

	private boolean matchesKeyword(Import importObj, String keyword) {
		return importObj.getImportID().toLowerCase().contains(keyword)
				|| importObj.getSupplier().toLowerCase().contains(keyword)
				|| importObj.getStaff().toLowerCase().contains(keyword)
				|| String.valueOf(importObj.getTotalAmount()).contains(keyword);
	}

	private void refreshData() {
		searchField.setText("Tìm kiếm...");
		searchField.setForeground(Color.GRAY);
		loadData();
	}

	private void loadData() {
		try {
			List<Import> imports = importBUS.getAllImports();
			updateTableWithAllData(imports);
		} catch (Exception e) {
			LOGGER.severe("Error loading data: " + e.getMessage());
			showErrorMessage("Lỗi khi tải dữ liệu: " + e.getMessage());
		}
	}

	private void updateTableWithAllData(List<Import> imports) {
		tableModel.setRowCount(0);
		double totalAmount = 0;

		for (Import importObj : imports) {
			addImportToTable(importObj);
			totalAmount += importObj.getTotalAmount();
		}

		updateSummaryInfo(imports.size(), totalAmount);
	}

	private void addImportToTable(Import importObj) {
		Object[] row = { importObj.getImportID(), importObj.getSupplier(), importObj.getStaff(),
				dateFormatter.format(importObj.getImportDate()), currencyFormatter.format(importObj.getTotalAmount()) };
		tableModel.addRow(row);
	}

	private void updateSummaryInfo(int recordCount, double totalAmount) {
		totalRecordsValue.setText(String.valueOf(recordCount));
		totalAmountValue.setText(currencyFormatter.format(totalAmount));
		LOGGER.info(String.format("Updated summary: %d records, total amount: %s", recordCount,
				currencyFormatter.format(totalAmount)));
	}

	private JPanel createMainPanel() {
		JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
		mainPanel.setBackground(Color.WHITE);

		// Create an enhanced scroll pane for the table
		JScrollPane scrollPane = createEnhancedScrollPane();

		// Add the main components to the panel
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		mainPanel.add(createSummaryPanel(), BorderLayout.SOUTH);

		return mainPanel;
	}

	private JScrollPane createEnhancedScrollPane() {
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.getViewport().setBackground(Color.WHITE);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(245, 245, 245)));

		// Customize the scroll bars for better visual appeal
		CustomScrollBarUI scrollBarUI = new CustomScrollBarUI();
		scrollPane.getVerticalScrollBar().setUI(scrollBarUI);
		scrollPane.getHorizontalScrollBar().setUI(scrollBarUI);

		// Add custom corner for a polished look
		scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, createCornerComponent());

		return scrollPane;
	}

	private JPanel createCornerComponent() {
		JPanel corner = new JPanel();
		corner.setBackground(Color.WHITE);
		return corner;
	}

	private JPanel createSummaryPanel() {
		// Create a panel for displaying summary information
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
				BorderFactory.createEmptyBorder(15, 20, 15, 20)));

		// Create and style the summary labels
		JLabel recordsLabel = createSummaryLabel("Tổng số phiếu:");
		JLabel amountLabel = createSummaryLabel("Tổng tiền:");

		// Style the value labels
		styleSummaryValueLabel(totalRecordsValue);
		styleSummaryValueLabel(totalAmountValue);

		// Add components with proper spacing
		panel.add(Box.createHorizontalGlue());
		panel.add(recordsLabel);
		panel.add(Box.createRigidArea(new Dimension(5, 0)));
		panel.add(totalRecordsValue);
		panel.add(Box.createRigidArea(new Dimension(30, 0)));
		panel.add(amountLabel);
		panel.add(Box.createRigidArea(new Dimension(5, 0)));
		panel.add(totalAmountValue);

		return panel;
	}

	private JLabel createSummaryLabel(String text) {
		JLabel label = new JLabel(text);
		label.setFont(CONTENT_FONT);
		label.setForeground(Color.DARK_GRAY);
		return label;
	}

	private void styleSummaryValueLabel(JLabel label) {
		label.setFont(HEADER_FONT);
		label.setForeground(PRIMARY_COLOR);
		label.setPreferredSize(new Dimension(120, label.getPreferredSize().height));
	}

	private void setupTableListeners() {
		// Add double-click listener for viewing details
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					showImportDetails();
				}
			}
		});

		// Add row selection listener for enabling/disabling buttons
		table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				updateButtonStates();
			}
		});
	}

	private void updateButtonStates() {
		boolean rowSelected = table.getSelectedRow() != -1;
		// Update button states based on selection if needed
	}

	// Utility methods for showing dialogs
	private void showWarningMessage(String message) {
		JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.WARNING_MESSAGE);
	}

	private void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
	}

	private void showInfoMessage(String message) {
		JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
	}

	private boolean showConfirmDialog(String message) {
		return JOptionPane.showConfirmDialog(this, message, "Xác nhận",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}

	private void configureButtonWithPermission(JButton button, String userId, String permission, Runnable action) {
		if (PermissionChecker.hasPermission(userId, permission)) {
			button.setEnabled(true);
			button.addActionListener(e -> {
				if (button.isEnabled()) {
					action.run();
				}
			});
		} else {
			button.setEnabled(false);
			String permissionDesc = PermissionChecker.getPermissionDescription(permission);
			button.setToolTipText("Bạn không có quyền " + permissionDesc);
			LOGGER.warning("User " + userId + " attempted to access feature requiring " + permissionDesc);
		}
	}
}