package dialog;

import entity.UserRole;
import entity.Permission;
import bus.PermissionBUS;
import bus.UserRoleBUS;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class UserRoleDialog extends JDialog {
	private JTextField txtId;
	private JTextField txtName;
	private List<JCheckBox> permissionCheckboxes;
	private boolean confirmed = false;
	private UserRole role;
	private boolean isAdd;
	private PermissionBUS permissionBUS;

	public UserRoleDialog(JFrame parent, String title, UserRole role, boolean isAdd) {
		super(parent, title, true);
		this.role = role;
		this.isAdd = isAdd;
		this.permissionBUS = new PermissionBUS();

		initComponents();
		if (!isAdd && role != null) {
			loadRoleData();
		} else {
			generateNewId();
		}
	}

	private void initComponents() {
		setLayout(new BorderLayout(10, 10));

		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// ID Field
		gbc.gridx = 0;
		gbc.gridy = 0;
		mainPanel.add(new JLabel("Mã vai trò:"), gbc);
		txtId = new JTextField(20);
		txtId.setEnabled(false);
		gbc.gridx = 1;
		mainPanel.add(txtId, gbc);

		// Name Field
		gbc.gridx = 0;
		gbc.gridy = 1;
		mainPanel.add(new JLabel("Tên vai trò:"), gbc);
		txtName = new JTextField(20);
		gbc.gridx = 1;
		mainPanel.add(txtName, gbc);

		// Permissions Panel
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		JPanel permissionPanel = new JPanel();
		permissionPanel.setBorder(BorderFactory.createTitledBorder("Quyền hạn"));
		permissionPanel.setLayout(new BoxLayout(permissionPanel, BoxLayout.Y_AXIS));

		permissionCheckboxes = new ArrayList<>();
		List<Permission> permissions = permissionBUS.getAllPermissions();
		for (Permission perm : permissions) {
			JCheckBox checkbox = new JCheckBox(perm.getName());
			checkbox.setActionCommand(perm.getPermissionID());
			permissionCheckboxes.add(checkbox);
			permissionPanel.add(checkbox);
		}
		mainPanel.add(permissionPanel, gbc);

		// Buttons
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnSave = new JButton("Lưu");
		JButton btnCancel = new JButton("Hủy");

		btnSave.addActionListener(e -> {
			if (validateInput()) {
				updateRoleFromFields();
				confirmed = true;
				dispose();
			}
		});
		btnCancel.addActionListener(e -> dispose());

		buttonPanel.add(btnSave);
		buttonPanel.add(btnCancel);

		add(mainPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(getParent());
	}

	private void generateNewId() {
		try {
			String newId = new UserRoleBUS().generateNewRoleId();
			txtId.setText(newId);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Lỗi tạo mã mới: " + e.getMessage());
		}
	}

	private boolean validateInput() {
		if (txtName.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Tên vai trò không được để trống");
			return false;
		}

		boolean hasSelectedPermission = false;
		for (JCheckBox checkbox : permissionCheckboxes) {
			if (checkbox.isSelected()) {
				hasSelectedPermission = true;
				break;
			}
		}

		if (!hasSelectedPermission) {
			JOptionPane.showMessageDialog(this, "Phải chọn ít nhất một quyền");
			return false;
		}

		return true;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	private void loadRoleData() {
		txtId.setText(role.getRoleID());
		txtName.setText(role.getRoleName());

		String permissions = role.getPermissions().replace("[", "").replace("]", "").replace("\"", "");

		String[] permissionIds = permissions.split(",");
		for (String permId : permissionIds) {
			for (JCheckBox checkbox : permissionCheckboxes) {
				if (checkbox.getActionCommand().equals(permId.trim())) {
					checkbox.setSelected(true);
				}
			}
		}
	}

	private void updateRoleFromFields() {
		role.setRoleID(txtId.getText().trim());
		role.setRoleName(txtName.getText().trim());

		List<String> selectedPermissions = new ArrayList<>();
		for (JCheckBox checkbox : permissionCheckboxes) {
			if (checkbox.isSelected()) {
				selectedPermissions.add(checkbox.getActionCommand());
			}
		}

		String permissions = String.join(",", selectedPermissions);
		role.setPermissions(permissions);
	}
}
