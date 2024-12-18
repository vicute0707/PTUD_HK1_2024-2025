package component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DeleteHandler {
    public static void handleDelete(JPanel parent, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parent,
                "Vui lòng chọn một dòng để xóa",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(parent,
            "Bạn có chắc chắn muốn xóa mục này?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.removeRow(table.convertRowIndexToModel(selectedRow));
            JOptionPane.showMessageDialog(parent,
                "Xóa thành công!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}