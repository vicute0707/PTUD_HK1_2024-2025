package export;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import entity.Import;
import entity.ImportDetail;

import java.awt.Color;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Logger;

public class PDFExporterHoaDonNhap {
	private static final Logger LOGGER = Logger.getLogger(PDFExporterHoaDonNhap.class.getName());

	// Shop information constants - could be moved to configuration
	private static final String SHOP_NAME = "FASHION SHOP";
	private static final String SHOP_ADDRESS = "123 Nguyễn Văn Cừ, Quận 5, TP.HCM";
	private static final String SHOP_PHONE = "0123 456 789";
	private static final String SHOP_EMAIL = "contact@fashionshop.com";

	public void exportToPDF(Import importOrder, List<ImportDetail> details, String filePath) throws Exception {
		// Create document with A4 size and margins
		Document document = new Document(PageSize.A4, 50, 50, 50, 50);

		try {
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
			document.open();

			// Set up fonts - using BaseFont for Vietnamese characters
			BaseFont baseFont = BaseFont.createFont("c:\\windows\\fonts\\arial.ttf", BaseFont.IDENTITY_H,
					BaseFont.EMBEDDED);
			Font titleFont = new Font(baseFont, 16, Font.BOLD);
			Font headerFont = new Font(baseFont, 12, Font.BOLD);
			Font normalFont = new Font(baseFont, 12, Font.NORMAL);

			// Add shop header
			addShopHeader(document, titleFont, normalFont);

			// Add document title and import information
			addImportHeader(document, importOrder, titleFont, headerFont, normalFont);

			// Add product table
			addProductTable(document, details, baseFont);

			// Add summary and signatures
			addSummaryAndSignatures(document, importOrder, headerFont, normalFont);

			LOGGER.info("Successfully generated PDF invoice: " + filePath);

		} finally {
			document.close();
		}
	}

	private void addShopHeader(Document document, Font titleFont, Font normalFont) throws DocumentException {
		Paragraph shopInfo = new Paragraph();
		shopInfo.setAlignment(Element.ALIGN_CENTER);

		// Add shop name and info
		shopInfo.add(new Chunk(SHOP_NAME + "\n", titleFont));
		shopInfo.add(new Chunk("Địa chỉ: " + SHOP_ADDRESS + "\n", normalFont));
		shopInfo.add(new Chunk("SĐT: " + SHOP_PHONE + " - Email: " + SHOP_EMAIL + "\n\n", normalFont));

		document.add(shopInfo);
	}

	private void addImportHeader(Document document, Import importOrder, Font titleFont, Font headerFont,
			Font normalFont) throws DocumentException {
		// Add document title
		Paragraph title = new Paragraph("PHIẾU NHẬP HÀNG", titleFont);
		title.setAlignment(Element.ALIGN_CENTER);
		document.add(title);

		// Add import ID
		Paragraph importId = new Paragraph("Số: " + importOrder.getImportID(), headerFont);
		importId.setAlignment(Element.ALIGN_CENTER);
		document.add(importId);
		document.add(new Paragraph("\n"));

		// Add import details
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Paragraph details = new Paragraph();
		details.add(new Chunk("Nhà cung cấp: " + importOrder.getSupplier() + "\n", normalFont));
		details.add(new Chunk("Ngày nhập: " + dateFormat.format(importOrder.getImportDate()) + "\n", normalFont));
		details.add(new Chunk("Nhân viên: " + importOrder.getStaff() + "\n\n", normalFont));

		document.add(details);
	}

	private void addProductTable(Document document, List<ImportDetail> details, BaseFont baseFont)
			throws DocumentException {
		PdfPTable table = new PdfPTable(new float[] { 0.5f, 2f, 1f, 1f, 1f, 1.5f });
		table.setWidthPercentage(100);
		table.setSpacingBefore(10);
		table.setSpacingAfter(10);

		// Create fonts for the table
		Font headerFont = new Font(baseFont, 12, Font.BOLD);
		Font contentFont = new Font(baseFont, 12);

		// Add table headers
		String[] headers = { "STT", "Tên sản phẩm", "Đơn giá", "SL", "ĐVT", "Thành tiền" };
		for (String header : headers) {
			PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBackgroundColor(new Color(240, 240, 240));
			cell.setPadding(8);
			table.addCell(cell);
		}

		// Add product details
		double totalAmount = 0;
		int stt = 1;
		for (ImportDetail detail : details) {
			// STT
			addCenterCell(table, String.valueOf(stt++), contentFont);

			// Product name and variant
			addLeftCell(table, detail.getProductName() + " - " + detail.getVariantName(), contentFont);

			// Price
			addRightCell(table, formatCurrency(detail.getPrice()), contentFont);

			// Quantity
			addCenterCell(table, String.valueOf(detail.getQuantity()), contentFont);

			// Unit
			addCenterCell(table, "Cái", contentFont);

			// Total amount
			double lineTotal = detail.getPrice() * detail.getQuantity();
			totalAmount += lineTotal;
			addRightCell(table, formatCurrency(lineTotal), contentFont);
		}

		// Add total row
		PdfPCell totalLabelCell = new PdfPCell(new Phrase("Tổng cộng:", headerFont));
		totalLabelCell.setColspan(5);
		totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		totalLabelCell.setPadding(8);
		table.addCell(totalLabelCell);

		PdfPCell totalValueCell = new PdfPCell(new Phrase(formatCurrency(totalAmount), headerFont));
		totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		totalValueCell.setPadding(8);
		table.addCell(totalValueCell);

		document.add(table);
	}

	private void addSummaryAndSignatures(Document document, Import importOrder, Font headerFont, Font normalFont)
			throws DocumentException {
		// Add empty space
		document.add(new Paragraph("\n"));

		// Add signatures
		PdfPTable signatureTable = new PdfPTable(3);
		signatureTable.setWidthPercentage(100);

		// Add signature headers
		String[] signatures = { "Người lập phiếu", "Người giao hàng", "Người nhận hàng" };
		for (String signature : signatures) {
			PdfPCell cell = new PdfPCell(new Phrase(signature, headerFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setPaddingBottom(50);
			signatureTable.addCell(cell);
		}

		document.add(signatureTable);
	}

	// Helper methods for table cells
	private void addCenterCell(PdfPTable table, String text, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setPadding(5);
		table.addCell(cell);
	}

	private void addLeftCell(PdfPTable table, String text, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setPadding(5);
		table.addCell(cell);
	}

	private void addRightCell(PdfPTable table, String text, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setPadding(5);
		table.addCell(cell);
	}

	private String formatCurrency(double amount) {
		return String.format("%,.0f VND", amount);
	}
}