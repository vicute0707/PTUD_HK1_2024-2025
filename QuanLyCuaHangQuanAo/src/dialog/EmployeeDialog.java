package dialog;

import com.toedter.calendar.JDateChooser;

import bus.UserBUS;
import bus.UserRoleBUS;
import component.ComboItem;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Date;
import entity.User;
import entity.UserRole;
import style.CreateRoundedButton;

public class EmployeeDialog extends JDialog {
	private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
	private final User user;
	private boolean confirmed = false;
	private boolean isNewUser;
	private final UserRoleBUS userRoleBUS;
    private JComboBox<ComboItem> roleComboBox; // Changed variable name for consistency

	private JTextField txtFullName, txtPhone, txtEmail, txtUsername, txtPassword;
	private JComboBox<String> cboGender;
	private JComboBox<ComboItem> cboRole;
	private JDateChooser dateChooser;

	public EmployeeDialog(Frame owner, String title, User user, boolean isNew) {
		  super(owner, title, true);
	        this.user = user;
	        this.isNewUser = isNew;
	        this.userRoleBUS = new UserRoleBUS();
	        
	        // Initialize all components first
	        initializeFields();
	        // Then set up the layout
	        initializeLayout();
	        // Load data after everything is initialized
	        loadUserData();
	        
	        // Set dialog properties
	        setSize(450, 550);
	        setLocationRelativeTo(owner);
	        setResizable(false);
	}

    private void initializeFields() {
        // Initialize all fields before creating the layout
        txtFullName = createStyledTextField();
        txtUsername = createStyledTextField();
        txtPassword = createStyledTextField();
        cboGender = new JComboBox<>(new String[] { "Nam", "Nữ" });
        dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(200, 30));
        txtPhone = createStyledTextField();
        txtEmail = createStyledTextField();
        cboRole = new JComboBox<>();
        
