package dialog;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.logging.Logger;

import bus.SanPhamBUS;
import bus.DanhMucBUS;
import entity.SanPham;
import entity.DanhMuc;

public class ThemSanPham extends JDialog {
    private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font CONTENT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    
    private JTextField txtTenSP, txtThuongHieu;
    private JLabel lblImage;
    private JComboBox<String> cboDanhMuc;
    private String imagePath = "";
    private boolean isConfirmed = false;
    private SanPham sanPham;
    private SanPhamBUS sanPhamBUS;
    private DanhMucBUS danhMucBUS;
    Frame owner;
    private static final Logger LOGGER = Logger.getLogger(ThemSanPham.class.getName());
    public ThemSanPham(Frame owner) {
        super(owner, "Thêm Sản Phẩm Mới", true);
        this.sanPham = new SanPham();
        this.sanPhamBUS = new SanPhamBUS();
        this.danhMucBUS = new DanhMucBUS();
        initComponents();
        loadDanhMuc();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("THÊM SẢN PHẨM MỚI");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Image Panel
        JPanel imagePanel = createImagePanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 20);
        mainPanel.add(imagePanel, gbc);

        // Form Panel
        JPanel formPanel = createFormPanel();
        gbc.gridx = 1;
        gbc.weightx = 0.6;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(formPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(800, 600);
        setLocationRelativeTo(owner);
    }

    private JPanel createImagePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            "Ảnh sản phẩm",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            CONTENT_FONT
        ));

        lblImage = new JLabel();
        lblImage.setPreferredSize(new Dimension(250, 300));
        lblImage.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblImage.setBackground(Color.WHITE);
        lblImage.setOpaque(true);

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
        gbc.weightx = 1.0;
        
        // Initialize components
        txtTenSP = createStyledTextField();
        cboDanhMuc = new JComboBox<>();
        cboDanhMuc.setFont(CONTENT_FONT);
        txtThuongHieu = createStyledTextField();

        // Add form fields
        addFormRow(panel, "Tên sản phẩm:", txtTenSP, 0);
        addFormRow(panel, "Danh mục:", cboDanhMuc, 1);
        addFormRow(panel, "Thương hiệu:", txtThuongHieu, 2);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBackground(Color.WHITE);

        JButton btnAdd = createStyledButton("Thêm phân loại", PRIMARY_COLOR);
        JButton btnCancel = createStyledButton("Hủy", new Color(156, 163, 175));

        btnAdd.addActionListener(e -> showThemPhanLoaiDialog());
        btnCancel.addActionListener(e -> dispose());

        panel.add(btnAdd);
        panel.add(btnCancel);

        return panel;
    }

    private void loadDanhMuc() {
        cboDanhMuc.removeAllItems();
        for (DanhMuc dm : danhMucBUS.getDanhSachDanhMuc()) {
            cboDanhMuc.addItem(dm.getTenDM().toString());
        }
    }
    private void showThemPhanLoaiDialog() {
        // First save the product if it hasn't been saved
    	saveProduct();
        if (sanPham.getMaSP() == null || sanPham.getMaSP().isEmpty()) {
            if (!validateProductInfo()) return;
            updateSanPhamFromFields();
            if (!sanPhamBUS.addSanPham(sanPham)) {
                JOptionPane.showMessageDialog(this, "Vui lòng lưu sản phẩm trước khi thêm phân loại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Now open dialog with saved product ID
        ThemPhanLoai dialog = new ThemPhanLoai(
            (Frame) SwingUtilities.getWindowAncestor(this),
            sanPham.getMaSP()
        );
        dialog.setVisible(true);
    }

    

    private void saveProduct() {
        if (!validateProductInfo()) return;

        updateSanPhamFromFields();
        
        try {
            if (sanPhamBUS.addSanPham(sanPham)) {
                isConfirmed = true;
                LOGGER.info("Created product with ID: " + sanPham.getMaSP()); // Add here
                JOptionPane.showMessageDialog(this, 
                    "Thêm sản phẩm thành công!", 
                    "Thông báo", 
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    

    private boolean validateProductInfo() {
        if (txtTenSP.getText().trim().isEmpty()) {
            showError("Vui lòng nhập tên sản phẩm");
            return false;
        }
        if (cboDanhMuc.getSelectedItem() == null) {
            showError("Vui lòng chọn danh mục");
            return false;
        }
        if (txtThuongHieu.getText().trim().isEmpty()) {
            showError("Vui lòng nhập thương hiệu");
            return false;
        }
        if (imagePath.isEmpty()) {
            showError("Vui lòng chọn ảnh sản phẩm");
            return false;
        }
        return true;
    }

    private void updateSanPhamFromFields() {
        if (sanPham.getMaSP() == null || sanPham.getMaSP().isEmpty()) {
            sanPham.setMaSP(sanPhamBUS.generateNewProductId());
        }
        sanPham.setTenSP(txtTenSP.getText().trim());
        sanPham.setDanhmuc(danhMucBUS.getDanhMucByName(cboDanhMuc.getSelectedItem().toString()));
        sanPham.setThuongHieu(txtThuongHieu.getText().trim());
        sanPham.setHinhAnh(imagePath);
     
        sanPham.setTinhTrang("Chưa nhập về");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Image Files", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            imagePath = file.getAbsolutePath();
            
            try {
                ImageIcon imageIcon = new ImageIcon(imagePath);
                Image image = imageIcon.getImage().getScaledInstance(250, 300, Image.SCALE_SMOOTH);
                lblImage.setIcon(new ImageIcon(image));
            } catch (Exception e) {
                showError("Error loading image: " + e.getMessage());
            }
        }
    }

    // UI Helper Methods
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(CONTENT_FONT);
        field.setPreferredSize(new Dimension(0, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private void addFormRow(JPanel panel, String labelText, JComponent field, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel label = new JLabel(labelText);
        label.setFont(CONTENT_FONT);
        
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(CONTENT_FONT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 40));

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

    // Getters
    public boolean isConfirmed() {
        return isConfirmed;
    }

    public SanPham getSanPham() {
        return sanPham;
    }
}