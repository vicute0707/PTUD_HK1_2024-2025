package dialog;

import dao.UserDAO;
import dao.UserRoleDAO;
import entity.User;
import entity.UserRole;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;

public class SuaTaiKhoanDialog extends JDialog {
    private UserDAO userDAO;
    private UserRoleDAO userRoleDAO;
    private User user;
    private Runnable refreshCallback;
    private JComboBox<String> cboRole;
    private JComboBox<String> cboStatus;
    private JButton btnLuu, btnHuy;

    public SuaTaiKhoanDialog(Frame parent, boolean modal, User user, 
            UserDAO userDAO, UserRoleDAO userRoleDAO, Runnable refreshCallback) {
        super(parent, "Sửa thông tin tài khoản", modal);
        this.user = user;
        this.userDAO = userDAO;
        this.userRoleDAO = userRoleDAO;
        this.refreshCallback = refreshCallback;
        initComponents();
        loadUserData();
    }

    public SuaTaiKhoanDialog(Window windowAncestor, boolean modal, User account, UserDAO userDAO2,
			UserRoleDAO userRoleDAO2, Runnable refreshCallback2) {
		// TODO Auto-generated constructor stub
	}

	private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setSize(400, 200);
        setLocationRelativeTo(getOwner());

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Initialize components
        List<UserRole> roles = userRoleDAO.getAllRoles();
        String[] roleNames = roles.stream()
                                .map(UserRole::getRoleName)
                                .toArray(String[]::new);
        cboRole = new JComboBox<>(roleNames);
        cboStatus = new JComboBox<>(new String[]{"active", "inactive"});

        // Add components
        addComponent(mainPanel, gbc, "Vai trò:", cboRole, 0);
        addComponent(mainPanel, gbc, "Trạng thái:", cboStatus, 1);

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

    private void loadUserData() {
        if (user != null) {
            cboRole.setSelectedItem(user.getRole());
            cboStatus.setSelectedItem(user.getStatus());
        }
    }

    private void handleSave() {
        try {
            user.setRole(cboRole.getSelectedItem().toString());
            user.setStatus(cboStatus.getSelectedItem().toString());

            if (userDAO.updateUser(user)) {
                JOptionPane.showMessageDialog(this,
                    "Cập nhật tài khoản thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                refreshCallback.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Cập nhật tài khoản thất bại!",
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
}