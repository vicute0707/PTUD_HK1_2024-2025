package component;

import entity.User;
import entity.UserRole;
import bus.UserBUS;
import bus.UserRoleBUS;
import dialog.EmployeeDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.stream.Collectors;
import java.awt.Dimension;
import java.awt.Font;
import java.text.SimpleDateFormat;

public class EmployeeManagementPanel extends BaseManagementPanel {
	private final UserBUS userBUS;
	private final UserRoleBUS userRoleBUS;
	private final SimpleDateFormat dateFormat;
	private JComboBox<String> roleComboBox;
	private JComboBox<String> statusComboBox;

	public EmployeeManagementPanel() {
		super("Quản lý nhân viên", new String[] { "Mã NV", "Họ tên", "Tên đăng nhập", "Giới tính", "Ngày sinh", "SDT",
				"Email", "Vai trò", "Trạng thái" }, loadEmployeeData());
		userBUS = new UserBUS();
		userRoleBUS = new UserRoleBUS();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		loadRoles();
	}

	private void loadRoles() {
		try {
			List<UserRole> roles = userRoleBUS.getAllRoles();
			roleComboBox.removeAllItems();
			roleComboBox.addItem("Tất cả");
			for (UserRole role : roles) {
				roleComboBox.addItem(role.getRoleName());
			}
		} catch (Exception e) {
			
		}
	}

	@Override
	protected void handleAdd() {
		try {
			User newUser = new User();
			newUser.setUserID(userBUS.generateNewUserID());
			newUser.setStatus("Đang hoạt động");

			EmployeeDialog dialog = new EmployeeDialog((JFrame) SwingUtilities.getWindowAncestor(this),
					"Thêm nhân viên mới", newUser, true);

			dialog.setLocationRelativeTo(this);
			dialog.setVisible(true);

			if (dialog.isConfirmed()) {
				boolean success = userBUS.addUser(newUser);
				if (success) {
					refreshTable();
					JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
				} else {
					JOptionPane.showMessageDialog(this, "Không thể thêm nhân viên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Lỗi khi thêm nhân viên: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	protected void handleAbout() {
		try {
			int selectedRow = table.getSelectedRow();
			if (selectedRow < 0) {
				JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân viên để xem chi tiết.", "Thông báo",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			String userID = table.getValueAt(selectedRow, 0).toString();
			User user = userBUS.getUserById(userID);

			if (user != null) {
				StringBuilder details = new StringBuilder();
				details.append("Thông tin chi tiết nhân viên:\n\n");
				details.append("Mã NV: ").append(user.getUserID()).append("\n");
				details.append("Họ tên: ").append(user.getFullName()).append("\n");
				details.append("Tên đăng nhập: ").append(user.getUsername()).append("\n");
				details.append("Giới tính: ").append(user.getGender()).append("\n");
				details.append("Ngày sinh: ").append(dateFormat.format(user.getBirthDate())).append("\n");
				details.append("Số điện thoại: ").append(user.getPhone()).append("\n");
				details.append("Email: ").append(user.getEmail()).append("\n");
				details.append("Vai trò: ").append(user.getRole()).append("\n");
				details.append("Trạng thái: ").append(user.getStatus()).append("\n");

				JTextArea textArea = new JTextArea(details.toString());
				textArea.setEditable(false);
				textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
				textArea.setMargin(new java.awt.Insets(10, 10, 10, 10));

				JScrollPane scrollPane = new JScrollPane(textArea);
				scrollPane.setPreferredSize(new Dimension(400, 300));

				JOptionPane.showMessageDialog(this, scrollPane, "Chi tiết nhân viên", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Lỗi khi hiển thị thông tin: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void refreshTable() {
		try {
			List<User> users = userBUS.getAllUsers();
			updateTableData(users);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Lỗi khi làm mới bảng: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void updateTableData(List<User> users) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.setRowCount(0);

		for (User user : users) {
			model.addRow(new Object[] { user.getUserID(), user.getFullName(), user.getUsername(), user.getGender(),
					dateFormat.format(user.getBirthDate()), user.getPhone(), user.getEmail(), userRoleBUS.getRoleById(user.getRole()).getRoleName(),
					user.getStatus() });
		}
	}

	private static Object[][] loadEmployeeData() {
		UserBUS bus = new UserBUS();
		UserRoleBUS userRoleBUS = new UserRoleBUS();
		List<User> users = bus.getAllUsers();
		Object[][] data = new Object[users.size()][9];
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		for (int i = 0; i < users.size(); i++) {
			User user = users.get(i);
			data[i] = new Object[] { user.getUserID(), user.getFullName(), user.getUsername(), user.getGender(),
					sdf.format(user.getBirthDate()), user.getPhone(), user.getEmail(), userRoleBUS.getRoleById(user.getRole()).getRoleName(),
					user.getStatus() };
		}
		return data;
	}

	@Override
	protected void handleEdit() {
		try {
			int selectedRow = table.getSelectedRow();
			if (selectedRow < 0) {
				JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân viên để sửa.", "Thông báo",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			String userID = table.getValueAt(selectedRow, 0).toString();
			User user = userBUS.getUserById(userID);

			if (user == null) {
				JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin nhân viên.", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			EmployeeDialog dialog = new EmployeeDialog((JFrame) SwingUtilities.getWindowAncestor(this),
					"Sửa thông tin nhân viên", user, false // isNew = false for editing
			);
			dialog.setVisible(true);

			if (dialog.isConfirmed()) {
				refreshTable();
				JOptionPane.showMessageDialog(this, "Cập nhật thông tin nhân viên thành công!");
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Lỗi khi sửa nhân viên: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	protected void handleDelete() {
		try {
			int selectedRow = table.getSelectedRow();
			if (selectedRow < 0) {
				JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân viên để xóa.", "Thông báo",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			String userID = table.getValueAt(selectedRow, 0).toString();
			String fullName = table.getValueAt(selectedRow, 1).toString();

			int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa nhân viên " + fullName + "?",
					"Xác nhận xóa", JOptionPane.YES_NO_OPTION);

			if (confirm == JOptionPane.YES_OPTION) {
				boolean success = userBUS.deleteUser(userID);
				if (success) {
					refreshTable();
					JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!");
				} else {
					JOptionPane.showMessageDialog(this, "Không thể xóa nhân viên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Lỗi khi xóa nhân viên: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}