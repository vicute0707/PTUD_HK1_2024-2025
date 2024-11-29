package dialog;

import dao.UserDAO;
import entity.User;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Date;
import com.toedter.calendar.JDateChooser;

public class SuaNhanVienDialog extends JDialog {
    private UserDAO userDAO;
    private User user;
    private Runnable refreshCallback;
    private JTextField txtHoTen, txtPhone, txtEmail;
    private JComboBox<String> cboGioiTinh, cboRole, cboStatus;
    private JDateChooser dateChooser;
    private JButton btnLuu, btnHuy;

    public SuaNhanVienDialog(Frame parent, boolean modal, User user, UserDAO userDAO, Runnable refreshCallback) {
        super(parent, "Sửa thông tin nhân viên", modal);
        this.user = user;
        this.userDAO = userDAO;
        this.refreshCallback = refreshCallback;
        initComponents();
        loadUserData();
    }

    public SuaNhanVienDialog(Window windowAncestor, boolean modal, User user2, UserDAO userDAO2,
			Runnable refreshCallback2) {
		// TODO Auto-generated constructor stub
	}

	private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setSize(400, 450);
        setLocationRelativeTo(getOwner());

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Initialize components
        txtHoTen = new JTextField(20);
        txtPhone = new JTextField(20);
        txtEmail = new JTextField(20);
        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        cboRole = new JComboBox<>(new String[]{"ROLE001", "ROLE002", "ROLE003"});
        cboStatus = new JComboBox<>(new String[]{"active", "inactive"});
        dateChooser = new JDateChooser();

        // Add components
        addComponent(mainPanel, gbc, "Họ tên:", txtHoTen, 0);
        addComponent(mainPanel, gbc, "Giới tính:", cboGioiTinh, 1);
        addComponent(mainPanel, gbc, "Ngày sinh:", dateChooser, 2);
        addComponent(mainPanel, gbc, "Số điện thoại:", txtPhone, 3);
        addComponent(mainPanel, gbc, "Email:", txtEmail, 4);
        addComponent(mainPanel, gbc, "Vai trò:", cboRole, 5);
        addComponent(mainPanel, gbc, "Trạng thái:", cboStatus, 6);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnLuu = new JButton("Lưu");
        btnHuy = new JButton("Hủy");

        btnLuu.addActionListener(e -> handleSave());
        btnHuy.addActionListener(e -> dispose());

        buttonPanel.add(btnLuu);
        buttonPanel.add(btnHuy);

        // Add panels to dialog
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadUserData() {
        if (user != null) {
            txtHoTen.setText(user.getFullName());
            cboGioiTinh.setSelectedItem(user.getGender());
            dateChooser.setDate(user.getBirthDate());
            txtPhone.setText(user.getPhone());
            txtEmail.setText(user.getEmail());
            cboRole.setSelectedItem(user.getRole());
            cboStatus.setSelectedItem(user.getStatus());
        }
    }

    private void addComponent(JPanel panel, GridBagConstraints gbc, String label, Component component, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(component, gbc);
    }

    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        try {
            user.setFullName(txtHoTen.getText().trim());
            user.setGender(cboGioiTinh.getSelectedItem().toString());
            user.setBirthDate(dateChooser.getDate());
            user.setPhone(txtPhone.getText().trim());
            user.setEmail(txtEmail.getText().trim());
            user.setRole(cboRole.getSelectedItem().toString());
            user.setStatus(cboStatus.getSelectedItem().toString());

            if (userDAO.updateUser(user)) {
                JOptionPane.showMessageDialog(this,
                    "Cập nhật thông tin thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                refreshCallback.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Cập nhật thông tin thất bại!",
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
        if (txtHoTen.getText().trim().isEmpty() ||
            txtPhone.getText().trim().isEmpty() ||
            txtEmail.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập đầy đủ thông tin!",
                "Lỗi",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}