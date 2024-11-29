package table;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import style.CustomScrollBarUI;

public class TBL_SP {
    private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
    private static final Color CONTENT_COLOR = new Color(255, 192, 203);
    private static final Color HOVER_COLOR = new Color(252, 231, 243);
    private static final Font HEADER_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12);
    private static final Font CONTENT_FONT = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12);
    
    private JTable table;
    private DefaultTableModel tableModel;

    public JPanel createTablePanel(JTable inputTable, DefaultTableModel inputTableModel) {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 15));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Khởi tạo columns
        String[] columns = {
            "Mã SP", "Tên sản phẩm", "Danh mục", "Số lượng tồn", 
            "Giá nhập", "Giá bán", "Thương hiệu", "Tình trạng"
        };

        // Khởi tạo tableModel
        this.tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Khởi tạo table với model vừa tạo
        this.table = new JTable(this.tableModel);
        setupTable();

        // Thêm dữ liệu mẫu
        addSampleData();

        // Setup scrollPane
        JScrollPane scrollPane = setupScrollPane();
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void setupTable() {
        table.setFont(CONTENT_FONT);
        table.setRowHeight(32);
        table.setGridColor(new Color(245, 245, 245));
        table.setSelectionBackground(HOVER_COLOR);
        table.setSelectionForeground(Color.BLACK);
        table.setIntercellSpacing(new Dimension(10, 10));
        table.setPreferredScrollableViewportSize(new Dimension(800, 400));
        table.setFillsViewportHeight(true);

        // Setup header
        JTableHeader header = table.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(Color.WHITE);
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        // Set status column renderer
        setupStatusColumnRenderer();
    }

    private JScrollPane setupScrollPane() {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(10, 10, 10, 10),
            BorderFactory.createLineBorder(new Color(245, 245, 245))
        ));

        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI());

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        return scrollPane;
    }

    private void setupStatusColumnRenderer() {
        table.getColumnModel().getColumn(7).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(value.toString());
            label.setOpaque(true);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setFont(CONTENT_FONT);
            label.setBorder(new EmptyBorder(0, 5, 0, 5));

            switch (value.toString()) {
                case "Còn hàng":
                    label.setForeground(new Color(0, 128, 0));
                    break;
                case "Sắp hết":
                    label.setForeground(new Color(255, 140, 0));
                    break;
                case "Hết hàng":
                    label.setForeground(Color.RED);
                    break;
            }

            if (isSelected) {
                label.setBackground(HOVER_COLOR);
            } else {
                label.setBackground(table.getBackground());
            }

            return label;
        });
    }

    private void addSampleData() {
        Object[][] data = {
            {"SP001", "Váy ngắn kẻ caro", "Váy", 36, "200,000 vnd", "250,000 vnd", "VietNam", "Còn hàng"},
            {"SP002", "Quần dài", "Quần", 3, "200,000 vnd", "250,000 vnd", "Shien", "Sắp hết"},
            {"SP003", "Áo thun trơn", "Áo", 0, "120,000 vnd", "199,000 vnd", "T-Lab", "Hết hàng"},
            {"SP004", "Áo sơ mi", "Áo", 15, "180,000 vnd", "250,000 vnd", "Local Brand", "Còn hàng"},
            {"SP005", "Quần jean", "Quần", 20, "300,000 vnd", "450,000 vnd", "Levi's", "Còn hàng"},
            {"SP006", "Váy dài", "Váy", 8, "250,000 vnd", "350,000 vnd", "Zara", "Sắp hết"},
            {"SP001", "Váy ngắn kẻ caro", "Váy", 36, "200,000 vnd", "250,000 vnd", "VietNam", "Còn hàng"},
            {"SP002", "Quần dài", "Quần", 3, "200,000 vnd", "250,000 vnd", "Shien", "Sắp hết"},
            {"SP003", "Áo thun trơn", "Áo", 0, "120,000 vnd", "199,000 vnd", "T-Lab", "Hết hàng"},
            {"SP004", "Áo sơ mi", "Áo", 15, "180,000 vnd", "250,000 vnd", "Local Brand", "Còn hàng"},
            {"SP005", "Quần jean", "Quần", 20, "300,000 vnd", "450,000 vnd", "Levi's", "Còn hàng"},
            {"SP006", "Váy dài", "Váy", 8, "250,000 vnd", "350,000 vnd", "Zara", "Sắp hết"},
            {"SP007", "Áo khoác", "Áo", 12, "400,000 vnd", "600,000 vnd", "H&M", "Còn hàng"}
        };

        for (Object[] row : data) {
            this.tableModel.addRow(row);
        }
    }

    public JTable getTable() {
        return this.table;
    }

    public DefaultTableModel getTableModel() {
        return this.tableModel;
    }
}