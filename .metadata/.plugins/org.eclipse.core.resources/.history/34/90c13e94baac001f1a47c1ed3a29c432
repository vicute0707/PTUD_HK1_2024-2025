package export;

import java.io.FileOutputStream;

import javax.swing.JTable;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelExporterNV {
	public boolean exportTable(JTable table, String filePath, String sheetName) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(sheetName);
            
            // Create header row
            XSSFRow headerRow = sheet.createRow(0);
            for (int i = 0; i < table.getColumnCount(); i++) {
                XSSFCell cell = headerRow.createCell(i);
                cell.setCellValue(table.getColumnName(i));
            }
            
            // Create data rows
            for (int i = 0; i < table.getRowCount(); i++) {
                XSSFRow row = sheet.createRow(i + 1);
                for (int j = 0; j < table.getColumnCount(); j++) {
                    XSSFCell cell = row.createCell(j);
                    if (table.getValueAt(i, j) != null) {
                        cell.setCellValue(table.getValueAt(i, j).toString());
                    }
                }
            }
            
            // Auto size columns
            for (int i = 0; i < table.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            workbook.close();
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
