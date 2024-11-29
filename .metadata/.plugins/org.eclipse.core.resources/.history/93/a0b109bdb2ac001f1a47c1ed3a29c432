package component;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import entity.UserRole;
import style.CreateRoundedButton;

public class ButtonChucNangMenu extends JPanel {
	// Constants from Form_SanPham
	private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
	private static final Color CONTENT_COLOR = new Color(255, 192, 203);
	private static final Color HOVER_COLOR = new Color(252, 231, 243);

	private HashMap<String, JButton> buttons;
	private UserRole userRole;
	private CreateRoundedButton styleBtn;

	public ButtonChucNangMenu(UserRole userRole) {
		this.userRole = userRole;
		this.styleBtn = new CreateRoundedButton();

		setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 0));
		setBackground(Color.WHITE);
		initButtons();
	}

	private void initButtons() {
		buttons = new HashMap<>();

		// Tạo các button với style giống Form_SanPham
		buttons.put("ADD_ITEM", createPrimaryButton("Thêm sản phẩm", "/icon/circle-plus.png"));
		buttons.put("EDIT_ITEM", createSecondaryButton("Edit", "/icon/pencil.png"));
		buttons.put("DELETE_ITEM", createSecondaryButton("Xóa", "/icon/trash.png"));
		buttons.put("VIEW_DETAIL", createSecondaryButton("About", "/icon/info.png"));
		buttons.put("EXPORT_EXCEL", createSecondaryButton("Xuất Excel", "/icon/printer.png"));

		// Thêm buttons vào panel theo thứ tự
		String[] buttonOrder = { "ADD_ITEM", "EDIT_ITEM", "DELETE_ITEM", "VIEW_DETAIL", "EXPORT_EXCEL" };

		for (String permission : buttonOrder) {
			JButton button = buttons.get(permission);
			if (button != null) {
				add(button);
			}
		}

		updateButtonVisibility();
	}

	private JButton createPrimaryButton(String text, String iconPath) {
		JButton button = styleBtn.createRoundedButton(text, iconPath, true);
		button.setBackground(PRIMARY_COLOR);
		button.setForeground(Color.WHITE);
		button.setPreferredSize(new Dimension(160, 38));
		return button;
	}

	private JButton createSecondaryButton(String text, String iconPath) {
		JButton button = styleBtn.createRoundedButton(text, iconPath, true);
		if (text.equals("Xuất Excel")) {
			button.setPreferredSize(new Dimension(160, 38));
		}
		return button;
	}

	private void updateButtonVisibility() {
		if (userRole == null)
			return;

		java.util.Set<String> permissions = userRole.getPermissionSet();

		// Cập nhật trạng thái của từng button dựa trên quyền
		  buttons.forEach((permission, button) -> {
	            boolean hasPermission = permissions.contains(permission);
	            button.setVisible(hasPermission);
	            button.setEnabled(hasPermission);
	        });

		// Xử lý đặc biệt cho role
		if (userRole.isAdminRole()) {
			buttons.values().forEach(button -> {
				button.setVisible(true);
				button.setEnabled(true);
			});
			return;
		} else if (userRole.isManagerRole()) {
			buttons.get("DELETE_ITEM").setVisible(false);
		}
	}

	public JButton getButton(String permission) {
		return buttons.get(permission);
	}

	public void updateUserRole(UserRole newRole) {
		this.userRole = newRole;
		updateButtonVisibility();
	}

	// Helper method để thêm tooltip
	public void addTooltip(String permission, String tooltip) {
		JButton button = buttons.get(permission);
		if (button != null) {
			button.setToolTipText(tooltip);
		}
	}
}