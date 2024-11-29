package export;

import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExcelExporterNCC {
    private static final String[] HEADERS = {"Mã NCC", "Tên nhà cung cấp", "Địa chỉ", "Email", "Số điện thoại"};
    
    public void exportToExcel(JTable table, String filePath) throws IOException {
        TableModel model = table.getModel();
        
        // Sử dụng HSSFWorkbook thay vì XSSFWorkbook
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Danh sách nhà cung cấp");
        
        // Style cho tiêu đề
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        
        // Tạo tiêu đề
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DANH SÁCH NHÀ CUNG CẤP");
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
        
        // Style cho header của bảng
        CellStyle tableHeaderStyle = workbook.createCellStyle();
        Font tableHeaderFont = workbook.createFont();
        tableHeaderFont.setBold(true);
        tableHeaderStyle.setFont(tableHeaderFont);
        tableHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
        tableHeaderStyle.setBorderBottom(BorderStyle.THIN);
        tableHeaderStyle.setBorderTop(BorderStyle.THIN);
        tableHeaderStyle.setBorderRight(BorderStyle.THIN);
        tableHeaderStyle.setBorderLeft(BorderStyle.THIN);
        tableHeaderStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        tableHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        // Tạo header cho bảng
        Row headerRow = sheet.createRow(2);
        for (int col = 0; col < HEADERS.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(HEADERS[col]);
            cell.setCellStyle(tableHeaderStyle);
        }
        
        // Style cho dữ liệu
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        
        // Thêm dữ liệu
        for (int row = 0; row < model.getRowCount(); row++) {
            Row dataRow = sheet.createRow(row + 3);
            for (int col = 0; col < model.getColumnCount(); col++) {
                Cell cell = dataRow.createCell(col);
                Object value = model.getValueAt(row, col);
                cell.setCellValue(value != null ? value.toString() : "");
                cell.setCellStyle(dataStyle);
            }
        }
        
        // Thêm dòng tổng số lượng
        Row totalRow = sheet.createRow(model.getRowCount() + 4);
        Cell totalLabelCell = totalRow.createCell(0);
        totalLabelCell.setCellValue("Tổng số nhà cung cấp: " + model.getRowCount());
        
        // Tự động điều chỉnh độ rộng cột
        for (int col = 0; col < HEADERS.length; col++) {
            sheet.autoSizeColumn(col);
        }
        
        // Lưu file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        } finally {
            workbook.close();
        }
    }
}