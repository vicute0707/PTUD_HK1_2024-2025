package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.*;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

import bus.DanhMucBUS;
import bus.SanPhamBUS;
import component.ImagePanel;
import component.ProductCardImageLoader;
import entity.DanhMuc;
import entity.NhaCC;
import entity.SanPham;
import entity.UserSession;
import service.PermissionChecker;

public class Form_DanhMuc extends JPanel {
	private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
	private static final Color CONTENT_COLOR = new Color(255, 192, 203);
	private static final Color HOVER_COLOR = new Color(252, 231, 243);
	private static final Font HEADER_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12);
	private static final Font CONTENT_FONT = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12);
	private static final Font TITLE_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 14);

	private int selectedRow = -1;
	private JTable table;
	private DefaultTableModel tableModel;
	private JTextField searchField;
	private JPanel rightPanel;
	private JPanel productsPanel;
	private static final Logger LOGGER = Logger.getLogger(Form_DanhMuc.class.getName());
	private DanhMucBUS danhMucBUS;
	private SanPhamBUS sanPhamBUS;
	private static final String IMAGE_DIRECTORY = "src/img_product";
	private static final String DEFAULT_IMAGE_NAME = "default-product.jpg";

	public Form_DanhMuc() {
		danhMucBUS = new DanhMucBUS();
		sanPhamBUS = new SanPhamBUS();

		initComponents();
		danhMucBUS.hienThiDanhMuc(tableModel);

	}

	private void initComponents() {
		// Set up the main panel layout with a 10-pixel horizontal gap
		setLayout(new BorderLayout(10, 0));
		setBackground(Color.WHITE);
		// Add padding around the entire form
		setBorder(new EmptyBorder(30, 30, 30, 30));

		// Create the main panel that will contain the category table
		// Using BorderLayout for flexible resizing and 20-pixel gaps between components
		JPanel mainPanel = new JPanel(new BorderLayout(20, 0));
		mainPanel.setBackground(Color.WHITE);
		// Set the main panel to take up 70% of the total width
		mainPanel.setPreferredSize(new Dimension(getWidth() * 7 / 10, getHeight()));

		// Create the left panel for category management
		JPanel leftPanel = new JPanel(new BorderLayout(0, 20));
		leftPanel.setBackground(Color.WHITE);

		// Add the top control panel (search and buttons) and table to the left panel
		leftPanel.add(createTopPanel(), BorderLayout.NORTH);
		leftPanel.add(createTablePanel(), BorderLayout.CENTER);
		mainPanel.add(leftPanel, BorderLayout.CENTER);

		// Create the right panel for displaying products
		rightPanel = new JPanel(new BorderLayout());
		rightPanel.setBackground(Color.WHITE);
		// Add a subtle border to separate it from the main panel
		rightPanel.setBorder(BorderFactory.createLineBorder(new Color(245, 245, 245)));
		// Set the right panel to a fixed width of 320 pixels
		rightPanel.setPreferredSize(new Dimension(320, getHeight()));

		// Create the title panel for the products section
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setBackground(Color.WHITE);
		titlePanel.setBorder(new EmptyBorder(15, 20, 15, 20));

		// Add the "Danh sách sản phẩm" title with custom font
		JLabel titleLabel = new JLabel("Danh sách sản phẩm");
		titleLabel.setFont(TITLE_FONT);
		titlePanel.add(titleLabel, BorderLayout.WEST);

		// Create the panel that will hold the product cards
		productsPanel = new JPanel();
		productsPanel.setBackground(Color.WHITE);
		// Use BoxLayout for vertical stacking of product cards
		productsPanel.setLayout(new BoxLayout(productsPanel, BoxLayout.Y_AXIS));
		productsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Create a scroll pane for the products panel
		JScrollPane scrollPane = new JScrollPane(productsPanel);
		scrollPane.setBorder(null); // Remove the default border
		// Prevent horizontal scrolling since cards will have fixed width
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		// Add custom styling to the scroll bar
		scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());

		// Assemble the right panel
		rightPanel.add(titlePanel, BorderLayout.NORTH);
		rightPanel.add(scrollPane, BorderLayout.CENTER);

		// Add both main panels to the form
		add(mainPanel, BorderLayout.CENTER);
		add(rightPanel, BorderLayout.EAST);
	}

	private JPanel createTopPanel() {
		JPanel topPanel = new JPanel(new BorderLayout(20, 0));
		topPanel.setBackground(Color.WHITE);

		// Get current user for permission checking
		String currentUserId = UserSession.getInstance().getCurrentUser().getUserID();

		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
		searchPanel.setBackground(Color.WHITE);

		searchField = new JTextField();
		searchField.setPreferredSize(new Dimension(200, 35));
		searchField.setFont(CONTENT_FONT);

		// Thêm KeyListener để tìm kiếm khi gõ
		searchField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String keyword = searchField.getText().trim();
				performSearch(keyword);
				updateProductsForSearch(keyword);
			}
		});

		// Thêm placeholder
		searchField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (searchField.getText().equals("Tìm kiếm danh mục...")) {
					searchField.setText("");
					searchField.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (searchField.getText().isEmpty()) {
					searchField.setText("Tìm kiếm danh mục...");
					searchField.setForeground(Color.GRAY);
				}
			}
		});

		searchField.setText("Tìm kiếm danh mục...");
		searchField.setForeground(Color.GRAY);

		// Border và padding cho search field
		searchField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(230, 230, 230), 1, true),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));

		searchPanel.add(searchField);

		// Actions panel
		JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
		actionsPanel.setBackground(Color.WHITE);

		// Add Category Button
		JButton addButton = createRoundedButton("Thêm danh mục", "/icon/circle-plus.png", true);
		addButton.setBackground(PRIMARY_COLOR);
		addButton.setForeground(Color.WHITE);
		addButton.setPreferredSize(new Dimension(160, 38));
		configureButtonWithPermission(addButton, currentUserId, PermissionChecker.PERM_PRODUCT_MANAGEMENT, () -> {
			showAddCategoryDialog();
		});

		// Edit Button
		JButton editButton = createRoundedButton("Edit", "/icon/pencil.png", true);
		configureButtonWithPermission(editButton, currentUserId, PermissionChecker.PERM_PRODUCT_MANAGEMENT, () -> {
			if (selectedRow == -1) {
				JOptionPane.showMessageDialog(this, "Vui lòng chọn một danh mục để chỉnh sửa.", "Thông báo",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			showEditCategoryDialog();
		});

		// Delete Button
		JButton deleteButton = createRoundedButton("Xóa", "/icon/trash.png", true);
		configureButtonWithPermission(deleteButton, currentUserId, PermissionChecker.PERM_PRODUCT_MANAGEMENT, () -> {
			if (selectedRow == -1) {
				JOptionPane.showMessageDialog(this, "Vui lòng chọn một danh mục để xóa.", "Thông báo",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			showDeleteConfirmDialog();
		});

		actionsPanel.add(addButton);
		actionsPanel.add(editButton);
		actionsPanel.add(deleteButton);

		topPanel.add(searchPanel, BorderLayout.WEST);
		topPanel.add(actionsPanel, BorderLayout.EAST);

		return topPanel;
	}

	private void updateProductsForSearch(String searchText) {
		if (searchText == null || searchText.trim().isEmpty() || searchText.equals("Tìm kiếm danh mục...")) {
			productsPanel.removeAll();
			productsPanel.revalidate();
			productsPanel.repaint();
			return;
		}

		try {
			List<SanPham> searchResults = sanPhamBUS.searchProductsByName(searchText);
			productsPanel.removeAll();

			if (searchResults.isEmpty()) {
				JLabel noResultsLabel = new JLabel("Không tìm thấy sản phẩm");
				noResultsLabel.setFont(CONTENT_FONT);
				noResultsLabel.setForeground(Color.GRAY);
				noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
				productsPanel.add(noResultsLabel);
			} else {
				for (SanPham product : searchResults) {
					JPanel productCard = createProductCard(product);
					productCard.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							showProductDetails(product);
						}
					});
					productsPanel.add(productCard);
				}
			}

			productsPanel.revalidate();
			productsPanel.repaint();
		} catch (Exception e) {
			LOGGER.severe("Error searching products: " + e.getMessage());
			JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm sản phẩm: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void showProductDetails(SanPham product) {
		JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi tiết sản phẩm", true);
		dialog.setLayout(new BorderLayout(10, 10));
		dialog.setSize(400, 500);
		dialog.setLocationRelativeTo(null);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		contentPanel.setBackground(Color.WHITE);

		// Load and display product image
		try {
			String imageName = product.getHinhAnh();
			Path imagePath = Paths.get(IMAGE_DIRECTORY, imageName != null ? imageName : DEFAULT_IMAGE_NAME);

			if (Files.exists(imagePath)) {
				BufferedImage img = ImageIO.read(imagePath.toFile());
				Image scaledImg = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
				JLabel imageLabel = new JLabel(new ImageIcon(scaledImg));
				imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
				contentPanel.add(imageLabel);
			} else {
				JLabel noImageLabel = new JLabel("No Image Available");
				noImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
				contentPanel.add(noImageLabel);
			}
		} catch (IOException e) {
			LOGGER.warning("Error loading product image: " + e.getMessage());
			JLabel errorLabel = new JLabel("Error loading image");
			errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			contentPanel.add(errorLabel);
		}

		// Add product information
		contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		JLabel nameLabel = new JLabel(product.getTenSP());
		nameLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 16));
		nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel quantityLabel = new JLabel("Số lượng: " + product.getSoLuongTonKho());
		quantityLabel.setFont(CONTENT_FONT);
		quantityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		contentPanel.add(nameLabel);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		contentPanel.add(quantityLabel);

		dialog.add(contentPanel);
		dialog.setVisible(true);
	}

	private String truncateText(String text, int limit) {
		if (text.length() <= limit)
			return text;
		return text.substring(0, limit - 3) + "...";
	}

	// Thêm phương thức configureButtonWithPermission
	private void configureButtonWithPermission(JButton button, String userId, String permission, Runnable action) {
		// Check if user has the required permission
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
		}
	}

	// Tách các dialog thành các phương thức riêng
	private void showAddCategoryDialog() {
		JTextField nameField = new JTextField();
        JTextField noteField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Tên danh mục:"));
        panel.add(nameField);
        panel.add(new JLabel("Ghi chú:"));
        panel.add(noteField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm danh mục",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String note = noteField.getText().trim();
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên danh mục không được để trống!");
                return;
            }

            if (danhMucBUS.themDanhMuc(name, note)) {
                loadCategoryData();
                JOptionPane.showMessageDialog(this, "Thêm danh mục thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Thêm danh mục thất bại!");
            }
        }
	}

	private void showEditCategoryDialog() {
		if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục cần sửa!");
            return;
        }

        String maDM = (String) table.getValueAt(selectedRow, 0);
        String currentName = (String) table.getValueAt(selectedRow, 1);
        String currentNote = (String) table.getValueAt(selectedRow, 2);

        JTextField nameField = new JTextField(currentName);
        JTextField noteField = new JTextField(currentNote);

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Tên danh mục:"));
        panel.add(nameField);
        panel.add(new JLabel("Ghi chú:"));
        panel.add(noteField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Sửa danh mục",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newName = nameField.getText().trim();
            String newNote = noteField.getText().trim();

            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên danh mục không được để trống!");
                return;
            }

            if (danhMucBUS.suaDanhMuc(maDM, newName, newNote)) {
                loadCategoryData();
                JOptionPane.showMessageDialog(this, "Cập nhật danh mục thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật danh mục thất bại!");
            }
        }
	}

	private void handleEditCategory(String newName, String newNote) {
		try {
			if (newName == null || newName.trim().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Tên danh mục không được để trống.", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Cập nhật dữ liệu trong bảng
			tableModel.setValueAt(newName, selectedRow, 1);
			tableModel.setValueAt(newNote, selectedRow, 2);

			// Log và thông báo
			LOGGER.info("Đã cập nhật danh mục: " + tableModel.getValueAt(selectedRow, 0));
			JOptionPane.showMessageDialog(this, "Cập nhật danh mục thành công!");

		} catch (Exception e) {
			LOGGER.severe("Lỗi cập nhật danh mục: " + e.getMessage());
			JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật danh mục: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void handleDeleteCategory(String maDM) {
		try {
			// Xóa dòng khỏi bảng
			tableModel.removeRow(selectedRow);

			// Log và thông báo
			LOGGER.info("Đã xóa danh mục: " + maDM);
			JOptionPane.showMessageDialog(this, "Xóa danh mục thành công!", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);

			// Reset selection
			selectedRow = -1;

		} catch (Exception e) {
			LOGGER.severe("Lỗi xóa danh mục: " + e.getMessage());
			JOptionPane.showMessageDialog(this, "Lỗi khi xóa danh mục: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void showDeleteConfirmDialog() {
		if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục cần xóa!");
            return;
        }

        String maDM = (String) table.getValueAt(selectedRow, 0);
        String tenDM = (String) table.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa danh mục '" + tenDM + "'?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (danhMucBUS.xoaDanhMuc(maDM)) {
                loadCategoryData();
                JOptionPane.showMessageDialog(this, "Xóa danh mục thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Xóa danh mục thất bại!");
            }
        }
	}

    private void loadCategoryData() {
        tableModel.setRowCount(0);
        danhMucBUS.refreshData();
        for (DanhMuc dm : danhMucBUS.getDanhSachDanhMuc()) {
            tableModel.addRow(new Object[]{dm.getMaDM(), dm.getTenDM(), dm.getGhiChu()});
        }
        selectedRow = -1;
    }

	// Trong các phương thức xử lý
	private void handleAddCategory(String name, String note) {
		try {
			if (name == null || name.trim().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Tên danh mục không được để trống.");
				return;
			}
			String maDM = "CAT" + String.format("%03d", tableModel.getRowCount() + 1);

			// Thêm logging
			LOGGER.info("Adding new category: " + maDM + " - " + name);

			Object[] rowData = { maDM, name, note };
			tableModel.addRow(rowData);
			JOptionPane.showMessageDialog(this, "Thêm Danh Mục thành công");
		} catch (Exception e) {
			LOGGER.severe("Error adding category: " + e.getMessage());
			JOptionPane.showMessageDialog(this, "Lỗi khi thêm danh mục: " + e.getMessage());
		}
	}

	private JPanel createTablePanel() {
		JPanel tablePanel = new JPanel(new BorderLayout(0, 15));
		tablePanel.setBackground(Color.WHITE);

		String[] columns = { "Mã DM", "Tên danh mục", "Ghi chú" };
		tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 2;
			}
		};

		table = new JTable(tableModel);
		table.setFont(CONTENT_FONT);
		table.setRowHeight(32);
		table.setGridColor(new Color(245, 245, 245));
		table.setSelectionBackground(HOVER_COLOR);
		table.setSelectionForeground(Color.BLACK);
		table.setShowVerticalLines(true);
		table.setShowHorizontalLines(true);
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottomPanel.setBackground(Color.WHITE);
		// Căn giữa nội dung và điều chỉnh độ rộng cột
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);

		for (int i = 0; i < table.getColumnCount(); i++) {
			TableColumn column = table.getColumnModel().getColumn(i);
			column.setCellRenderer(centerRenderer);

			switch (i) {
			case 0: // Mã DM
				column.setPreferredWidth(80);
				column.setMaxWidth(80);
				column.setMinWidth(80);
				break;
			case 1: // Tên danh mục
				column.setPreferredWidth(150);
				break;
			case 2: // Ghi chú
				column.setPreferredWidth(200);
				break;
			}
		}

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				selectedRow = table.getSelectedRow();
				if (selectedRow != -1) {
					String categoryId = (String) table.getValueAt(selectedRow, 0);
					searchField.setText((String) tableModel.getValueAt(selectedRow, 1));
					updateProductsPanel(categoryId); // Add this line
				}
			}
		});
		JButton refreshButton = createRoundedButton("Làm mới", "", true);
		refreshButton.setPreferredSize(new Dimension(130, 38));

		// Get current user for permission checking
		String currentUserId = UserSession.getInstance().getCurrentUser().getUserID();

		// Configure refresh button with permission
		configureButtonWithPermission(refreshButton, currentUserId, PermissionChecker.PERM_PRODUCT_MANAGEMENT, () -> {
			refreshTableData();
		});

		bottomPanel.add(refreshButton);

		// Header styling
		JTableHeader header = table.getTableHeader();
		header.setFont(HEADER_FONT);
		header.setBackground(Color.WHITE);
		header.setForeground(Color.BLACK);
		header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
		((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

		// Scroll pane
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(245, 245, 245)));
		scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());

		tablePanel.add(scrollPane, BorderLayout.CENTER);
		tablePanel.add(bottomPanel, BorderLayout.SOUTH);

		return tablePanel;
	}

	private void updateProductsPanel(String categoryId) {
		// Clear existing products
		productsPanel.removeAll();

		if (categoryId == null || categoryId.trim().isEmpty()) {
			productsPanel.revalidate();
			productsPanel.repaint();
			return;
		}

		try {
			// Get products for the selected category
			List<SanPham> products = sanPhamBUS.getSanPhamByCategory(categoryId);

			if (products.isEmpty()) {
				// Show "no products" message if category is empty
				JLabel emptyLabel = new JLabel("Không có sản phẩm trong danh mục này");
				emptyLabel.setFont(CONTENT_FONT);
				emptyLabel.setForeground(Color.GRAY);
				emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
				productsPanel.add(emptyLabel);
			} else {
				// Add product cards
				for (SanPham product : products) {
					JPanel productCard = createProductCard(product);
					productsPanel.add(productCard);

					// Add some vertical spacing between cards
					productsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
				}
			}

			// Update the panel
			productsPanel.revalidate();
			productsPanel.repaint();

		} catch (Exception e) {
			LOGGER.severe("Error loading products: " + e.getMessage());
			JOptionPane.showMessageDialog(this, "Lỗi khi tải sản phẩm: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private JPanel createProductCard(SanPham product) {
	    // Main card panel
	    JPanel card = new JPanel(new BorderLayout(10, 10));
	    card.setBackground(Color.WHITE);
	    card.setBorder(BorderFactory.createCompoundBorder(
	        new LineBorder(new Color(245, 245, 245)),
	        new EmptyBorder(10, 10, 10, 10)
	    ));
	    card.setMaximumSize(new Dimension(280, 100));
	    card.setPreferredSize(new Dimension(280, 100));

	    // Image section
	    JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	    imagePanel.setBackground(Color.WHITE);
	    
	    // Load and display image
	    ImageIcon productImage = ProductCardImageLoader.loadProductImage(product.getHinhAnh(), 80, 80);
	    JLabel imageLabel;
	    if (productImage != null) {
	        imageLabel = new JLabel(productImage);
	    } else {
	        imageLabel = new JLabel("No Image");
	        imageLabel.setPreferredSize(new Dimension(80, 80));
	        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
	        imageLabel.setBorder(new LineBorder(Color.LIGHT_GRAY));
	    }
	    imagePanel.add(imageLabel);

	    // Product information section
	    JPanel infoPanel = new JPanel();
	    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
	    infoPanel.setBackground(Color.WHITE);

	    // Product name
	    JLabel nameLabel = new JLabel(truncateText(product.getTenSP(), 25));
	    nameLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 13));
	    nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
	    
	    // Product quantity
	    JLabel quantityLabel = new JLabel("Số lượng: " + product.getSoLuongTonKho());
	    quantityLabel.setFont(CONTENT_FONT);
	    quantityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
	    quantityLabel.setForeground(getQuantityColor(product.getSoLuongTonKho()));

	    // Add components to info panel
	    infoPanel.add(nameLabel);
	    infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
	    infoPanel.add(quantityLabel);

	    // Add hover effect
	    addCardHoverEffect(card, imagePanel, infoPanel);

	    // Assemble the card
	    card.add(imagePanel, BorderLayout.WEST);
	    card.add(infoPanel, BorderLayout.CENTER);

	    return card;
	}
	private Color getQuantityColor(int quantity) {
	    if (quantity <= 10) return new Color(220, 38, 38);      // Red for low stock
	    if (quantity <= 30) return new Color(234, 88, 12);      // Orange for medium stock
	    return Color.BLACK;                                      // Black for normal stock
	}

	private void addCardHoverEffect(JPanel card, JPanel... panels) {
	    MouseAdapter hover = new MouseAdapter() {
	        @Override
	        public void mouseEntered(MouseEvent e) {
	            card.setBackground(HOVER_COLOR);
	            for (JPanel panel : panels) {
	                panel.setBackground(HOVER_COLOR);
	            }
	        }

	        @Override
	        public void mouseExited(MouseEvent e) {
	            card.setBackground(Color.WHITE);
	            for (JPanel panel : panels) {
	                panel.setBackground(Color.WHITE);
	            }
	        }
	    };

	    card.addMouseListener(hover);
	    for (JPanel panel : panels) {
	        panel.addMouseListener(hover);
	    }
	}


	private JPanel createProductInfoPanel(SanPham product) {
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

		// Product name with proper truncation and tooltip
		JLabel nameLabel = new JLabel(truncateText(product.getTenSP(), 25));
		nameLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 13));
		nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		nameLabel.setToolTipText(product.getTenSP());

		// Quantity display with color coding
		JLabel quantityLabel = createQuantityLabel(product.getSoLuongTonKho());
		quantityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// Add components to info panel
		infoPanel.add(nameLabel);
		infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		infoPanel.add(quantityLabel);

		return infoPanel;
	}

	private JLabel createQuantityLabel(int quantity) {
		JLabel label = new JLabel("Số lượng: " + quantity);
		label.setFont(CONTENT_FONT);

		// Color coding based on stock levels
		if (quantity <= 10) {
			label.setForeground(new Color(220, 38, 38)); // Red for critically low stock
		} else if (quantity <= 30) {
			label.setForeground(new Color(234, 88, 12)); // Orange for low stock
		} else {
			label.setForeground(Color.BLACK); // Default color for normal stock
		}

		return label;
	}

	private void addHoverEffect(JPanel card, JPanel imageContainer, JPanel infoPanel) {
		MouseAdapter hoverAdapter = new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				card.setBackground(HOVER_COLOR);
				imageContainer.setBackground(HOVER_COLOR);
				infoPanel.setBackground(HOVER_COLOR);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				card.setBackground(Color.WHITE);
				imageContainer.setBackground(Color.WHITE);
				infoPanel.setBackground(Color.WHITE);
			}
		};

		// Apply hover effect to all components
		card.addMouseListener(hoverAdapter);
		imageContainer.addMouseListener(hoverAdapter);
		infoPanel.addMouseListener(hoverAdapter);
	}

	private void refreshTableData() {
		danhMucBUS.hienThiDanhMuc(tableModel);
		tableModel.setRowCount(0);
		selectedRow = -1;
		searchField.setText("");

		// Refresh and reload data through BUS layer
		danhMucBUS.refreshData();

		// Load danh mục từ BUS layer vào table
		for (DanhMuc dm : danhMucBUS.getDanhSachDanhMuc()) {
			tableModel.addRow(new Object[] { dm.getMaDM(), dm.getTenDM(), dm.getGhiChu() });
		}

		// Clear products panel
		productsPanel.removeAll();
		productsPanel.revalidate();
		productsPanel.repaint();

	}

	private JButton createRoundedButton(String text, String iconPath, boolean isRounded) {
		JButton button = new JButton(text);
		button.setFont(CONTENT_FONT);
		if (iconPath != null && !iconPath.isEmpty()) {
			button.setIcon(new ImageIcon(getClass().getResource(iconPath)));
		}

		if (isRounded) {
			button.setBorder(new LineBorder(new Color(230, 230, 230), 1, true));
		} else {
			button.setBorder(BorderFactory.createEmptyBorder());
		}

		button.setFocusPainted(false);
		button.setContentAreaFilled(true);
		button.setBackground(Color.WHITE);
		button.setPreferredSize(new Dimension(text.isEmpty() ? 38 : 130, 38));

		// Add hover effect
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (!button.getForeground().equals(Color.WHITE)) {
					button.setBackground(HOVER_COLOR);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (button.getForeground().equals(Color.WHITE)) {
					button.setBackground(PRIMARY_COLOR);
				} else {
					button.setBackground(Color.WHITE);
				}
			}
		});

		return button;
	}

	private class CustomScrollBarUI extends BasicScrollBarUI {
		@Override
		protected void configureScrollBarColors() {
			this.thumbColor = CONTENT_COLOR;
			this.trackColor = Color.WHITE;
		}

		@Override
		protected JButton createDecreaseButton(int orientation) {
			return createZeroButton();
		}

		@Override
		protected JButton createIncreaseButton(int orientation) {
			return createZeroButton();
		}

		@Override
		protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setPaint(thumbColor);
			g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
			g2.dispose();
		}

		@Override
		protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setPaint(trackColor);
			if (this.scrollbar.getOrientation() == JScrollBar.VERTICAL) {
				g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
			} else {
				g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
			}
			g2.dispose();
		}

		private JButton createZeroButton() {
			JButton button = new JButton();
			button.setPreferredSize(new Dimension(0, 0));
			button.setMinimumSize(new Dimension(0, 0));
			button.setMaximumSize(new Dimension(0, 0));
			return button;
		}
	}

	private void performSearch(String searchText) {
		if (searchText == null || searchText.trim().isEmpty()) {
			// Nếu từ khóa tìm kiếm rỗng, hiển thị lại toàn bộ dữ liệu
			refreshTableData();
			return;
		}

		// Clear table hiện tại
		tableModel.setRowCount(0);

		// Lấy dữ liệu tìm kiếm từ BUS
		for (DanhMuc dm : danhMucBUS.timKiemDanhMuc(searchText)) {
			tableModel.addRow(new Object[] { dm.getMaDM(), dm.getTenDM(), dm.getGhiChu() });
		}
	}

	// Các phương thức getter và utility
	public JTable getTable() {
		return table;
	}

	public DefaultTableModel getTableModel() {
		return tableModel;
	}

	public int getSelectedRow() {
		return selectedRow;
	}

	// Phương thức refresh
	public void refreshProductsPanel() {
		if (selectedRow != -1) {
			String category = (String) table.getValueAt(selectedRow, 0);
		}
	}

	public class TimDanhMuc extends JDialog {
		private static final long serialVersionUID = 1L;
		private final JPanel contentPanel = new JPanel();
		private JTable table;
		private DefaultTableModel tableModel;
		private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
		private static final Color CONTENT_COLOR = new Color(255, 192, 203);
		private static final Color HOVER_COLOR = new Color(252, 231, 243);
		private static final Font HEADER_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12);
		private static final Font CONTENT_FONT = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12);
		private static final Font TITLE_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 14);

		/**
		 * Launch the application.
		 */

		/**
		 * Create the dialog.
		 */
		public TimDanhMuc() {
			setTitle("Tìm Danh Mục");
			setBounds(100, 100, 600, 300);
			getContentPane().setLayout(new BorderLayout());

			contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(contentPanel, BorderLayout.CENTER);
			contentPanel.setLayout(new BorderLayout(0, 0));

			contentPanel.add(createTablePanel(), BorderLayout.CENTER);

			// Optional: Add a button panel (for example, a Close button)
			JPanel buttonPanel = new JPanel();
			contentPanel.add(buttonPanel, BorderLayout.SOUTH);

			JButton closeButton = new JButton("Close");
			closeButton.addActionListener(e -> dispose());
			buttonPanel.add(closeButton);
		}

		private JPanel createTablePanel() {
			JPanel tablePanel = new JPanel(new BorderLayout(0, 15));
			tablePanel.setBackground(Color.WHITE);

			String[] columns = { "Mã DM", "Tên danh mục", "Ghi chú" };
			tableModel = new DefaultTableModel(columns, 0) {
				@Override
				public boolean isCellEditable(int row, int column) {
					return column == 2; // Only the "Ghi chú" column is editable
				}
			};

			table = new JTable(tableModel);
			table.setFont(CONTENT_FONT);
			table.setRowHeight(32);
			table.setGridColor(new Color(245, 245, 245));
			table.setSelectionBackground(HOVER_COLOR);
			table.setSelectionForeground(Color.BLACK);
			table.setShowVerticalLines(true);
			table.setShowHorizontalLines(true);

			// Center-align content and adjust column width
			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment(JLabel.CENTER);

			for (int i = 0; i < table.getColumnCount(); i++) {
				TableColumn column = table.getColumnModel().getColumn(i);
				column.setCellRenderer(centerRenderer);

				switch (i) {
				case 0: // Mã DM
					column.setPreferredWidth(80);
					column.setMaxWidth(80);
					column.setMinWidth(80);
					break;
				case 1: // Tên danh mục
					column.setPreferredWidth(150);
					break;
				case 2: // Ghi chú
					column.setPreferredWidth(200);
					break;
				}
			}

			// Header styling
			JTableHeader header = table.getTableHeader();
			header.setFont(HEADER_FONT);
			header.setBackground(Color.WHITE);
			header.setForeground(Color.BLACK);
			header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
			((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

			// Scroll pane
			JScrollPane scrollPane = new JScrollPane(table);
			scrollPane.setBorder(BorderFactory.createLineBorder(new Color(245, 245, 245)));

			tablePanel.add(scrollPane, BorderLayout.CENTER);

			return tablePanel;
		}

		public void hienThiKetQuaTimkiem(ArrayList<DanhMuc> danhSach) {
			// Xóa dữ liệu cũ trong bảng
			tableModel.setRowCount(0);
			// Thêm dữ liệu mới vào bảng
			for (DanhMuc s : danhSach) {
				String[] row = { s.getMaDM(), s.getTenDM(), s.getGhiChu() };
				tableModel.addRow(row);
			}
		}
	}

}