package dialog;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import bus.ProductVariantBUS;
import entity.PhanLoaiSanPham;
import entity.ProductVariant;

import java.awt.*;
import java.awt.event.*;

public class XemChiTietPhanLoai extends JDialog {
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

	public XemChiTietPhanLoai(Frame owner, String maSP) {
		super(owner, "Chỉnh Sửa Phân Loại Sản Phẩm", true);
		this.productId = maSP;
		this.variantBUS = new ProductVariantBUS();
		initComponents();
		loadVariants(); // Load variants for the specific product
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

		

		// Table Panel
		JPanel tablePanel = createTablePanel();

		JPanel rightButtonPanel = new JPanel(new GridLayout(4, 1, 0, 5)); // Reduce vertical space
		rightButtonPanel.setBackground(Color.WHITE);
		rightButtonPanel.setBorder(new EmptyBorder(0, 10, 0, 20));

		// Bottom Button Panel
		JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
		bottomButtonPanel.setBackground(Color.WHITE);
		bottomButtonPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

		JButton btnCancel = createStyledButton("Quay lại", new Color(255, 182, 193));

		btnCancel.setPreferredSize(new Dimension(150, 40));

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

		btnCancel.addActionListener(e -> dispose());

		// Dialog settings
		setSize(900, 600);
		setLocationRelativeTo(getOwner());
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

	public boolean isConfirmed() {
		return isConfirmed;
	}

}