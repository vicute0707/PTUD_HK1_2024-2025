package dialog;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import bus.ProductVariantBUS;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import entity.PhanLoaiSanPham;
import entity.ProductVariant;
import entity.SanPham;

public class SuaPhanLoai extends JDialog {
	private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
	private static final Color CONTENT_COLOR = new Color(255, 192, 203);
	private static final Color HOVER_COLOR = new Color(252, 231, 243);
	private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
	private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
	private static final Font CONTENT_FONT = new Font("Segoe UI", Font.PLAIN, 14);

	private JTextField txtMauSac, txtKichCo, txtTonKho;
	private JTable table;
	private DefaultTableModel tableModel;
	private boolean isConfirmed = false;

	private String productId;
	private ProductVariantBUS variantBUS;
	private PhanLoaiSanPham plsp = new PhanLoaiSanPham();

	public SuaPhanLoai(Frame owner, String productId) {
		super(owner, "Chỉnh Sửa Phân Loại Sản Phẩm", true);
		this.productId = productId;
		this.variantBUS = new ProductVariantBUS();
		initComponents();
		loadVariants(); // Load variants for the specific product
	}

	private void loadVariants() {
		tableModel.setRowCount(0);
		java.util.List<ProductVariant> variants = variantBUS.getAllVariantsByProductID(productId);
		int stt = 1;
		for (ProductVariant variant : variants) {
			Object[] row = { stt++, variant.getVariantID(), variant.getColor(), variant.getSize(),
					variant.getQuantity() };
			tableModel.addRow(row);
		}
	}

