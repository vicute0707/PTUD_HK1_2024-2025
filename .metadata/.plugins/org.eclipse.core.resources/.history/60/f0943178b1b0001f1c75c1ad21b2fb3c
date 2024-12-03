package gui;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.toedter.calendar.JDateChooser;

import component.CreateDateChooser;
import component.SetupTable;
import dialog.ChiTietPhieuNhap;
import entity.UserSession;
import service.PermissionChecker;
import style.CreateActionButton;
import style.CreateFilter;
import style.CustomScrollBarUI;
import style.StyleComboBox;
import style.StyleFormattedTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class QuanLyNhapHang extends JPanel {
	// Constants
	private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
	private static final Color CONTENT_COLOR = new Color(255, 242, 242);
	private static final Color HOVER_COLOR = new Color(252, 231, 243);
	private static final Font HEADER_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 14);
	private static final Font CONTENT_FONT = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12);
	private static final Font TITLE_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 16);
	// Components
	private JTable table;
	private DefaultTableModel tableModel;
	private JTextField searchField;
	private JFormattedTextField fromAmountField;
	private JFormattedTextField toAmountField;
	private JDateChooser fromDateChooser;
	private JDateChooser toDateChooser;
	private JComboBox<String> supplierCombo;
	private JComboBox<String> employeeCombo;
	private JLabel totalRecordsValue;
	private JLabel totalAmountValue;
	private static final Logger LOGGER = Logger.getLogger(QuanLyNhapHang.class.getName());

	public QuanLyNhapHang() {
		initializeComponents();
		setupLayout();
		loadSampleData();
	}

	private void initializeComponents() {
		// Initialize labels first
		totalRecordsValue = new JLabel("0");
		totalRecordsValue.setFont(HEADER_FONT);
		totalRecordsValue.setForeground(PRIMARY_COLOR);
		totalAmountValue = new JLabel("0 VND");
		totalAmountValue.setFont(HEADER_FONT);
		totalAmountValue.setForeground(PRIMARY_COLOR);
		// Initialize table model
		String[] columns = { "Mã phiếu", "Nhà cung cấp", "Nhân viên nhập hàng", "Thời gian", "Tổng tiền" };
		tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		// Initialize table
		table = new JTable(tableModel);
		SetupTable setupTable = new SetupTable();
		setupTable.setupTable(table);

		// Initialize search components
		searchField = new JTextField();
		searchField.setPreferredSize(new Dimension(220, 35));
		searchField.setFont(CONTENT_FONT);

		// Initialize filter components
		initializeFilterComponents();
	}

	private void initializeFilterComponents() {
		// Initialize combo boxes
		supplierCombo = new JComboBox<>(
				new String[] { "Tất cả", "Xưởng may Đại Nam", "Xưởng may Hoàng Gia", "Công ty TNHH May Việt Tiến" });
		StyleComboBox styleComboBox = new StyleComboBox();
		styleComboBox.styleComboBox(supplierCombo);

		employeeCombo = new JComboBox<>(new String[] { "Tất cả", "Nguyễn Thị Tường Vi", "Trần Văn Nam", "Lê Thị Hoa" });
		styleComboBox.styleComboBox(employeeCombo);
		CreateDateChooser createDateChooser = new CreateDateChooser();
		// Initialize date choosers
		fromDateChooser = createDateChooser.createDateChooser();
		toDateChooser = createDateChooser.createDateChooser();

		// Initialize amount fields
		NumberFormat format = NumberFormat.getIntegerInstance();
		format.setGroupingUsed(true);
		fromAmountField = new JFormattedTextField(format);
		toAmountField = new JFormattedTextField(format);
		StyleFormattedTextField styleFomatText = new StyleFormattedTextField();
		styleFomatText.styleFormattedTextField(fromAmountField);
		styleFomatText.styleFormattedTextField(toAmountField);
	}

	private void setupLayout() {
		setLayout(new BorderLayout(0, 20));
		setBackground(Color.WHITE);
		setBorder(new EmptyBorder(30, 30, 30, 30));

		// Add main panels
		add(createTopPanel(), BorderLayout.NORTH);
		add(createFilterPanel(), BorderLayout.WEST);
		add(createMainPanel(), BorderLayout.CENTER);
	}

	private JPanel createTopPanel() {
		JPanel panel = new JPanel(new BorderLayout(20, 0));
		panel.setBackground(Color.WHITE);

		// Get current user for permission checking
		String currentUserId = UserSession.getInstance().getCurrentUser().getUserID();

		// Search panel
		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
		searchPanel.setBackground(Color.WHITE);
		searchPanel.add(searchField);
		CreateFilter c = new CreateFilter();

		JButton searchButton = c.createSearchButton();
		configureButtonWithPermission(searchButton, currentUserId, PermissionChecker.PERM_ORDER_MANAGEMENT, () -> {
			String keyword = searchField.getText().trim();
			if (keyword.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm!", "Thông báo",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
		});
		searchPanel.add(searchButton);

		// Action buttons panel
		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
		actionPanel.setBackground(Color.WHITE);
		CreateActionButton createActionButton = new CreateActionButton();

		// Add Import Button
		JButton addButton = createActionButton.createActionButton("Thêm phiếu nhập", "/icon/circle-plus.png", true,
				true);
		configureButtonWithPermission(addButton, currentUserId, PermissionChecker.PERM_ORDER_MANAGEMENT, () -> {
			Container mainContent = QuanLyNhapHang.this.getParent();
			mainContent.removeAll();
			ThemDonNhap addPanel = new ThemDonNhap();
			mainContent.add(addPanel, BorderLayout.CENTER);
			mainContent.revalidate();
			mainContent.repaint();
		});
		addButton.setPreferredSize(new Dimension(160, 38));

		// Delete Button
		JButton deleteButton = createActionButton.createActionButton("Xóa", "/icon/trash.png", true, false);
		configureButtonWithPermission(deleteButton, currentUserId, PermissionChecker.PERM_ORDER_MANAGEMENT, () -> {
			handleDelete();
		});

		// Info Button
		JButton aboutButton = createActionButton.createActionButton("About", "/icon/info.png", true, false);
		configureButtonWithPermission(aboutButton, currentUserId, PermissionChecker.PERM_ORDER_MANAGEMENT, () -> {
			ChiTietPhieuNhap chiTietPhieuNhap = new ChiTietPhieuNhap();
			chiTietPhieuNhap.chiTietPhieuNhap(table, tableModel);
		});

		// Export Button
		JButton exportButton = createActionButton.createActionButton("Xuất Excel", "/icon/printer.png", true, false);
		configureButtonWithPermission(exportButton, currentUserId, PermissionChecker.PERM_REPORT, () -> {
			handleExport();
		});
		exportButton.setPreferredSize(new Dimension(160, 38));

		actionPanel.add(addButton);
		actionPanel.add(deleteButton);
		actionPanel.add(aboutButton);
		actionPanel.add(exportButton);

		panel.add(searchPanel, BorderLayout.WEST);
		panel.add(actionPanel, BorderLayout.EAST);

		return panel;
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

	private void handleDelete() {
		int selectedRows[] = table.getSelectedRows();
		if (selectedRows.length == 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu nhập cần xóa!", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		int option = JOptionPane.showConfirmDialog(this,
				"Bạn có chắc muốn xóa " + selectedRows.length + " phiếu nhập đã chọn?", "Xác nhận xóa",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (option == JOptionPane.YES_OPTION) {
			try {
				// Xóa từ dưới lên trên để tránh lỗi index
				for (int i = selectedRows.length - 1; i >= 0; i--) {
					String maPhieu = tableModel.getValueAt(selectedRows[i], 0).toString();
					LOGGER.info("Deleting import order: " + maPhieu);
					tableModel.removeRow(selectedRows[i]);
				}
				updateSummary();
				JOptionPane.showMessageDialog(this, "Đã xóa thành công " + selectedRows.length + " phiếu nhập!",
						"Thông báo", JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e) {
				LOGGER.severe("Error deleting import orders: " + e.getMessage());
				JOptionPane.showMessageDialog(this, "Lỗi khi xóa phiếu nhập: " + e.getMessage(), "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void handleExport() {
		try {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Xuất danh sách phiếu nhập");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setSelectedFile(new File("PhieuNhap.xlsx"));

			if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				String filePath = fileChooser.getSelectedFile().getAbsolutePath();
				if (!filePath.endsWith(".xlsx")) {
					filePath += ".xlsx";
				}
				// Implement actual export logic here
				LOGGER.info("Exporting import orders to: " + filePath);
				JOptionPane.showMessageDialog(this, "Xuất file thành công: " + filePath, "Thông báo",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (Exception e) {
			LOGGER.severe("Error exporting file: " + e.getMessage());
			JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// Add logging

	private JPanel createFilterPanel() {
		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
		filterPanel.setBackground(CONTENT_COLOR);
		filterPanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0, 0, 0, 0), BorderFactory
				.createCompoundBorder(new LineBorder(PRIMARY_COLOR, 0, true), new EmptyBorder(20, 20, 20, 20))));
		filterPanel.setPreferredSize(new Dimension(350, 0));
		CreateFilter createFiltera = new CreateFilter();
		// Tiêu đề panel
		filterPanel.add(createFiltera.createFilterTitle());

		filterPanel.add(Box.createVerticalStrut(20));

		// Nhà cung cấp
		CreateFilter createFilter = new CreateFilter();
		filterPanel.add(createFilter.createFilterField("Nhà cung cấp", supplierCombo));
		filterPanel.add(Box.createVerticalStrut(15));

		// Nhân viên
		filterPanel.add(createFilter.createFilterField("Nhân viên tiếp nhận", employeeCombo));
		filterPanel.add(Box.createVerticalStrut(15));

		// Thời gian
		filterPanel.add(createFilter.createDateFilterPanel(fromDateChooser, toDateChooser));
		filterPanel.add(Box.createVerticalStrut(15));

		// Khoảng tiền
		filterPanel.add(createAmountFilterPanel());
		filterPanel.add(Box.createVerticalStrut(25));
		filterPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		// Buttons

		filterPanel.add(createFilter.createFilterButtonsPanel());

		return filterPanel;
	}

	private JPanel createMainPanel() {
		JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
		mainPanel.setBackground(Color.WHITE);

		// Create scroll pane for table
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.getViewport().setBackground(Color.WHITE);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(245, 245, 245)));

		// Tùy chỉnh thanh cuộn dọc
		JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
		CustomScrollBarUI customScrollBarUI = new CustomScrollBarUI();
		verticalScrollBar.setUI(customScrollBarUI);

		// Tùy chỉnh thanh cuộn ngang
		JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
		horizontalScrollBar.setUI(customScrollBarUI);

		// Add components
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		mainPanel.add(createSummaryPanel(), BorderLayout.SOUTH);

		return mainPanel;
	}

	private JPanel createSummaryPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

		JLabel totalRecordsLabel = new JLabel("Tổng số phiếu: ");
		totalRecordsLabel.setFont(CONTENT_FONT);

		JLabel totalAmountLabel = new JLabel("Tổng tiền: ");
		totalAmountLabel.setFont(CONTENT_FONT);

		panel.add(totalRecordsLabel);
		panel.add(totalRecordsValue);
		panel.add(Box.createHorizontalStrut(20));
		panel.add(totalAmountLabel);
		panel.add(totalAmountValue);

		return panel;
	}

	private JPanel createAmountFilterPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(CONTENT_COLOR);
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel titleLabel = new JLabel("Khoảng tiền (VND)");
		titleLabel.setFont(CONTENT_FONT);
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(titleLabel);
		panel.add(Box.createVerticalStrut(12));

		JPanel fieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
		fieldsPanel.setBackground(CONTENT_COLOR);

		fromAmountField.setPreferredSize(new Dimension(120, 35));
		toAmountField.setPreferredSize(new Dimension(120, 35));

		fieldsPanel.add(fromAmountField);
		fieldsPanel.add(new JLabel("—"));
		fieldsPanel.add(toAmountField);

		panel.add(fieldsPanel);
		return panel;
	}

	private void loadSampleData() {
		Object[][] data = { { "P01", "Xưởng may Đại Nam", "Nguyễn Thị Tường Vi", "01/11/2023", "15,000,000 VND" },
				{ "P02", "Xưởng may Hoàng Gia", "Trần Văn Nam", "02/11/2023", "12,500,000 VND" },
				{ "P03", "Công ty TNHH May Việt Tiến", "Lê Thị Hoa", "03/11/2023", "18,750,000 VND" },
				{ "P04", "Xưởng may Đại Nam", "Nguyễn Thị Tường Vi", "05/11/2023", "22,000,000 VND" },
				{ "P05", "Xưởng may Hoàng Gia", "Trần Văn Nam", "07/11/2023", "9,800,000 VND" },
				{ "P05", "Xưởng may Hoàng Gia", "Trần Văn Nam", "07/11/2023", "9,800,000 VND" },
				{ "P05", "Xưởng may Hoàng Gia", "Trần Văn Nam", "07/11/2023", "9,800,000 VND" },
				{ "P05", "Xưởng may Hoàng Gia", "Trần Văn Nam", "07/11/2023", "9,800,000 VND" },
				{ "P05", "Xưởng may Hoàng Gia", "Trần Văn Nam", "07/11/2023", "9,800,000 VND" },
				{ "P05", "Xưởng may Hoàng Gia", "Trần Văn Nam", "07/11/2023", "9,800,000 VND" },
				{ "P05", "Xưởng may Hoàng Gia", "Trần Văn Nam", "07/11/2023", "9,800,000 VND" },
				{ "P05", "Xưởng may Hoàng Gia", "Trần Văn Nam", "07/11/2023", "9,800,000 VND" },
				{ "P05", "Xưởng may Hoàng Gia", "Trần Văn Nam", "07/11/2023", "9,800,000 VND" },
				{ "P05", "Xưởng may Hoàng Gia", "Trần Văn Nam", "07/11/2023", "9,800,000 VND" },
				{ "P06", "Công ty TNHH May Việt Tiến", "Lê Thị Hoa", "10/11/2023", "16,300,000 VND" } };

		for (Object[] row : data) {
			tableModel.addRow(row);
		}
		updateSummary();
	}

	private void updateSummary() {
		double totalAmount = 0;
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			String amountStr = tableModel.getValueAt(i, 4).toString().replace(" VND", "").replace(",", "");
			totalAmount += Double.parseDouble(amountStr);
		}

		NumberFormat currencyFormat = NumberFormat.getNumberInstance();
		String formattedAmount = currencyFormat.format(totalAmount) + " VND";

		totalRecordsValue.setText(String.valueOf(tableModel.getRowCount()));
		totalAmountValue.setText(formattedAmount);
	}

}