        // Load roles immediately after creating the combobox
        loadRoles();
    }

    private void loadRoles() {
        try {
            java.util.List<UserRole> roles = userRoleBUS.getAllRoles();
            cboRole.removeAllItems();
            
            // Add a default selection prompt
            cboRole.addItem(new ComboItem("", "-- Chọn vai trò --"));
            
            for (UserRole role : roles) {
                cboRole.addItem(new ComboItem(role.getRoleID(), role.getRoleName()));
            }
        } catch (Exception e) {
            
        }
    }

    private void initializeLayout() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Set up constraints
        GridBagConstraints gbc = createGBC();

        // Add components to the panel
        int row = 0;
        addFormField(mainPanel, gbc, "Họ tên:", txtFullName, row++);
        addFormField(mainPanel, gbc, "Tên đăng nhập:", txtUsername, row++);
        
        if (isNewUser) {
            addFormField(mainPanel, gbc, "Mật khẩu:", txtPassword, row++);
        }
        
        addFormField(mainPanel, gbc, "Giới tính:", cboGender, row++);
        addFormField(mainPanel, gbc, "Ngày sinh:", dateChooser, row++);
        addFormField(mainPanel, gbc, "SDT:", txtPhone, row++);
        addFormField(mainPanel, gbc, "Email:", txtEmail, row++);
        addFormField(mainPanel, gbc, "Vai trò:", cboRole, row);

        // Create button panel
        JPanel buttonPanel = createButtonPanel();

        // Add panels to dialog
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

	private void initComponents() {
		setLayout(new BorderLayout());
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		GridBagConstraints gbc = createGBC();

		// Initialize components
		txtFullName = createStyledTextField();
		txtUsername = createStyledTextField();
		txtPassword = createStyledTextField();
		txtPassword.setEnabled(isNewUser); // Only enable for new users
		cboGender = new JComboBox<>(new String[] { "Nam", "Nữ" });
		dateChooser = new JDateChooser();
		dateChooser.setPreferredSize(new Dimension(200, 30));
		txtPhone = createStyledTextField();
		txtEmail = createStyledTextField();
		cboRole = new JComboBox<>();
		loadRoles();

		// Add components
		addFormField(mainPanel, gbc, "Họ tên:", txtFullName, 0);
		addFormField(mainPanel, gbc, "Tên đăng nhập:", txtUsername, 1);
		if (isNewUser) {
			addFormField(mainPanel, gbc, "Mật khẩu:", txtPassword, 2);
		}
		addFormField(mainPanel, gbc, "Giới tính:", cboGender, isNewUser ? 3 : 2);
		addFormField(mainPanel, gbc, "Ngày sinh:", dateChooser, isNewUser ? 4 : 3);
		addFormField(mainPanel, gbc, "SDT:", txtPhone, isNewUser ? 5 : 4);
		addFormField(mainPanel, gbc, "Email:", txtEmail, isNewUser ? 6 : 5);
		addFormField(mainPanel, gbc, "Vai trò:", cboRole, isNewUser ? 7 : 6);
		// Buttons
		JPanel buttonPanel = createButtonPanel();

		add(mainPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}



	private void loadUserData() {
		if (user != null) {
			txtFullName.setText(user.getFullName());
			txtUsername.setText(user.getUsername());
			cboGender.setSelectedItem(user.getGender());
			dateChooser.setDate(user.getBirthDate());
			txtPhone.setText(user.getPhone());
			txtEmail.setText(user.getEmail());

			// Set selected role
			for (int i = 0; i < cboRole.getItemCount(); i++) {
				ComboItem item = cboRole.getItemAt(i);
				if (item.getValue().equals(user.getRole())) {
					cboRole.setSelectedItem(item);
					break;
				}
			}
		}
	}

	private void save() {
		try {
			validateInput();

			user.setFullName(txtFullName.getText().trim());
			user.setUsername(txtUsername.getText().trim());
			if (isNewUser) {
				user.setPassword(txtPassword.getText().trim());
			}
			user.setGender(cboGender.getSelectedItem().toString());
			user.setBirthDate(dateChooser.getDate());
			user.setPhone(txtPhone.getText().trim());
			user.setEmail(txtEmail.getText().trim());

			ComboItem selectedRole = (ComboItem) cboRole.getSelectedItem();
			user.setRole(selectedRole.getValue());

			UserBUS bus = new UserBUS();
			boolean success;
			if (isNewUser) {
				success = bus.addUser(user);
			} else {
				success = bus.updateUser(user);
			}

			if (success) {
				confirmed = true;
				dispose();
			} else {
				throw new Exception("Không thể " + (isNewUser ? "thêm" : "cập nhật") + " nhân viên");
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
		}
	}

	private void validateInput() throws Exception {
		if (txtFullName.getText().trim().isEmpty()) {
			throw new Exception("Họ tên không được để trống");
		}
		if (dateChooser.getDate() == null) {
			throw new Exception("Vui lòng chọn ngày sinh");
		}
		// Add more validations as needed
	}

	private JTextField createStyledTextField() {
		JTextField field = new JTextField();
		field.setPreferredSize(new Dimension(200, 30));
		field.setFont(new Font("Roboto", Font.PLAIN, 14));
		return field;
	}

	private GridBagConstraints createGBC() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		return gbc;
	}

	private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int row) {
		gbc.gridy = row;

		gbc.gridx = 0;
		gbc.weightx = 0.3;
		JLabel label = new JLabel(labelText);
		label.setFont(new Font("Roboto", Font.BOLD, 14));
		panel.add(label, gbc);

		gbc.gridx = 1;
		gbc.weightx = 0.7;
		panel.add(field, gbc);
	}

	public boolean isConfirmed() {
		return confirmed;
	}
	private JPanel createButtonPanel() {
	    JPanel panel = new JPanel();
	    panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
	    panel.setBorder(new EmptyBorder(5, 10, 10, 10));

	    // Create buttons with proper styling
	    JButton btnSave = createStyledButton("Lưu", e -> save());
	    JButton btnReset = createStyledButton("Làm mới", e -> resetForm());
	    JButton btnCancel = createStyledButton("Hủy", e -> handleCancel());

	    // Style the save button as primary
	    btnSave.setBackground(PRIMARY_COLOR);
	    btnSave.setForeground(Color.WHITE);
	    
	    // Add hover effects
	    addHoverEffect(btnSave);
	    addHoverEffect(btnReset);
	    addHoverEffect(btnCancel);

	    // Add buttons to panel
	    panel.add(btnSave);
	    panel.add(btnReset);
	    panel.add(btnCancel);

	    return panel;
	}

	private JButton createStyledButton(String text, ActionListener listener) {
	    JButton button = new JButton(text);
	    button.setPreferredSize(new Dimension(100, 35));
	    button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
	    button.setFocusPainted(false);
	    button.setBorder(new EmptyBorder(5, 15, 5, 15));
	    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    button.addActionListener(listener);
	    return button;
	}

	private void addHoverEffect(JButton button) {
	    Color originalColor = button.getBackground();
	    button.addMouseListener(new java.awt.event.MouseAdapter() {
	        public void mouseEntered(java.awt.event.MouseEvent evt) {
	            button.setBackground(button.getBackground().darker());
	        }
	        public void mouseExited(java.awt.event.MouseEvent evt) {
	            button.setBackground(originalColor);
	        }
	    });
	}

	private void resetForm() {
	    txtFullName.setText("");
	    txtUsername.setText("");
	    if (isNewUser) {
	        txtPassword.setText("");
	    }
	    dateChooser.setDate(null);
	    txtPhone.setText("");
	    txtEmail.setText("");
	    cboGender.setSelectedIndex(0);
	    cboRole.setSelectedIndex(0);
	}

	private void handleCancel() {
	    if (isFormModified()) {
	        int response = JOptionPane.showConfirmDialog(
	            this,
	            "Bạn có chắc muốn hủy? Các thay đổi sẽ không được lưu.",
	            "Xác nhận",
	            JOptionPane.YES_NO_OPTION,
	            JOptionPane.QUESTION_MESSAGE
	        );
	        if (response == JOptionPane.YES_OPTION) {
	            dispose();
	        }
	    } else {
	        dispose();
	    }
	}

	private boolean isFormModified() {
	    if (user == null) return false;
	    
	    return !txtFullName.getText().equals(user.getFullName() != null ? user.getFullName() : "") ||
	           !txtUsername.getText().equals(user.getUsername() != null ? user.getUsername() : "") ||
	           !txtPhone.getText().equals(user.getPhone() != null ? user.getPhone() : "") ||
	           !txtEmail.getText().equals(user.getEmail() != null ? user.getEmail() : "") ||
	           (dateChooser.getDate() != null && !dateChooser.getDate().equals(user.getBirthDate())) ||
	           !cboGender.getSelectedItem().toString().equals(user.getGender() != null ? user.getGender() : "") ||
	           !((ComboItem)cboRole.getSelectedItem()).getValue().equals(user.getRole());
	}
}