package component;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import style.CustomScrollBarUI;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

import entity.UserSession;
import export.ExcelExporterNV;
import service.PermissionChecker;

public abstract class BaseManagementPanel extends JPanel {
	protected static final Color PRIMARY_COLOR = new Color(219, 39, 119);
	protected static final Color CONTENT_COLOR = new Color(255, 192, 203);
	protected static final Color HOVER_COLOR = new Color(252, 231, 243);
	protected static final Font HEADER_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12);
	protected static final Font CONTENT_FONT = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12);

	protected JTable table;
	protected String title;
	protected String[] columnNames;
	protected Object[][] data;
	protected DefaultTableModel tableModel;
	protected JTextField searchField;
	private static final Logger LOGGER = Logger.getLogger(BaseManagementPanel.class.getName());


	public BaseManagementPanel(String title, String[] columnNames, Object[][] data) {
		this.title = title;
		this.columnNames = columnNames;
		this.data = data;
		initComponents();
	}

	protected void initComponents() {
		setLayout(new BorderLayout(0, 20));
		setBackground(Color.WHITE);
		setBorder(new EmptyBorder(30, 30, 30, 30));

		// Add top panel with search and buttons
		add(createTopPanel(), BorderLayout.NORTH);

		// Add table panel
		add(createTablePanel(), BorderLayout.CENTER);
	}

	protected JPanel createTopPanel() {
		JPanel topPanel = new JPanel(new BorderLayout(20, 0));
		topPanel.setBackground(Color.WHITE);
		topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

		// Get current user for permission checking
		String currentUserId = UserSession.getInstance().getCurrentUser().getUserID();
		boolean hasUserManagement = PermissionChecker.hasPermission(currentUserId,
				PermissionChecker.PERM_USER_MANAGEMENT);

		// Left components (search)
		JPanel leftPanel = createSearchPanel(currentUserId);

		// Right components (action buttons)
		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
		rightPanel.setBackground(Color.WHITE);

		// Add User Button
		JButton addButton = createRoundedButton("Thêm " + title.toLowerCase(), "/icon/circle-plus.png", true);
		addButton.setBackground(PRIMARY_COLOR);
		addButton.setForeground(Color.WHITE);
		addButton.setPreferredSize(new Dimension(160, 38));
		configureButtonWithPermission(addButton, currentUserId, PermissionChecker.PERM_USER_MANAGEMENT,
				() -> handleAdd());

		// Edit Button
		JButton editButton = createRoundedButton("Sửa", "/icon/pencil.png", true);
		configureButtonWithPermission(editButton, currentUserId, PermissionChecker.PERM_USER_MANAGEMENT,
				() -> handleEdit());

		// Delete Button
		JButton deleteButton = createRoundedButton("Xóa", "/icon/trash.png", true);
		configureButtonWithPermission(deleteButton, currentUserId, PermissionChecker.PERM_USER_MANAGEMENT,
				() -> handleDelete());

		// Info Button
		JButton infoButton = createRoundedButton("About", "/icon/info.png", true);
		configureButtonWithPermission(infoButton, currentUserId, PermissionChecker.PERM_USER_MANAGEMENT,
				() -> handleAbout());

		// Export Button
		JButton exportButton = createRoundedButton("Xuất Excel", "/icon/printer.png", true);
		exportButton.setPreferredSize(new Dimension(160, 38));
		configureButtonWithPermission(exportButton, currentUserId, PermissionChecker.PERM_REPORT, () -> handleExport());

		rightPanel.add(addButton);
		rightPanel.add(editButton);
		rightPanel.add(deleteButton);
		rightPanel.add(infoButton);
		rightPanel.add(exportButton);

		topPanel.add(leftPanel, BorderLayout.WEST);
		topPanel.add(rightPanel, BorderLayout.EAST);

		return topPanel;
	}

	private JPanel createSearchPanel(String currentUserId) {
		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
		leftPanel.setBackground(Color.WHITE);

		JComboBox<String> filterCombo = createFilterComboBox();
		searchField = new JTextField();
		searchField.setPreferredSize(new Dimension(220, 35));
		searchField.setFont(CONTENT_FONT);

		JButton searchButton = createRoundedButton("", "/icon/search.png", false);
		configureButtonWithPermission(searchButton, currentUserId, PermissionChecker.PERM_USER_MANAGEMENT,
				() -> handleSearch());

		leftPanel.add(filterCombo);
		leftPanel.add(searchField);
		leftPanel.add(searchButton);

		return leftPanel;
	}

	private void configureButtonWithPermission(JButton button, String userId, String permission, Runnable action) {
		// Check if user has the required permission
		if (PermissionChecker.hasPermission(userId, permission)) {
			button.setEnabled(true);
			button.addActionListener(e -> {
				if (button.isEnabled()) {
					action.run();
				}
			});
		} else {
			button.setEnabled(false);
			String permissionDesc = PermissionChecker.getPermissionDescription(permission);
			button.setToolTipText("Bạn không có quyền " + permissionDesc);

			// Log attempt to access unauthorized feature
			LOGGER.warning("User " + userId + " attempted to access feature requiring " + permissionDesc);
		}
	}

	// Add logging

	// Update abstract methods to include permission checks
	protected void handleAction(String permission, Runnable action) {
		String currentUserId = UserSession.getInstance().getCurrentUser().getUserID();
		if (PermissionChecker.hasPermission(currentUserId, permission)) {
			action.run();
		} else {
			String permissionDesc = PermissionChecker.getPermissionDescription(permission);
			JOptionPane.showMessageDialog(this, "Bạn không có quyền " + permissionDesc, "Không có quyền truy cập",
					JOptionPane.WARNING_MESSAGE);
			LOGGER.warning("User " + currentUserId + " attempted unauthorized action requiring " + permissionDesc);
		}
	}

	protected JPanel createTablePanel() {
		JPanel tablePanel = new JPanel(new BorderLayout(0, 15));
		tablePanel.setBackground(Color.WHITE);
		tablePanel.setBorder(new EmptyBorder(10, 0, 0, 0));

		// Create table model
		tableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		// Add data to model
		for (Object[] row : data) {
			tableModel.addRow(row);
		}

		// Create and setup table
		table = new JTable(tableModel);
		setupTable();

		// Create scroll pane
		JScrollPane scrollPane = createScrollPane();
		tablePanel.add(scrollPane, BorderLayout.CENTER);

		return tablePanel;
	}

	protected void setupTable() {
		table.setFont(CONTENT_FONT);
		table.setRowHeight(32);
		table.setGridColor(new Color(245, 245, 245));
		table.setSelectionBackground(HOVER_COLOR);
		table.setSelectionForeground(Color.BLACK);
		table.setIntercellSpacing(new Dimension(10, 10));
		table.setPreferredScrollableViewportSize(new Dimension(800, 400));
		table.setFillsViewportHeight(true);

		// Setup header
		JTableHeader header = table.getTableHeader();
		header.setFont(HEADER_FONT);
		header.setBackground(Color.WHITE);
		header.setForeground(Color.BLACK);
		header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
		header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

	}

	protected JScrollPane createScrollPane() {
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.getViewport().setBackground(Color.WHITE);
		scrollPane.setBackground(Color.WHITE);
		scrollPane.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(10, 10, 10, 10),
				BorderFactory.createLineBorder(new Color(245, 245, 245))));

		// Custom scroll bars
		scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
		scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI());

		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		return scrollPane;
	}

	protected JComboBox<String> createFilterComboBox() {
		JComboBox<String> filterCombo = new JComboBox<>(new String[] { "Theo Mã", "Tên" });
		filterCombo.setPreferredSize(new Dimension(120, 35));
		filterCombo.setFont(CONTENT_FONT);
		filterCombo.setBorder(BorderFactory.createEmptyBorder());
		filterCombo.setBackground(Color.WHITE);

		// Custom UI for combo box
		filterCombo.setUI(new CustomComboBoxUI());

		return filterCombo;
	}

	protected JButton createRoundedButton(String text, String iconPath, boolean isRounded) {
		JButton button = new JButton(text);
		button.setFont(CONTENT_FONT);

		if (iconPath != null && !iconPath.isEmpty()) {
			button.setIcon(new ImageIcon(getClass().getResource(iconPath)));
		}

		if (isRounded) {
			button.setBorder(new LineBorder(new Color(230, 230, 230), 1, true));
		} else {
			button.setBorder(BorderFactory.createEmptyBorder());
		}

		button.setFocusPainted(false);
		button.setBackground(Color.WHITE);
		button.setPreferredSize(new Dimension(text.isEmpty() ? 38 : 130, 38));

		// Hover effect
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(HOVER_COLOR);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (button.getForeground().equals(Color.WHITE)) {
					button.setBackground(PRIMARY_COLOR);
				} else {
					button.setBackground(Color.WHITE);
				}
			}
		});

		return button;
	}

	// Abstract methods to be implemented by subclasses
	protected abstract void handleAdd();

	// Common handlers with default implementations
	protected void handleSearch() {
		String searchText = searchField.getText().trim();
		if (searchText.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm!", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		// Implement search logic in subclass if needed
	}

	protected abstract void handleEdit();

	protected abstract void handleDelete();

	protected void handleExport() {
		try {
			ExcelExporterNV.exportToExcel(table, title, this);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Lỗi khi xuất dữ liệu: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	protected abstract void handleAbout();
}

class CustomComboBoxUI extends javax.swing.plaf.basic.BasicComboBoxUI {
	@Override
	protected JButton createArrowButton() {
		JButton button = new JButton();
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setContentAreaFilled(false);
		button.setIcon(new ImageIcon(getClass().getResource("/icon/arrow-down.png")));
		return button;
	}
}