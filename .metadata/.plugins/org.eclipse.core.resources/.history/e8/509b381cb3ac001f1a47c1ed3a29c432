package dialog;

import dao.PermissionDAO;
import entity.Permission;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ThemQuyenDialog extends JDialog {
    private PermissionDAO permissionDAO;
    private Runnable refreshCallback;
    private JTextField txtMaQuyen, txtTenQuyen;
    private JTextArea txtMoTa;
    private JButton btnThem, btnHuy;

    public ThemQuyenDialog(Frame parent, boolean modal, PermissionDAO permissionDAO, Runnable refreshCallback) {
        super(parent, "Thêm quyền mới", modal);
        this.permissionDAO = permissionDAO;
        this.refreshCallback = refreshCallback;
        initComponents();
    }

    public ThemQuyenDialog(Window windowAncestor, boolean modal, PermissionDAO permissionDAO2,
			Runnable refreshCallback2) {
		// TODO Auto-generated constructor stub
	}

	private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setSize(400, 300);
        setLocationRelativeTo(getOwner());

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Initialize components
        txtMaQuyen = new JTextField(20);
        txtTenQuyen = new JTextField(20);
        txtMoTa = new JTextArea(3, 20);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);

        // Add components
        addComponent(mainPanel, gbc, "Mã quyền:", txtMaQuyen, 0);
        addComponent(mainPanel, gbc, "Tên quyền:", txtTenQuyen, 1);
        addComponent(mainPanel, gbc, "Mô tả:", new JScrollPane(txtMoTa), 2);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnThem = new JButton("Thêm");
        btnHuy = new JButton("Hủy");

        btnThem.addActionListener(e -> handleAdd());
        btnHuy.addActionListener(e -> dispose());

        buttonPanel.add(btnThem);
        buttonPanel.add(btnHuy);

        // Add panels to dialog
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addComponent(JPanel panel, GridBagConstraints gbc, String label, Component component, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(component, gbc);
    }

    private void handleAdd() {
        if (!validateInput()) {
            return;
        }

        try {
            Permission permission = new Permission();
            permission.setPermissionID(txtMaQuyen.getText().trim());
            permission.setName(txtTenQuyen.getText().trim());
            permission.setDescription(txtMoTa.getText().trim());

            if (permissionDAO.addPermission(permission)) {
                JOptionPane.showMessageDialog(this,
                    "Thêm quyền thành công!",
                    "Thông báo",JOptionPane.INFORMATION_MESSAGE);
                refreshCallback.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Thêm quyền thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateInput() {
        if (txtMaQuyen.getText().trim().isEmpty() ||
            txtTenQuyen.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập đầy đủ thông tin bắt buộc!",
                "Lỗi",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}