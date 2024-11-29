package component;

import bus.UserRoleBUS;
import dao.PermissionDAO;
import dialog.UserRoleDialog;
import entity.UserRole;
import javax.swing.*;
import java.util.List;

public class UserRoleManagementPanel extends BaseManagementPanel {
	private final UserRoleBUS userRoleBUS;

	public UserRoleManagementPanel() {
		super("Quản lý vai trò", new String[] { "Mã vai trò", "Tên vai trò", "Quyền hạn" }, loadRoleData());
		userRoleBUS = new UserRoleBUS();
	}

	@Override
	protected void handleAdd() {
		UserRole role = new UserRole();
		role.setRoleID(userRoleBUS.generateNewRoleId());

		UserRoleDialog dialog = new UserRoleDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thêm vai trò",
				role, true);

		dialog.setVisible(true);

		if (dialog.isConfirmed()) {
			try {
				if (userRoleBUS.addRole(role)) {
					refreshTable();
					JOptionPane.showMessageDialog(this, "Thêm vai trò thành công!");
				} else {
					JOptionPane.showMessageDialog(this, "Không thể thêm vai trò", "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@Override
	protected void handleEdit() {
		// Check if a row is selected in the table
		int selectedRow = table.getSelectedRow();
		if (selectedRow < 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một vai trò để chỉnh sửa");
			return;
		}

		// Retrieve the role ID from the selected row and get the corresponding role
		// object
		String roleId = table.getValueAt(selectedRow, 0).toString();
		UserRole role = userRoleBUS.getRoleById(roleId);

		if (role != null) {
			// Create and show the edit dialog
			UserRoleDialog dialog = new UserRoleDialog((JFrame) SwingUtilities.getWindowAncestor(this),
					"Chỉnh sửa vai trò", role, false);
			dialog.setVisible(true);

			// If the user confirms the changes, update the role
			if (dialog.isConfirmed()) {
				try {
					if (userRoleBUS.updateRole(role)) {
						refreshTable();
						JOptionPane.showMessageDialog(this, "Cập nhật vai trò thành công!");
					} else {
						JOptionPane.showMessageDialog(this, "Không thể cập nhật vai trò", "Lỗi",
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
		// Check if a row is selected in the table
		int selectedRow = table.getSelectedRow();
		if (selectedRow < 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một vai trò để xóa");
			return;
		}

		// Get the role ID from the selected row
		String roleId = table.getValueAt(selectedRow, 0).toString();
		UserRole role = userRoleBUS.getRoleById(roleId);

		if (role != null) {
			// Show confirmation dialog before proceeding with deletion
			int response = JOptionPane.showConfirmDialog(this,
					"Bạn có chắc chắn muốn xóa vai trò này?\n"
							+ "Lưu ý: Xóa vai trò có thể ảnh hưởng đến các tài khoản đang sử dụng vai trò này.",
					"Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

			if (response == JOptionPane.YES_OPTION) {
				try {
					// Attempt to delete the role
					if (userRoleBUS.deleteRole(roleId)) {
						refreshTable();
						JOptionPane.showMessageDialog(this, "Xóa vai trò thành công!");
					} else {
						JOptionPane.showMessageDialog(this, "Không thể xóa vai trò. Vai trò có thể đang được sử dụng.",
								"Lỗi", JOptionPane.ERROR_MESSAGE);
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
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một vai trò để chỉnh sửa");
			return;
		}

		String roleId = table.getValueAt(selectedRow, 0).toString();
		UserRole role = userRoleBUS.getRoleById(roleId);

		if (role != null) {
			UserRoleDialog dialog = new UserRoleDialog((JFrame) SwingUtilities.getWindowAncestor(this),
					"Chỉnh sửa vai trò", role, false);
			dialog.setVisible(true);

			if (dialog.isConfirmed()) {
				try {
					if (userRoleBUS.updateRole(role)) {
						refreshTable();
						JOptionPane.showMessageDialog(this, "Cập nhật vai trò thành công!");
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private static Object[][] loadRoleData() {
		UserRoleBUS bus = new UserRoleBUS();
		PermissionDAO permissionDAO = new PermissionDAO();
		List<UserRole> roles = bus.getAllRoles();
		Object[][] data = new Object[roles.size()][3];

		for (int i = 0; i < roles.size(); i++) {
			UserRole role = roles.get(i);
			// Clean up the permissions string by removing JSON formatting
			String permissions = role.getPermissions().replace("[", "").replace("]", "").replace("\"", "");
			String[] permissionIds = permissions.split(",");

			// Build permission names string
			StringBuilder permissionNames = new StringBuilder();
			for (int j = 0; j < permissionIds.length; j++) {
				String permId = permissionIds[j].trim();
				if (!permId.isEmpty()) {
					if (j > 0) {
						permissionNames.append(", ");
					}
					String permName = permissionDAO.getPermissionNameById(permId);
					permissionNames.append(permName);
				}
			}

			data[i] = new Object[] { role.getRoleID(), role.getRoleName(), permissionNames.toString() };
		}
		return data;
	}

	private void refreshTable() {
		try {
			tableModel.setRowCount(0);
			List<UserRole> roles = userRoleBUS.getAllRoles();
			PermissionDAO permissionDAO = new PermissionDAO();

			for (UserRole role : roles) {
				// Clean up the permissions string
				String permissionStr = role.getPermissions().replace("[", "").replace("]", "").replace("\"", "");
				String[] permissionIds = permissionStr.split(",");

				// Convert permission IDs to names
				StringBuilder permissionNames = new StringBuilder();
				for (int i = 0; i < permissionIds.length; i++) {
					String permId = permissionIds[i].trim();
					if (!permId.isEmpty()) {
						if (i > 0) {
							permissionNames.append(", ");
						}
						String permName = permissionDAO.getPermissionNameById(permId);
						if (permName != null && !permName.isEmpty()) {
							permissionNames.append(permName);
						} else {
							permissionNames.append(permId); // Fallback to ID if name not found
						}
					}
				}

				tableModel.addRow(new Object[] { role.getRoleID(), role.getRoleName(), permissionNames.toString() });
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Lỗi khi làm mới bảng: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}