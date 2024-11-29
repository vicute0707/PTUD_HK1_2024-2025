package dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import dao.DangNhap_Dao;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

public class QuenMatKhauDialog extends JDialog {
    private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
    private JTextField txtEmail;
    private JTextField txtUsername;
    private JTextField txtPhone;
    private DangNhap_Dao dangNhapDao;
    
    public QuenMatKhauDialog(JFrame parent) {
        super(parent, "Quên mật khẩu", true);
        dangNhapDao = new DangNhap_Dao();
        
        // Panel chính
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Panel form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Title
        JLabel lblTitle = new JLabel("Lấy lại mật khẩu", SwingConstants.CENTER);
        lblTitle.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 20));
        lblTitle.setForeground(PRIMARY_COLOR);
        
        // Username field
        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        lblUsername.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 13));
        txtUsername = createStyledTextField();
        
        // Email field
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 13));
        txtEmail = createStyledTextField();
        
        // Phone field
        JLabel lblPhone = new JLabel("Số điện thoại:");
        lblPhone.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 13));
        txtPhone = createStyledTextField();
        
        // Add components
        gbc.gridy = 0;
        formPanel.add(lblUsername, gbc);
        gbc.gridy = 1;
        formPanel.add(txtUsername, gbc);
        gbc.gridy = 2;
        formPanel.add(lblEmail, gbc);
        gbc.gridy = 3;
        formPanel.add(txtEmail, gbc);
        gbc.gridy = 4;
        formPanel.add(lblPhone, gbc);
        gbc.gridy = 5;
        formPanel.add(txtPhone, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        // Submit button
        JButton submitButton = createStyledButton("Lấy lại mật khẩu", "");
        submitButton.addActionListener(e -> layLaiMatKhau());
        
        // Cancel button
        JButton cancelButton = createStyledButton("Hủy", "");
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        
        // Add to main panel
        mainPanel.add(lblTitle, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Setup dialog
        setContentPane(mainPanel);
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private void layLaiMatKhau() {
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        
        // Validate input
        if(username.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập đầy đủ thông tin!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate email format
        if(!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                "Email không hợp lệ!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show loading dialog
        JDialog loadingDialog = createLoadingDialog("Đang xử lý...");
        loadingDialog.setVisible(true);
        
        // Xử lý trong thread riêng
        new Thread(() -> {
            String newPassword = dangNhapDao.layLaiMatKhau(username, email, phone);
            
            // Đóng loading dialog
            loadingDialog.dispose();
            
            if(newPassword != null) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                        "Mật khẩu mới đã được gửi đến email của bạn.\nVui lòng kiểm tra email và đăng nhập lại!",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                        "Thông tin không chính xác hoặc không thể gửi email!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private JDialog createLoadingDialog(String message) {
        JDialog dialog = new JDialog(this, "Đang xử lý", false);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel lblMessage = new JLabel(message);
        lblMessage.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 14));
        panel.add(lblMessage);
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        return dialog;
    }
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }
    
    private JButton createStyledButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setIcon(new ImageIcon(getClass().getResource(iconPath)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(190, 24, 93));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
        
        return button;
    }
}