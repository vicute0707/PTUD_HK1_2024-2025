package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.toedter.calendar.JDateChooser;

import bus.DanhMucBUS;
import bus.ImportBUS;
import bus.SanPhamBUS;
import entity.DanhMuc;
import entity.Import;
import entity.ImportDetail;
import entity.SanPham;
import entity.UserSession;
import service.PermissionChecker;
import style.CreateRoundedButton;
import style.CustomScrollBarUI;
import style.RoundedPanel;
import style.ShadowBorder;

public class Form_ThongKe extends JPanel {
	private static final Color PRIMARY_COLOR = new Color(219, 39, 119);
	private static final Font HEADER_FONT = new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12);
	private static final Font CONTENT_FONT = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12);

	private JTable importTable;
	private JTable productTable;
	private DefaultTableModel importTableModel;
	private DefaultTableModel productTableModel;
	private JDateChooser startDateChooser;
	private JDateChooser endDateChooser;
	private JPanel importChartPanel;
	private JPanel productChartPanel;
	private ImportBUS importBUS;
	private SanPhamBUS sanPhamBUS;
	private JPanel stockValuePanel; // Panel to hold the stock value bar chart
	private JPanel categoryPanel; // Panel to hold the category distribution pie chart

	public Form_ThongKe() {
		  importBUS = new ImportBUS();
		    sanPhamBUS = new SanPhamBUS();
		    initComponents();
		    loadInitialData();
		    initializeChartPanels(); // Add this line
	}

	private void loadInitialData() {
		// Set default date range to last 30 days
		LocalDate endDate = LocalDate.now();
		LocalDate startDate = endDate.minusDays(30);

		// Convert LocalDate to Date for JDateChooser
		startDateChooser.setDate(java.sql.Date.valueOf(startDate));
		endDateChooser.setDate(java.sql.Date.valueOf(endDate));

		// Load import statistics
		loadImportStatistics(startDate, endDate);

		// Load product statistics
		loadProductStatistics(startDate, endDate);
	}

	private void loadImportStatistics(LocalDate startDate, LocalDate endDate) {
		try {
			// Clear existing data
			importTableModel.setRowCount(0);

			// Get all imports
			List<Import> imports = importBUS.getAllImports();

			// Filter imports by date range and collect statistics
			Map<LocalDate, List<Import>> importsByDate = imports.stream().filter(imp -> {
				LocalDate importDate = imp.getImportDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				return !importDate.isBefore(startDate) && !importDate.isAfter(endDate);
			}).collect(Collectors
					.groupingBy(imp -> imp.getImportDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));

			// Create dataset for chart
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			BigDecimal totalImportValue = BigDecimal.ZERO;
			int totalProducts = 0;

			// Process each day's imports
			for (Map.Entry<LocalDate, List<Import>> entry : importsByDate.entrySet()) {
				LocalDate date = entry.getKey();
				List<Import> dayImports = entry.getValue();

				// Calculate daily totals
				BigDecimal dailyTotal = dayImports.stream().map(imp -> BigDecimal.valueOf(imp.getTotalAmount()))
						.reduce(BigDecimal.ZERO, BigDecimal::add);

				// Add to total
				totalImportValue = totalImportValue.add(dailyTotal);

				// Get product counts for each import
				for (Import imp : dayImports) {
					try {
						List<ImportDetail> details = importBUS.getImportDetailsByImportId(imp.getImportID());
						int productCount = details.stream().mapToInt(ImportDetail::getQuantity).sum();
						totalProducts += productCount;

						// Add row to table
						Object[] row = { imp.getImportID(), imp.getImportDate(), imp.getSupplier(), productCount,
								imp.getTotalAmount() };
						importTableModel.addRow(row);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				// Add to chart dataset
				dataset.addValue(dailyTotal.doubleValue(), "Giá trị nhập hàng",
						date.format(DateTimeFormatter.ofPattern("dd/MM")));
			}

			// Create and configure chart
			JFreeChart chart = ChartFactory.createBarChart(
					"Thống kê nhập hàng từ " + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " đến "
							+ endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
					"Ngày", "Giá trị (VNĐ)", dataset, PlotOrientation.VERTICAL, true, true, false);

			// Customize chart appearance
			chart.setBackgroundPaint(Color.WHITE);
			chart.getPlot().setBackgroundPaint(Color.WHITE);

			// Update chart panel
			ChartPanel chartPanel = new ChartPanel(chart);
			chartPanel.setPreferredSize(new Dimension(importChartPanel.getWidth(), 300));
			importChartPanel.removeAll();
			importChartPanel.add(chartPanel);
			importChartPanel.revalidate();

			// Display summary statistics
			showImportSummary(totalImportValue, totalProducts, importsByDate.size());

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu thống kê nhập hàng: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void loadProductStatistics(LocalDate startDate, LocalDate endDate) {
		try {
			// Clear existing data
			productTableModel.setRowCount(0);

			// Get all products and their variants
			List<SanPham> products = sanPhamBUS.getAllSanPham();
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();

			// Process each product
			for (SanPham product : products) {
				// Get import details for this product's variants
				int totalImported = calculateTotalImported(product, startDate, endDate);
				double totalValue = calculateProductValue(product);

				// Add to table
				Object[] row = { product.getMaSP(), product.getTenSP(), totalImported, product.getSoLuongTonKho(),
						formatCurrency(totalValue) };
				productTableModel.addRow(row);

				// Add to chart dataset for top products by value
				dataset.addValue(totalValue, "Giá trị tồn kho", product.getTenSP());
			}

			// Create product value chart
			JFreeChart chart = ChartFactory.createBarChart("Giá trị tồn kho theo sản phẩm", "Sản phẩm", "Giá trị (VNĐ)",
					dataset, PlotOrientation.VERTICAL, true, true, false);

			// Customize chart appearance
			chart.setBackgroundPaint(Color.WHITE);
			chart.getPlot().setBackgroundPaint(Color.WHITE);

			// Update chart panel
			ChartPanel chartPanel = new ChartPanel(chart);
			chartPanel.setPreferredSize(new Dimension(productChartPanel.getWidth(), 300));
			productChartPanel.removeAll();
			productChartPanel.add(chartPanel);
			productChartPanel.revalidate();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu thống kê sản phẩm: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	// Helper methods

	private void showImportSummary(BigDecimal totalValue, int totalProducts, int totalDays) {
		JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
		summaryPanel.setBackground(Color.WHITE);

		summaryPanel.add(createSummaryLabel("Tổng giá trị nhập:", formatCurrency(totalValue.doubleValue())));
		summaryPanel.add(createSummaryLabel("Tổng số sản phẩm:", String.format("%,d", totalProducts)));
		summaryPanel.add(createSummaryLabel("Số ngày có nhập hàng:", String.valueOf(totalDays)));

		// Add to the main panel above the chart
		add(summaryPanel, BorderLayout.NORTH);
	}

	private JLabel createSummaryLabel(String title, String value) {
		JLabel label = new JLabel(String.format("<html><b>%s</b> %s</html>", title, value));
		label.setFont(CONTENT_FONT);
		return label;
	}

	private String formatCurrency(double amount) {
		return String.format("%,d VNĐ", (long) amount);
	}

	private int calculateTotalImported(SanPham product, LocalDate startDate, LocalDate endDate) {
		try {
			// Get all imports within date range
			List<Import> imports = importBUS.getAllImports().stream().filter(imp -> {
				// Convert java.util.Date to LocalDate for comparison
				LocalDate importDate = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(imp.getImportDate()));
				return !importDate.isBefore(startDate) && !importDate.isAfter(endDate);
			}).collect(Collectors.toList());

			int totalQuantity = 0;

			// Sum up quantities for this product across all imports
			for (Import imp : imports) {
				List<ImportDetail> details = importBUS.getImportDetailsByImportId(imp.getImportID());
				for (ImportDetail detail : details) {
					// Check if this detail belongs to any variant of the product
					if (detail.getProductName().equals(product.getTenSP())) {
						totalQuantity += detail.getQuantity();
					}
				}
			}

			return totalQuantity;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private double calculateProductValue(SanPham product) {
		try {
			// Get all variants of this product
			double totalValue = 0;

			// Sum up (quantity * average price) for each variant
			List<Import> imports = importBUS.getAllImports();
			for (Import imp : imports) {
				List<ImportDetail> details = importBUS.getImportDetailsByImportId(imp.getImportID());
				for (ImportDetail detail : details) {
					if (detail.getProductName().equals(product.getTenSP())) {
						totalValue += detail.getQuantity() * detail.getPrice();
					}
				}
			}

			return totalValue;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private double getAverageImportPrice(String productId) {
		try {
			List<Import> imports = importBUS.getAllImports();
			List<Double> prices = new ArrayList<>();

			// Collect all import prices for this product
			for (Import imp : imports) {
				List<ImportDetail> details = importBUS.getImportDetailsByImportId(imp.getImportID());
				for (ImportDetail detail : details) {
					// Match by product name since we don't have direct product ID in ImportDetail
					SanPham product = sanPhamBUS.getSanPhamById(productId);
					if (detail.getProductName().equals(product.getTenSP())) {
						prices.add(detail.getPrice());
					}
				}
			}

			// Calculate average price
			if (prices.isEmpty()) {
				return 0;
			}

			return prices.stream().mapToDouble(Double::doubleValue).average().orElse(0);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private void initComponents() {
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(20, 20, 20, 20));
		setBackground(Color.WHITE);

		// Create tabbed pane for different statistics
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setFont(HEADER_FONT);
		tabbedPane.setBackground(Color.WHITE);

		// Add tabs for import and product statistics
		tabbedPane.addTab("Thống kê nhập hàng", createImportPanel());
		tabbedPane.addTab("Thống kê sản phẩm", createProductPanel());

		add(tabbedPane, BorderLayout.CENTER);
	}

	private JPanel createImportPanel() {
		JPanel panel = new JPanel(new BorderLayout(0, 20));
		panel.setBackground(Color.WHITE);
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Control panel at the top
		panel.add(createImportControlPanel(), BorderLayout.NORTH);

		// Split pane for chart and table
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(300);
		splitPane.setBackground(Color.WHITE);

		// Chart panel
		importChartPanel = new JPanel(new BorderLayout());
		importChartPanel.setBackground(Color.WHITE);
		splitPane.setTopComponent(importChartPanel);

		// Table panel
		JPanel tablePanel = createImportTablePanel();
		splitPane.setBottomComponent(tablePanel);

		panel.add(splitPane, BorderLayout.CENTER);
		return panel;
	}

	private JPanel createProductPanel() {
		// Main panel with BorderLayout for organized component placement
		JPanel panel = new JPanel(new BorderLayout(0, 20));
		panel.setBackground(Color.WHITE);
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Add the control panel at the top
		panel.add(createProductControlPanel(), BorderLayout.NORTH);

		// Create split pane for chart and table
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(300);
		splitPane.setBackground(Color.WHITE);

		// Initialize and add chart panel
		productChartPanel = new JPanel(new BorderLayout());
		productChartPanel.setBackground(Color.WHITE);
		splitPane.setTopComponent(createProductChartPanel());

		// Add table panel to bottom of split pane
		splitPane.setBottomComponent(createProductTablePanel());

		// Add split pane to main panel
		panel.add(splitPane, BorderLayout.CENTER);

		return panel;
	}

	private JPanel createProductControlPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);

		// Left side - Category filter
		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		leftPanel.setBackground(Color.WHITE);

		JComboBox<String> categoryCombo = new JComboBox<>(new String[] { "Tất cả danh mục", "Áo", "Quần", "Váy" });
		categoryCombo.setPreferredSize(new Dimension(150, 30));
		categoryCombo.setFont(CONTENT_FONT);

		// Add listener to update statistics when category changes
		categoryCombo.addActionListener(e -> updateProductStatistics((String) categoryCombo.getSelectedItem()));

		leftPanel.add(new JLabel("Danh mục:"));
		leftPanel.add(categoryCombo);

		// Right side - Export button
		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
		rightPanel.setBackground(Color.WHITE);

		CreateRoundedButton createRoundedButton = new CreateRoundedButton();
		JButton exportButton = createRoundedButton.createRoundedButton("Xuất báo cáo", "/icon/printer.png", true);

		// Configure export button with permissions
		configureButtonWithPermission(exportButton, UserSession.getInstance().getCurrentUser().getUserID(),
				PermissionChecker.PERM_REPORT, () -> exportImportReport());

		rightPanel.add(exportButton);

		// Add panels to main control panel
		panel.add(leftPanel, BorderLayout.WEST);
		panel.add(rightPanel, BorderLayout.EAST);

		return panel;
	}

	private void updateProductStatistics(String selectedCategory) {
		try {
			// Get all products first
			List<SanPham> products = sanPhamBUS.getAllSanPham();

			// Filter products by category if a specific one is selected
			if (selectedCategory != null && !selectedCategory.equals("Tất cả danh mục")) {
				products = products.stream().filter(p -> p.getDanhmuc().getTenDM().equals(selectedCategory))
						.collect(Collectors.toList());
			}

			// Clear existing table data
			productTableModel.setRowCount(0);

			// Variables for summary statistics
			double totalStockValue = 0;
			int totalProducts = 0;
			Map<String, Integer> stockStatus = new HashMap<>();

			// Update table and calculate statistics
			for (SanPham product : products) {
				// Calculate product statistics
				double stockValue = calculateProductValue(product);
				String status = determineStockStatus(product);

				// Add to summary statistics
				totalStockValue += stockValue;
				totalProducts++;
				stockStatus.merge(status, 1, Integer::sum);

				// Add row to table
				Object[] row = { product.getMaSP(), product.getTenSP(), product.getDanhmuc().getTenDM(),
						product.getSoLuongTonKho(), stockValue, status };
				productTableModel.addRow(row);
			}

			// Update charts
			updateProductCharts(products);

			// Update summary panel
			updateProductSummary(totalProducts, totalStockValue, stockStatus);

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật thống kê sản phẩm: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void updateProductCharts(List<SanPham> products) {
		// Create datasets for both charts
		DefaultCategoryDataset stockDataset = new DefaultCategoryDataset();
		DefaultPieDataset categoryDataset = new DefaultPieDataset();

		// Process products for stock value chart (top 10)
		products.stream().sorted((p1, p2) -> Double.compare(calculateProductValue(p2), calculateProductValue(p1)))
				.limit(10).forEach(product -> {
					stockDataset.addValue(calculateProductValue(product), "Giá trị tồn kho", product.getTenSP());
				});

		// Process products for category distribution
		Map<String, Long> categoryCount = products.stream()
				.collect(Collectors.groupingBy(p -> p.getDanhmuc().getTenDM(), Collectors.counting()));
		categoryCount.forEach(categoryDataset::setValue);

		// Create and update charts
		updateStockValueChart(stockDataset);
		updateCategoryChart(categoryDataset);
	}

	private JPanel createProductChartPanel() {
		// Create the main panel to hold all chart components
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(new EmptyBorder(0, 5, 5, 5));

		// Initialize our chart panels
		initializeChartPanels();

		// Create a title panel for the charts section
		JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		titlePanel.setBackground(Color.WHITE);
		JLabel titleLabel = new JLabel("Biểu đồ thống kê");
		titleLabel.setFont(HEADER_FONT);
		titlePanel.add(titleLabel);

		// Add components to the main panel
		panel.add(titlePanel, BorderLayout.NORTH);

		return panel;
	}

	private JPanel createStockValueChart() {
		// Create dataset for stock value chart
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		// Get all products and sort by value
		List<SanPham> products = sanPhamBUS.getAllSanPham();
		products.sort((p1, p2) -> Double.compare(calculateProductValue(p2), calculateProductValue(p1)));

		// Add top 10 products to dataset
		products.stream().limit(10).forEach(product -> {
			dataset.addValue(calculateProductValue(product), "Giá trị tồn kho", product.getTenSP());
		});

		// Create and customize the chart
		JFreeChart chart = ChartFactory.createBarChart("Top 10 sản phẩm theo giá trị tồn kho", "Sản phẩm",
				"Giá trị (VNĐ)", dataset, PlotOrientation.VERTICAL, true, true, false);

		// Customize chart appearance
		customizeChart(chart);
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setSeriesPaint(0, new Color(219, 39, 119));

		// Create panel for chart
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(450, 300));

		// Wrap chart in panel with title
		return wrapChartInPanel(chartPanel, "Giá trị tồn kho theo sản phẩm");
	}

	private JPanel createCategoryDistributionChart() {
		DefaultPieDataset dataset = new DefaultPieDataset();

		// Get products grouped by category
		Map<String, Long> categoryCount = sanPhamBUS.getAllSanPham().stream()
				.collect(Collectors.groupingBy(p -> p.getDanhmuc().getTenDM(), Collectors.counting()));

		// Add data to dataset
		categoryCount.forEach(dataset::setValue);

		// Create and customize the chart
		JFreeChart chart = ChartFactory.createPieChart("Phân bố sản phẩm theo danh mục", dataset, true, true, false);

		// Customize chart appearance
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setSectionPaint("Áo", new Color(219, 39, 119));
		plot.setSectionPaint("Quần", new Color(147, 51, 234));
		plot.setSectionPaint("Váy", new Color(59, 130, 246));

		// Create panel for chart
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(450, 300));

		// Wrap chart in panel with title
		return wrapChartInPanel(chartPanel, "Phân bố sản phẩm theo danh mục");
	}

	private JPanel createProductTablePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);

		// Initialize table model with columns
		String[] columns = { "Mã SP", "Tên sản phẩm", "Danh mục", "Tồn kho", "Giá trị tồn", "Trạng thái" };

		productTableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		// Create and setup table
		JTable productTable = new JTable(productTableModel);
		setupProductTable(productTable);

		// Create scroll pane for table
		JScrollPane scrollPane = new JScrollPane(productTable);
		scrollPane.getViewport().setBackground(Color.WHITE);

		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
	}

	private void setupProductTable(JTable table) {
		table.setFont(CONTENT_FONT);
		table.setRowHeight(30);
		table.setGridColor(new Color(245, 245, 245));
		table.setSelectionBackground(new Color(252, 231, 243));
		table.setSelectionForeground(Color.BLACK);

		JTableHeader header = table.getTableHeader();
		header.setFont(HEADER_FONT);
		header.setBackground(Color.WHITE);
		header.setBorder(BorderFactory.createLineBorder(new Color(245, 245, 245)));

		// Updated cell renderer for the value column (index 4)
		table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				setHorizontalAlignment(JLabel.RIGHT);

				// Handle different value types
				if (value instanceof Double) {
					setText(formatCurrency((Double) value));
				} else if (value instanceof String) {
					// If it's already formatted as currency string, leave it as is
					setText(value.toString());
				} else if (value instanceof Number) {
					setText(formatCurrency(((Number) value).doubleValue()));
				} else {
					setText(value != null ? value.toString() : "");
				}

				return this;
			}
		});

		// Status column renderer (index 5)
		table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				setHorizontalAlignment(JLabel.CENTER);

				String status = value != null ? value.toString() : "";
				switch (status) {
				case "Đang kinh doanh":
					setForeground(new Color(0, 128, 0));
					break;
				case "Sắp hết":
					setForeground(new Color(255, 140, 0));
					break;
				case "Ngừng kinh doanh":
					setForeground(Color.RED);
					break;
				default:
					setForeground(Color.BLACK);
				}

				return this;
			}
		});
	}

	private String determineStockStatus(SanPham product) {
		// Get the current stock quantity for evaluation
		int quantity = product.getSoLuongTonKho();

		// Define threshold values for different status levels
		final int OUT_OF_STOCK_THRESHOLD = 0;
		final int LOW_STOCK_THRESHOLD = 10;
		final int MODERATE_STOCK_THRESHOLD = 30;

		// Determine status based on thresholds
		if (quantity <= OUT_OF_STOCK_THRESHOLD) {
			return "Ngừng kinh doanh";
		} else if (quantity < LOW_STOCK_THRESHOLD) {
			return "Sắp hết";
		} else if (quantity < MODERATE_STOCK_THRESHOLD) {
			return "Số lượng thấp";
		} else {
			return "Đang kinh doanh";
		}
	}

	/**
	 * Updates the summary panel with current product statistics. This method
	 * calculates and displays key metrics about the product inventory, including
	 * total products, value, and status distribution.
	 */
	private void updateProductSummary(int totalProducts, double totalStockValue, Map<String, Integer> stockStatus) {
		// Create a new panel for summary information
		JPanel summaryPanel = new JPanel(new GridLayout(2, 3, 20, 10));
		summaryPanel.setBackground(Color.WHITE);
		summaryPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
				new EmptyBorder(10, 10, 10, 10)));

		// Create and style labels for statistics
		JLabel totalProductsLabel = createStatsLabel("Tổng số sản phẩm", String.format("%,d", totalProducts),
				new Color(25, 118, 210));

		JLabel totalValueLabel = createStatsLabel("Tổng giá trị tồn kho", formatCurrency(totalStockValue),
				new Color(56, 142, 60));

		JLabel avgValueLabel = createStatsLabel("Giá trị trung bình",
				formatCurrency(totalProducts > 0 ? totalStockValue / totalProducts : 0), new Color(191, 54, 12));

		// Create status distribution labels
		JLabel activeLabel = createStatsLabel("Đang kinh doanh",
				String.format("%,d", stockStatus.getOrDefault("Đang kinh doanh", 0)), new Color(0, 128, 0));

		JLabel lowStockLabel = createStatsLabel("Sắp hết hàng",
				String.format("%,d", stockStatus.getOrDefault("Sắp hết", 0)), new Color(255, 140, 0));

		JLabel discontinuedLabel = createStatsLabel("Ngừng kinh doanh",
				String.format("%,d", stockStatus.getOrDefault("Ngừng kinh doanh", 0)), Color.RED);

		// Add all labels to the summary panel
		summaryPanel.add(totalProductsLabel);
		summaryPanel.add(totalValueLabel);
		summaryPanel.add(avgValueLabel);
		summaryPanel.add(activeLabel);
		summaryPanel.add(lowStockLabel);
		summaryPanel.add(discontinuedLabel);

		// Update the UI with new summary panel
		if (productChartPanel.getComponentCount() > 0) {
			productChartPanel.remove(0);
		}
		productChartPanel.add(summaryPanel, BorderLayout.NORTH);
		productChartPanel.revalidate();
		productChartPanel.repaint();
	}

	/**
	 * Updates the stock value chart with new product data. Creates a bar chart
	 * showing the top products by stock value.
	 */
	private void updateStockValueChart(DefaultCategoryDataset dataset) {
		// Create the stock value chart
		JFreeChart stockChart = ChartFactory.createBarChart("Top 10 sản phẩm theo giá trị tồn kho", // Chart title
				"Sản phẩm", // X-axis label
				"Giá trị (VNĐ)", // Y-axis label
				dataset, PlotOrientation.VERTICAL, true, // Include legend
				true, // Include tooltips
				false // Include URLs
		);

		// Customize the chart appearance
		CategoryPlot plot = (CategoryPlot) stockChart.getPlot();

		// Set custom colors for bars
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setSeriesPaint(0, new Color(219, 39, 119));
		renderer.setBarPainter(new StandardBarPainter());

		// Customize value axis (Y-axis)
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setNumberFormatOverride(new DecimalFormat("#,##0 VNĐ"));

		// Rotate category labels for better readability
		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));

		// Update the chart panel
		ChartPanel chartPanel = new ChartPanel(stockChart);
		chartPanel.setPreferredSize(new Dimension(500, 300));

		// Replace existing chart if any
		if (stockValuePanel != null) {
			stockValuePanel.removeAll();
			stockValuePanel.add(chartPanel);
			stockValuePanel.revalidate();
		} else {
			stockValuePanel = new JPanel(new BorderLayout());
			stockValuePanel.add(chartPanel);
		}
	}

	/**
	 * Updates the category distribution chart with new product data. Creates a pie
	 * chart showing the distribution of products across categories.
	 */
	private void updateCategoryChart(DefaultPieDataset dataset) {
		// Create the category distribution pie chart
		JFreeChart categoryChart = ChartFactory.createPieChart("Phân bố sản phẩm theo danh mục", // Chart title
				dataset, true, // Include legend
				true, // Include tooltips
				false // Include URLs
		);

		// Customize the chart appearance
		PiePlot plot = (PiePlot) categoryChart.getPlot();

		// Set custom colors for different categories
		plot.setSectionPaint("Áo", new Color(219, 39, 119));
		plot.setSectionPaint("Quần", new Color(147, 51, 234));
		plot.setSectionPaint("Váy", new Color(59, 130, 246));

		// Customize label appearance
		plot.setLabelFont(CONTENT_FONT);
		plot.setLabelBackgroundPaint(Color.WHITE);
		plot.setLabelOutlinePaint(null);
		plot.setLabelShadowPaint(null);

		// Set label format to show percentage and value
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})", new DecimalFormat("0"),
				new DecimalFormat("0.0%")));

		// Create and update the chart panel
		ChartPanel chartPanel = new ChartPanel(categoryChart);
		chartPanel.setPreferredSize(new Dimension(500, 300));

		// Replace existing chart if any
		if (categoryPanel != null) {
			categoryPanel.removeAll();
			categoryPanel.add(chartPanel);
			categoryPanel.revalidate();
		} else {
			categoryPanel = new JPanel(new BorderLayout());
			categoryPanel.add(chartPanel);
		}
	}

	/**
	 * Helper method to create consistently styled statistics labels.
	 */
	private JLabel createStatsLabel(String title, String value, Color valueColor) {
		JLabel label = new JLabel(String.format(
				"<html><div style='text-align: center;'>" + "<span style='font-size: 12px; color: #666;'>%s</span><br>"
						+ "<span style='font-size: 16px; color: %s;'><b>%s</b></span>" + "</div></html>",
				title, String.format("#%02x%02x%02x", valueColor.getRed(), valueColor.getGreen(), valueColor.getBlue()),
				value));

		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		return label;
	}

	private void customizeChart(JFreeChart chart) {
		chart.setBackgroundPaint(Color.WHITE);
		chart.getPlot().setBackgroundPaint(Color.WHITE);

		// Customize text fonts
		chart.getTitle().setFont(HEADER_FONT);

		if (chart.getPlot() instanceof CategoryPlot) {
			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			plot.getDomainAxis().setTickLabelFont(CONTENT_FONT);
			plot.getRangeAxis().setTickLabelFont(CONTENT_FONT);
			plot.setOutlinePaint(null);
			plot.setRangeGridlinePaint(new Color(229, 231, 235));
		}
	}

	private JPanel createImportControlPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		panel.setBackground(Color.WHITE);

		startDateChooser = new JDateChooser();
		endDateChooser = new JDateChooser();
		startDateChooser.setPreferredSize(new Dimension(150, 30));
		endDateChooser.setPreferredSize(new Dimension(150, 30));

		CreateRoundedButton createRoundedButton = new CreateRoundedButton();

		JButton searchButton = createRoundedButton.createRoundedButton("Thống kê", "/icon/search.png", true);
		configureButtonWithPermission(searchButton, UserSession.getInstance().getCurrentUser().getUserID(),
				PermissionChecker.PERM_REPORT, () -> updateImportStatistics());

		JButton exportButton = createRoundedButton.createRoundedButton("Xuất báo cáo", "/icon/printer.png", true);
		configureButtonWithPermission(exportButton, UserSession.getInstance().getCurrentUser().getUserID(),
				PermissionChecker.PERM_REPORT, () -> exportImportReport());

		panel.add(new JLabel("Từ ngày:"));
		panel.add(startDateChooser);
		panel.add(new JLabel("Đến ngày:"));
		panel.add(endDateChooser);
		panel.add(searchButton);
		panel.add(exportButton);

		return panel;
	}

	private JPanel createImportTablePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);

		// Initialize table model with columns
		String[] columns = { "Mã phiếu nhập", "Ngày nhập", "Nhà cung cấp", "Số lượng SP", "Tổng tiền" };
		importTableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		importTable = new JTable(importTableModel);
		setupTable(importTable);

		JScrollPane scrollPane = new JScrollPane(importTable);
		scrollPane.getViewport().setBackground(Color.WHITE);

		panel.add(scrollPane, BorderLayout.CENTER);
		return panel;
	}

	private void setupTable(JTable table) {
		table.setFont(CONTENT_FONT);
		table.setRowHeight(30);
		table.setGridColor(new Color(245, 245, 245));
		table.setSelectionBackground(new Color(252, 231, 243));
		table.setSelectionForeground(Color.BLACK);

		JTableHeader header = table.getTableHeader();
		header.setFont(HEADER_FONT);
		header.setBackground(Color.WHITE);
		header.setBorder(BorderFactory.createLineBorder(new Color(245, 245, 245)));

		// Set number format renderer for amount columns
		DefaultTableCellRenderer amountRenderer = new DefaultTableCellRenderer() {
			@Override
			public void setValue(Object value) {
				if (value instanceof Number) {
					setHorizontalAlignment(JLabel.RIGHT);
					setText(String.format("%,d VNĐ", ((Number) value).longValue()));
				} else {
					super.setValue(value);
				}
			}
		};
		table.getColumnModel().getColumn(4).setCellRenderer(amountRenderer);
	}

	private void updateImportStatistics() {
		if (startDateChooser.getDate() == null || endDateChooser.getDate() == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn khoảng thời gian", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		LocalDate startDate = startDateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate endDate = endDateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		// Get import data and update table
		List<Import> imports = importBUS.getAllImports();
		updateImportTable(imports, startDate, endDate);

		// Update chart
		updateImportChart(imports, startDate, endDate);
	}

	private void updateImportTable(List<Import> imports, LocalDate startDate, LocalDate endDate) {
		importTableModel.setRowCount(0);

		imports.stream().filter(imp -> {
			LocalDate importDate = imp.getImportDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			return !importDate.isBefore(startDate) && !importDate.isAfter(endDate);
		}).forEach(imp -> {
			try {
				List<ImportDetail> details = importBUS.getImportDetailsByImportId(imp.getImportID());
				Object[] row = { imp.getImportID(), imp.getImportDate(), imp.getSupplier(),
						details.stream().mapToInt(ImportDetail::getQuantity).sum(), imp.getTotalAmount() };
				importTableModel.addRow(row);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private void updateImportChart(List<Import> imports, LocalDate startDate, LocalDate endDate) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		// Group imports by date and calculate daily totals
		Map<LocalDate, Double> dailyTotals = imports.stream().filter(imp -> {
			LocalDate importDate = imp.getImportDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			return !importDate.isBefore(startDate) && !importDate.isAfter(endDate);
		}).collect(Collectors.groupingBy(
				imp -> imp.getImportDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
				Collectors.summingDouble(Import::getTotalAmount)));

		// Add data to chart dataset
		dailyTotals.forEach((date, total) -> {
			dataset.addValue(total, "Tổng tiền nhập", date.toString());
		});

		// Create chart
		JFreeChart chart = ChartFactory.createBarChart("Biểu đồ nhập hàng", "Ngày", "Tổng tiền (VNĐ)", dataset,
				PlotOrientation.VERTICAL, true, true, false);

		// Update chart panel
		ChartPanel chartPanel = new ChartPanel(chart);
		importChartPanel.removeAll();
		importChartPanel.add(chartPanel);
		importChartPanel.revalidate();
	}

	private void exportImportReport() {
		// Implementation for exporting import statistics report
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Chọn vị trí lưu báo cáo");

		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			String filePath = fileChooser.getSelectedFile().getAbsolutePath();
			if (!filePath.endsWith(".html")) {
				filePath += ".html";
			}

			try (FileWriter writer = new FileWriter(filePath)) {
				// Write report content
				writer.write("<html><head><style>");
				writer.write("table { border-collapse: collapse; width: 100%; }");
				writer.write("th, td { border: 1px solid black; padding: 8px; text-align: left; }");
				writer.write("</style></head><body>");

				// Add report title and date range
				writer.write("<h2>Báo cáo nhập hàng</h2>");
				writer.write("<p>Từ ngày: " + startDateChooser.getDate() + "</p>");
				writer.write("<p>Đến ngày: " + endDateChooser.getDate() + "</p>");

				// Add table data
				writer.write("<table>");
				// Add headers
				writer.write("<tr>");
				for (int i = 0; i < importTable.getColumnCount(); i++) {
					writer.write("<th>" + importTable.getColumnName(i) + "</th>");
				}
				writer.write("</tr>");

				// Add data rows
				for (int i = 0; i < importTable.getRowCount(); i++) {
					writer.write("<tr>");
					for (int j = 0; j < importTable.getColumnCount(); j++) {
						writer.write("<td>" + importTable.getValueAt(i, j) + "</td>");
					}
					writer.write("</tr>");
				}

				writer.write("</table></body></html>");

				JOptionPane.showMessageDialog(this, "Xuất báo cáo thành công!", "Thông báo",
						JOptionPane.INFORMATION_MESSAGE);

			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Lỗi khi xuất báo cáo: " + e.getMessage(), "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// Similar methods for product statistics panel...
	// (I'll continue with the product statistics implementation in the next part)

	private void configureButtonWithPermission(JButton button, String userId, String permission, Runnable action) {
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
		}
	}

	/**
	 * Initializes the chart panels that will contain our visualizations. This
	 * method creates the base panels with proper layouts and styling, preparing
	 * them to receive the charts when data is available.
	 */
	private void initializeChartPanels() {
		JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
		mainPanel.setBackground(Color.WHITE);
		mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

		// Add metrics panel at the top
		mainPanel.add(createMetricsPanel(), BorderLayout.NORTH);

		// Create charts container with grid layout for side by side charts
		JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
		chartsPanel.setBackground(Color.WHITE);

		// Create and add stock value chart (bar chart)
		JPanel stockValueChart = createStockValueChart();
		JPanel categoryChart = createCategoryDistributionChart();

		// Add both charts to the container
		chartsPanel.add(stockValueChart);
		chartsPanel.add(categoryChart);

		// Add charts panel to main container
		mainPanel.add(chartsPanel, BorderLayout.CENTER);

		// Add main panel to product chart panel
		productChartPanel.removeAll();
		productChartPanel.add(mainPanel);
		productChartPanel.revalidate();
		productChartPanel.repaint();
	}

	private JPanel createMetricsPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
		panel.setBackground(Color.WHITE);
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Create individual metric cards with real data
		panel.add(createMetricCard("Tổng sản phẩm", getTotalProducts(), getGrowthRate(), new Color(219, 39, 119) // #DB2777
		));

		panel.add(createMetricCard("Giá trị tồn kho", formatCurrency(getTotalStockValue()), getValueGrowthRate(),
				new Color(147, 51, 234) // #9333EA
		));

		panel.add(createMetricCard("Sản phẩm cảnh báo", getLowStockCount(), getLowStockGrowthRate(),
				new Color(59, 130, 246) // #3B82F6
		));

		panel.add(createMetricCard("Số danh mục", getCategoryCount(), getCategoryGrowthRate(), new Color(16, 185, 129) // #10B981
		));

		return panel;
	}
	// Helper methods for metrics calculations

	private String getTotalProducts() {
		try {
			// Get all products from database
			List<SanPham> products = sanPhamBUS.getAllSanPham();
			return String.format("%,d", products.size());
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
	}

	private double getTotalStockValue() {
		try {
			// Calculate total value of all products in stock
			return sanPhamBUS.getAllSanPham().stream().mapToDouble(product -> {
				// For each product, calculate its value based on stock and average import price
				double stockValue = calculateProductValue(product);
				return stockValue;
			}).sum();
		} catch (Exception e) {
			e.printStackTrace();
			return 0.0;
		}
	}

	private String getLowStockCount() {
		try {
			// Count products with low stock (below threshold)
			final int LOW_STOCK_THRESHOLD = 10;
			long lowStockCount = sanPhamBUS.getAllSanPham().stream()
					.filter(product -> product.getSoLuongTonKho() < LOW_STOCK_THRESHOLD).count();
			return String.format("%d", lowStockCount);
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
	}

	private String getCategoryCount() {
		try {
			// Get count of unique categories
			DanhMucBUS danhMucBUS = new DanhMucBUS();
			List<DanhMuc> categories = danhMucBUS.getDanhSachDanhMuc();
			return String.format("%d", categories.size());
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
	}

	// Growth rate calculations (comparing with previous period)
	private double getGrowthRate() {
		try {
			// Calculate growth rate for total products
			LocalDate now = LocalDate.now();
			LocalDate lastMonth = now.minusMonths(1);

			// Get current count
			int currentCount = sanPhamBUS.getAllSanPham().size();

			// Get previous count by analyzing import history
			int previousCount = getPreviousMonthProductCount(lastMonth);

			if (previousCount == 0)
				return 0.0;
			return ((double) (currentCount - previousCount) / previousCount) * 100;
		} catch (Exception e) {
			e.printStackTrace();
			return 0.0;
		}
	}

	private int getPreviousMonthProductCount(LocalDate lastMonth) {
		try {
			// Get all imports from the database
			List<Import> imports = importBUS.getAllImports();

			// Create a set to track unique product names
			Set<String> uniqueProducts = new HashSet<>();

			// Process each import
			for (Import imp : imports) {
				// Convert the import date safely
				LocalDate importDate = new java.sql.Date(imp.getImportDate().getTime()).toLocalDate();

				// Check if this import is from before last month
				if (importDate.isBefore(lastMonth)) {
					try {
						// Get details for this import
						List<ImportDetail> details = importBUS.getImportDetailsByImportId(imp.getImportID());

						// Add each product name to our set (set automatically handles duplicates)
						for (ImportDetail detail : details) {
							uniqueProducts.add(detail.getProductName());
						}
					} catch (Exception e) {
						System.err.println(
								"Error getting details for import " + imp.getImportID() + ": " + e.getMessage());
					}
				}
			}

			// Return the count of unique products
			return uniqueProducts.size();

		} catch (Exception e) {
			System.err.println("Error calculating previous month product count: " + e.getMessage());
			e.printStackTrace();
			return 0;
		}
	}

	private double getValueGrowthRate() {
		try {
			// Calculate growth rate for stock value
			double currentValue = getTotalStockValue();
			double previousValue = getPreviousMonthStockValue();

			if (previousValue == 0)
				return 0.0;
			return ((currentValue - previousValue) / previousValue) * 100;
		} catch (Exception e) {
			e.printStackTrace();
			return 0.0;
		}
	}

	private double getLowStockGrowthRate() {
		try {
			// Compare current low stock count with previous month
			final int LOW_STOCK_THRESHOLD = 10;

			// Current low stock count
			long currentLowStock = sanPhamBUS.getAllSanPham().stream()
					.filter(product -> product.getSoLuongTonKho() < LOW_STOCK_THRESHOLD).count();

			// Previous month's low stock count
			long previousLowStock = getPreviousMonthLowStockCount();

			if (previousLowStock == 0)
				return 0.0;
			return ((double) (currentLowStock - previousLowStock) / previousLowStock) * 100;
		} catch (Exception e) {
			e.printStackTrace();
			return 0.0;
		}
	}

	private double getCategoryGrowthRate() {
		try {
			// Compare current category count with previous month
			DanhMucBUS danhMucBUS = new DanhMucBUS();
			int currentCount = danhMucBUS.getDanhSachDanhMuc().size();

			// Get previous month's category count
			int previousCount = getPreviousMonthCategoryCount();

			if (previousCount == 0)
				return 0.0;
			return ((double) (currentCount - previousCount) / previousCount) * 100;
		} catch (Exception e) {
			e.printStackTrace();
			return 0.0;
		}
	}

	private int getPreviousMonthCategoryCount() {
		try {
			// Count categories that had products before last month
			LocalDate lastMonth = LocalDate.now().minusMonths(1);

			return (int) sanPhamBUS.getAllSanPham().stream().filter(product -> {
				// Check if product existed in previous month
				try {
					return importBUS.getAllImports().stream().anyMatch(imp -> {
						LocalDate importDate = imp.getImportDate().toInstant().atZone(ZoneId.systemDefault())
								.toLocalDate();
						return importDate.isBefore(lastMonth);
					});
				} catch (Exception e) {
					return false;
				}
			}).map(product -> product.getDanhmuc().getMaDM()).distinct().count();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private double getPreviousMonthStockValue() {
		try {
			LocalDate lastMonth = LocalDate.now().minusMonths(1);

			return importBUS.getAllImports().stream().filter(imp -> {
				LocalDate importDate = imp.getImportDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				return importDate.isBefore(lastMonth);
			}).mapToDouble(Import::getTotalAmount).sum();
		} catch (Exception e) {
			e.printStackTrace();
			return 0.0;
		}
	}

	private long getPreviousMonthLowStockCount() {
		try {
			// Analyze historical import/export data to determine previous low stock count
			final int LOW_STOCK_THRESHOLD = 10;
			LocalDate lastMonth = LocalDate.now().minusMonths(1);

			// This is a simplified calculation - you may need to adjust based on your
			// actual data model
			Map<String, Integer> previousStockLevels = new HashMap<>();

			// Calculate stock levels at end of previous month
			importBUS.getAllImports().stream().filter(imp -> {
				LocalDate importDate = imp.getImportDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				return importDate.isBefore(lastMonth);
			}).forEach(imp -> {
				try {
					importBUS.getImportDetailsByImportId(imp.getImportID()).forEach(detail -> {
						previousStockLevels.merge(detail.getProductName(), detail.getQuantity(), Integer::sum);
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			return previousStockLevels.values().stream().filter(quantity -> quantity < LOW_STOCK_THRESHOLD).count();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private JPanel createMetricCard(String title, String value, double growthRate, Color color) {
		// Create card panel with rounded corners and shadow
		JPanel card = new RoundedPanel(15); // 15px border radius
		card.setLayout(new BorderLayout(10, 10));
		card.setBackground(Color.WHITE);
		card.setBorder(BorderFactory.createCompoundBorder(new ShadowBorder(), new EmptyBorder(15, 15, 15, 15)));

		// Create icon container with background
		JPanel iconContainer = new RoundedPanel(10);
		iconContainer.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
		iconContainer.setPreferredSize(new Dimension(45, 45));

		// Add icon based on metric type
		JLabel icon = new JLabel();
		icon.setHorizontalAlignment(SwingConstants.CENTER);
		icon.setForeground(color);
		iconContainer.add(icon);

		// Create text container
		JPanel textContainer = new JPanel(new GridLayout(2, 1, 5, 5));
		textContainer.setBackground(Color.WHITE);

		// Add title
		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(new Font(CONTENT_FONT.getName(), Font.PLAIN, 13));
		titleLabel.setForeground(new Color(107, 114, 128));

		// Add value
		JLabel valueLabel = new JLabel(value);
		valueLabel.setFont(new Font(HEADER_FONT.getName(), Font.BOLD, 20));
		valueLabel.setForeground(new Color(17, 24, 39));

		textContainer.add(titleLabel);
		textContainer.add(valueLabel);

		// Add growth indicator
		JPanel growthContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		growthContainer.setBackground(Color.WHITE);

		String growthText = String.format("%+.1f%%", growthRate);
		JLabel growthLabel = new JLabel(growthText);
		growthLabel.setFont(new Font(CONTENT_FONT.getName(), Font.BOLD, 12));
		growthLabel.setForeground(growthRate >= 0 ? new Color(16, 185, 129) : new Color(239, 68, 68));

		growthContainer.add(growthLabel);

		// Assemble the card
		card.add(iconContainer, BorderLayout.WEST);
		card.add(textContainer, BorderLayout.CENTER);
		card.add(growthContainer, BorderLayout.EAST);

		return card;
	}

	private JFreeChart createModernBarChart(DefaultCategoryDataset dataset) {
		JFreeChart chart = ChartFactory.createBarChart(null, // No title (we'll add it in the panel)
				"Danh mục", // X-axis label
				"Giá trị (VNĐ)", // Y-axis label
				dataset, PlotOrientation.VERTICAL, false, // No legend
				true, // Tooltips
				false // URLs
		);

		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		BarRenderer renderer = (BarRenderer) plot.getRenderer();

		// Modern styling
		renderer.setSeriesPaint(0, new Color(219, 39, 119));
		renderer.setBarPainter(new StandardBarPainter());
		renderer.setShadowVisible(false);
		renderer.setMaximumBarWidth(0.1);

		// Customize axes
		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryMargin(0.4);
		domainAxis.setLowerMargin(0.03);
		domainAxis.setUpperMargin(0.03);

		// Style plot
		plot.setBackgroundPaint(Color.WHITE);
		plot.setOutlinePaint(null);
		plot.setRangeGridlinePaint(new Color(229, 231, 235));

		return chart;
	}

	private JFreeChart createModernPieChart(DefaultPieDataset dataset) {
		JFreeChart chart = ChartFactory.createPieChart(null, // No title (we'll add it in the panel)
				dataset, false, // No legend
				true, // Tooltips
				false // URLs
		);

		PiePlot plot = (PiePlot) chart.getPlot();

		// Modern styling
		plot.setSectionPaint("Áo", new Color(219, 39, 119));
		plot.setSectionPaint("Quần", new Color(147, 51, 234));
		plot.setSectionPaint("Váy", new Color(59, 130, 246));

		plot.setBackgroundPaint(Color.WHITE);
		plot.setOutlinePaint(null);
		plot.setInteriorGap(0.04);
		plot.setSectionOutlinesVisible(false);

		// Modern labels
		plot.setLabelGenerator(
				new StandardPieSectionLabelGenerator("{0}: {2}", new DecimalFormat("0"), new DecimalFormat("0.0%")));
		plot.setLabelFont(new Font(CONTENT_FONT.getName(), Font.PLAIN, 11));
		plot.setLabelBackgroundPaint(new Color(255, 255, 255, 200));
		plot.setLabelOutlinePaint(null);
		plot.setLabelShadowPaint(null);

		return chart;
	}

	private ChartPanel createChartPanel(JFreeChart chart) {
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setBackground(Color.WHITE);
		chartPanel.setPreferredSize(new Dimension(400, 300));
		return chartPanel;
	}

	private JPanel wrapChartInPanel(ChartPanel chartPanel, String title) {
		JPanel wrapper = new JPanel(new BorderLayout(0, 10));
		wrapper.setBackground(Color.WHITE);
		wrapper.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)),
				new EmptyBorder(15, 15, 15, 15)));

		// Add title
		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(new Font(HEADER_FONT.getName(), Font.BOLD, 14));
		wrapper.add(titleLabel, BorderLayout.NORTH);

		// Add chart
		wrapper.add(chartPanel, BorderLayout.CENTER);

		return wrapper;
	}

	private void updateChartsData(String selectedCategory, DefaultCategoryDataset barDataset,
			DefaultPieDataset pieDataset) {
		barDataset.clear();
		pieDataset.clear();

		List<SanPham> filteredProducts = sanPhamBUS.getAllSanPham().stream().filter(
				p -> selectedCategory.equals("Tất cả danh mục") || p.getDanhmuc().getTenDM().equals(selectedCategory))
				.collect(Collectors.toList());

		// Update bar chart data
		Map<String, Double> categoryValues = filteredProducts.stream().collect(Collectors
				.groupingBy(p -> p.getDanhmuc().getTenDM(), Collectors.summingDouble(this::calculateProductValue)));

		categoryValues.forEach((category, value) -> {
			barDataset.addValue(value, "Giá trị tồn kho", category);
		});

		// Update pie chart data
		Map<String, Long> categoryCount = filteredProducts.stream()
				.collect(Collectors.groupingBy(p -> p.getDanhmuc().getTenDM(), Collectors.counting()));

		categoryCount.forEach((category, count) -> {
			pieDataset.setValue(category, count);
		});
	}

	private JFreeChart createBarChart(DefaultCategoryDataset dataset) {
		JFreeChart chart = ChartFactory.createBarChart("Giá trị tồn kho theo danh mục", "Danh mục", "Giá trị (VNĐ)",
				dataset, PlotOrientation.VERTICAL, true, true, false);

		CategoryPlot plot = chart.getCategoryPlot();
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setSeriesPaint(0, new Color(219, 39, 119));
		plot.setBackgroundPaint(Color.WHITE);

		return chart;
	}

	private JFreeChart createPieChart(DefaultPieDataset dataset) {
		JFreeChart chart = ChartFactory.createPieChart("Phân bố sản phẩm theo danh mục", dataset, true, true, false);

		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE);

		return chart;
	}
}