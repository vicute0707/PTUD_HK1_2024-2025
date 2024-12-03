package export;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import entity.Import;
import entity.ImportDetail;

public class ExcelExporterHoaDonNhap {
	private static final Logger LOGGER = Logger.getLogger(ExcelExporterHoaDonNhap.class.getName());

	// Shop information constants
	private static final String SHOP_NAME = "FASHION SHOP";
	private static final String SHOP_ADDRESS = "123 Nguyễn Văn Cừ, Quận 5, TP.HCM";
	private static final String SHOP_PHONE = "0123 456 789";
	private static final String SHOP_EMAIL = "contact@fashionshop.com";

	/**
	 * Exports import order data to Excel with professional formatting
	 */
	public void exportToExcel(Import importOrder, List<ImportDetail> details, String filePath) throws IOException {
		LOGGER.info("Starting export to Excel: " + filePath);

		try (HSSFWorkbook workbook = new HSSFWorkbook()) {
			// Create main sheet
			HSSFSheet sheet = workbook.createSheet("Phiếu nhập hàng");
			sheet.setDefaultColumnWidth(15);

			// Create styles
			CellStyle headerStyle = createHeaderStyle(workbook);
			CellStyle titleStyle = createTitleStyle(workbook);
			CellStyle normalStyle = createNormalStyle(workbook);
			CellStyle currencyStyle = createCurrencyStyle(workbook);

			int currentRow = 0;

			// Add shop information header
			currentRow = addShopInfo(sheet, currentRow, titleStyle, normalStyle);

			// Add import order information
			currentRow = addImportInfo(sheet, importOrder, currentRow, headerStyle, normalStyle);

			// Add product table
			currentRow = addProductTable(sheet, details, currentRow, headerStyle, normalStyle, currencyStyle);

			// Add signatures
			addSignatures(sheet, currentRow, headerStyle);

			// Auto-size columns
			for (int i = 0; i < 6; i++) {
				sheet.autoSizeColumn(i);
			}

			// Write to file
			try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
				workbook.write(fileOut);
				LOGGER.info("Successfully exported to Excel: " + filePath);
			}

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error exporting to Excel", e);
			throw new IOException("Lỗi khi xuất file Excel: " + e.getMessage());
		}
	}

	private int addShopInfo(HSSFSheet sheet, int startRow, CellStyle titleStyle, CellStyle normalStyle) {
		// Shop name
		Row shopNameRow = sheet.createRow(startRow++);
		Cell shopNameCell = shopNameRow.createCell(0);
		shopNameCell.setCellValue(SHOP_NAME);
		shopNameCell.setCellStyle(titleStyle);
		sheet.addMergedRegion(new CellRangeAddress(startRow - 1, startRow - 1, 0, 5));

		// Shop address
		Row addressRow = sheet.createRow(startRow++);
		Cell addressCell = addressRow.createCell(0);
		addressCell.setCellValue("Địa chỉ: " + SHOP_ADDRESS);
		addressCell.setCellStyle(normalStyle);
		sheet.addMergedRegion(new CellRangeAddress(startRow - 1, startRow - 1, 0, 5));

		// Contact info
		Row contactRow = sheet.createRow(startRow++);
		Cell contactCell = contactRow.createCell(0);
		contactCell.setCellValue("SĐT: " + SHOP_PHONE + " - Email: " + SHOP_EMAIL);
		contactCell.setCellStyle(normalStyle);
		sheet.addMergedRegion(new CellRangeAddress(startRow - 1, startRow - 1, 0, 5));

		// Add empty row for spacing
		sheet.createRow(startRow++);

		return startRow;
	}

	private int addImportInfo(HSSFSheet sheet, Import importOrder, int startRow, CellStyle headerStyle,
			CellStyle normalStyle) {
		// Title
		Row titleRow = sheet.createRow(startRow++);
		Cell titleCell = titleRow.createCell(0);
		titleCell.setCellValue("PHIẾU NHẬP HÀNG");
		titleCell.setCellStyle(headerStyle);
		sheet.addMergedRegion(new CellRangeAddress(startRow - 1, startRow - 1, 0, 5));

		// Import ID
		Row idRow = sheet.createRow(startRow++);
		Cell idCell = idRow.createCell(0);
		idCell.setCellValue("Số: " + importOrder.getImportID());
		idCell.setCellStyle(normalStyle);
		sheet.addMergedRegion(new CellRangeAddress(startRow - 1, startRow - 1, 0, 5));

		// Import info
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		Row supplierRow = sheet.createRow(startRow++);
		supplierRow.createCell(0).setCellValue("Nhà cung cấp:");
		supplierRow.createCell(1).setCellValue(importOrder.getSupplier());

		Row dateRow = sheet.createRow(startRow++);
		dateRow.createCell(0).setCellValue("Ngày nhập:");
		dateRow.createCell(1).setCellValue(dateFormat.format(importOrder.getImportDate()));

		Row staffRow = sheet.createRow(startRow++);
		staffRow.createCell(0).setCellValue("Nhân viên:");
		staffRow.createCell(1).setCellValue(importOrder.getStaff());

		// Add empty row
		sheet.createRow(startRow++);

		return startRow;
	}

	private int addProductTable(HSSFSheet sheet, List<ImportDetail> details, int startRow, CellStyle headerStyle,
			CellStyle normalStyle, CellStyle currencyStyle) {
		// Create headers
		String[] headers = { "STT", "Tên sản phẩm", "Đơn giá", "Số lượng", "ĐVT", "Thành tiền" };
		Row headerRow = sheet.createRow(startRow++);

		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerStyle);
		}

		// Add products
		int stt = 1;
		double totalAmount = 0;

		for (ImportDetail detail : details) {
			Row row = sheet.createRow(startRow++);

			// STT
			row.createCell(0).setCellValue(stt++);

			// Product name
			row.createCell(1).setCellValue(detail.getProductName() + " - " + detail.getVariantName());

			// Price
			Cell priceCell = row.createCell(2);
			priceCell.setCellValue(detail.getPrice());
			priceCell.setCellStyle(currencyStyle);

			// Quantity
			row.createCell(3).setCellValue(detail.getQuantity());

			// Unit
			row.createCell(4).setCellValue("Cái");

			// Total
			double lineTotal = detail.getPrice() * detail.getQuantity();
			totalAmount += lineTotal;
			Cell totalCell = row.createCell(5);
			totalCell.setCellValue(lineTotal);
			totalCell.setCellStyle(currencyStyle);
		}

		// Add total row
		Row totalRow = sheet.createRow(startRow++);
		Cell totalLabelCell = totalRow.createCell(0);
		totalLabelCell.setCellValue("Tổng cộng:");
		totalLabelCell.setCellStyle(headerStyle);
		sheet.addMergedRegion(new CellRangeAddress(startRow - 1, startRow - 1, 0, 4));

		Cell totalValueCell = totalRow.createCell(5);
		totalValueCell.setCellValue(totalAmount);
		totalValueCell.setCellStyle(currencyStyle);

		return startRow;
	}

	private void addSignatures(HSSFSheet sheet, int startRow, CellStyle headerStyle) {
		// Add empty rows for spacing
		startRow += 2;

		Row signRow = sheet.createRow(startRow);
		String[] signatures = { "Người lập phiếu", "Người giao hàng", "Người nhận hàng" };

		for (int i = 0; i < signatures.length; i++) {
			Cell cell = signRow.createCell(i * 2);
			cell.setCellValue(signatures[i]);
			cell.setCellStyle(headerStyle);
		}
	}

	// Style creation methods
	private CellStyle createHeaderStyle(HSSFWorkbook workbook) {
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(true);
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return style;
	}

	private CellStyle createTitleStyle(HSSFWorkbook workbook) {
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 16);
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);
		return style;
	}

	private CellStyle createNormalStyle(HSSFWorkbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		return style;
	}

	private CellStyle createCurrencyStyle(HSSFWorkbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setAlignment(HorizontalAlignment.RIGHT);
		style.setDataFormat(workbook.createDataFormat().getFormat("#,##0 'VND'"));
		return style;
	}
}