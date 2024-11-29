package table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import style.CustomScrollBarUI;

public class TBL_DM {
    private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
    private static final Color CONTENT_COLOR = new Color(255, 192, 203);
    private static final Color HOVER_COLOR = new Color(252, 231, 243);
    private static final Font HEADER_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12);
    private static final Font CONTENT_FONT = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12);
    private static final Font TITLE_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 14);

    private int selectedRow = -1;
    private JTable table;
    private DefaultTableModel tableModel;

    public JPanel createTablePanel(final JTable inputTable, final DefaultTableModel inputTableModel) {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 15));
        tablePanel.setBackground(Color.WHITE);

        // Initialize table model with columns
        String[] columns = {"Mã DM", "Tên danh mục", "Ghi chú"};
        this.tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };

        // Initialize table
        this.table = new JTable(this.tableModel);
        
        // Add mouse listener for row selection
        this.table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    // No need to modify inputTable reference here
                }
            }
        });

        this.table.setFont(CONTENT_FONT);
        this.table.setRowHeight(32);
        this.table.setGridColor(new Color(245, 245, 245));
        this.table.setSelectionBackground(HOVER_COLOR);
        this.table.setSelectionForeground(Color.BLACK);
        this.table.setShowVerticalLines(true);
        this.table.setShowHorizontalLines(true);

        // Set up column renderers and widths
        setupTableColumns();

        // Style the header
        setupTableHeader();

        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(this.table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(245, 245, 245)));
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Add sample data
        addSampleData();

        return tablePanel;
    }

    private void setupTableColumns() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setCellRenderer(centerRenderer);

            switch (i) {
                case 0: // Mã DM
                    column.setPreferredWidth(80);
                    column.setMaxWidth(80);
                    column.setMinWidth(80);
                    break;
                case 1: // Tên danh mục
                    column.setPreferredWidth(150);
                    break;
                case 2: // Ghi chú
                    column.setPreferredWidth(200);
                    break;
            }
        }
    }

    private void setupTableHeader() {
        JTableHeader header = table.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(Color.WHITE);
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
    }

    private void addSampleData() {
        Object[][] data = {
            {"DM001", "Váy", "Các loại váy ngắn, váy dài"},
            {"DM002", "Quần", "Quần jean, quần tây, quần short"},
            {"DM003", "Áo", "Áo thun, áo sơ mi, áo khoác"},
            {"DM004", "Phụ kiện", "Túi xách, ví, dây nịt"},
            {"DM005", "Giày dép", "Giày cao gót, giày thể thao"}
        };

        for (Object[] row : data) {
            this.tableModel.addRow(row);
        }
    }

    // Getters for the table and table model
    public JTable getTable() {
        return this.table;
    }

    public DefaultTableModel getTableModel() {
        return this.tableModel;
    }
}