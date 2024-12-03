package export;

import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExcelExporterDonNhap {
	// Define headers for both summary and detail sheets
	private static final String[] IMPORT_HEADERS = { "Mã phiếu nhập", "Nhà cung cấp", "Nhân viên nhập hàng",
			"Thời gian", "Tổng tiền" };

	private static final String[] DETAIL_HEADERS = { "Mã phiếu nhập", "Mã sản phẩm", "Tên sản phẩm", "Phân loại",
			"Đơn giá", "Số lượng", "Thành tiền" };

	/**
	 * Exports import order data to an Excel file with summary and detail sheets
	 * 
	 * @param mainTable   The table containing import order summary data
	 * @param detailTable The table containing import order detail data
	 * @param filePath    The path where the Excel file will be saved
	 */
	public void exportToExcel(JTable mainTable, JTable detailTable, String filePath) throws IOException {
		Workbook workbook = new HSSFWorkbook();

		// Create and populate the summary sheet
		Sheet summarySheet = createSummarySheet(workbook, mainTable);
		// Create and populate the detail sheet
		Sheet detailSheet = createDetailSheet(workbook, detailTable);

		// Write the workbook to file
		try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
			workbook.write(fileOut);
		} finally {
			workbook.close();
		}
	}

	/**
	 * Creates the summary sheet with import order overview
	 */
	private Sheet createSummarySheet(Workbook workbook, JTable table) {
		Sheet sheet = workbook.createSheet("Danh sách phiếu nhập");
		TableModel model = table.getModel();

		// Create styles
		CellStyle titleStyle = createTitleStyle(workbook);
		CellStyle headerStyle = createHeaderStyle(workbook);
		CellStyle dataStyle = createDataStyle(workbook);
		CellStyle currencyStyle = createCurrencyStyle(workbook);

		// Add title
		Row titleRow = sheet.createRow(0);
		Cell titleCell = titleRow.createCell(0);
		titleCell.setCellValue("DANH SÁCH PHIẾU NHẬP HÀNG");
		titleCell.setCellStyle(titleStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, IMPORT_HEADERS.length - 1));

		// Add headers
		Row headerRow = sheet.createRow(2);
		for (int col = 0; col < IMPORT_HEADERS.length; col++) {
			Cell cell = headerRow.createCell(col);
			cell.setCellValue(IMPORT_HEADERS[col]);
			cell.setCellStyle(headerStyle);
		}

		// Add data rows
		double totalAmount = 0;
		for (int row = 0; row < model.getRowCount(); row++) {
			Row dataRow = sheet.createRow(row + 3);
			for (int col = 0; col < model.getColumnCount(); col++) {
				Cell cell = dataRow.createCell(col);
				Object value = model.getValueAt(row, col);

				// Apply special formatting for the amount column
				if (col == 4) { // Total amount column
					String amountStr = value.toString().replaceAll("[^\\d]", "");
					double amount = Double.parseDouble(amountStr);
					cell.setCellValue(amount);
					cell.setCellStyle(currencyStyle);
					totalAmount += amount;
				} else {
					cell.setCellValue(value != null ? value.toString() : "");
					cell.setCellStyle(dataStyle);
				}
			}
		}

		// Add summary rows
		int lastRow = model.getRowCount() + 4;
		Row totalRow = sheet.createRow(lastRow);
		Cell totalLabelCell = totalRow.createCell(0);
		totalLabelCell.setCellValue("Tổng số phiếu nhập: " + model.getRowCount());
		totalLabelCell.setCellStyle(headerStyle);

		Cell totalAmountLabelCell = totalRow.createCell(3);
		totalAmountLabelCell.setCellValue("Tổng tiền:");
		totalAmountLabelCell.setCellStyle(headerStyle);

		Cell totalAmountCell = totalRow.createCell(4);
		totalAmountCell.setCellValue(totalAmount);
		totalAmountCell.setCellStyle(currencyStyle);

		// Auto-size columns
		for (int col = 0; col < IMPORT_HEADERS.length; col++) {
			sheet.autoSizeColumn(col);
		}

		return sheet;
	}

	/**
	 * Creates the detail sheet with individual items from each import order
	 */
	private Sheet createDetailSheet(Workbook workbook, JTable table) {
		Sheet sheet = workbook.createSheet("Chi tiết phiếu nhập");
		TableModel model = table.getModel();

		// Create styles
		CellStyle headerStyle = createHeaderStyle(workbook);
		CellStyle dataStyle = createDataStyle(workbook);
		CellStyle currencyStyle = createCurrencyStyle(workbook);

		// Add headers
		Row headerRow = sheet.createRow(0);
		for (int col = 0; col < DETAIL_HEADERS.length; col++) {
			Cell cell = headerRow.createCell(col);
			cell.setCellValue(DETAIL_HEADERS[col]);
			cell.setCellStyle(headerStyle);
		}

		// Add data rows
		for (int row = 0; row < model.getRowCount(); row++) {
			Row dataRow = sheet.createRow(row + 1);
			for (int col = 0; col < model.getColumnCount(); col++) {
				Cell cell = dataRow.createCell(col);
				Object value = model.getValueAt(row, col);

				// Apply special formatting for amount columns
				if (col == 4 || col == 6) { // Price and total amount columns
					String amountStr = value.toString().replaceAll("[^\\d]", "");
					cell.setCellValue(Double.parseDouble(amountStr));
					cell.setCellStyle(currencyStyle);
				} else {
					cell.setCellValue(value != null ? value.toString() : "");
					cell.setCellStyle(dataStyle);
				}
			}
		}

		// Auto-size columns
		for (int col = 0; col < DETAIL_HEADERS.length; col++) {
			sheet.autoSizeColumn(col);
		}

		return sheet;
	}

	/**
	 * Creates the style for the main title
	 */
	private CellStyle createTitleStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 16);
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);
		return style;
	}

	/**
	 * Creates the style for table headers
	 */
	private CellStyle createHeaderStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(true);
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return style;
	}

	/**
	 * Creates the style for regular data cells
	 */
	private CellStyle createDataStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		return style;
	}

	/**
	 * Creates the style for currency values
	 */
	private CellStyle createCurrencyStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setAlignment(HorizontalAlignment.RIGHT);
		DataFormat format = workbook.createDataFormat();
		style.setDataFormat(format.getFormat("#,##0 'VND'"));
		return style;
	}
}