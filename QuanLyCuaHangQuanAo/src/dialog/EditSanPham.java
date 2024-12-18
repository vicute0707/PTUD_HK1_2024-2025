package dialog;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import bus.DanhMucBUS;
import bus.SanPhamBUS;
import component.ImageUploadUtil;
import entity.SanPham;
import entity.DanhMuc;
import entity.PhanLoaiSanPham;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class EditSanPham extends JDialog {
	private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
	private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
	private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
	private static final Font CONTENT_FONT = new Font("Segoe UI", Font.PLAIN, 14);

	private JTextField txtMauSac, txtKichCo, txtChatLieu;

	private static final Color PASTEL_PINK = new Color(255, 255, 255); // Màu hồng pastel nhẹ nhàng
	private static final Color DARK_PINK = new Color(219, 39, 119); // Màu hồng đậm cho các nút
	private static final Color LIGHT_PINK = new Color(252, 231, 243); // Màu hồng nhạt cho hover
	private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);

	private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
	private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

	private PhanLoaiSanPham plsp = new PhanLoaiSanPham();
	Frame owner;

	private static final String IMAGE_DIRECTORY = "src/img_product";
	private JTextField txtMa, txtTenSP, txtTonKho, txtThuongHieu;
	private JLabel lblImage;
	private JComboBox<String> cboDanhMuc;
	private String selectedImagePath = null;
	private String currentImageName = null;
	private boolean isConfirmed = false;
	private DefaultTableModel tableModel;
	private int selectedRow;
	private String maSP;
	private SanPhamBUS sanPhamBUS;
	private DanhMucBUS danhMucBUS;
	private static final Logger LOGGER = Logger.getLogger(EditSanPham.class.getName());

	public EditSanPham(Frame owner, DefaultTableModel tableModel, int selectedRow) {
		super(owner, "Sửa Sản Phẩm", true);
		danhMucBUS = new DanhMucBUS();
		this.tableModel = tableModel;
		this.selectedRow = selectedRow;
		this.sanPhamBUS = new SanPhamBUS();
		this.maSP = (String) tableModel.getValueAt(selectedRow, 0);
		initComponents();
		loadProductData();
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

	private JPanel createButtonPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
		panel.setBackground(Color.WHITE);
		JButton btnLuu = createStyledButton("Lưu", PRIMARY_COLOR);
		JButton btnAdd = createStyledButton("Sửa PL", PRIMARY_COLOR);
		JButton btnCancel = createStyledButton("Hủy", new Color(156, 163, 175));
		btnLuu.addActionListener(e -> saveChanges());
		btnAdd.addActionListener(e -> openPhanLoaiDialog());
		btnCancel.addActionListener(e -> dispose());
		panel.add(btnLuu);
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

	private JPanel createImagePanel() {
		JPanel panel = new JPanel(new BorderLayout(0, 10));
		panel.setBackground(Color.WHITE);
		panel.setBorder(new EmptyBorder(0, 0, 0, 20));

		lblImage = new JLabel();
		lblImage.setPreferredSize(new Dimension(200, 200));
		lblImage.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
		lblImage.setHorizontalAlignment(SwingConstants.CENTER);

		JButton btnChooseImage = createStyledButton("Chọn ảnh", PRIMARY_COLOR);
		btnChooseImage.addActionListener(e -> chooseImage());

		panel.add(lblImage, BorderLayout.CENTER);
		panel.add(btnChooseImage, BorderLayout.SOUTH);

		return panel;
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
		txtThuongHieu = createStyledTextField();
		txtTonKho = createStyledTextField();
		txtTonKho.setEditable(false);
		cboDanhMuc = new JComboBox<>();

		// Add components to panel
		addFormRow(panel, "Mã sản phẩm:", txtMa, 0);
		addFormRow(panel, "Tên sản phẩm:", txtTenSP, 1);
		addFormRow(panel, "Danh mục:", cboDanhMuc, 2);

		addFormRow(panel, "Số lượng tồn:", txtTonKho, 3);
		addFormRow(panel, "Thương hiệu:", txtThuongHieu, 4);

		return panel;
	}

	private void addFormRow(JPanel panel, String labelText, JComponent component, int row) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 5, 5, 15);

		JLabel label = new JLabel(labelText);
		label.setFont(LABEL_FONT);
		panel.add(label, gbc);

		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		panel.add(component, gbc);
	}

	private void loadProductData() {
		try {
			SanPham sp = sanPhamBUS.getSanPhamById(maSP);
			if (sp != null) {
				txtMa.setText(sp.getMaSP());
				txtTenSP.setText(sp.getTenSP());
				txtTonKho.setText(String.valueOf(sp.getSoLuongTonKho()));
				txtThuongHieu.setText(sp.getThuongHieu());

				// Load categories and select current one
				loadDanhMucComboBox();
				cboDanhMuc.setSelectedItem(sp.getDanhmuc().getTenDM());

				// Handle image display
				currentImageName = sp.getHinhAnh(); // Now stores just filename
				if (currentImageName != null && !currentImageName.isEmpty()) {
					displayImageFromName(currentImageName);
				}
			}
		} catch (Exception e) {
			LOGGER.severe("Error loading product data: " + e.getMessage());
			JOptionPane.showMessageDialog(this, "Error loading product data: " + e.getMessage());
		}
	}

	private void displayImageFromName(String imageName) {
		try {
			// Construct full path from image name
			Path imagePath = Paths.get(IMAGE_DIRECTORY, imageName);
			if (Files.exists(imagePath)) {
				ImageIcon imageIcon = new ImageIcon(imagePath.toString());
				Image image = imageIcon.getImage();
				Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
				lblImage.setIcon(new ImageIcon(scaledImage));
			} else {
				lblImage.setIcon(null);
				lblImage.setText("Image not found");
			}
		} catch (Exception e) {
			LOGGER.warning("Error displaying image: " + e.getMessage());
			lblImage.setIcon(null);
			lblImage.setText("No Image");
		}
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

	private void saveChanges() {
		 try {
		        if (!validateInput()) {
		            return;
		        }

		        // Start with current image name
		        String imageNameForDb = currentImageName;

		        // Only process image if a new one was selected
		        if (selectedImagePath != null && !selectedImagePath.isEmpty()) {
		            LOGGER.info("Processing new image from: " + selectedImagePath);
		            
		            // Pass the full file path to the update method
		            imageNameForDb = ImageUploadUtil.updateProductImage(
		                selectedImagePath,    // This should be the full path now
		                txtMa.getText(),      // Product ID
		                currentImageName      // Current image name in database
		            );
		            
		            LOGGER.info("Image update result: " + imageNameForDb);
		        }

		        // Create the product object with updated information
		        SanPham sp = new SanPham();
		        sp.setMaSP(txtMa.getText());
		        sp.setTenSP(txtTenSP.getText());
		        sp.setThuongHieu(txtThuongHieu.getText());
		        sp.setHinhAnh(selectedImagePath);  // Store the image name in database
		        sp.setSoLuongTonKho(Integer.parseInt(txtTonKho.getText()));

		        if (cboDanhMuc.getSelectedItem() != null) {
		            DanhMuc dm = danhMucBUS.getDanhMucByName(cboDanhMuc.getSelectedItem().toString());
		            sp.setDanhmuc(dm);
		        }

		        if (sanPhamBUS.updateSanPham(sp)) {
		            LOGGER.info("Product update successful with image: " + imageNameForDb);
		            JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thành công!");
		            isConfirmed = true;
		            dispose();
		        } else {
		            JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thất bại!");
		        }

		    } catch (Exception e) {
		        LOGGER.severe("Error in saveChanges: " + e.getMessage());
		        JOptionPane.showMessageDialog(this, "Lỗi khi lưu thay đổi: " + e.getMessage());
		    }
	}

	private boolean validateInput() {
		StringBuilder errorMessage = new StringBuilder("Please correct the following errors:\n");
		boolean isValid = true;

		// Validate product name
		if (txtTenSP.getText().trim().isEmpty()) {
			errorMessage.append("- Product name cannot be empty\n");
			isValid = false;
		}

		// Validate brand
		if (txtThuongHieu.getText().trim().isEmpty()) {
			errorMessage.append("- Brand cannot be empty\n");
			isValid = false;
		}

		// Validate category selection
		if (cboDanhMuc.getSelectedItem() == null) {
			errorMessage.append("- Please select a category\n");
			isValid = false;
		}

		// Show error message if validation failed
		if (!isValid) {
			JOptionPane.showMessageDialog(this, errorMessage.toString(), "Validation Error", JOptionPane.ERROR_MESSAGE);
		}

		return isValid;
	}

	private void chooseImage() {
	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
	        "Image Files", "jpg", "jpeg", "png"
	    ));

	    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
	        // Store the FULL PATH of the selected file
	        selectedImagePath = fileChooser.getSelectedFile().getAbsolutePath();;
	        
	        // Display the image preview
	        try {
	            ImageIcon imageIcon = new ImageIcon(selectedImagePath);
	            Image image = imageIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
	            lblImage.setIcon(new ImageIcon(image));
	            LOGGER.info("Selected image path: " + selectedImagePath);
	        } catch (Exception e) {
	            LOGGER.severe("Error previewing image: " + e.getMessage());
	            lblImage.setIcon(null);
	            lblImage.setText("Error loading image");
	        }
	    }
	}

	private void openPhanLoaiDialog() {
		// First verify we have a valid product ID
		String productId = txtMa.getText();
		if (productId == null || productId.trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Không thể mở phân loại - Mã sản phẩm không hợp lệ", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Before opening the variant dialog, we should save any changes to the main
		// product
		if (validateInput()) {
			// Create a product object with the current form data
			SanPham sp = new SanPham();
			sp.setMaSP(txtMa.getText());
			sp.setTenSP(txtTenSP.getText());
			sp.setThuongHieu(txtThuongHieu.getText());

			// Handle the image name instead of full path
			String imageName = currentImageName; // Use existing image by default
			if (selectedImagePath != null) {
				// Update the image and get the new filename
				File newImageFile = new File(selectedImagePath);
				imageName = ImageUploadUtil.updateProductImage(selectedImagePath, txtMa.getText(), currentImageName);
			}
			sp.setHinhAnh(selectedImagePath); // Store just the filename

			sp.setSoLuongTonKho(Integer.parseInt(txtTonKho.getText()));

			// Set the category if one is selected
			if (cboDanhMuc.getSelectedItem() != null) {
				DanhMuc dm = danhMucBUS.getDanhMucByName(cboDanhMuc.getSelectedItem().toString());
				sp.setDanhmuc(dm);
			}

			// Try to save the product changes first
			if (!sanPhamBUS.updateSanPham(sp)) {
				JOptionPane.showMessageDialog(this, "Cập nhật thông tin sản phẩm thất bại. Không thể mở phân loại.",
						"Lỗi", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		// If everything is saved successfully, open the variant dialog
		SuaPhanLoai dialog = new SuaPhanLoai((Frame) SwingUtilities.getWindowAncestor(this), productId);
		dialog.setVisible(true);

		// If changes were made in the variant dialog, refresh our display
		if (dialog.isConfirmed()) {
			loadProductData(); // Reload all product data
			isConfirmed = true; // Signal to parent that changes were made
		}
	}

	public boolean isConfirmed() {
		return isConfirmed;
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

}