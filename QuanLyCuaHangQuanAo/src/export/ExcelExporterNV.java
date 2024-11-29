package export;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExcelExporterNV {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");

    public static void exportToExcel(JTable table, String title, Component parentComponent) {
        try {
            // Tạo workbook mới
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("Báo cáo");

            // Thiết lập style cho tiêu đề
            HSSFCellStyle titleStyle = workbook.createCellStyle();
            HSSFFont titleFont = workbook.createFont();
            titleFont.setBold(true);  // Thay thế cho setBoldweight
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);  // Thay thế cho ALIGN_CENTER

            // Thiết lập style cho header
            HSSFCellStyle headerStyle = workbook.createCellStyle();
            HSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);  // Thay thế cho setBoldweight
            headerFont.setColor(IndexedColors.WHITE.getIndex());  // Thay thế cho HSSFColor.WHITE.index
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());  // Thay thế cho HSSFColor.ROYAL_BLUE.index
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);  // Thay thế cho SOLID_FOREGROUND
            headerStyle.setAlignment(HorizontalAlignment.CENTER);  // Thay thế cho ALIGN_CENTER
            headerStyle.setBorderTop(BorderStyle.THIN);  // Thay thế cho BORDER_THIN
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Tạo style cho dữ liệu
            HSSFCellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderTop(BorderStyle.THIN);  // Thay thế cho BORDER_THIN
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setAlignment(HorizontalAlignment.LEFT);  // Căn lề trái cho dữ liệu
            
            // Tạo tiêu đề báo cáo
            int currentRow = 0;
            Row titleRow = sheet.createRow(currentRow++);
            titleRow.setHeight((short) 600);  // Đặt chiều cao cho dòng tiêu đề
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("BÁO CÁO " + title.toUpperCase());
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, table.getColumnCount() - 1));

            // Thêm ngày xuất báo cáo
            Row dateRow = sheet.createRow(currentRow++);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Ngày xuất: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, table.getColumnCount() - 1));

            // Tạo một dòng trống để phân cách
            currentRow++;

            // Tạo header cho bảng
            Row headerRow = sheet.createRow(currentRow++);
            headerRow.setHeight((short) 400);  // Đặt chiều cao cho dòng header
            TableModel model = table.getModel();

            for (int col = 0; col < model.getColumnCount(); col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(model.getColumnName(col));
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(col, 15 * 256);  // Đặt độ rộng cột cơ bản
            }

            // Thêm dữ liệu từ bảng
            for (int row = 0; row < model.getRowCount(); row++) {
                Row dataRow = sheet.createRow(currentRow++);
                dataRow.setHeight((short) 350);  // Đặt chiều cao cho dòng dữ liệu
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Cell cell = dataRow.createCell(col);
                    Object value = model.getValueAt(row, col);
                    cell.setCellValue(value != null ? value.toString() : "");
                    cell.setCellStyle(dataStyle);
                }
            }

            // Tự động điều chỉnh độ rộng cột
            for (int col = 0; col < model.getColumnCount(); col++) {
                sheet.autoSizeColumn(col);
                // Thêm một chút padding
                int currentWidth = sheet.getColumnWidth(col);
                sheet.setColumnWidth(col, currentWidth + 1000);
            }

            // Hiển thị hộp thoại chọn nơi lưu file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn vị trí lưu file");
            String defaultFileName = title.toLowerCase().replace(" ", "_") + "_" + 
                                   DATE_FORMAT.format(new Date()) + ".xls";
            fileChooser.setSelectedFile(new File(defaultFileName));

            if (fileChooser.showSaveDialog(parentComponent) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".xls")) {
                    file = new File(file.getAbsolutePath() + ".xls");
                }

                try (FileOutputStream out = new FileOutputStream(file)) {
                    workbook.write(out);
                    workbook.close();
                    
                    JOptionPane.showMessageDialog(
                        parentComponent,
                        "Xuất file Excel thành công!\nVị trí: " + file.getAbsolutePath(),
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                parentComponent,
                "Lỗi khi xuất file Excel: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}