	private void initComponents() {
		setLayout(new BorderLayout(0, 20));
		setBackground(Color.WHITE);

		// Header
		JLabel headerLabel = new JLabel("PHÂN LOẠI SẢN PHẨM", SwingConstants.CENTER);
		headerLabel.setFont(HEADER_FONT);
		headerLabel.setForeground(PRIMARY_COLOR);
		headerLabel.setBorder(new EmptyBorder(20, 0, 20, 0));

		// Input Panel
		JPanel inputPanel = new JPanel(new GridLayout(1, 3, 20, 0));
		inputPanel.setBackground(Color.WHITE);
		inputPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

		// Tạo các trường nhập liệu
		txtMauSac = createStyledTextField("Màu sắc");
		txtKichCo = createStyledTextField("Kích cỡ");
		txtTonKho = createStyledTextField("Tồn kho");

		inputPanel.add(createInputGroup("Màu sắc", txtMauSac));
		inputPanel.add(createInputGroup("Kích cỡ", txtKichCo));
		inputPanel.add(createInputGroup("Số lượng", txtTonKho));

		// Table Panel
		JPanel tablePanel = createTablePanel();

		JPanel rightButtonPanel = new JPanel(new GridLayout(4, 1, 0, 5)); // Reduce vertical space
		rightButtonPanel.setBackground(Color.WHITE);
		rightButtonPanel.setBorder(new EmptyBorder(0, 10, 0, 20));

		JButton btnThem = createStyledButton("Thêm phân loại", new Color(219, 39, 119));
		JButton btnSua = createStyledButton("Sửa phân loại", new Color(124, 58, 237));
		JButton btnXoa = createStyledButton("Xóa phân loại", new Color(225, 29, 72));
		JButton btnLamMoi = createStyledButton("Làm mới", new Color(104, 129, 57));

		// Set preferred sizes to make buttons smaller
		btnThem.setPreferredSize(new Dimension(160, 30));
		btnSua.setPreferredSize(new Dimension(160, 30));
		btnXoa.setPreferredSize(new Dimension(160, 30));
		btnLamMoi.setPreferredSize(new Dimension(160, 30));

		rightButtonPanel.add(btnThem);
		rightButtonPanel.add(btnSua);
		rightButtonPanel.add(btnXoa);
		rightButtonPanel.add(btnLamMoi);

		// Bottom Button Panel
		JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
		bottomButtonPanel.setBackground(Color.WHITE);
		bottomButtonPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

		JButton btnSave = createStyledButton("Lưu thông tin", PRIMARY_COLOR);
		JButton btnCancel = createStyledButton("Quay lại", new Color(255, 182, 193));

		btnSave.setPreferredSize(new Dimension(150, 40));
		btnCancel.setPreferredSize(new Dimension(150, 40));

		bottomButtonPanel.add(btnSave);
		bottomButtonPanel.add(btnCancel);

		// Main content panel
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.add(inputPanel, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBackground(Color.WHITE);
		centerPanel.add(tablePanel, BorderLayout.CENTER);
		centerPanel.add(rightButtonPanel, BorderLayout.EAST);

		contentPanel.add(centerPanel, BorderLayout.CENTER);

		// Add components to dialog
		add(headerLabel, BorderLayout.NORTH);
		add(contentPanel, BorderLayout.CENTER);
		add(bottomButtonPanel, BorderLayout.SOUTH);

		// Button Actions
		btnThem.addActionListener(e -> addVariant());
		btnSua.addActionListener(e -> editVariant());
		btnXoa.addActionListener(e -> deleteVariant());
		btnLamMoi.addActionListener(e -> clearFields());
		btnSave.addActionListener(e -> {
			isConfirmed = true;
			dispose();
		});
		btnCancel.addActionListener(e -> dispose());

		// Dialog settings
		setSize(900, 600);
		setLocationRelativeTo(getOwner());
	}

	private JPanel createInputGroup(String labelText, JTextField textField) {
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.setBackground(Color.WHITE);

		JLabel label = new JLabel(labelText);
		label.setFont(LABEL_FONT);

		panel.add(label, BorderLayout.NORTH);
		panel.add(textField, BorderLayout.CENTER);

		return panel;
	}

	private JTextField createStyledTextField(String placeholder) {
		JTextField textField = new JTextField();
		textField.setFont(CONTENT_FONT);
		textField.setPreferredSize(new Dimension(200, 35));
		textField.setBorder(
				BorderFactory.createCompoundBorder(new LineBorder(PRIMARY_COLOR, 1), new EmptyBorder(5, 10, 5, 10)));
		return textField;
	}

	private JButton createStyledButton(String text, Color bgColor) {
		JButton button = new JButton(text);
		button.setFont(CONTENT_FONT);
		button.setBackground(bgColor);
		button.setForeground(Color.WHITE);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setPreferredSize(new Dimension(120, 35));

		button.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				button.setBackground(bgColor.brighter());
			}

			public void mouseExited(MouseEvent e) {
				button.setBackground(bgColor);
			}
		});

		return button;
	}

	private JPanel createTablePanel() {
		// Create table model
		String[] columns = { "STT", "Mã PL", "Màu sắc", "Kích cỡ", "Số lượng" };
		tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		// Create table
		table = new JTable(tableModel);
		table.setFont(CONTENT_FONT);
		table.setRowHeight(35);
		table.getTableHeader().setFont(LABEL_FONT);
		table.getTableHeader().setBackground(Color.WHITE);
		table.setSelectionBackground(HOVER_COLOR);
		table.setSelectionForeground(Color.BLACK);
		table.setGridColor(new Color(245, 245, 245));

		// Create scroll pane
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(245, 245, 245)));

		// Create panel for table
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.add(scrollPane, BorderLayout.CENTER);
		panel.setBorder(new EmptyBorder(0, 20, 0, 10));

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int selectedRow = table.getSelectedRow();

				// Convert values to String safely
				txtMauSac.setText(String.valueOf(tableModel.getValueAt(selectedRow, 2)));
				txtKichCo.setText(String.valueOf(tableModel.getValueAt(selectedRow, 3)));
				txtTonKho.setText(String.valueOf(tableModel.getValueAt(selectedRow, 4)));
			}
		});

		return panel;
	}

	private boolean isDuplicateVariant(String color, String size, String variantId) {
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			String existingVariantId = String.valueOf(tableModel.getValueAt(i, 1));
			String existingColor = String.valueOf(tableModel.getValueAt(i, 2));
			String existingSize = String.valueOf(tableModel.getValueAt(i, 3));

			// Skip checking against itself when editing
			if (variantId != null && existingVariantId.equals(variantId)) {
				continue;
			}

			// Check if color and size combination already exists
			if (existingColor.equalsIgnoreCase(color) && existingSize.equalsIgnoreCase(size)) {
				JOptionPane.showMessageDialog(this, "Phân loại với màu sắc và kích thước này đã tồn tại", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
				return true;
			}
		}
		return false;
	}

	private void addVariant() {
		if (!validateInput()) {
			return;
		}

		String mauSac = txtMauSac.getText().trim();
		String kichCo = txtKichCo.getText().trim();

		// Check for duplicates
		if (isDuplicateVariant(mauSac, kichCo, null)) {
			return;
		}

		ProductVariant variant = new ProductVariant();
		variant.setProductID(productId);
		variant.setColor(mauSac);
		variant.setSize(kichCo);
		variant.setQuantity(Integer.parseInt(txtTonKho.getText().trim()));

		if (variantBUS.addVariant(variant)) {
			loadVariants();
			clearFields();
			JOptionPane.showMessageDialog(this, "Thêm phân loại thành công", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "Thêm phân loại thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void editVariant() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow < 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn phân loại cần sửa", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		if (!validateInput()) {
			return;
		}

		String variantId = String.valueOf(tableModel.getValueAt(selectedRow, 1));
		String mauSac = txtMauSac.getText().trim();
		String kichCo = txtKichCo.getText().trim();

		// Check for duplicates, excluding the current variant
		if (isDuplicateVariant(mauSac, kichCo, variantId)) {
			return;
		}

		ProductVariant variant = new ProductVariant();
		variant.setVariantID(variantId);
		variant.setProductID(productId);
		variant.setColor(mauSac);
		variant.setSize(kichCo);
		variant.setQuantity(Integer.parseInt(txtTonKho.getText().trim()));

		if (variantBUS.updateVariant(variant)) {
			loadVariants();
			clearFields();
			JOptionPane.showMessageDialog(this, "Cập nhật phân loại thành công", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "Cập nhật phân loại thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void deleteVariant() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow < 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn phân loại cần xóa", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa phân loại này?", "Xác nhận xóa",
				JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			String variantId = (String) tableModel.getValueAt(selectedRow, 1);
			if (variantBUS.deleteVariant(variantId, productId)) {
				loadVariants();
				clearFields();
				JOptionPane.showMessageDialog(this, "Xóa phân loại thành công", "Thông báo",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "Xóa phân loại thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private boolean validateInput() {
		String mauSac = txtMauSac.getText().trim();
		String kichCo = txtKichCo.getText().trim();
		String tonkho = txtTonKho.getText().trim();

		if (mauSac.isEmpty() || kichCo.isEmpty() || tonkho.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		try {
			int soLuong = Integer.parseInt(tonkho);
			if (soLuong < 0) {
				JOptionPane.showMessageDialog(this, "Số lượng tồn kho không được âm", "Lỗi", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Số lượng tồn kho phải là số", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		return true;
	}

	private void clearFields() {
		txtMauSac.setText("");
		txtKichCo.setText("");
		txtTonKho.setText("");
		table.clearSelection();
	}

	public boolean isConfirmed() {
		return isConfirmed;
	}

	// Methods to get variant data
	public Object[][] getVariantData() {
		int rowCount = tableModel.getRowCount();
		Object[][] data = new Object[rowCount][4]; // Excluding STT column

		for (int i = 0; i < rowCount; i++) {
			data[i][0] = tableModel.getValueAt(i, 1); // Màu sắc
			data[i][1] = tableModel.getValueAt(i, 2); // Kích cỡ
			data[i][2] = tableModel.getValueAt(i, 3); // Chất liệu
			data[i][3] = tableModel.getValueAt(i, 4); // Thương hiệu
		}

		return data;
	}

}