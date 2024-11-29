package dialog;

import dao.UserDAO;
import entity.User;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Date;
import com.toedter.calendar.JDateChooser;

public class ThemNhanVienDialog extends JDialog {
    private UserDAO userDAO;
    private Runnable refreshCallback;
    private JTextField txtMaNV, txtHoTen, txtPhone, txtEmail, txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cboGioiTinh, cboRole;
    private JDateChooser dateChooser;
    private JButton btnThem, btnHuy;

    public ThemNhanVienDialog(Frame parent, boolean modal, UserDAO userDAO, Runnable refreshCallback) {
        super(parent, "Thêm nhân viên mới", modal);
        this.userDAO = userDAO;
        this.refreshCallback = refreshCallback;
        initComponents();
    }

    public ThemNhanVienDialog(Window windowAncestor, boolean modal, UserDAO userDAO2, Runnable refreshCallback2) {
		// TODO Auto-generated constructor stub
	}

	private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setSize(400, 500);
        setLocationRelativeTo(getOwner());

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Initialize components
        txtMaNV = new JTextField(20);
        txtHoTen = new JTextField(20);
        txtPhone = new JTextField(20);
        txtEmail = new JTextField(20);
        txtUsername = new JTextField(20);
        txtPassword = new JPasswordField(20);
        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        cboRole = new JComboBox<>(new String[]{"ROLE001", "ROLE002", "ROLE003"});
        dateChooser = new JDateChooser();
        dateChooser.setDate(new Date());

        // Add components
        addComponent(mainPanel, gbc, "Mã nhân viên:", txtMaNV, 0);
        addComponent(mainPanel, gbc, "Họ tên:", txtHoTen, 1);
        addComponent(mainPanel, gbc, "Giới tính:", cboGioiTinh, 2);
        addComponent(mainPanel, gbc, "Ngày sinh:", dateChooser, 3);
        addComponent(mainPanel, gbc, "Số điện thoại:", txtPhone, 4);
        addComponent(mainPanel, gbc, "Email:", txtEmail, 5);
        addComponent(mainPanel, gbc, "Tên đăng nhập:", txtUsername, 6);
        addComponent(mainPanel, gbc, "Mật khẩu:", txtPassword, 7);
        addComponent(mainPanel, gbc, "Vai trò:", cboRole, 8);

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
            User newUser = new User();
            newUser.setUserID(txtMaNV.getText().trim());
            newUser.setFullName(txtHoTen.getText().trim());
            newUser.setGender(cboGioiTinh.getSelectedItem().toString());
            newUser.setBirthDate(dateChooser.getDate());
            newUser.setPhone(txtPhone.getText().trim());
            newUser.setEmail(txtEmail.getText().trim());
            newUser.setUsername(txtUsername.getText().trim());
            newUser.setPassword(new String(txtPassword.getPassword()));
            newUser.setRole(cboRole.getSelectedItem().toString());
            newUser.setStatus("active");

            if (userDAO.addUser(newUser)) {
                JOptionPane.showMessageDialog(this,
                    "Thêm nhân viên thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                refreshCallback.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Thêm nhân viên thất bại!",
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
        if (txtMaNV.getText().trim().isEmpty() ||
            txtHoTen.getText().trim().isEmpty() ||
            txtPhone.getText().trim().isEmpty() ||
            txtEmail.getText().trim().isEmpty() ||
            txtUsername.getText().trim().isEmpty() ||
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