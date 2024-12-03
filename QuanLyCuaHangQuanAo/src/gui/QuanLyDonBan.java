package gui;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.toedter.calendar.JDateChooser;

import component.CreateDateChooser;
import component.ShowSuccessDialog;
import dialog.ChiTietPhieuBan;
import dialog.ChiTietPhieuNhap;
import entity.UserSession;
import service.PermissionChecker;
import style.CreateActionButton;
import style.CreateFilter;
import style.CreateRoundedButton;
import style.CustomScrollBarUI;
import style.StyleComboBox;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.logging.Logger;
import java.util.logging.Level;

public class QuanLyDonBan extends JPanel {
	// Constants
	private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
	private static final Color CONTENT_COLOR = new Color(255, 242, 242);
	private static final Color HOVER_COLOR = new Color(252, 231, 243);
	private static final Font HEADER_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 14);
	private static final Font CONTENT_FONT = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12);
	private static final Font TITLE_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 16);
	private static final Logger LOGGER = Logger.getLogger(QuanLyDonBan.class.getName());

	// Components
	private JTable table;
	private DefaultTableModel tableModel;
	private JTextField searchField;
	private JFormattedTextField fromAmountField;
	private JFormattedTextField toAmountField;
	private JDateChooser fromDateChooser;
	private JDateChooser toDateChooser;
	private JComboBox<String> employeeCombo;
	private JLabel totalRecordsValue;
	private JLabel totalAmountValue;
	CreateFilter createFilter;

	private final String currentUserId;
	private final boolean canManageOrders;
	private final boolean canGenerateReports;

	public QuanLyDonBan() {
		createFilter = new CreateFilter();
		currentUserId = UserSession.getInstance().getCurrentUser().getUserID();

		// Check permissions during initialization
		canManageOrders = PermissionChecker.hasPermission(currentUserId, PermissionChecker.PERM_ORDER_MANAGEMENT);
		canGenerateReports = PermissionChecker.hasPermission(currentUserId, PermissionChecker.PERM_REPORT);

		LOGGER.info("Initializing QuanLyDonBan for user: " + currentUserId + " (Order Management: " + canManageOrders
				+ ", Reports: " + canGenerateReports + ")");

		initializeComponents();
		setupLayout();
	}

	private void initializeComponents() {
		// Initialize labels
		totalRecordsValue = new JLabel("0");
		totalRecordsValue.setFont(HEADER_FONT);
		totalRecordsValue.setForeground(PRIMARY_COLOR);
		totalAmountValue = new JLabel("0 VND");
		totalAmountValue.setFont(HEADER_FONT);
		totalAmountValue.setForeground(PRIMARY_COLOR);

		String[] columns = { "STT", "Mã đơn bán", // orderID
				"Nhân viên bán", // staff - references user table
				"Thời gian", // orderDate
				"Tổng tiền", // totalAmount
				"Phương thức" // paymentMethod - adding this from database schema
		};

		tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		// Initialize table
		table = new JTable(tableModel);
		setupTable();

		// Initialize search components
		searchField = new JTextField();
		searchField.setPreferredSize(new Dimension(220, 35));
		searchField.setFont(CONTENT_FONT);

		// Initialize filter components
		initializeFilterComponents();
	}

	private void setupTable() {
		table.setFont(CONTENT_FONT);
		table.setRowHeight(40);
		table.setGridColor(new Color(245, 245, 245));
		table.setSelectionBackground(HOVER_COLOR);
		table.setSelectionForeground(Color.BLACK);
		table.setShowVerticalLines(true);
		table.setShowHorizontalLines(true);

		// Set column widths
		int[] columnWidths = { 50, 150, 200, 150, 200 };
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
		table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // STT
		table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Mã đơn bán
		table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Thời gian

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
		table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer); // Tổng tiền

	}

	private void initializeFilterComponents() {
		employeeCombo = new JComboBox<>(new String[] { "Tất cả", "Nguyễn Văn An", "Trần Thị Bình", "Lê Hoàng Nam" });
		StyleComboBox styleComboBox = new StyleComboBox();
		styleComboBox.styleComboBox(employeeCombo);

		// Initialize date choosers
		CreateDateChooser dateStyle = new CreateDateChooser();
		fromDateChooser = dateStyle.createDateChooser();
		toDateChooser = dateStyle.createDateChooser();

		// Initialize amount fields
		NumberFormat format = NumberFormat.getIntegerInstance();
		format.setGroupingUsed(true);
		fromAmountField = new JFormattedTextField(format);
		toAmountField = new JFormattedTextField(format);
		createFilter.styleFormattedTextField(fromAmountField);
		createFilter.styleFormattedTextField(toAmountField);
	}

	private void setupLayout() {
		setLayout(new BorderLayout(0, 20));
		setBackground(Color.WHITE);
		setBorder(new EmptyBorder(30, 30, 30, 30));

		add(createTopPanel(), BorderLayout.NORTH);
		add(createMainPanel(), BorderLayout.CENTER);
	}

	private JPanel createTopPanel() {
		JPanel panel = new JPanel(new BorderLayout(20, 0));
		panel.setBackground(Color.WHITE);

		// Search panel
		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
		searchPanel.setBackground(Color.WHITE);
		searchPanel.add(searchField);
		searchPanel.add(createFilter.createSearchButton());

		// Action buttons panel
		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
		actionPanel.setBackground(Color.WHITE);
		CreateActionButton createActionButton = new CreateActionButton();

		// Add Order Button with permission check
		if (canManageOrders) {
			JButton addButton = createActionButton.createActionButton("Thêm đơn bán", "/icon/circle-plus.png", true,
					true);
			addButton.addActionListener(e -> navigateToAddOrder());
			addButton.setPreferredSize(new Dimension(160, 38));
			actionPanel.add(addButton);

			// Delete Button
			JButton deleteButton = createActionButton.createActionButton("Xóa", "/icon/trash.png", true, false);
			deleteButton.addActionListener(e -> deleteRecord());
			actionPanel.add(deleteButton);

			// View Details Button
			JButton detailsButton = createActionButton.createActionButton("Chi tiết", "/icon/info.png", true, false);
			detailsButton.addActionListener(e -> showOrderDetails());
			actionPanel.add(detailsButton);
		}

		// Export Button with permission check
		if (canGenerateReports) {
			JButton exportButton = createActionButton.createActionButton("Xuất Excel", "/icon/printer.png", true,
					false);
			exportButton.addActionListener(e -> exportToExcel());
			exportButton.setPreferredSize(new Dimension(160, 38));
			actionPanel.add(exportButton);
		}

		panel.add(searchPanel, BorderLayout.WEST);
		panel.add(actionPanel, BorderLayout.EAST);
		return panel;
	}

	private void navigateToAddOrder() {
		try {
			Container mainContent = this.getParent();
			mainContent.removeAll();
			mainContent.add(new ThemDonBan(), BorderLayout.CENTER);
			mainContent.revalidate();
			mainContent.repaint();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error navigating to add order form", e);
			showErrorMessage("Không thể mở form thêm đơn bán: " + e.getMessage());
		}
	}

	private void showOrderDetails() {
		try {
			ChiTietPhieuBan chiTietPhieuBan = new ChiTietPhieuBan();
			chiTietPhieuBan.viewDetail(table);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error showing order details", e);
			showErrorMessage("Không thể hiển thị chi tiết đơn bán: " + e.getMessage());
		}
	}

	private void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
	}

	
	private JPanel createMainPanel() {

		JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
		mainPanel.setBackground(Color.WHITE);

		// Create scroll pane for table
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.getViewport().setBackground(Color.WHITE);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(245, 245, 245)));

		// Tùy chỉnh thanh cuộn
		JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
		JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
		CustomScrollBarUI customScrollBarUI = new CustomScrollBarUI();
		// Tạo UI tùy chỉnh cho thanh cuộn
		verticalScrollBar.setUI(customScrollBarUI);

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

		JLabel totalRecordsLabel = new JLabel("Tổng số đơn: ");
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

	private JPanel createDateFilterPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(CONTENT_COLOR);
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel titleLabel = new JLabel("Thời gian");
		titleLabel.setFont(CONTENT_FONT);
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(titleLabel);
		panel.add(Box.createVerticalStrut(8)); // Giảm khoảng cách
		// Tạo panel chứa cả 2 date chooser
		JPanel dateChoosersPanel = new JPanel();
		dateChoosersPanel.setLayout(new BoxLayout(dateChoosersPanel, BoxLayout.Y_AXIS));
		dateChoosersPanel.setBackground(CONTENT_COLOR);
		JPanel fromDatePanel = createFilter.createDateChooserPanel("Từ ngày:", fromDateChooser);
		JPanel toDatePanel = createFilter.createDateChooserPanel("Đến ngày:", toDateChooser);
		dateChoosersPanel.add(fromDatePanel);
		dateChoosersPanel.add(Box.createVerticalStrut(5)); // Giảm khoảng cách giữa 2 date chooser
		dateChoosersPanel.add(toDatePanel);
		panel.add(dateChoosersPanel);
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



	// Bổ sung phương thức xử lý xuất Excel
	private void exportToExcel() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Chọn vị trí lưu file Excel");
		fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xlsx"));

		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				String filePath = fileChooser.getSelectedFile().getAbsolutePath();
				if (!filePath.endsWith(".xlsx")) {
					filePath += ".xlsx";
				}

				JOptionPane.showMessageDialog(this, "Xuất Excel thành công: " + filePath, "Thông báo",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Lỗi khi xuất file Excel: " + ex.getMessage(), "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}




	private void deleteRecord() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow != -1) {
			String maDonBan = table.getValueAt(selectedRow, 1).toString();

			// Tạo custom dialog xác nhận
			JDialog confirmDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Xác nhận xóa", true);
			confirmDialog.setLayout(new BorderLayout());
			confirmDialog.setBackground(Color.WHITE);

			// Panel chứa nội dung
			JPanel contentPanel = new JPanel();
			contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
			contentPanel.setBackground(Color.WHITE);
			contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

			// Icon cảnh báo
			JLabel iconLabel = new JLabel(new ImageIcon(getClass().getResource("/icon/circle-alert.png")));
			iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

			// Message
			JLabel messageLabel = new JLabel("Bạn có chắc muốn xóa đơn bán " + maDonBan + "?");
			messageLabel.setFont(new Font(CONTENT_FONT.getFamily(), Font.BOLD, 14));
			messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

			JLabel subMessageLabel = new JLabel("Hành động này không thể hoàn tác!");
			subMessageLabel.setFont(CONTENT_FONT);
			subMessageLabel.setForeground(Color.RED);
			subMessageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

			// Panel chứa buttons
			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
			buttonPanel.setBackground(Color.WHITE);

			JButton deleteButton = createFilter.createDetailButton("Xóa", null, true);
			JButton cancelButton = createFilter.createDetailButton("Hủy", null, false);

			// Thêm components vào panel
			contentPanel.add(iconLabel);
			contentPanel.add(Box.createVerticalStrut(15));
			contentPanel.add(messageLabel);
			contentPanel.add(Box.createVerticalStrut(5));
			contentPanel.add(subMessageLabel);
			contentPanel.add(Box.createVerticalStrut(20));

			buttonPanel.add(deleteButton);
			buttonPanel.add(cancelButton);

			// Xử lý sự kiện buttons
			deleteButton.addActionListener(e -> {
				tableModel.removeRow(selectedRow);
				updateSummary();
				confirmDialog.dispose();

				// Hiển thị thông báo thành công
				ShowSuccessDialog showSuccessDialog = new ShowSuccessDialog();
				showSuccessDialog.showSuccessDialog("Đã xóa đơn bán " + maDonBan + " thành công!");
			});

			cancelButton.addActionListener(e -> confirmDialog.dispose());

			// Thêm panels vào dialog
			confirmDialog.add(contentPanel, BorderLayout.CENTER);
			confirmDialog.add(buttonPanel, BorderLayout.SOUTH);

			// Hiển thị dialog
			confirmDialog.pack();
			confirmDialog.setLocationRelativeTo(this);
			confirmDialog.setVisible(true);
		}
	}
}