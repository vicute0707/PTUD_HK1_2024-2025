package dialog;

import dao.UserDAO;
import dao.UserRoleDAO;
import entity.User;
import entity.UserRole;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;

public class ThemTaiKhoanDialog extends JDialog {
    private UserDAO userDAO;
    private UserRoleDAO userRoleDAO;
    private Runnable refreshCallback;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cboRole;
    private JButton btnThem, btnHuy;

    public ThemTaiKhoanDialog(Frame parent, boolean modal, UserDAO userDAO, 
            UserRoleDAO userRoleDAO, Runnable refreshCallback) {
        super(parent, "Thêm tài khoản mới", modal);
        this.userDAO = userDAO;
        this.userRoleDAO = userRoleDAO;
        this.refreshCallback = refreshCallback;
        initComponents();
    }

    public ThemTaiKhoanDialog(Window windowAncestor, boolean modal, UserDAO userDAO2, UserRoleDAO userRoleDAO2,
			Runnable refreshCallback2) {
		// TODO Auto-generated constructor stub
	}

	private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setSize(400, 250);
        setLocationRelativeTo(getOwner());

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Initialize components
        txtUsername = new JTextField(20);
        txtPassword = new JPasswordField(20);
        
        // Load roles for combobox
        List<UserRole> roles = userRoleDAO.getAllRoles();
        String[] roleNames = roles.stream()
                                .map(UserRole::getRoleName)
                                .toArray(String[]::new);
        cboRole = new JComboBox<>(roleNames);

        // Add components
        addComponent(mainPanel, gbc, "Tên đăng nhập:", txtUsername, 0);
        addComponent(mainPanel, gbc, "Mật khẩu:", txtPassword, 1);
        addComponent(mainPanel, gbc, "Vai trò:", cboRole, 2);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnThem = new JButton("Thêm");
        btnHuy = new JButton("Hủy");

        btnThem.addActionListener(e -> handleAdd());
        btnHuy.addActionListener(e -> dispose());

        buttonPanel.add(btnThem);
        buttonPanel.add(btnHuy);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleAdd() {
        if (!validateInput()) {
            return;
        }

        try {
            User newUser = new User();
            newUser.setUsername(txtUsername.getText().trim());
            newUser.setPassword(new String(txtPassword.getPassword()));
            newUser.setRole(cboRole.getSelectedItem().toString());
            newUser.setStatus("active");

            if (userDAO.addUser(newUser)) {
                JOptionPane.showMessageDialog(this,
                    "Thêm tài khoản thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                refreshCallback.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Thêm tài khoản thất bại!",
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
        if (txtUsername.getText().trim().isEmpty() ||
            new String(txtPassword.getPassword()).trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập đầy đủ thông tin!",
                "Lỗi",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}

