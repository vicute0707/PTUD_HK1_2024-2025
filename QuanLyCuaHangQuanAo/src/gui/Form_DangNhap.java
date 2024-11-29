package gui;  

import javax.swing.*;  
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

import dao.DangNhap_Dao;
import dialog.QuenMatKhauDialog;
import entity.User;
import entity.UserSession;

import javax.swing.border.BevelBorder;  
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;  

public class Form_DangNhap extends JFrame {  

    private static final long serialVersionUID = 1L;  
    private JPanel contentPane;  
    
    private static final Color PRIMARY_COLOR = new Color(219, 39, 119); // pink-600  
    private static final Color HOVER_COLOR = new Color(190, 24, 93); // darker pink for hover
    private static final Color LINK_HOVER_COLOR = new Color(236, 72, 153); // lighter pink for link hover
    public Form_DangNhap() {  
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        setBounds(100, 100, 1000, 600);  
        contentPane = new JPanel();  
        contentPane.setBackground(new Color(253, 242, 248));  
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));  

        setContentPane(contentPane);  
        contentPane.setLayout(null);  

        JLabel lbllogo = new JLabel("");  
        lbllogo.setBounds(0, 0, 290, 250);  
        lbllogo.setIcon(new ImageIcon(Form_DangNhap.class.getResource("/img/logoHoa - Copy.png")));  
        contentPane.add(lbllogo);  

        // Custom rounded panel for login
        JPanel panelDangNhap = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, 30, 30));
            }
        };
        panelDangNhap.setBounds(296, 35, 400, 500);  
        panelDangNhap.setBackground(new Color(255, 255, 255));  
        panelDangNhap.setPreferredSize(new Dimension(400, 500));  
        panelDangNhap.setLayout(new BorderLayout());

        // Create login form panel  
        JPanel formPanel = new JPanel(new GridBagLayout()) {  
            @Override  
            protected void paintComponent(Graphics g) {  
                super.paintComponent(g);  
                Graphics2D g2d = (Graphics2D) g;  
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  
                g2d.setColor(Color.white);  
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));  
            }  
        };  
        formPanel.setOpaque(false);  
        formPanel.setPreferredSize(new Dimension(400, 450));  

        GridBagConstraints gbc = new GridBagConstraints();  
        gbc.gridwidth = GridBagConstraints.REMAINDER;  
        gbc.fill = GridBagConstraints.HORIZONTAL;  
        gbc.insets = new Insets(5, 0, 15, 0);  

        // Logo/Title  
        JLabel titleLabel = new JLabel("Đăng nhập");  
        titleLabel.setFont(new Font(FlatRobotoFont.FAMILY_SEMIBOLD, Font.BOLD, 26));  
        titleLabel.setForeground(PRIMARY_COLOR);  
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);  
        formPanel.add(titleLabel, gbc);  

        // Username  
        JLabel userLabel = new JLabel("Tên đăng nhập");
       
        userLabel.setForeground(new Color(107, 114, 128));
        userLabel.setFont(new java.awt.Font("Segoe UI", 0, 13));
        formPanel.add(userLabel, gbc);  

        JTextField userField = createStyledTextField();  
        formPanel.add(userField, gbc);  

        // Password  
        JLabel passLabel = new JLabel("Mật khẩu");
        passLabel.setFont(new java.awt.Font("Segoe UI", 0, 13));
        passLabel.setForeground(new Color(107, 114, 128));  
        formPanel.add(passLabel, gbc);  

        JPasswordField passField = createStyledPasswordField();  
        formPanel.add(passField, gbc);  

        // Login Button  
        JButton loginButton = createStyledButton("Đăng nhập");  
        gbc.insets = new Insets(20, 0, 10, 0);  
        formPanel.add(loginButton, gbc);  
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText().trim();
                String password = new String(passField.getPassword()).trim();
                
                // Kiểm tra dữ liệu nhập
                if(username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(Form_DangNhap.this, 
                        "Vui lòng nhập đầy đủ thông tin đăng nhập!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Để test, bạn có thể in ra password đã mã hóa
                System.out.println("Password nhập vào: " + password);

                
                // Kiểm tra đăng nhập
                DangNhap_Dao dangNhapDao = new DangNhap_Dao();
                User user = dangNhapDao.kiemTraDangNhap(username, password);
                
                if(user != null) {
                    // Lưu thông tin user vào session
                    UserSession.getInstance().setCurrentUser(user);
                    
                    JOptionPane.showMessageDialog(Form_DangNhap.this,
                        "Đăng nhập thành công! Xin chào " + user.getFullName(),
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                        
                    // Mở form chính tương ứng với role
                    setVisible(false);
                    switch(user.getRole()) {
                        case "ROLE001": // Admin
                            new Form_GiaoDienChinh(userLabel.getText()).setVisible(true);
                            System.out.println("Đăng nhập với quyền Admin");
                            break;
                        case "ROLE002": // Nhân viên kho
                            new Form_GiaoDienChinh(userLabel.getText()).setVisible(true);
                            System.out.println("Đăng nhập với quyền Nhân viên kho");
                            break;
                        case "ROLE003": // Nhân viên bán hàng  
                            new Form_GiaoDienChinh(userLabel.getText()).setVisible(true);
                            System.out.println("Đăng nhập với quyền Nhân viên bán hàng");
                            break;
                        default:
                            new Form_GiaoDienChinh(userLabel.getText()).setVisible(true);
                            break;
                    }
                } else {
                    JOptionPane.showMessageDialog(Form_DangNhap.this,
                        "Tên đăng nhập hoặc mật khẩu không đúng!",
                        "Lỗi đăng nhập",
                        JOptionPane.ERROR_MESSAGE);
                    passField.setText("");
                    passField.requestFocus();
                }
            }
        });

        // Register Link  
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));  
        linkPanel.setOpaque(false);  
        JLabel registerLabel = new JLabel("Bạn quên mật khẩu? ");  
        JLabel registerLink = new JLabel("Lấy mật khẩu") {
        	{
                {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                    setForeground(PRIMARY_COLOR);
                    
                    // Thêm hiệu ứng hover cho link
                    addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            setForeground(LINK_HOVER_COLOR);
                            // Thêm gạch chân khi hover
                            setText("<html><u>Lấy mật khẩu</u></html>");
                            setFont(getFont().deriveFont(Font.BOLD));
                            
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            setForeground(PRIMARY_COLOR);
                            setText("Lấy mật khẩu");
                            setFont(getFont().deriveFont(Font.PLAIN));
                        }

                        @Override
                        public void mouseClicked(MouseEvent e) {
                            QuenMatKhauDialog dialog = new QuenMatKhauDialog(
                                (JFrame) SwingUtilities.getWindowAncestor(Form_DangNhap.this));
                            dialog.setVisible(true);
                        }
                    });
                }
            };
        };  
        registerLink.setForeground(PRIMARY_COLOR);  
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));  

        linkPanel.add(registerLabel);  
        linkPanel.add(registerLink);  
        formPanel.add(linkPanel, gbc);  

        panelDangNhap.add(formPanel);  
        contentPane.add(panelDangNhap);  

        JLabel lbllogo_1 = new JLabel("");  
        lbllogo_1.setIcon(new ImageIcon(Form_DangNhap.class.getResource("/img/logoHoa - Copy - Copy.png")));  
        lbllogo_1.setBounds(110, 359, 182, 160);  
        contentPane.add(lbllogo_1);  

        JLabel lbllogo_1_1 = new JLabel("");  
        lbllogo_1_1.setIcon(new ImageIcon(Form_DangNhap.class.getResource("/img/logoHoa - Copy - Copy.png")));  
        lbllogo_1_1.setBounds(739, 214, 177, 163);  
        contentPane.add(lbllogo_1_1);  
    }
    

    private JButton createStyledButton(String text) {  
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, 20, 20));
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Arial", Font.BOLD, 16));  
        button.setForeground(Color.WHITE);  
        button.setBackground(PRIMARY_COLOR);  
        button.setFocusPainted(false);  
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));  
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);

        // Thêm hiệu ứng hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
                // Thêm hiệu ứng phóng to nhẹ
                button.setBorder(BorderFactory.createEmptyBorder(9, 19, 9, 19));
                button.setFont(new Font("Arial", Font.BOLD, 17));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
                // Trở về kích thước bình thường
                button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
                button.setFont(new Font("Arial", Font.BOLD, 16));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(new Color(157, 23, 77)); // Màu tối hơn khi click
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
            }
        });

        return button;  
    }  
    private JPasswordField createStyledPasswordField() {  
        JPasswordField passwordField = new JPasswordField(15);  
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));  
        passwordField.setBorder(BorderFactory.createCompoundBorder(  
                BorderFactory.createLineBorder(new Color(180, 180, 180)),  
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));  
        passwordField.setOpaque(false);  
        return passwordField;  
    }  
    private JTextField createStyledTextField() {  
        JTextField textField = new JTextField(15);  
        textField.setFont(new java.awt.Font("Segoe UI", 0, 13));  
        textField.setBorder(BorderFactory.createCompoundBorder(  
                BorderFactory.createLineBorder(new Color(180, 180, 180)),  
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));  
        textField.setOpaque(false);  
        return textField;  
    }

}