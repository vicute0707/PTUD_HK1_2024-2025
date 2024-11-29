package component;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

class StatusColumnRenderer extends DefaultTableCellRenderer {
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