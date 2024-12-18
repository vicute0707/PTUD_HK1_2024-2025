package gui;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import bus.ImportBUS;
import dao.NhaCungCapDAO;
import dao.ProductVariantDAO;
import dao.SanPhamDAO;
import dao.UserDAO;
import entity.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.*;

import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

public class ThemDonNhap extends JPanel {
	// Constants for styling
	private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
	private static final Color CONTENT_COLOR = new Color(255, 242, 242);
	private static final Color HOVER_COLOR = new Color(252, 231, 243);
	private static final Font HEADER_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12);
	private static final Font CONTENT_FONT = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12);
	private static final Logger LOGGER = Logger.getLogger(ThemDonNhap.class.getName());

	// Main UI Components
	private JTable danhSachSPTable;
	private DefaultTableModel danhSachSPModel;
	private JTable donNhapTable;
	private DefaultTableModel donNhapModel;
	private JLabel tongTienLabel;

	// Input Fields
	private JTextField maDonNhapField;
	private JTextField maSPField;
	private JTextField tenSPField;
	private JTextField giaNhapField;
	private JTextField thuongHieuField;
	private JTextField soLuongField;
	private JComboBox<String> phanLoaiCombo;

	// Selection Combos
	private JComboBox<String> nhanVienCombo;
	private JComboBox<String> nhaCungCapCombo;

	// Action Buttons
	private JButton themSPButton;
	private JButton suaButton;
	private JButton xoaButton;
	private JButton nhapHangButton;
	private JButton danhSachButton;

	// Business Logic
	private ImportBUS importBUS;
	private SanPhamDAO sanPhamDAO;

	private class VariantInfo {
		String color;
		String size;
		int quantity;

		VariantInfo(String variantDisplay) {
			try {
				// Format: "Màu: Đen - Size: M (Còn: 10)"
				String[] parts = variantDisplay.split(" - ");

				// Extract color: "Màu: Đen" -> "Đen"
				this.color = parts[0].substring(parts[0].indexOf(":") + 2).trim();

				// Extract size: "Size: M (Còn: 10)" -> "M"
				String sizePart = parts[1];
				this.size = sizePart.substring(sizePart.indexOf(":") + 2, sizePart.indexOf("(")).trim();

				// Extract quantity: "Size: M (Còn: 10)" -> 10
				String qtyStr = sizePart.substring(sizePart.indexOf("Còn:") + 5, sizePart.indexOf(")")).trim();
				this.quantity = Integer.parseInt(qtyStr);

				LOGGER.info(String.format("Parsed variant info - Color: %s, Size: %s, Qty: %d", color, size, quantity));

			} catch (Exception e) {
				LOGGER.warning("Error parsing variant info: " + variantDisplay + " - " + e.getMessage());
				this.color = "";
				this.size = "";
				this.quantity = 0;
			}
		}
	}

	public ThemDonNhap() {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		setBorder(new EmptyBorder(20, 20, 20, 20));

		// Initialize business logic classes
		sanPhamDAO = new SanPhamDAO();
		importBUS = new ImportBUS();

		initializeComponents();

		// Generate import ID
		maDonNhapField.setText(importBUS.generateNewImportID());
		maDonNhapField.setEditable(false);

		// Add numeric formatting for price and quantity fields
		addNumericFieldFormatting(giaNhapField);
		addNumericFieldFormatting(soLuongField);

		// Load initial data
		loadStaffData();
		loadSupplierData();

		setupLayout();
		addEventListeners();
		loadProductData();
	}

	private void loadStaffData() {
		try {
			// Clear current items to start fresh
			nhanVienCombo.removeAllItems();

			// Get the current user from session for consistency
			User currentLoggedInUser = entity.UserSession.getInstance().getCurrentUser();

			// Validate that we have a logged-in user
			if (currentLoggedInUser == null) {
				throw new IllegalStateException("Không tìm thấy thông tin người dùng đăng nhập");
			}

			// Format the display text for the current user
			String displayText = String.format("%s - %s", currentLoggedInUser.getUserID(),
					currentLoggedInUser.getFullName());

			// Add the current user to the combo box
			nhanVienCombo.addItem(displayText);

			// Disable the combo box since we only want to show the current user
			nhanVienCombo.setEnabled(false);

			// Add visual feedback that the field is disabled
			nhanVienCombo.setBackground(new Color(245, 245, 245));

			// Add tooltip to explain why it's disabled
			nhanVienCombo
					.setToolTipText("Đơn nhập được tạo bởi người dùng hiện tại: " + currentLoggedInUser.getFullName());

			// Log success for debugging
			LOGGER.info("Đã thiết lập người dùng hiện tại: " + displayText);

		} catch (Exception e) {
			// Log the error for debugging
			LOGGER.severe("Lỗi khi thiết lập người dùng: " + e.getMessage());

			// Show user-friendly error message
			showMessage("Lỗi khi tải thông tin người dùng: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}

	private void loadSupplierData() {
		try {
			// Clear current items
			nhaCungCapCombo.removeAllItems();
			nhaCungCapCombo.addItem("Chọn nhà cung cấp");

			// Get suppliers from database
			NhaCungCapDAO nccDAO = new NhaCungCapDAO();
			List<NhaCC> danhSachNCC = nccDAO.getAllNhaCC();

			// Add suppliers to combo box
			for (NhaCC ncc : danhSachNCC) {
				nhaCungCapCombo.addItem(ncc.getTenNCC());
			}

		} catch (Exception e) {
			LOGGER.severe("Error loading supplier data: " + e.getMessage());
			showMessage("Lỗi khi tải danh sách nhà cung cấp", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void setupLayout() {
		// Tạo layout tổng thể là BorderLayout với khoảng cách 20px
		setLayout(new BorderLayout(0, 20));

		// Panel phía trên chứa thông tin đơn nhập
		add(createTopPanel(), BorderLayout.NORTH);

		// Panel ở giữa với layout chia đôi
		JPanel centerPanel = new JPanel(new BorderLayout(20, 0));
		centerPanel.setBackground(Color.WHITE);

		// Panel bên trái chứa danh sách sản phẩm
		JPanel leftPanel = createLeftPanel();
		leftPanel.setPreferredSize(new Dimension(400, 0));

		// Panel bên phải chứa thông tin chi tiết và bảng đơn nhập
		JPanel rightPanel = createRightPanel();

		// Thêm hai panel vào panel giữa
		centerPanel.add(leftPanel, BorderLayout.WEST);
		centerPanel.add(rightPanel, BorderLayout.CENTER);

		// Thêm panel giữa vào layout chính
		add(centerPanel, BorderLayout.CENTER);

		// Panel dưới cùng chứa tổng tiền và các nút
		add(createBottomPanel(), BorderLayout.SOUTH);
	}

	private JPanel createTopPanel() {
		// Create main panel with GridBagLayout for flexible positioning
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Add some bottom padding

		// Create GridBagConstraints for layout control
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL; // Components stretch horizontally
		gbc.insets = new Insets(5, 5, 5, 15); // Spacing between components
		gbc.weightx = 1.0; // Allow horizontal stretching

		// Left section - Import ID
		JPanel importIdPanel = new JPanel(new BorderLayout(10, 0));
		importIdPanel.setBackground(Color.WHITE);

		JLabel importIdLabel = new JLabel("Mã đơn nhập:");
		importIdLabel.setFont(CONTENT_FONT);
		importIdPanel.add(importIdLabel, BorderLayout.WEST);
		importIdPanel.add(maDonNhapField, BorderLayout.CENTER);

		// Center section - Staff selection
		JPanel staffPanel = new JPanel(new BorderLayout(10, 0));
		staffPanel.setBackground(Color.WHITE);

		JLabel staffLabel = new JLabel("Nhân viên phụ trách:");
		staffLabel.setFont(CONTENT_FONT);
		staffPanel.add(staffLabel, BorderLayout.WEST);
		staffPanel.add(nhanVienCombo, BorderLayout.CENTER);

		// Right section - Supplier selection
		JPanel supplierPanel = new JPanel(new BorderLayout(10, 0));
		supplierPanel.setBackground(Color.WHITE);

		JLabel supplierLabel = new JLabel("Nhà cung cấp:");
		supplierLabel.setFont(CONTENT_FONT);
		supplierPanel.add(supplierLabel, BorderLayout.WEST);
		supplierPanel.add(nhaCungCapCombo, BorderLayout.CENTER);

		// Add components to main panel with GridBagLayout
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.3;
		panel.add(importIdPanel, gbc);

		gbc.gridx = 1;
		gbc.weightx = 0.35;
		panel.add(staffPanel, gbc);

		gbc.gridx = 2;
		gbc.weightx = 0.35;
		panel.add(supplierPanel, gbc);

		return panel;
	}

	private JPanel createBottomPanel() {
		// Create main panel with BorderLayout to separate total and buttons
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)), // Top border
				BorderFactory.createEmptyBorder(15, 0, 0, 0) // Top padding
		));

		// Left side - Total amount display
		JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		totalPanel.setBackground(Color.WHITE);

		// Style the total amount label
		tongTienLabel.setFont(HEADER_FONT);
		tongTienLabel.setForeground(PRIMARY_COLOR);
		totalPanel.add(tongTienLabel);

		// Right side - Action buttons
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		buttonPanel.setBackground(Color.WHITE);

		// Style and configure the list button
		danhSachButton.setPreferredSize(new Dimension(200, 35));
		buttonPanel.add(danhSachButton);

		// Style and configure the import button
		nhapHangButton.setPreferredSize(new Dimension(150, 35));
		buttonPanel.add(nhapHangButton);

		// Add panels to main panel
		panel.add(totalPanel, BorderLayout.WEST);
		panel.add(buttonPanel, BorderLayout.EAST);

		return panel;
	}

	// Panel bên trái chứa danh sách sản phẩm
	private JPanel createLeftPanel() {
		JPanel panel = new JPanel(new BorderLayout(0, 10));
		panel.setBackground(CONTENT_COLOR);
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Tiêu đề panel
		JLabel titleLabel = new JLabel("Danh sách sản phẩm");
		titleLabel.setFont(HEADER_FONT);
		panel.add(titleLabel, BorderLayout.NORTH);

		// Bảng sản phẩm với thanh cuộn
		JScrollPane scrollPane = new JScrollPane(danhSachSPTable);
		customizeScrollPane(scrollPane);
		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
	}

	// Panel bên phải chứa form nhập và bảng đơn hàng
	private JPanel createRightPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(Color.WHITE);

		// Panel nhập thông tin sản phẩm
		JPanel inputPanel = new JPanel(new GridBagLayout());
		inputPanel.setBackground(Color.WHITE);
		inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);

		// Thêm các trường nhập liệu
		addInputField(inputPanel, "Mã SP:", maSPField, gbc, 0, 0);
		addInputField(inputPanel, "Tên SP:", tenSPField, gbc, 0, 2);
		addInputField(inputPanel, "Thương hiệu:", thuongHieuField, gbc, 1, 0);
		addInputField(inputPanel, "Phân loại:", phanLoaiCombo, gbc, 1, 2);
		addInputField(inputPanel, "Giá nhập:", giaNhapField, gbc, 2, 0);
		addInputField(inputPanel, "Số lượng:", soLuongField, gbc, 2, 2);

		panel.add(inputPanel);

		// Panel chứa bảng đơn hàng
		JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
		tablePanel.setBackground(Color.WHITE);

		// Panel chứa các nút thao tác
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.add(themSPButton);
		buttonPanel.add(suaButton);
		buttonPanel.add(xoaButton);

		tablePanel.add(buttonPanel, BorderLayout.NORTH);

		// Bảng đơn hàng với thanh cuộn
		JScrollPane scrollPane = new JScrollPane(donNhapTable);
		customizeScrollPane(scrollPane);
		tablePanel.add(scrollPane, BorderLayout.CENTER);

		panel.add(tablePanel);

		return panel;
	}

	// Phương thức tiện ích để thêm trường nhập liệu vào panel
	private void addInputField(JPanel panel, String label, JComponent field, GridBagConstraints gbc, int row, int col) {
		gbc.gridx = col;
		gbc.gridy = row;
		gbc.gridwidth = 1;

		JLabel lblField = new JLabel(label);
		lblField.setFont(CONTENT_FONT);
		panel.add(lblField, gbc);

		gbc.gridx = col + 1;
		gbc.weightx = 1.0;
		panel.add(field, gbc);
		gbc.weightx = 0.0;
	}

	// Tùy chỉnh thanh cuộn
	private void customizeScrollPane(JScrollPane scrollPane) {
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
		scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = PRIMARY_COLOR;
				this.trackColor = new Color(245, 245, 245);
			}
		});
	}

	// Initialize all UI components
	private void initializeComponents() {
		// Setup tables
		setupDanhSachSPTable();
		setupDonNhapTable();

		// Initialize text fields
		maDonNhapField = createStyledTextField(15);
		maSPField = createStyledTextField(10);
		tenSPField = createStyledTextField(20);
		giaNhapField = createStyledTextField(10);
		thuongHieuField = createStyledTextField(15);
		soLuongField = createStyledTextField(5);

		// Make certain fields read-only
		maSPField.setEditable(false);
		tenSPField.setEditable(false);
		thuongHieuField.setEditable(false);

		// Initialize combo boxes
		phanLoaiCombo = createStyledComboBox(new String[] { "Chọn phân loại" });
		nhanVienCombo = createStyledComboBox(
				new String[] { "Chọn nhân viên", "Nguyễn Thị Tường Vi", "Trần Văn Nam", "Lê Thị Hoa" });
		nhaCungCapCombo = createStyledComboBox(new String[] { "Chọn nhà cung cấp", "Xưởng may Đại Nam",
				"Xưởng may Hoàng Gia", "Công ty TNHH May Việt Tiến" });

		// Initialize buttons
		themSPButton = createStyledButton("Thêm SP vào đơn", "/icon/circle-plus.png", true);
		suaButton = createStyledButton("Sửa", "/icon/pencil.png", false);
		xoaButton = createStyledButton("Xóa", "/icon/trash.png", false);
		nhapHangButton = createStyledButton("NHẬP HÀNG", null, true);
		danhSachButton = createStyledButton("DANH SÁCH ĐƠN NHẬP", null, true);

		// Style list button differently
		danhSachButton.setBackground(Color.WHITE);
		danhSachButton.setFont(CONTENT_FONT);
		danhSachButton.setForeground(Color.BLACK);
		danhSachButton.setPreferredSize(new Dimension(200, 35));

		// Initialize total amount label
		tongTienLabel = new JLabel("TỔNG TIỀN: 0 đ");
		tongTienLabel.setFont(HEADER_FONT);
		tongTienLabel.setForeground(PRIMARY_COLOR);
	}

	// Text field creation and styling methods
	private JTextField createStyledTextField(int columns) {
		JTextField field = new JTextField(columns);

		// Set basic appearance
		field.setFont(CONTENT_FONT);
		field.setPreferredSize(new Dimension(field.getPreferredSize().width, 35)); // Consistent height

		// Create a compound border with both line and padding
		field.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(230, 230, 230), 1), // Outer border:
																										// light gray,
																										// 1px
				BorderFactory.createEmptyBorder(5, 8, 5, 8) // Inner padding: top, left, bottom, right
		));

		// Add focus effects for better visual feedback
		field.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				// Highlight border when focused
				field.setBorder(BorderFactory.createCompoundBorder(new LineBorder(PRIMARY_COLOR, 1),
						BorderFactory.createEmptyBorder(5, 8, 5, 8)));
			}

			@Override
			public void focusLost(FocusEvent e) {
				// Return to normal border when focus lost
				field.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(230, 230, 230), 1),
						BorderFactory.createEmptyBorder(5, 8, 5, 8)));
			}
		});

		return field;
	}

	// Combo box creation and styling
	private JComboBox<String> createStyledComboBox(String[] items) {
		JComboBox<String> comboBox = new JComboBox<>(items);

		// Basic styling
		comboBox.setFont(CONTENT_FONT);
		comboBox.setBackground(Color.WHITE);
		comboBox.setPreferredSize(new Dimension(comboBox.getPreferredSize().width, 35));

		// Custom UI for better appearance
		comboBox.setUI(new BasicComboBoxUI() {
			@Override
			protected JButton createArrowButton() {
				// Create a custom arrow button
				JButton button = super.createArrowButton();
				button.setBackground(Color.WHITE);
				button.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
				return button;
			}
		});

		// Custom border styling
		comboBox.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(230, 230, 230), 1),
				BorderFactory.createEmptyBorder(5, 8, 5, 8)));

		// Custom renderer for dropdown items
		comboBox.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

				// Apply consistent styling to dropdown items
				setFont(CONTENT_FONT);
				setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

				// Custom selection highlighting
				if (isSelected) {
					setBackground(HOVER_COLOR);
					setForeground(Color.BLACK);
				} else {
					setBackground(Color.WHITE);
					setForeground(Color.BLACK);
				}

				return this;
			}
		});

		// Add hover effects
		comboBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (comboBox.isEnabled()) {
					comboBox.setBorder(BorderFactory.createCompoundBorder(new LineBorder(PRIMARY_COLOR, 1),
							BorderFactory.createEmptyBorder(5, 8, 5, 8)));
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (!comboBox.hasFocus()) {
					comboBox.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(230, 230, 230), 1),
							BorderFactory.createEmptyBorder(5, 8, 5, 8)));
				}
			}
		});

		return comboBox;
	}

	// Button creation and styling
	private JButton createStyledButton(String text, String iconPath, boolean isPrimary) {
		JButton button = new JButton(text);

		// Set basic properties
		button.setFont(CONTENT_FONT);
		button.setPreferredSize(new Dimension(120, 40));
		button.setFocusPainted(false); // Remove focus border

		// Add icon if specified
		if (iconPath != null) {
			button.setIcon(new ImageIcon(getClass().getResource(iconPath)));
		}

		// Apply primary or secondary styling
		if (isPrimary) {
			// Primary button styling
			button.setBackground(PRIMARY_COLOR);
			button.setForeground(Color.WHITE);
			button.setBorder(new LineBorder(PRIMARY_COLOR));
		} else {
			// Secondary button styling
			button.setBackground(Color.WHITE);
			button.setForeground(Color.BLACK);
			button.setBorder(new LineBorder(new Color(230, 230, 230)));
		}

		// Add hover effects
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (isPrimary) {
					button.setBackground(PRIMARY_COLOR.darker());
				} else {
					button.setBackground(HOVER_COLOR);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (isPrimary) {
					button.setBackground(PRIMARY_COLOR);
				} else {
					button.setBackground(Color.WHITE);
				}
			}
		});

		return button;
	}

	private void setupDanhSachSPTable() {
		// Define columns for the product list table
		String[] columns = { "Mã SP", "Tên sản phẩm", "Số lượng tồn" };
		danhSachSPModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Make table read-only
			}
		};
		danhSachSPTable = new JTable(danhSachSPModel);
		setupTable(danhSachSPTable, true); // true indicates this is the product list table
	}

	private void setupDonNhapTable() {
		// Define columns for the order details table
		String[] columns = { "Mã SP", "Tên sản phẩm", "Màu sắc", "Kích cỡ", "Thương hiệu", "Đơn giá", "Số lượng" };
		donNhapModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Make table read-only
			}
		};
		donNhapTable = new JTable(donNhapModel);
		setupTable(donNhapTable, false); // false indicates this is the order table
	}

	private void setupTable(JTable table, boolean isProductList) {
		// Apply consistent table styling
		table.setFont(CONTENT_FONT);
		table.setRowHeight(35);
		table.setGridColor(new Color(230, 230, 230));
		table.setShowVerticalLines(true);
		table.setShowHorizontalLines(true);

		// Setup table header
		JTableHeader header = table.getTableHeader();
		header.setFont(HEADER_FONT);
		header.setBackground(isProductList ? CONTENT_COLOR : Color.WHITE);
		header.setForeground(Color.BLACK);
		header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

		// Configure selection behavior
		table.setSelectionBackground(HOVER_COLOR);
		table.setSelectionForeground(Color.BLACK);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Set up cell renderers for proper alignment
		setupTableRenderers(table, isProductList);
	}

	private void setupTableRenderers(JTable table, boolean isProductList) {
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

		if (isProductList) {
			// Setup renderers for product list table
			table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã SP
			table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Số lượng
			table.setBackground(CONTENT_COLOR);
		} else {
			// Setup renderers for order table
			table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã SP
			table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Kích cỡ
			table.getColumnModel().getColumn(5).setCellRenderer(rightRenderer); // Đơn giá
			table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // Số lượng
		}
	}

	private void loadVariantsForProduct(String productId) {
		try {
			// Clear existing items
			phanLoaiCombo.removeAllItems();
			phanLoaiCombo.addItem("Chọn phân loại");

			// Get variants from database
			ProductVariantDAO variantDAO = new ProductVariantDAO();
			List<ProductVariant> variants = variantDAO.getAllVariantsByProductID(productId);

			if (variants.isEmpty()) {
				// Nếu không có phân loại, tự động tạo một variant mặc định
				phanLoaiCombo.addItem("Mặc định");
				phanLoaiCombo.setSelectedItem("Mặc định");
				phanLoaiCombo.setEnabled(false); // Disable combobox
			} else {
				// Add variants to combo box
				for (ProductVariant variant : variants) {
					String variantDisplay = String.format("Màu: %s - Size: %s (Còn: %d)", variant.getColor(),
							variant.getSize(), variant.getQuantity());
					phanLoaiCombo.addItem(variantDisplay);
				}
				phanLoaiCombo.setEnabled(true);
			}

		} catch (Exception e) {
			LOGGER.severe("Error loading variants: " + e.getMessage());
			showMessage("Lỗi khi tải phân loại sản phẩm", JOptionPane.ERROR_MESSAGE);
		}
	}

	// Event handling methods
	private void addEventListeners() {
		// Product selection handler
		danhSachSPTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				handleProductSelection();
			}
		});

		// Add product to order handler
		themSPButton.addActionListener(e -> {
			if (validateProductInput()) {
				addProductToOrder();
			}
		});

		// Edit product handler
		suaButton.addActionListener(e -> {
			int row = donNhapTable.getSelectedRow();
			if (row >= 0) {
				loadProductForEdit(row);
			} else {
				showMessage("Vui lòng chọn sản phẩm cần sửa", JOptionPane.WARNING_MESSAGE);
			}
		});

		// Delete product handler
		xoaButton.addActionListener(e -> {
			int row = donNhapTable.getSelectedRow();
			if (row >= 0) {
				int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa sản phẩm này?", "Xác nhận xóa",
						JOptionPane.YES_NO_OPTION);

				if (confirm == JOptionPane.YES_OPTION) {
					donNhapModel.removeRow(row);
					updateTongTien();
				}
			} else {
				showMessage("Vui lòng chọn sản phẩm cần xóa", JOptionPane.WARNING_MESSAGE);
			}
		});

		// Import order handler
		nhapHangButton.addActionListener(e -> {
			if (validateOrder()) {
				saveOrder();
			}
		});

		// Back to list handler
		danhSachButton.addActionListener(e -> switchToQuanLyNhapHang());

		// Variant selection handler
		phanLoaiCombo.addActionListener(e -> {
			if (phanLoaiCombo.getSelectedIndex() > 0) {
				soLuongField.requestFocus();
			}
		});
	}

	private void handleProductSelection() {
		int selectedRow = danhSachSPTable.getSelectedRow();
		if (selectedRow >= 0) {
			try {
				// Lấy mã sản phẩm từ dòng được chọn
				String maSP = danhSachSPModel.getValueAt(selectedRow, 0).toString();

				// Lấy thông tin chi tiết sản phẩm từ database
				SanPham sp = sanPhamDAO.getSanPhamByID(maSP);

				if (sp != null) {
					// Điền thông tin sản phẩm vào các trường
					maSPField.setText(sp.getMaSP());
					tenSPField.setText(sp.getTenSP());
					thuongHieuField.setText(sp.getThuongHieu());

					// Reset các trường nhập liệu
					giaNhapField.setText("");
					soLuongField.setText("");

					// Quan trọng: Load phân loại của sản phẩm
					phanLoaiCombo.removeAllItems();
					phanLoaiCombo.addItem("Chọn phân loại");

					// Tạo instance của ProductVariantDAO
					ProductVariantDAO variantDAO = new ProductVariantDAO();

					// Lấy danh sách phân loại và thêm vào combo box
					List<ProductVariant> variants = variantDAO.getAllVariantsByProductID(maSP);

					for (ProductVariant variant : variants) {
						String variantDisplay = String.format("Màu: %s - Size: %s (Còn: %d)", variant.getColor(),
								variant.getSize(), variant.getQuantity());
						LOGGER.info("Found variant: Color=" + variant.getColor() + ", Size=" + variant.getSize()
								+ ", Quantity=" + variant.getQuantity());
						phanLoaiCombo.addItem(variantDisplay);
					}

					// Enable combo box nếu có phân loại
					phanLoaiCombo.setEnabled(phanLoaiCombo.getItemCount() > 1);

					// Focus vào trường giá nhập
					giaNhapField.requestFocus();

					LOGGER.info("Đã load " + variants.size() + " phân loại cho sản phẩm " + maSP);
				}
			} catch (Exception e) {
				LOGGER.severe("Lỗi khi load thông tin sản phẩm: " + e.getMessage());
				showMessage("Lỗi khi tải thông tin sản phẩm: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void addProductToOrder() {
		try {
			// Kiểm tra đã chọn sản phẩm chưa
			if (maSPField.getText().trim().isEmpty()) {
				throw new Exception("Vui lòng chọn sản phẩm!");
			}

			// Lấy thông tin cơ bản của sản phẩm
			String productID = maSPField.getText().trim();
			String productName = tenSPField.getText().trim();
			String brand = thuongHieuField.getText().trim();

			// Kiểm tra và xử lý giá nhập
			if (giaNhapField.getText().trim().isEmpty()) {
				throw new Exception("Vui lòng nhập giá!");
			}
			double price = parsePrice(giaNhapField.getText().trim());

			// Kiểm tra và xử lý số lượng
			if (soLuongField.getText().trim().isEmpty()) {
				throw new Exception("Vui lòng nhập số lượng!");
			}
			int quantity = Integer.parseInt(soLuongField.getText().trim());

			// Xử lý phân loại sản phẩm
			String selectedVariant = phanLoaiCombo.getSelectedItem().toString();
			if (selectedVariant.equals("Chọn phân loại")) {
				throw new Exception("Vui lòng chọn phân loại sản phẩm!");
			}

			// Parse thông tin variant
			VariantInfo variantInfo = new VariantInfo(selectedVariant);

			// Format giá để hiển thị
			NumberFormat formatter = NumberFormat.getNumberInstance();
			String formattedPrice = formatter.format(price);

			// Thêm vào bảng đơn nhập
			Object[] rowData = { productID, productName, variantInfo.color, variantInfo.size, brand, formattedPrice,
					quantity };

			donNhapModel.addRow(rowData);

			// Cập nhật tổng tiền và xóa form
			updateTongTien();
			clearProductInputs();

		} catch (Exception e) {
			LOGGER.severe("Lỗi khi thêm sản phẩm: " + e.getMessage());
			showMessage(e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}

	private String generateVariantKey(String productId, String color, String size) {
		return String.format("%s-%s-%s", productId, color, size);
	}

	private boolean isDuplicateVariant(String variantKey) {
		for (int i = 0; i < donNhapModel.getRowCount(); i++) {
			String existingKey = generateVariantKey(donNhapModel.getValueAt(i, 0).toString(),
					donNhapModel.getValueAt(i, 2).toString(), donNhapModel.getValueAt(i, 3).toString());
			if (existingKey.equals(variantKey)) {
				return true;
			}
		}
		return false;
	}

	private void saveOrder() {
		try {
			// Validate input data
			if (nhaCungCapCombo.getSelectedIndex() == 0) {
				throw new IllegalStateException("Vui lòng chọn nhà cung cấp!");
			}
			if (nhanVienCombo.getSelectedIndex() == 0) {
				throw new IllegalStateException("Vui lòng chọn nhân viên!");
			}
			if (donNhapModel.getRowCount() == 0) {
				throw new IllegalStateException("Vui lòng thêm ít nhất một sản phẩm vào đơn!");
			}

			// Create import object
			Import importOrder = new Import();
			importOrder.setImportID(maDonNhapField.getText().trim());
			importOrder.setImportDate(new Date());

			// Get supplier ID from selected name
			String supplierName = nhaCungCapCombo.getSelectedItem().toString();
			String supplierID = getSupplierIDFromName(supplierName);
			importOrder.setSupplier(supplierID);

			// Get staff ID from selected name
			String staffName = nhanVienCombo.getSelectedItem().toString();
			String staffID = getStaffIDFromName(staffName);
			importOrder.setStaff(staffID);

			// Create details list and calculate total amount
			double totalAmount = 0;
			List<ImportDetail> detailsList = new ArrayList<>();
			ProductVariantDAO variantDAO = new ProductVariantDAO();

			for (int i = 0; i < donNhapModel.getRowCount(); i++) {
				ImportDetail detail = new ImportDetail();
				detail.setImportID(importOrder.getImportID());

				// Get variant ID
				String productID = donNhapModel.getValueAt(i, 0).toString();
				String color = donNhapModel.getValueAt(i, 2).toString();
				String size = donNhapModel.getValueAt(i, 3).toString();

				// Xử lý variant
				String variantID;
				if (color.equals("Mặc định") && size.equals("Mặc định")) {
					// Lấy hoặc tạo variant mặc định
					variantID = variantDAO.getDefaultVariantID(productID);
					if (variantID == null) {
						variantID = variantDAO.createDefaultVariant(productID);
					}
				} else {
					// Lấy variant thông thường
					variantID = variantDAO.getVariantID(productID, color, size);
				}

				if (variantID == null) {
					throw new Exception("Không thể tìm thấy phân loại cho sản phẩm: " + productID);
				}

				detail.setVariantID(variantID);

				// Set quantity and price
				int quantity = Integer.parseInt(donNhapModel.getValueAt(i, 6).toString());
				double price = parsePrice(donNhapModel.getValueAt(i, 5).toString());

				detail.setQuantity(quantity);
				detail.setPrice(price);
				detailsList.add(detail);

				totalAmount += price * quantity;
			}

			// Set total amount and details list
			importOrder.setTotalAmount(Math.round(totalAmount * 100.0) / 100.0);
			importOrder.setImportDetails(detailsList);

			// Save to database
			boolean success = importBUS.createImport(importOrder);

			if (success) {
				showMessage("Đã lưu đơn nhập hàng thành công!", JOptionPane.INFORMATION_MESSAGE);
				clearForm();
				loadProductData(); // Refresh product list
				switchToQuanLyNhapHang();
			} else {
				showMessage("Không thể lưu đơn nhập hàng. Vui lòng thử lại!", JOptionPane.ERROR_MESSAGE);
			}

		} catch (IllegalStateException e) {
			showMessage(e.getMessage(), JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			LOGGER.severe("Error saving import order: " + e.getMessage());
			showMessage("Lỗi khi lưu đơn nhập: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}

	// Helper method to get supplier ID from name
	private String getSupplierIDFromName(String supplierName) throws Exception {
		NhaCungCapDAO nccDAO = new NhaCungCapDAO();
		List<NhaCC> suppliers = nccDAO.getAllNhaCC();

		for (NhaCC supplier : suppliers) {
			if (supplier.getTenNCC().equals(supplierName)) {
				return supplier.getMaNCC();
			}
		}
		throw new Exception("Không tìm thấy mã nhà cung cấp cho " + supplierName);
	}

	private String getStaffIDFromName(String staffDisplay) throws Exception {
		if (staffDisplay.equals("Chọn nhân viên")) {
			throw new Exception("Vui lòng chọn nhân viên!");
		}

		String userID = staffDisplay.split(" - ")[0].trim();
		return userID;
	}

	private String getVariantIDFromDatabase(String productID, String color, String size) {
		try {
			ProductVariantDAO variantDAO = new ProductVariantDAO();

			// Nếu không có màu hoặc size, tạo variant mặc định
			if (color == null || size == null || color.isEmpty() || size.isEmpty() || color.equals("Mặc định")
					|| size.equals("Mặc định")) {

				String defaultVariantID = variantDAO.getDefaultVariantID(productID);
				if (defaultVariantID == null) {
					defaultVariantID = variantDAO.createDefaultVariant(productID);
				}
				return defaultVariantID;
			}

			// Tìm variant thông thường
			List<ProductVariant> variants = variantDAO.getAllVariantsByProductID(productID);
			for (ProductVariant variant : variants) {
				if (variant.getColor().equals(color) && variant.getSize().equals(size)) {
					return variant.getVariantID();
				}
			}

			throw new Exception("Không tìm thấy variant cho sản phẩm " + productID);
		} catch (Exception e) {
			LOGGER.severe("Error getting variantID: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	// Helper methods for order processing
	private String generateImportID() {
		// Get current timestamp for unique ID
		long timestamp = System.currentTimeMillis();
		return String.format("IMP%d", timestamp % 100000);
	}

	private List<ImportDetail> createImportDetails() {
		List<ImportDetail> details = new ArrayList<>();

		for (int i = 0; i < donNhapModel.getRowCount(); i++) {
			ImportDetail detail = new ImportDetail();

			String productID = donNhapModel.getValueAt(i, 0).toString();
			String color = donNhapModel.getValueAt(i, 2).toString();
			String size = donNhapModel.getValueAt(i, 3).toString();

			// Tìm variantID từ database dựa trên productID, color và size
			ProductVariantDAO variantDAO = new ProductVariantDAO();
			List<ProductVariant> variants = variantDAO.getAllVariantsByProductID(productID);

			String variantID = null;
			for (ProductVariant v : variants) {
				if (v.getColor().equals(color) && v.getSize().equals(size)) {
					variantID = v.getVariantID();
					break;
				}
			}

			if (variantID == null) {
				throw new IllegalStateException(
						"Phân loại sản phẩm không tồn tại: " + productID + "-" + color + "-" + size);
			}

			detail.setVariantID(variantID);
			detail.setPrice(parsePrice(donNhapModel.getValueAt(i, 5).toString()));
			detail.setQuantity(Integer.parseInt(donNhapModel.getValueAt(i, 6).toString()));

			details.add(detail);
		}

		return details;
	}

	private double calculateTotalAmount(List<ImportDetail> details) {
		return details.stream().mapToDouble(detail -> detail.getPrice() * detail.getQuantity()).sum();
	}

	private void updateTongTien() {
		double total = 0;
		for (int i = 0; i < donNhapModel.getRowCount(); i++) {
			String priceStr = donNhapModel.getValueAt(i, 5).toString().replace(",", "");
			double price = Double.parseDouble(priceStr);
			int quantity = Integer.parseInt(donNhapModel.getValueAt(i, 6).toString());
			total += price * quantity;
		}

		NumberFormat formatter = NumberFormat.getNumberInstance();
		formatter.setMaximumFractionDigits(0);
		tongTienLabel.setText("TỔNG TIỀN: " + formatter.format(total) + " đ");
	}

	private double parsePrice(String priceStr) {
		return Double.parseDouble(priceStr.replaceAll("[^\\d.]", ""));
	}

	private void switchToQuanLyNhapHang() {
		Container mainContent = ThemDonNhap.this.getParent();
		mainContent.removeAll();
		mainContent.add(new QuanLyNhapHang());
		mainContent.revalidate();
		mainContent.repaint();
	}

	// Form validation methods
	private boolean validateProductInput() {
		try {
			// Check if product is selected
			if (maSPField.getText().trim().isEmpty()) {
				throw new IllegalStateException("Vui lòng chọn sản phẩm từ danh sách!");
			}

			// Parse and validate price
			String priceStr = giaNhapField.getText().trim();
			if (priceStr.isEmpty()) {
				throw new IllegalStateException("Vui lòng nhập giá nhập!");
			}
			double price = parsePrice(priceStr);
			if (price <= 0) {
				throw new IllegalStateException("Giá nhập phải lớn hơn 0!");
			}

			// Parse and validate quantity
			String qtyStr = soLuongField.getText().trim();
			if (qtyStr.isEmpty()) {
				throw new IllegalStateException("Vui lòng nhập số lượng!");
			}
			int quantity = Integer.parseInt(qtyStr);
			if (quantity <= 0) {
				throw new IllegalStateException("Số lượng phải lớn hơn 0!");
			}

			// Check variant stock (from combo box display)

			return true;

		} catch (IllegalStateException e) {
			showMessage(e.getMessage(), JOptionPane.ERROR_MESSAGE);
			return false;
		} catch (NumberFormatException e) {
			showMessage("Vui lòng nhập giá trị số hợp lệ!", JOptionPane.ERROR_MESSAGE);
			return false;
		} catch (Exception e) {
			LOGGER.severe("Error validating product input: " + e.getMessage());
			showMessage("Lỗi khi kiểm tra dữ liệu: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	private boolean validateOrder() {
		try {
			// Check import ID
			if (maDonNhapField.getText().trim().isEmpty()) {
				throw new IllegalStateException("Vui lòng nhập mã đơn nhập!");
			}

			// Validate staff selection
			if (nhanVienCombo.getSelectedIndex() == 0) {
				throw new IllegalStateException("Vui lòng chọn nhân viên phụ trách!");
			}

			// Validate supplier selection
			if (nhaCungCapCombo.getSelectedIndex() == 0) {
				throw new IllegalStateException("Vui lòng chọn nhà cung cấp!");
			}

			// Check if order has any items
			if (donNhapModel.getRowCount() == 0) {
				throw new IllegalStateException("Vui lòng thêm ít nhất một sản phẩm vào đơn!");
			}

			return true;

		} catch (IllegalStateException e) {
			showMessage(e.getMessage(), JOptionPane.ERROR_MESSAGE);
			return false;
		} catch (Exception e) {
			LOGGER.severe("Error validating order: " + e.getMessage());
			showMessage("Lỗi khi kiểm tra đơn nhập: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	// Form handling methods
	private void clearProductInputs() {
		maSPField.setText("");
		tenSPField.setText("");
		giaNhapField.setText("");
		thuongHieuField.setText("");
		soLuongField.setText("");
		phanLoaiCombo.setSelectedIndex(0);
		danhSachSPTable.clearSelection();
	}

	private void clearForm() {
		maDonNhapField.setText("");
		nhanVienCombo.setSelectedIndex(0);
		nhaCungCapCombo.setSelectedIndex(0);
		clearProductInputs();
		donNhapModel.setRowCount(0);
		updateTongTien();
	}

	private void loadProductData() {
		try {
			// Clear existing table data
			danhSachSPModel.setRowCount(0);

			// Get all active products from database
			ArrayList<SanPham> dsSanPham = sanPhamDAO.getAllSanPham();

			// Add products to table model
			for (SanPham sp : dsSanPham) {
				if ("Đang kinh doanh".equals(sp.getTinhTrang()) || "Sắp hết".equals(sp.getTinhTrang())
						|| "Chưa nhập về".equals(sp.getTinhTrang())) {
					Object[] row = { sp.getMaSP(), sp.getTenSP(), sp.getSoLuongTonKho() };
					danhSachSPModel.addRow(row);
				}
			}

			LOGGER.info("Loaded " + danhSachSPModel.getRowCount() + " active products");

		} catch (Exception e) {
			LOGGER.severe("Error loading product data: " + e.getMessage());
			showMessage("Lỗi khi tải dữ liệu sản phẩm: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}

	// UI utility methods
	private void showMessage(String message, int type) {
		SwingUtilities.invokeLater(() -> {
			JOptionPane.showMessageDialog(this, message, "Thông báo", type);
		});
	}

	// Add editor formatting for numerical fields
	private void addNumericFieldFormatting(JTextField field) {
		field.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
					e.consume();
				}
			}
		});

		field.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				try {
					String text = field.getText().trim();
					if (!text.isEmpty()) {
						double value = Double.parseDouble(text);
						NumberFormat formatter = NumberFormat.getNumberInstance();
						field.setText(formatter.format(value));
					}
				} catch (NumberFormatException ex) {
					field.setText("");
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
				field.setText(field.getText().replaceAll("[^\\d]", ""));
			}
		});
	}

	// Method to load product for editing
	private void loadProductForEdit(int row) {
		try {
			// Get variant info from selected row
			String maSP = donNhapModel.getValueAt(row, 0).toString();
			String tenSP = donNhapModel.getValueAt(row, 1).toString();
			String mauSac = donNhapModel.getValueAt(row, 2).toString();
			String kichCo = donNhapModel.getValueAt(row, 3).toString();
			String thuongHieu = donNhapModel.getValueAt(row, 4).toString();
			String giaNhap = donNhapModel.getValueAt(row, 5).toString();
			String soLuong = donNhapModel.getValueAt(row, 6).toString();

			// Set basic product info
			maSPField.setText(maSP);
			tenSPField.setText(tenSP);
			thuongHieuField.setText(thuongHieu);

			// Load variants and select the current one
			loadVariantsForProduct(maSP);
			selectVariantInCombo(mauSac, kichCo);

			// Set price and quantity
			giaNhapField.setText(giaNhap.replaceAll("[^\\d]", ""));
			soLuongField.setText(soLuong);

			// Remove the row from order table
			donNhapModel.removeRow(row);
			updateTongTien();

		} catch (Exception e) {
			LOGGER.severe("Error loading product for edit: " + e.getMessage());
			showMessage("Lỗi khi tải thông tin sản phẩm: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}

	private void selectVariantInCombo(String color, String size) {
		String targetVariant = String.format("Màu: %s - Size: %s", color, size);
		for (int i = 0; i < phanLoaiCombo.getItemCount(); i++) {
			String item = phanLoaiCombo.getItemAt(i);
			if (item.startsWith(targetVariant)) {
				phanLoaiCombo.setSelectedIndex(i);
				break;
			}
		}
	}
}