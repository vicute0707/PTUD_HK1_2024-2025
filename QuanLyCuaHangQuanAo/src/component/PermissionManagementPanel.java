package component;

import bus.PermissionBUS;
import dialog.PermissionDialog;
import entity.Permission;
import javax.swing.*;
import java.util.List;

public class PermissionManagementPanel extends BaseManagementPanel {
	private final PermissionBUS permissionBUS;

	public PermissionManagementPanel() {
		super("Phân quyền", new String[] { "Mã quyền", "Tên quyền", "Mô tả" }, loadPermissionData());
		permissionBUS = new PermissionBUS();
	}

	private static Object[][] loadPermissionData() {
		PermissionBUS bus = new PermissionBUS();
		List<Permission> permissions = bus.getAllPermissions();
		Object[][] data = new Object[permissions.size()][3];

		for (int i = 0; i < permissions.size(); i++) {
			Permission perm = permissions.get(i);
			data[i] = new Object[] { perm.getPermissionID(), perm.getName(), perm.getDescription() };
		}
		return data;
	}

	@Override
	protected void handleAdd() {
		Permission permission = new Permission();
		PermissionDialog dialog = new PermissionDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thêm quyền",
				permission, true);

		dialog.setVisible(true);

		if (dialog.isConfirmed()) {
			try {
				if (permissionBUS.addPermission(permission)) {
					refreshTable();
					JOptionPane.showMessageDialog(this, "Thêm quyền thành công!");
				} else {
					JOptionPane.showMessageDialog(this, "Không thể thêm quyền", "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@Override
	protected void handleEdit() {
		// Get the selected row from the table
		int selectedRow = table.getSelectedRow();
		if (selectedRow < 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một quyền để chỉnh sửa");
			return;
		}

		// Get the permission ID from the selected row
		String permId = table.getValueAt(selectedRow, 0).toString();
		Permission permission = permissionBUS.getPermissionById(permId);

		if (permission != null) {
			// Create and show the edit dialog
			PermissionDialog dialog = new PermissionDialog((JFrame) SwingUtilities.getWindowAncestor(this),
					"Chỉnh sửa quyền", permission, false);
			dialog.setVisible(true);

			// If the user confirmed the changes, update the permission
			if (dialog.isConfirmed()) {
				try {
					if (permissionBUS.updatePermission(permission)) {
						refreshTable();
						JOptionPane.showMessageDialog(this, "Cập nhật quyền thành công!");
					} else {
						JOptionPane.showMessageDialog(this, "Không thể cập nhật quyền", "Lỗi",
								JOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	@Override
	protected void handleDelete() {
		// Get the selected row from the table
		int selectedRow = table.getSelectedRow();
		if (selectedRow < 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một quyền để xóa");
			return;
		}

		// Get the permission ID from the selected row
		String permId = table.getValueAt(selectedRow, 0).toString();
		Permission permission = permissionBUS.getPermissionById(permId);

		if (permission != null) {
			// Show confirmation dialog
			int response = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa quyền này?", "Xác nhận xóa",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			if (response == JOptionPane.YES_OPTION) {
				try {
					if (permissionBUS.deletePermission(permission.getPermissionID())) {
						refreshTable();
						JOptionPane.showMessageDialog(this, "Xóa quyền thành công!");
					} else {
						JOptionPane.showMessageDialog(this, "Không thể xóa quyền", "Lỗi", JOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	@Override
	protected void handleAbout() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow < 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một quyền để xem chi tiết");
			return;
		}

		String permId = table.getValueAt(selectedRow, 0).toString();
		Permission permission = permissionBUS.getPermissionById(permId);

		if (permission != null) {
			PermissionDialog dialog = new PermissionDialog((JFrame) SwingUtilities.getWindowAncestor(this),
					"Chi tiết quyền", permission, false);
			dialog.setVisible(true);
		}
	}

	private void refreshTable() {
		tableModel.setRowCount(0);
		List<Permission> permissions = permissionBUS.getAllPermissions();
		for (Permission perm : permissions) {
			tableModel.addRow(new Object[] { perm.getPermissionID(), perm.getName(), perm.getDescription() });
		}
	}
}