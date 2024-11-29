package dialog;

import dao.PermissionDAO;
import entity.Permission;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SuaQuyenDialog extends JDialog {
    private PermissionDAO permissionDAO;
    private Permission permission;
    private Runnable refreshCallback;
    private JTextField txtTenQuyen;
    private JTextArea txtMoTa;
    private JButton btnLuu, btnHuy;

    public SuaQuyenDialog(Frame parent, boolean modal, Permission permission, 
            PermissionDAO permissionDAO, Runnable refreshCallback) {
        super(parent, "Sửa thông tin quyền", modal);
        this.permission = permission;
        this.permissionDAO = permissionDAO;
        this.refreshCallback = refreshCallback;
        initComponents();
        loadPermissionData();
    }

    public SuaQuyenDialog(Window windowAncestor, boolean modal, Permission permission2, PermissionDAO permissionDAO2,
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
        txtTenQuyen = new JTextField(20);
        txtMoTa = new JTextArea(3, 20);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);

        // Add components
        addComponent(mainPanel, gbc, "Tên quyền:", txtTenQuyen, 0);
        addComponent(mainPanel, gbc, "Mô tả:", new JScrollPane(txtMoTa), 1);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnLuu = new JButton("Lưu");
        btnHuy = new JButton("Hủy");

        btnLuu.addActionListener(e -> handleSave());
        btnHuy.addActionListener(e -> dispose());

        buttonPanel.add(btnLuu);
        buttonPanel.add(btnHuy);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadPermissionData() {
        if (permission != null) {
            txtTenQuyen.setText(permission.getName());
            txtMoTa.setText(permission.getDescription());
        }
    }

    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        try {
            permission.setName(txtTenQuyen.getText().trim());
            permission.setDescription(txtMoTa.getText().trim());

            if (permissionDAO.updatePermission(permission)) {
                JOptionPane.showMessageDialog(this,
                    "Cập nhật quyền thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                refreshCallback.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Cập nhật quyền thất bại!",
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

    private void addComponent(JPanel panel, GridBagConstraints gbc, String label, Component component, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(component, gbc);
    }

    private boolean validateInput() {
        if (txtTenQuyen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập tên quyền!",
                "Lỗi",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}
