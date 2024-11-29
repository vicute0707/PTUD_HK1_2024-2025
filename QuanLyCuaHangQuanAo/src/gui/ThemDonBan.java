package gui;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;

public class ThemDonBan extends JPanel {
	// Constants
	private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
	private static final Color CONTENT_COLOR = new Color(255, 242, 242);
	private static final Color HOVER_COLOR = new Color(252, 231, 243);
	private static final Font HEADER_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12);
	private static final Font CONTENT_FONT = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12);

	// Components
	private JTable danhSachSPTable;
	private DefaultTableModel danhSachSPModel;
	private JTable donBanTable;
	private DefaultTableModel donBanModel;
	private JLabel tongTienLabel;

	// Text Fields for input
	private JTextField maDonBanField;
	private JTextField maSPField;
	private JTextField tenSPField;
	private JTextField giaBanField;
	private JTextField thuongHieuField;
	private JTextField mauSacField;
	private JTextField kichCoField;
	private JTextField soLuongField;

	// Combo boxes
	private JComboBox<String> nhanVienCombo;
	
	private JComboBox<String> phuongThucThanhToanCombo;

	// Buttons
	private JButton themSPButton;
	private JButton suaButton;
	private JButton xoaButton;
	private JButton banHangButton;
	private JButton danhSachButton;

	public ThemDonBan() {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		setBorder(new EmptyBorder(20, 20, 20, 20));

		initializeComponents();
		setupLayout();
		addEventListeners();
		loadSampleData();
	}

	private void initializeComponents() {
		// Initialize tables
		setupDanhSachSPTable();
		setupDonBanTable();

		// Initialize text fields
		maDonBanField = createStyledTextField(15);
		maSPField = createStyledTextField(10);
		tenSPField = createStyledTextField(20);
		giaBanField = createStyledTextField(10);
		thuongHieuField = createStyledTextField(15);
		mauSacField = createStyledTextField(10);
		kichCoField = createStyledTextField(10);
		soLuongField = createStyledTextField(5);

		// Disable fields that should be read-only
		maSPField.setEditable(false);
		tenSPField.setEditable(false);
		thuongHieuField.setEditable(false);

		// Initialize combo boxes
		nhanVienCombo = createStyledComboBox(
				new String[] { "Chọn nhân viên", "Nguyễn Thị Tường Vi", "Trần Văn Nam", "Lê Thị Hoa" });
		
		phuongThucThanhToanCombo = createStyledComboBox(
				new String[] { "Chọn phương thức thanh toán", "Tiền mặt", "Chuyển khoản", "Thẻ tín dụng" });

		// Initialize buttons
		themSPButton = createStyledButton("Thêm SP vào đơn", "/icon/circle-plus.png", true);
		suaButton = createStyledButton("Sửa", "/icon/pencil.png", false);
		xoaButton = createStyledButton("Xóa", "/icon/trash.png", false);
		banHangButton = createStyledButton("HOÀN TẤT ĐƠN", null, true);
		danhSachButton = createStyledButton("DANH SÁCH ĐƠN BÁN", null, true);
		danhSachButton.setBackground(Color.white);
		danhSachButton.setFont(CONTENT_FONT);
		danhSachButton.setForeground(Color.black);
		danhSachButton.setPreferredSize(new Dimension(200, 35));

		// Initialize tổng tiền label
		tongTienLabel = new JLabel("TỔNG TIỀN: 0 đ");
		tongTienLabel.setFont(HEADER_FONT);
		tongTienLabel.setForeground(PRIMARY_COLOR);
	}

	private void setupDanhSachSPTable() {
		String[] columns = { "Mã SP", "Tên sản phẩm", "Giá bán", "Số lượng tồn" };
		danhSachSPModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		danhSachSPTable = new JTable(danhSachSPModel);
		setupTable(danhSachSPTable);
	}

	private void setupDonBanTable() {
		String[] columns = { "Mã SP", "Tên sản phẩm", "Màu sắc", "Kích cỡ", "Thương hiệu", "Đơn giá", "Số lượng",
				"Thành tiền" };
		donBanModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		donBanTable = new JTable(donBanModel);
		setupTable(donBanTable);
	}

	private void setupTable(JTable table) {
		table.setFont(CONTENT_FONT);
		table.setRowHeight(35);
		table.setGridColor(new Color(230, 230, 230));
		table.setShowVerticalLines(true);
		table.setShowHorizontalLines(true);

		JTableHeader header = table.getTableHeader();
		header.setFont(HEADER_FONT);
		header.setBackground(table == danhSachSPTable ? CONTENT_COLOR : Color.WHITE);
		header.setForeground(Color.BLACK);
		header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

		table.setSelectionBackground(HOVER_COLOR);
		table.setSelectionForeground(Color.BLACK);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

		if (table == danhSachSPTable) {
			table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã SP
			table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer); // Giá bán
			table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Số lượng tồn
			table.setBackground(CONTENT_COLOR);
		} else {
			table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã SP
			table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Kích cỡ
			table.getColumnModel().getColumn(5).setCellRenderer(rightRenderer); // Đơn giá
			table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // Số lượng
			table.getColumnModel().getColumn(7).setCellRenderer(rightRenderer); // Thành tiền
		}
	}

	private void setupLayout() {
		setLayout(new BorderLayout(0, 20));

		// Top panel
		add(createTopPanel(), BorderLayout.NORTH);

		// Center panel with split layout
		JPanel centerPanel = new JPanel(new BorderLayout(20, 0));
		centerPanel.setBackground(Color.WHITE);

		// Left side - Product list panel
		JPanel leftPanel = createDanhSachSPPanel();
		leftPanel.setPreferredSize(new Dimension(400, 0));

		// Right side panel
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setBackground(Color.WHITE);

		rightPanel.add(createProductInputPanel());
		rightPanel.add(Box.createVerticalStrut(20));
		rightPanel.add(createOrderTablePanel());

		centerPanel.add(leftPanel, BorderLayout.WEST);
		centerPanel.add(rightPanel, BorderLayout.CENTER);

		add(centerPanel, BorderLayout.CENTER);

		add(createBottomPanel(), BorderLayout.SOUTH);
	}

	private JPanel createTopPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 15);
        
        // Adjust layout to remove khachHangCombo
        addFormField(panel, "Mã đơn bán:", maDonBanField, gbc, 0, 0);
        addFormField(panel, "Nhân viên phụ trách:", nhanVienCombo, gbc, 0, 2);
        addFormField(panel, "Phương thức thanh toán:", phuongThucThanhToanCombo, gbc, 0, 4);
        
        return panel;
	}

	private JPanel createDanhSachSPPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(CONTENT_COLOR);
		panel.setBorder(
				BorderFactory.createCompoundBorder(new EmptyBorder(0, 0, 0, 0), new EmptyBorder(10, 10, 10, 10)));

		JLabel titleLabel = new JLabel("Danh sách sản phẩm");
		titleLabel.setFont(HEADER_FONT);
		titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
		panel.add(titleLabel, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(danhSachSPTable);
		styleScrollPane(scrollPane);
		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
	}

	private JPanel createProductInputPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);

		JLabel titleLabel = new JLabel("Thông tin sản phẩm bán");
		titleLabel.setFont(HEADER_FONT);
		titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
		panel.add(titleLabel, BorderLayout.NORTH);

		JPanel fieldsPanel = new JPanel(new GridBagLayout());
		fieldsPanel.setBackground(Color.WHITE);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 5, 5, 15);

		addFormField(fieldsPanel, "Mã SP:", maSPField, gbc, 0, 0);
		addFormField(fieldsPanel, "Tên sản phẩm:", tenSPField, gbc, 0, 2);
		addFormField(fieldsPanel, "Giá bán:", giaBanField, gbc, 1, 0);
		addFormField(fieldsPanel, "Thương hiệu:", thuongHieuField, gbc, 1, 2);
		addFormField(fieldsPanel, "Màu sắc:", mauSacField, gbc, 2, 0);
		addFormField(fieldsPanel, "Kích cỡ:", kichCoField, gbc, 2, 2);
		addFormField(fieldsPanel, "Số lượng:", soLuongField, gbc, 2, 4);

		panel.add(fieldsPanel, BorderLayout.CENTER);

		return panel;
	}

	private JPanel createOrderTablePanel() {
		JPanel panel = new JPanel(new BorderLayout(0, 10));
		panel.setBackground(Color.WHITE);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.add(themSPButton);
		themSPButton.setPreferredSize(new Dimension(170, 35));
		buttonPanel.add(suaButton);
		buttonPanel.add(xoaButton);

		panel.add(buttonPanel, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(donBanTable);
		styleScrollPane(scrollPane);
		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
	}

	private JPanel createBottomPanel() {
		JPanel panel = new JPanel(new BorderLayout(10, 0));
		panel.setBackground(Color.WHITE);
		panel.setBorder(new EmptyBorder(10, 0, 0, 0));

		panel.add(tongTienLabel, BorderLayout.WEST);

		JPanel panelBt = new JPanel();
		panelBt.add(danhSachButton);
		danhSachButton.addActionListener(e -> {
			Container mainContent = ThemDonBan.this.getParent();
			mainContent.removeAll();
			QuanLyDonBan quanLyPanel = new QuanLyDonBan();
			mainContent.add(quanLyPanel);
			mainContent.revalidate();
			mainContent.repaint();
		});
		panelBt.add(banHangButton);

		panel.add(panelBt, BorderLayout.EAST);

		return panel;
	}

	private void addFormField(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc, int row,
			int col) {
		gbc.gridx = col;
		gbc.gridy = row;

		JLabel label = new JLabel(labelText);
		label.setFont(CONTENT_FONT);
		panel.add(label, gbc);

		gbc.gridx = col + 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		panel.add(field, gbc);

		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0;
	}

	private JTextField createStyledTextField(int columns) {
		JTextField field = new JTextField(columns);
		field.setFont(CONTENT_FONT);
		field.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(230, 230, 230), 1),
				BorderFactory.createEmptyBorder(5, 8, 5, 8)));

		field.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				field.setBorder(BorderFactory.createCompoundBorder(new LineBorder(PRIMARY_COLOR, 1),
						BorderFactory.createEmptyBorder(5, 8, 5, 8)));
			}

			@Override
			public void focusLost(FocusEvent e) {
				field.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(230, 230, 230), 1),
						BorderFactory.createEmptyBorder(5, 8, 5, 8)));
			}
		});

		return field;
	}

	private JComboBox<String> createStyledComboBox(String[] items) {
		JComboBox<String> comboBox = new JComboBox<>(items);
		comboBox.setFont(CONTENT_FONT);
		comboBox.setBackground(Color.WHITE);

		comboBox.setUI(new BasicComboBoxUI() {
			@Override
			protected JButton createArrowButton() {
				JButton button = super.createArrowButton();
				button.setBackground(Color.WHITE);
				button.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
				return button;
			}
		});

		comboBox.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(230, 230, 230), 1),
				BorderFactory.createEmptyBorder(5, 8, 5, 8)));

		comboBox.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				setFont(CONTENT_FONT);
				setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

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

	private JButton createStyledButton(String text, String iconPath, boolean isPrimary) {
		JButton button = new JButton(text);
		button.setFont(CONTENT_FONT);
		button.setPreferredSize(new Dimension(120, 40));

		if (iconPath != null) {
			button.setIcon(new ImageIcon(getClass().getResource(iconPath)));
		}

		if (isPrimary) {
			button.setBackground(PRIMARY_COLOR);
			button.setForeground(Color.WHITE);
			button.setBorder(new LineBorder(PRIMARY_COLOR));
		} else {
			button.setBackground(Color.WHITE);
			button.setForeground(Color.BLACK);
			button.setBorder(new LineBorder(new Color(230, 230, 230)));
		}

		button.setFocusPainted(false);

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

	private void styleScrollPane(JScrollPane scrollPane) {
		scrollPane.setBorder(new LineBorder(new Color(230, 230, 230)));
		scrollPane.getViewport().setBackground(Color.WHITE);

		scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = PRIMARY_COLOR;
				this.trackColor = new Color(245, 245, 245);
			}

			@Override
			protected JButton createDecreaseButton(int orientation) {
				return createZeroButton();
			}

			@Override
			protected JButton createIncreaseButton(int orientation) {
				return createZeroButton();
			}
		});
	}

	private JButton createZeroButton() {
		JButton button = new JButton();
		button.setPreferredSize(new Dimension(0, 0));
		button.setMinimumSize(new Dimension(0, 0));
		button.setMaximumSize(new Dimension(0, 0));
		return button;
	}

	private void addEventListeners() {
		danhSachSPTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = danhSachSPTable.getSelectedRow();
				if (row >= 0) {
					String maSP = danhSachSPModel.getValueAt(row, 0).toString();
					String tenSP = danhSachSPModel.getValueAt(row, 1).toString();
					String giaBan = danhSachSPModel.getValueAt(row, 2).toString();

					maSPField.setText(maSP);
					tenSPField.setText(tenSP);
					giaBanField.setText(giaBan);
					mauSacField.requestFocus();
				}
			}
		});

		themSPButton.addActionListener(e -> {
			if (validateProductInput()) {
				addProductToOrder();
				clearProductInputs();
			}
		});

		suaButton.addActionListener(e -> {
			int row = donBanTable.getSelectedRow();
			if (row >= 0) {
				loadProductForEdit(row);
			} else {
				showMessage("Vui lòng chọn sản phẩm cần sửa!", JOptionPane.WARNING_MESSAGE);
			}
		});

		xoaButton.addActionListener(e -> {
			int row = donBanTable.getSelectedRow();
			if (row >= 0) {
				int option = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa sản phẩm này?", "Xác nhận xóa",
						JOptionPane.YES_NO_OPTION);

				if (option == JOptionPane.YES_OPTION) {
					donBanModel.removeRow(row);
					updateTongTien();
				}
			} else {
				showMessage("Vui lòng chọn sản phẩm cần xóa!", JOptionPane.WARNING_MESSAGE);
			}
		});

		banHangButton.addActionListener(e -> {
			if (validateOrder()) {
				saveOrder();
				Container parentPanel = banHangButton.getParent();
				while (!(parentPanel instanceof ThemDonBan)) {
					parentPanel = parentPanel.getParent();
				}
				parentPanel.removeAll();
				QuanLyDonBan addPanel = new QuanLyDonBan();
				parentPanel.add(addPanel);
				parentPanel.revalidate();
				parentPanel.repaint();
			}
		});
	}

	private boolean validateProductInput() {
		if (maSPField.getText().trim().isEmpty()) {
			showMessage("Vui lòng chọn sản phẩm từ danh sách!", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (mauSacField.getText().trim().isEmpty()) {
			showMessage("Vui lòng nhập màu sắc!", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (kichCoField.getText().trim().isEmpty()) {
			showMessage("Vui lòng nhập kích cỡ!", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (soLuongField.getText().trim().isEmpty()) {
			showMessage("Vui lòng nhập số lượng!", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		try {
			int soLuong = Integer.parseInt(soLuongField.getText().trim());
			if (soLuong <= 0) {
				showMessage("Số lượng phải lớn hơn 0!", JOptionPane.ERROR_MESSAGE);
				return false;
			}

// Check against available quantity
			int row = danhSachSPTable.getSelectedRow();
			if (row >= 0) {
				int available = Integer.parseInt(danhSachSPModel.getValueAt(row, 3).toString());
				if (soLuong > available) {
					showMessage("Số lượng vượt quá số lượng tồn kho!", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
		} catch (NumberFormatException ex) {
			showMessage("Số lượng không hợp lệ!", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		return true;
	}

	private void addProductToOrder() {
		String maSP = maSPField.getText().trim();
		String tenSP = tenSPField.getText().trim();
		String mauSac = mauSacField.getText().trim();
		String kichCo = kichCoField.getText().trim();
		String thuongHieu = thuongHieuField.getText().trim();

		double giaBan = Double.parseDouble(giaBanField.getText().trim().replace(",", ""));
		int soLuong = Integer.parseInt(soLuongField.getText().trim());
		double thanhTien = giaBan * soLuong;

		NumberFormat formatter = NumberFormat.getNumberInstance();
		String giaBanFormatted = formatter.format(giaBan);
		String thanhTienFormatted = formatter.format(thanhTien);

		donBanModel.addRow(
				new Object[] { maSP, tenSP, mauSac, kichCo, thuongHieu, giaBanFormatted, soLuong, thanhTienFormatted });

		updateTongTien();
	}

	private void loadProductForEdit(int row) {
		maSPField.setText(donBanModel.getValueAt(row, 0).toString());
		tenSPField.setText(donBanModel.getValueAt(row, 1).toString());
		mauSacField.setText(donBanModel.getValueAt(row, 2).toString());
		kichCoField.setText(donBanModel.getValueAt(row, 3).toString());
		thuongHieuField.setText(donBanModel.getValueAt(row, 4).toString());
		giaBanField.setText(donBanModel.getValueAt(row, 5).toString().replace(",", ""));
		soLuongField.setText(donBanModel.getValueAt(row, 6).toString());

		donBanModel.removeRow(row);
		updateTongTien();
	}

	private void clearProductInputs() {
		maSPField.setText("");
		tenSPField.setText("");
		giaBanField.setText("");
		mauSacField.setText("");
		kichCoField.setText("");
		thuongHieuField.setText("");
		soLuongField.setText("");
		danhSachSPTable.clearSelection();
	}

	private boolean validateOrder() {
		if (maDonBanField.getText().trim().isEmpty()) {
			showMessage("Vui lòng nhập mã đơn bán!", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (nhanVienCombo.getSelectedIndex() == 0) {
			showMessage("Vui lòng chọn nhân viên!", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (phuongThucThanhToanCombo.getSelectedIndex() == 0) {
			showMessage("Vui lòng chọn phương thức thanh toán!", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (donBanModel.getRowCount() == 0) {
			showMessage("Vui lòng thêm ít nhất một sản phẩm vào đơn!", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	private void saveOrder() {
// TODO: Implement order saving logic
		showMessage("Đã lưu đơn bán hàng thành công!", JOptionPane.INFORMATION_MESSAGE);
		clearForm();
	}

	private void clearForm() {
		maDonBanField.setText("");
		nhanVienCombo.setSelectedIndex(0);

		phuongThucThanhToanCombo.setSelectedIndex(0);
		clearProductInputs();
		donBanModel.setRowCount(0);
		updateTongTien();
	}

	private void updateTongTien() {
		double total = 0;
		for (int i = 0; i < donBanModel.getRowCount(); i++) {
			String thanhTienStr = donBanModel.getValueAt(i, 7).toString().replace(",", "");
			total += Double.parseDouble(thanhTienStr);
		}

		NumberFormat formatter = NumberFormat.getNumberInstance();
		tongTienLabel.setText("TỔNG TIỀN: " + formatter.format(total) + " đ");
	}

	private void loadSampleData() {
		// Sample data for danh sách SP
		Object[][] danhSachData = { { "SP01", "Váy kẻ caro cúc", "395,000", "30" },
				{ "SP02", "Áo sơ mi trắng", "295,000", "25" }, { "SP03", "Quần jean nữ", "495,000", "40" },
				{ "SP04", "Áo thun basic", "195,000", "50" }, { "SP05", "Chân váy xếp ly", "345,000", "35" } };

		for (Object[] row : danhSachData) {
			danhSachSPModel.addRow(row);
		}

		// Sample data for đơn bán
		Object[][] donBanData = { { "SP001", "Váy hoa dáng xòe", "Trắng", "M", "GUMAC", "450,000", "2", "900,000" } };

		for (Object[] row : donBanData) {
			donBanModel.addRow(row);
		}

		updateTongTien();
	}

	private void showMessage(String message, int messageType) {
		JOptionPane.showMessageDialog(this, message, "Thông báo", messageType);
	}
}
