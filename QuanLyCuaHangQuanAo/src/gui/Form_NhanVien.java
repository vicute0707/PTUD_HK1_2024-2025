package gui;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.toedter.calendar.JDateChooser;


import component.CreateTabPanel;
import component.EmployeeManagementPanel;
import component.PermissionManagementPanel;
import component.UserRoleManagementPanel;
import style.CustomScrollBarUI;
import style.StyleTabbedPane;
public class Form_NhanVien extends JPanel {
	private static final Font TAB_FONT = new Font("Arial", Font.BOLD, 14);
    private JTabbedPane tabbedPane;

    public Form_NhanVien() {
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        setBackground(Color.WHITE);
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(TAB_FONT);
        tabbedPane.setBackground(Color.WHITE);
        
        // Style cho tabbedPane
        StyleTabbedPane styleTabbedPane = new StyleTabbedPane();
        styleTabbedPane.styleTabbedPane(tabbedPane);
        
        // Tạo các tab panel
        EmployeeManagementPanel employeePanel = new EmployeeManagementPanel();
        PermissionManagementPanel permissionPanel = new PermissionManagementPanel();
        UserRoleManagementPanel userRoleManagementPanel = new UserRoleManagementPanel();
        // Thêm các tab
        tabbedPane.addTab("Nhân viên & Tài khoản", employeePanel);
        tabbedPane.addTab(" QL Quyền", permissionPanel);
        tabbedPane.addTab("Phân quyền", userRoleManagementPanel);

        
        // Set màu cho text của tab
        for(int i = 0; i < tabbedPane.getTabCount(); i++) {
            tabbedPane.setForegroundAt(i, new Color(50, 50, 50));
        }
        
        add(tabbedPane, BorderLayout.CENTER);
        setOpaque(true);
    }
}