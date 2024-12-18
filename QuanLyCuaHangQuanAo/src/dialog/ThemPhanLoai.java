package dialog;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import bus.ProductVariantBUS;
import entity.ProductVariant;

public class ThemPhanLoai extends JDialog {
	private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
	private static final Color HOVER_COLOR = new Color(252, 231, 243);
	private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
	private static final Font CONTENT_FONT = new Font("Segoe UI", Font.PLAIN, 14);

	private JTextField txtMauSac, txtKichCo;
	private JTable table;
	private DefaultTableModel tableModel;
	private String productID;
	private ProductVariantBUS variantBUS;
	private boolean isConfirmed = false;

	public ThemPhanLoai(Frame owner, String productID) {
		super(owner, "Thêm Phân Loại Sản Phẩm", true);
		this.productID = productID;
		this.variantBUS = new ProductVariantBUS();
		initComponents();
		loadVariants();
	}

	private void initComponents() {
		setLayout(new BorderLayout(0, 20));
		setBackground(Color.WHITE);

		// Header Panel
		add(createHeaderPanel(), BorderLayout.NORTH);

		// Main Content Panel
		JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

		contentPanel.add(createInputPanel(), BorderLayout.NORTH);
		contentPanel.add(createTablePanel(), BorderLayout.CENTER);
		contentPanel.add(createButtonPanel(), BorderLayout.EAST);

		add(contentPanel, BorderLayout.CENTER);

		// Bottom Button Panel
		add(createBottomButtonPanel(), BorderLayout.SOUTH);

		setSize(900, 600);
		setLocationRelativeTo(getOwner());
	}

	private JPanel createHeaderPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.setBackground(Color.WHITE);

		JLabel title = new JLabel("THÊM PHÂN LOẠI SẢN PHẨM");
		title.setFont(HEADER_FONT);
		title.setForeground(PRIMARY_COLOR);
		panel.add(title);

		return panel;
	}

	private JPanel createInputPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
		panel.setBackground(Color.WHITE);

		txtMauSac = createStyledTextField("Màu sắc");
		txtKichCo = createStyledTextField("Kích cỡ");

		panel.add(createLabeledField("Màu sắc:", txtMauSac));
		panel.add(createLabeledField("Kích cỡ:", txtKichCo));

		return panel;
	}

	private JPanel createTablePanel() {
		String[] columns = { "STT", "Mã PL", "Kích cỡ", "Màu sắc", "Số lượng" };
		tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		table = new JTable(tableModel);
		setupTable();

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
	}

	private JPanel createButtonPanel() {
		JPanel panel = new JPanel(new GridLayout(4, 1, 0, 10));
		panel.setBackground(Color.WHITE);
		panel.setBorder(new EmptyBorder(0, 10, 0, 0));

		panel.add(createStyledButton("Thêm", PRIMARY_COLOR, e -> addVariant()));
		panel.add(createStyledButton("Sửa", new Color(124, 58, 237), e -> updateVariant()));
		panel.add(createStyledButton("Xóa", new Color(225, 29, 72), e -> deleteVariant()));
		panel.add(createStyledButton("Làm mới", new Color(104, 129, 57), e -> clearFields()));

		return panel;
	}

	private JPanel createBottomButtonPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		panel.setBackground(Color.WHITE);

		panel.add(createStyledButton("Lưu", PRIMARY_COLOR, e -> saveAndClose()));
		panel.add(createStyledButton("Hủy", new Color(156, 163, 175), e -> dispose()));

		return panel;
	}

	private void setupTable() {
		table.setFont(CONTENT_FONT);
		table.setRowHeight(35);
		table.getTableHeader().setFont(CONTENT_FONT);
		table.setSelectionBackground(HOVER_COLOR);
		table.setGridColor(new Color(245, 245, 245));

		table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				int row = table.getSelectedRow();
				if (row != -1) {
					loadVariantToFields(row);
				}
			}
		});
	}

	private void loadVariants() {
		tableModel.setRowCount(0);
		List<ProductVariant> variants = variantBUS.getAllVariantsByProductID(productID);

		for (int i = 0; i < variants.size(); i++) {
			ProductVariant variant = variants.get(i);
			tableModel.addRow(new Object[] { i + 1, variant.getVariantID(), variant.getSize(), variant.getColor(),0 });
		}
	}

	private void addVariant() {
		try {
			validateInputs();
			ProductVariant variant = createVariantFromInputs();

			if (variantBUS.addVariant(variant)) {
				loadVariants();
				clearFields();
				JOptionPane.showMessageDialog(this, "Thêm phân loại thành công!");
			} else {
				JOptionPane.showMessageDialog(this, "Thêm phân loại thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void updateVariant() {
		int row = table.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn phân loại cần sửa!");
			return;
		}

		try {
			validateInputs();
			ProductVariant variant = createVariantFromInputs();
			variant.setVariantID((String) tableModel.getValueAt(row, 1));

			if (variantBUS.updateVariant(variant)) {
				loadVariants();
				clearFields();
				JOptionPane.showMessageDialog(this, "Cập nhật phân loại thành công!");
			} else {
				JOptionPane.showMessageDialog(this, "Cập nhật phân loại thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void deleteVariant() {
		int row = table.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn phân loại cần xóa!");
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa phân loại này?", "Xác nhận xóa",
				JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			String variantID = (String) tableModel.getValueAt(row, 1);
			if (variantBUS.deleteVariant(variantID, productID)) {
				loadVariants();
				clearFields();
				JOptionPane.showMessageDialog(this, "Xóa phân loại thành công!");
			} else {
				JOptionPane.showMessageDialog(this, "Xóa phân loại thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void loadVariantToFields(int row) {
		txtMauSac.setText((String) tableModel.getValueAt(row, 2));
		txtKichCo.setText((String) tableModel.getValueAt(row, 3));
	}

	private ProductVariant createVariantFromInputs() {
		ProductVariant variant = new ProductVariant();
		variant.setProductID(productID);
		variant.setColor(txtMauSac.getText().trim());
		variant.setSize(txtKichCo.getText().trim());
		return variant;
	}

	private void validateInputs() {
		if (txtMauSac.getText().trim().isEmpty()) {
			throw new IllegalArgumentException("Vui lòng nhập màu sắc!");
		}
		if (txtKichCo.getText().trim().isEmpty()) {
			throw new IllegalArgumentException("Vui lòng nhập kích cỡ!");
		}

	
	}

	private void clearFields() {
		txtMauSac.setText("");
		txtKichCo.setText("");
		table.clearSelection();
	}

	private void saveAndClose() {
		isConfirmed = true;
		dispose();
	}

	private JTextField createStyledTextField(String placeholder) {
		JTextField field = new JTextField();
		field.setFont(CONTENT_FONT);
		field.setPreferredSize(new Dimension(150, 35));
		field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(PRIMARY_COLOR),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));
		return field;
	}

	private JPanel createLabeledField(String labelText, JTextField field) {
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.setBackground(Color.WHITE);

		JLabel label = new JLabel(labelText);
		label.setFont(CONTENT_FONT);

		panel.add(label, BorderLayout.NORTH);
		panel.add(field, BorderLayout.CENTER);

		return panel;
	}

	private JButton createStyledButton(String text, Color bgColor, ActionListener listener) {
		JButton button = new JButton(text);
		button.setFont(CONTENT_FONT);
		button.setBackground(bgColor);
		button.setForeground(Color.WHITE);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.addActionListener(listener);

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

	public boolean isConfirmed() {
		return isConfirmed;
	}
}