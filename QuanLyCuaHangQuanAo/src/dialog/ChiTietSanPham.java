package dialog;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import bus.DanhMucBUS;
import bus.SanPhamBUS;
import entity.DanhMuc;
import entity.PhanLoaiSanPham;
import entity.SanPham;

import java.awt.*;
import java.awt.event.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class ChiTietSanPham extends JDialog {
	private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
	private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
	private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
	private static final Font CONTENT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
	private static final Color PASTEL_PINK = new Color(255, 255, 255); // Màu hồng pastel nhẹ nhàng
	private static final Color DARK_PINK = new Color(219, 39, 119); // Màu hồng đậm cho các nút
	private static final Color LIGHT_PINK = new Color(252, 231, 243); // Màu hồng nhạt cho hover
	private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);

	private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
	private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

	private PhanLoaiSanPham plsp = new PhanLoaiSanPham();
	Frame owner;
	private static final String IMAGE_DIRECTORY = "src/img_product";
	private static final Logger LOGGER = Logger.getLogger(ChiTietSanPham.class.getName());

	private JTextField txtMa, txtTenSP, txtTonKho, txtThuongHieu;
	private JComboBox<String> cboDanhMuc;
	private JLabel lblImage;
	private String currentImageName; // Store just the image filename
	private boolean isConfirmed = false;
	private DefaultTableModel tableModel;
	private int selectedRow;
	private String maSP;
	private SanPhamBUS sanPhamBUS;
	private DanhMucBUS danhMucBUS;

	public ChiTietSanPham(Frame owner, DefaultTableModel tableModel, int selectedRow) {
		super(owner, "Sửa Sản Phẩm", true);
		danhMucBUS = new DanhMucBUS();
		this.tableModel = tableModel;
		this.selectedRow = selectedRow;
		this.sanPhamBUS = new SanPhamBUS();
		this.maSP = (String) tableModel.getValueAt(selectedRow, 0);
		initComponents();
		loadProductData();
	}

	private void loadProductData() {
		try {
			SanPham sp = sanPhamBUS.getSanPhamById(maSP);
			if (sp != null) {
				// Load basic product information
				txtMa.setText(sp.getMaSP());
				txtTenSP.setText(sp.getTenSP());
				txtTonKho.setText(String.valueOf(sp.getSoLuongTonKho()));
				txtThuongHieu.setText(sp.getThuongHieu());

				// Load category
				loadDanhMucComboBox();
				cboDanhMuc.setSelectedItem(sp.getDanhmuc().getTenDM());

				// Handle image display
				currentImageName = sp.getHinhAnh(); // Now stores just filename
				if (currentImageName != null && !currentImageName.isEmpty()) {
					displayProductImage(currentImageName);
				}
			}
		} catch (Exception e) {
			LOGGER.severe("Error loading product data: " + e.getMessage());
			JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu sản phẩm: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void displayProductImage(String imageName) {
		try {
			// Construct the full path from the image name
			Path imagePath = Paths.get(IMAGE_DIRECTORY, imageName);

			if (Files.exists(imagePath)) {
				ImageIcon imageIcon = new ImageIcon(imagePath.toString());
				Image image = imageIcon.getImage();

				// Scale the image to fit the display area while maintaining aspect ratio
				int width = 200;
				int height = 200;

				// Calculate scaling dimensions
				double scale = Math.min((double) width / imageIcon.getIconWidth(),
						(double) height / imageIcon.getIconHeight());

				int scaledWidth = (int) (imageIcon.getIconWidth() * scale);
				int scaledHeight = (int) (imageIcon.getIconHeight() * scale);

				Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);

				// Center the image in the label
				lblImage.setIcon(new ImageIcon(scaledImage));
				lblImage.setHorizontalAlignment(SwingConstants.CENTER);
				lblImage.setVerticalAlignment(SwingConstants.CENTER);
			} else {
				// Show placeholder if image doesn't exist
				lblImage.setIcon(null);
				lblImage.setText("Image not found");
				LOGGER.warning("Product image not found: " + imagePath);
			}
		} catch (Exception e) {
			LOGGER.severe("Error displaying product image: " + e.getMessage());
			lblImage.setIcon(null);
			lblImage.setText("Cannot load image");
		}
	}

	private void initComponents() {
		setLayout(new BorderLayout(0, 20));
		setBackground(Color.WHITE);

		// Header Panel
		JPanel headerPanel = new JPanel();
		headerPanel.setBackground(Color.WHITE);
		JLabel headerLabel = new JLabel("CHỈNH SỬA SẢN PHẨM", SwingConstants.CENTER);
		headerLabel.setFont(HEADER_FONT);
		headerLabel.setForeground(PRIMARY_COLOR);
		headerPanel.add(headerLabel);

		// Main Content Panel
		JPanel mainPanel = new JPanel(new BorderLayout(20, 0));
		mainPanel.setBackground(Color.WHITE);
		mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

		// Left Panel - Image
		JPanel imagePanel = createImagePanel();

		// Right Panel - Form
		JPanel formPanel = createFormPanel();

		mainPanel.add(imagePanel, BorderLayout.WEST);
		mainPanel.add(formPanel, BorderLayout.CENTER);

		// Button Panel
		JPanel buttonPanel = createButtonPanel();

		// Add all panels to dialog
		add(headerPanel, BorderLayout.NORTH);
		add(mainPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		setSize(900, 600);
		setLocationRelativeTo(owner);
	}

	private JPanel createFormPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.WHITE);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		// Initialize components
		txtMa = createStyledTextField();
		txtMa.setEditable(false);
		txtTenSP = createStyledTextField();
		txtTenSP.setEditable(false);

		txtThuongHieu = createStyledTextField();
		txtThuongHieu.setEditable(false);
		txtTonKho = createStyledTextField();
		txtThuongHieu.setEditable(false);
		cboDanhMuc = new JComboBox<>();
		cboDanhMuc.setEditable(false);
		// Add components to panel
		addFormRow(panel, "Mã sản phẩm:", txtMa, 0);
		addFormRow(panel, "Tên sản phẩm:", txtTenSP, 1);
		addFormRow(panel, "Danh mục:", cboDanhMuc, 2);
		addFormRow(panel, "Số lượng tồn:", txtTonKho, 3);
		addFormRow(panel, "Thương hiệu:", txtThuongHieu, 4);

		return panel;
	}

	private JPanel createButtonPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
		panel.setBackground(Color.WHITE);

		JButton btnAdd = createStyledButton("Xem phân loại ", PRIMARY_COLOR);
		JButton btnCancel = createStyledButton("Hủy", new Color(156, 163, 175));
		btnAdd.addActionListener(e -> openPhanLoaiDialog());
		btnCancel.addActionListener(e -> dispose());
		panel.add(btnAdd);
		panel.add(btnCancel);
		return panel;
	}

	private JButton createStyledButton(String text, Color bgColor) {
		JButton button = new JButton(text);
		button.setFont(BUTTON_FONT);
		button.setPreferredSize(new Dimension(130, 40));
		button.setBackground(bgColor);
		button.setForeground(Color.WHITE);
		button.setBorderPainted(false);
		button.setFocusPainted(false);

		// Thêm hiệu ứng hover
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

	private void openPhanLoaiDialog() {
		String productId = txtMa.getText();
		if (productId == null || productId.trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Không thể mở phân loại - Mã sản phẩm không hợp lệ", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		// Then open the variant dialog
		XemChiTietPhanLoai dialog = new XemChiTietPhanLoai((Frame) SwingUtilities.getWindowAncestor(this), productId);

		dialog.setVisible(true);

		if (dialog.isConfirmed()) {
			isConfirmed = true; // Set confirmation flag for parent dialog
		}
	}

	private JPanel createImagePanel() {
		JPanel panel = new JPanel(new BorderLayout(0, 10));
		panel.setBackground(Color.WHITE);
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 20), new LineBorder(PRIMARY_COLOR)));

		// Create image label with fixed size
		lblImage = new JLabel("No Image", SwingConstants.CENTER);
		lblImage.setPreferredSize(new Dimension(200, 200));
		lblImage.setMinimumSize(new Dimension(200, 200));
		lblImage.setBackground(Color.WHITE);
		lblImage.setOpaque(true);
		lblImage.setFont(CONTENT_FONT);

		panel.add(lblImage, BorderLayout.CENTER);
		return panel;
	}

	private void displayImage(String path) {
		try {
			// Read and scale the image
			ImageIcon imageIcon = new ImageIcon(path);
			Image image = imageIcon.getImage();
			Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);

			// Set the scaled image to the label
			lblImage.setIcon(new ImageIcon(scaledImage));

		} catch (Exception e) {
			// If image loading fails, show a placeholder
			lblImage.setIcon(null);
			lblImage.setText("No Image Available");
			JOptionPane.showMessageDialog(this, "Could not load image: " + e.getMessage(), "Image Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void loadDanhMucComboBox() {
		try {
			// Clear existing items
			cboDanhMuc.removeAllItems();

			// Get categories from database through the BUS layer
			DanhMucBUS danhMucBUS = new DanhMucBUS();
			java.util.List<DanhMuc> danhMucList = danhMucBUS.getDanhSachDanhMuc();

			// Add categories to combo box
			for (DanhMuc dm : danhMucList) {
				cboDanhMuc.addItem(dm.getTenDM());
			}

			// Style the combo box
			cboDanhMuc.setFont(CONTENT_FONT);
			cboDanhMuc.setBackground(Color.WHITE);
			cboDanhMuc.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(PRIMARY_COLOR),
					BorderFactory.createEmptyBorder(5, 10, 5, 10)));

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(), "Database Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void addFormRow(JPanel panel, String labelText, JComponent component, int row) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(8, 5, 8, 15);

		JLabel label = new JLabel(labelText);
		label.setFont(LABEL_FONT);
		label.setForeground(Color.BLACK);
		panel.add(label, gbc);

		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		panel.add(component, gbc);
	}

	private JTextField createStyledTextField() {
		JTextField textField = new JTextField(20);
		textField.setFont(INPUT_FONT);
		textField.setPreferredSize(new Dimension(250, 35));
		textField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(DARK_PINK),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));
		textField.setBackground(Color.WHITE);
		return textField;
	}

}