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
import dialog.SuaNhanVienDialog;
import dialog.ThemNhanVienDialog;
import style.CustomScrollBarUI;
import style.StyleTabbedPane;
public class Form_NhanVien extends JPanel {

    private static final Color CONTENT_COLOR = new Color(255, 192, 203);
    private static final Color HOVER_COLOR = new Color(252, 231, 243);
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 12);
    private static final Font CONTENT_FONT = new Font("Arial", Font.PLAIN, 12);
    private static final Font TAB_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Color PRIMARY_COLOR = new Color(219, 39, 119);

    private JTabbedPane tabbedPane;
    private int selectedRow = -1;
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
        StyleTabbedPane styleTabbedPane =new StyleTabbedPane();
        styleTabbedPane.styleTabbedPane(tabbedPane);
        CreateTabPanel createTabPanel = new CreateTabPanel();
        // Tạo các panel
        JPanel quanLyNVPanel = createTabPanel.createTabPanel("Quản lý nhân viên", 
            new String[]{"Mã NV", "Họ và tên", "Giới tính", "Ngày sinh", "SDT", "Email"},
            createSampleEmployeeData());
        
        JPanel phanQuyenPanel = createTabPanel.createTabPanel("Phân quyền", 
            new String[]{"Mã quyền", "Tên quyền truy cập"},
            createSamplePermissionData());
            
        JPanel taiKhoanPanel = createTabPanel.createTabPanel("Tài khoản", 
            new String[]{"Mã NV", "Tên đăng nhập", "Nhóm quyền", "Trạng thái"},
            createSampleAccountData());
        
        // Thêm các tab
        tabbedPane.addTab("Quản lý nhân viên", quanLyNVPanel);
        tabbedPane.addTab("Phân quyền", phanQuyenPanel);
        tabbedPane.addTab("Tài khoản", taiKhoanPanel);
        
        // Set màu cho text của tab
        for(int i = 0; i < tabbedPane.getTabCount(); i++) {
            tabbedPane.setForegroundAt(i, new Color(50, 50, 50));
        }
        
        add(tabbedPane, BorderLayout.CENTER);
        setOpaque(true);
    }
    // Sample data methods
    private Object[][] createSampleEmployeeData() {
        return new Object[][] {
            {"NV001", "Nguyễn Văn A", "Nam", "1990-01-01", "0123456789", "nva@email.com"},
            {"NV002", "Trần Thị B", "Nữ", "1992-02-02", "0987654321", "ttb@email.com"}
        };
    }

    private Object[][] createSamplePermissionData() {
        return new Object[][] {
            {"Q001", "Quản lí cửa hàng",""},
            {"Q002", "Quản lí nhân viên",""},
            {"Q003", "Quản lí kho",""},
            {"Q004", "Quản lí bán hàng",""},
            {"Q005", "Quản lí báo cáo",""}
        };
    }

    private Object[][] createSampleAccountData() {
        return new Object[][] {
            {"NV001", "admin", "Quản lí", "Đang hoạt động"},
            {"NV002", "user", "Nhân viên", "Đang hoạt động"}
        };
    }
    

    // Event handlers
    private void handleSearch(String searchText, JTable table) {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        
        if (searchText.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }

    private void handleAdd(String tabTitle) {
        String message = "Thêm mới " + 
            (tabTitle.contains("nhân viên") ? "nhân viên" : 
             tabTitle.contains("quyền") ? "quyền" : "tài khoản");
             
        JOptionPane.showMessageDialog(this, 
            "Chức năng " + message + " sẽ được thực hiện tại đây",
            message,
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleEdit(JTable table, String tabTitle) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn một dòng để chỉnh sửa",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String message = "Chỉnh sửa " + 
            (tabTitle.contains("nhân viên") ? "nhân viên" : 
             tabTitle.contains("quyền") ? "quyền" : "tài khoản");
             
        JOptionPane.showMessageDialog(this, 
            "Chức năng " + message + " sẽ được thực hiện tại đây",
            message,
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleDelete(JTable table, String tabTitle) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn một dòng để xóa",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String itemType = tabTitle.contains("nhân viên") ? "nhân viên" : 
                         tabTitle.contains("quyền") ? "quyền" : "tài khoản";
                         
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa " + itemType + " này?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.removeRow(table.convertRowIndexToModel(selectedRow));
            JOptionPane.showMessageDialog(this,
                "Xóa " + itemType + " thành công!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleExport(String tabTitle) {
        String message = "Xuất Excel cho " + 
            (tabTitle.contains("nhân viên") ? "danh sách nhân viên" : 
             tabTitle.contains("quyền") ? "danh sách quyền" : "danh sách tài khoản");
             
        JOptionPane.showMessageDialog(this, 
            "Chức năng " + message + " sẽ được thực hiện tại đây",
            "Xuất Excel",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleAbout(String tabTitle) {
        String message = "Thông tin về quản lý " + 
            (tabTitle.contains("nhân viên") ? "nhân viên" : 
             tabTitle.contains("quyền") ? "quyền" : "tài khoản");
             
        JOptionPane.showMessageDialog(this, 
            message,
            "Thông tin",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // Custom cell renderers
    private class StatusColumnRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);
            
            if (value != null) {
                String status = value.toString();
                if (status.equals("Đang hoạt động")) {
                    setForeground(new Color(40, 167, 69));
                } else {
                    setForeground(new Color(220, 53, 69));
                }
            }
            
            setHorizontalAlignment(JLabel.CENTER);
            return c;
        }
    }

    private class CenterAlignRenderer extends DefaultTableCellRenderer {
        public CenterAlignRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }
    }

}