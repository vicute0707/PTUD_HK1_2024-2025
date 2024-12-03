package dialog;

import java.awt.*;
import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import bus.ImportBUS;
import entity.Import;
import entity.ImportDetail;
import export.ExcelExporterHoaDonNhap;
import export.PDFExporterHoaDonNhap;
import style.StyleButton;

public class ChiTietPhieuNhap extends JDialog {
	// Constants for styling
	private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
	private static final Color CONTENT_COLOR = new Color(255, 242, 242);
	private static final Color HOVER_COLOR = new Color(252, 231, 243);
	private static final Font HEADER_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 14);
	private static final Font CONTENT_FONT = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12);
	private static final Font TITLE_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 16);

	// Business logic and logging
	private final ImportBUS importBUS;
	private static final Logger LOGGER = Logger.getLogger(ChiTietPhieuNhap.class.getName());
	private final NumberFormat currencyFormat;

	// UI Components
	private JTable mainTable;
	private DefaultTableModel detailModel;
	private JDialog detailDialog;

	public ChiTietPhieuNhap() {
		importBUS = new ImportBUS();
		currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
	}

	public void showImportDetails(JTable table) {
		this.mainTable = table;
		int selectedRow = table.getSelectedRow();

		if (selectedRow == -1) {
			showWarningMessage("Vui lòng chọn phiếu nhập cần xem chi tiết!");
			return;
		}

		try {
			String importId = table.getValueAt(selectedRow, 0).toString();
			List<ImportDetail> details = importBUS.getImportDetailsByImportId(importId);

			if (details == null || details.isEmpty()) {
				showWarningMessage("Không tìm thấy chi tiết phiếu nhập!");
				return;
			}

			createAndShowDetailDialog(details, selectedRow);

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error loading import details", e);
			showErrorMessage("Lỗi khi tải chi tiết phiếu nhập: " + e.getMessage());
		}
	}

	private void createAndShowDetailDialog(List<ImportDetail> details, int selectedRow) {
		detailDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi tiết phiếu nhập", true);
		detailDialog.setLayout(new BorderLayout());

		// Create main content panel
		JPanel contentPanel = createContentPanel(details, selectedRow);
		JPanel buttonPanel = createButtonPanel();

		detailDialog.add(contentPanel, BorderLayout.CENTER);
		detailDialog.add(buttonPanel, BorderLayout.SOUTH);
		detailDialog.setSize(800, 600);
		detailDialog.setLocationRelativeTo(this);
		detailDialog.setVisible(true);
	}

	private JPanel createContentPanel(List<ImportDetail> details, int selectedRow) {
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

		// Add import information panel
		JPanel infoPanel = createInfoPanel(selectedRow);
		contentPanel.add(infoPanel);
		contentPanel.add(Box.createVerticalStrut(20));

		// Add details table
		JTable detailTable = createDetailTable(details);
		JScrollPane scrollPane = createScrollPane(detailTable);

		// Add detail label
		JLabel detailLabel = new JLabel("Chi tiết sản phẩm");
		detailLabel.setFont(HEADER_FONT);
		detailLabel.setForeground(PRIMARY_COLOR);

		contentPanel.add(detailLabel);
		contentPanel.add(Box.createVerticalStrut(10));
		contentPanel.add(scrollPane);

		return contentPanel;
	}

	private JPanel createInfoPanel(int selectedRow) {
		JPanel infoPanel = new JPanel(new GridBagLayout());
		infoPanel.setBackground(Color.WHITE);
		infoPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 10, 5, 10);

		// Add import information fields
		addDetailField(infoPanel, "Mã phiếu nhập:", mainTable.getValueAt(selectedRow, 0).toString(), gbc, 0);
		addDetailField(infoPanel, "Ngày nhập:", mainTable.getValueAt(selectedRow, 1).toString(), gbc, 1);
		addDetailField(infoPanel, "Nhà cung cấp:", mainTable.getValueAt(selectedRow, 2).toString(), gbc, 2);
		addDetailField(infoPanel, "Nhân viên:", mainTable.getValueAt(selectedRow, 3).toString(), gbc, 3);
		addDetailField(infoPanel, "Tổng tiền:", mainTable.getValueAt(selectedRow, 4).toString(), gbc, 4);

		return infoPanel;
	}

	private JTable createDetailTable(List<ImportDetail> details) {
		String[] columns = { "Mã SP", "Tên sản phẩm", "Phân loại", "Đơn giá", "Số lượng", "Thành tiền" };
		detailModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		// Populate table data
		for (ImportDetail detail : details) {
			double totalAmount = detail.getQuantity() * detail.getPrice();
			Object[] row = { detail.getVariantID(), detail.getProductName(), detail.getVariantName(),
					currencyFormat.format(detail.getPrice()), detail.getQuantity(),
					currencyFormat.format(totalAmount) };
			detailModel.addRow(row);
		}

		JTable detailTable = new JTable(detailModel);
		setupDetailTable(detailTable);
		return detailTable;
	}

	private JScrollPane createScrollPane(JTable table) {
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
		scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = PRIMARY_COLOR;
				this.trackColor = new Color(245, 245, 245);
			}
		});
		return scrollPane;
	}

	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		buttonPanel.setBackground(Color.WHITE);

		JButton printButton = new JButton("In phiếu");
		JButton closeButton = new JButton("Đóng");

		StyleButton styleButton = new StyleButton();
		styleButton.styleButton(printButton, true);
		styleButton.styleButton(closeButton, false);

		// Add print functionality
		printButton.addActionListener(e -> printImportDetails());
		closeButton.addActionListener(e -> detailDialog.dispose());

		buttonPanel.add(printButton);
		buttonPanel.add(closeButton);

		return buttonPanel;
	}

	private void printImportDetails() {
		try {
			int selectedRow = mainTable.getSelectedRow();
			if (selectedRow == -1) {
				showWarningMessage("Vui lòng chọn phiếu nhập để in!");
				return;
			}

			String importId = mainTable.getValueAt(selectedRow, 0).toString();
			Import importOrder = importBUS.getImportById(importId);
			List<ImportDetail> details = importBUS.getImportDetailsByImportId(importId);

			// Show format selection dialog
			String[] options = { "PDF", "Excel", "Hủy" };
			int choice = JOptionPane.showOptionDialog(this, "Chọn định dạng xuất phiếu nhập:", "Chọn định dạng",
					JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

			if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) {
				return;
			}

			// Configure file chooser
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Lưu phiếu nhập");

			// Set file extension based on choice
			String extension = (choice == 0) ? "pdf" : "xls";
			String defaultFileName = String.format("PhieuNhap_%s.%s", importId, extension);
			fileChooser.setSelectedFile(new File(defaultFileName));

			if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				String filePath = fileChooser.getSelectedFile().getAbsolutePath();
				if (!filePath.toLowerCase().endsWith("." + extension)) {
					filePath += "." + extension;
				}

				// Check for existing file
				File exportFile = new File(filePath);
				if (exportFile.exists()) {
					int response = JOptionPane.showConfirmDialog(this, "File đã tồn tại. Bạn có muốn ghi đè không?",
							"Xác nhận ghi đè", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

					if (response != JOptionPane.YES_OPTION) {
						return;
					}
				}

				// Export based on chosen format
				if (choice == 0) {
					// Export to PDF
					PDFExporterHoaDonNhap pdfExporter = new PDFExporterHoaDonNhap();
					pdfExporter.exportToPDF(importOrder, details, filePath);
				} else {
					// Export to Excel
					ExcelExporterHoaDonNhap excelExporter = new ExcelExporterHoaDonNhap();
					excelExporter.exportToExcel(importOrder, details, filePath);
				}

				// Show success message and offer to open file
				int openResponse = JOptionPane.showConfirmDialog(this,
						"Xuất phiếu nhập thành công!\nBạn có muốn mở file không?", "Thông báo",
						JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

				if (openResponse == JOptionPane.YES_OPTION) {
					if (Desktop.isDesktopSupported()) {
						Desktop.getDesktop().open(new File(filePath));
					} else {
						Runtime.getRuntime().exec("cmd /c start " + filePath);
					}
				}

				LOGGER.info("Successfully exported import details to " + extension.toUpperCase() + ": " + filePath);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error exporting import details", e);
			showErrorMessage("Lỗi khi xuất phiếu nhập: " + e.getMessage());
		}
	}

	private void setupDetailTable(JTable table) {
		table.setFont(CONTENT_FONT);
		table.setRowHeight(35);
		table.setGridColor(new Color(230, 230, 230));
		table.setShowVerticalLines(true);
		table.setShowHorizontalLines(true);

		// Setup header
		JTableHeader header = table.getTableHeader();
		header.setFont(HEADER_FONT);
		header.setBackground(Color.WHITE);
		header.setForeground(Color.BLACK);
		header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

		// Setup cell renderers
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

		// Apply cell renderers
		table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã SP
		table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // Đơn giá
		table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Số lượng
		table.getColumnModel().getColumn(5).setCellRenderer(rightRenderer); // Thành tiền
	}

	private void addDetailField(JPanel panel, String label, String value, GridBagConstraints gbc, int row) {
		gbc.gridy = row;

		gbc.gridx = 0;
		JLabel lblField = new JLabel(label);
		lblField.setFont(CONTENT_FONT);
		panel.add(lblField, gbc);

		gbc.gridx = 1;
		JLabel lblValue = new JLabel(value);
		lblValue.setFont(CONTENT_FONT);
		panel.add(lblValue, gbc);
	}

	private void showWarningMessage(String message) {
		JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.WARNING_MESSAGE);
	}

	private void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
	}
}