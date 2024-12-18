package dialog;

import entity.Permission;
import java.awt.*;
import javax.swing.*;

import bus.PermissionBUS;

public class PermissionDialog extends JDialog {
	private JTextField txtId;
	private JTextField txtName;
	private JPanel permissionPanel;
	private boolean confirmed = false;
	private Permission permission;
	private boolean isAdd;

	private JCheckBox[] permissionCheckboxes = new JCheckBox[] { new JCheckBox("Quản lý người dùng"),
			new JCheckBox("Quản lý sản phẩm"), new JCheckBox("Quản lý kho"), new JCheckBox("Quản lý bán hàng"),
			new JCheckBox("Quản lý nhập hàng"), new JCheckBox("Quản lý báo cáo"), new JCheckBox("Quản lý phân quyền") };

	public PermissionDialog(JFrame parent, String title, Permission permission, boolean isAdd) {
		super(parent, title, true);
		this.permission = permission;
		this.isAdd = isAdd;

		initComponents();
		if (!isAdd && permission != null) {
			loadPermissionData();
		} else {
			generateNewId();
		}
	}

	private void initComponents() {
		setLayout(new BorderLayout(10, 10));

		// Main panel
		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// ID Field
		gbc.gridx = 0;
		gbc.gridy = 0;
		mainPanel.add(new JLabel("Mã quyền:"), gbc);
		txtId = new JTextField(20);
		txtId.setEnabled(false);
		gbc.gridx = 1;
		mainPanel.add(txtId, gbc);

		// Name Field
		gbc.gridx = 0;
		gbc.gridy = 1;
		mainPanel.add(new JLabel("Tên quyền:"), gbc);
		txtName = new JTextField(20);
		gbc.gridx = 1;
		mainPanel.add(txtName, gbc);

		// Permissions Panel
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		permissionPanel = new JPanel();
		permissionPanel.setBorder(BorderFactory.createTitledBorder("Chức năng"));
		permissionPanel.setLayout(new BoxLayout(permissionPanel, BoxLayout.Y_AXIS));

		for (JCheckBox checkbox : permissionCheckboxes) {
			permissionPanel.add(checkbox);
		}
		mainPanel.add(permissionPanel, gbc);

		// Button Panel
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnSave = new JButton("Lưu");
		JButton btnCancel = new JButton("Hủy");

		btnSave.addActionListener(e -> {
			if (validateInput()) {
				updatePermissionFromFields();
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
			String newId = new PermissionBUS().generateNewPermissionId();
			txtId.setText(newId);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Lỗi khi tạo mã quyền mới");
		}
	}

	private void loadPermissionData() {
		txtId.setText(permission.getPermissionID());
		txtName.setText(permission.getName());

		String description = permission.getDescription();
		if (description != null) {
			String[] permissions = description.split(",");
			for (String perm : permissions) {
				for (JCheckBox checkbox : permissionCheckboxes) {
					if (checkbox.getText().equals(perm.trim())) {
						checkbox.setSelected(true);
					}
				}
			}
		}
	}

	private boolean validateInput() {
		if (txtName.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Tên quyền không được để trống");
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
			JOptionPane.showMessageDialog(this, "Phải chọn ít nhất một chức năng");
			return false;
		}

		return true;
	}

	private void updatePermissionFromFields() {
		permission.setPermissionID(txtId.getText().trim());
		permission.setName(txtName.getText().trim());

		StringBuilder description = new StringBuilder();
		for (JCheckBox checkbox : permissionCheckboxes) {
			if (checkbox.isSelected()) {
				if (description.length() > 0) {
					description.append(", ");
				}
				description.append(checkbox.getText());
			}
		}
		permission.setDescription(description.toString());
	}

	public boolean isConfirmed() {
		return confirmed;
	}
}