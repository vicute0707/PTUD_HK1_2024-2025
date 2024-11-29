package dialog;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import entity.User;
import gui.Form_DangNhap;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

public class UserInfoDialog extends JDialog {
    private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
    private static final Font LABEL_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 13);
    private static final Font VALUE_FONT = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13);
    
    public UserInfoDialog(JFrame parent) {
        super(parent, "Thông tin người dùng", true);
        
        // Lấy thông tin user từ session
        User user = entity.UserSession.getInstance().getCurrentUser();
        
        // Panel chính
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Panel thông tin
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Avatar panel
        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        avatarPanel.setBackground(Color.WHITE);
        JLabel avatarLabel = new JLabel(new ImageIcon(getClass().getResource("/icon/circle-user.png")));
        avatarPanel.add(avatarLabel);
        
        // Thêm thông tin
        addInfoField(infoPanel, gbc, 0, "Mã nhân viên:", user.getUserID());
        addInfoField(infoPanel, gbc, 1, "Họ tên:", user.getFullName());
        addInfoField(infoPanel, gbc, 2, "Giới tính:", user.getGender());
        addInfoField(infoPanel, gbc, 3, "Ngày sinh:", user.getBirthDate().toString());
        addInfoField(infoPanel, gbc, 4, "Số điện thoại:", user.getPhone());
        addInfoField(infoPanel, gbc, 5, "Email:", user.getEmail());
        String roleText = switch(user.getRole()) {
            case "ROLE001" -> "Quản trị viên";
            case "ROLE002" -> "Nhân viên kho";
            case "ROLE003" -> "Nhân viên bán hàng";
            default -> user.getRole();
        };
        addInfoField(infoPanel, gbc, 6, "Chức vụ:", roleText);
        
        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
         
        // Nút đăng xuất
        JButton logoutBtn = createStyledButton("Đăng xuất", "");
        logoutBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn đăng xuất?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION);
                
            if (choice == JOptionPane.YES_OPTION) {
                entity.UserSession.getInstance().clearSession();
                parent.dispose();
                new Form_DangNhap().setVisible(true);
                dispose();
            }
        });
        

        buttonPanel.add(logoutBtn);
        
        // Thêm vào panel chính
        mainPanel.add(avatarPanel, BorderLayout.NORTH);
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Cài đặt dialog
        setContentPane(mainPanel);
        setSize(400, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private void addInfoField(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridy = row;
        
        // Label
        gbc.gridx = 0;
        JLabel lblField = new JLabel(label);
        lblField.setFont(LABEL_FONT);
        lblField.setForeground(PRIMARY_COLOR);
        panel.add(lblField, gbc);
        
        // Value
        gbc.gridx = 1;
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(VALUE_FONT);
        panel.add(lblValue, gbc);
    }
    
    private JButton createStyledButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setIcon(new ImageIcon(getClass().getResource(iconPath)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200,30));
        
        // Hiệu ứng hover
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