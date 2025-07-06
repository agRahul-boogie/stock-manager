package com.stockmanager.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility component for validating Excel files.
 * This replaces the standalone ExcelTester with a proper Spring component
 * that can be injected and used by other components.
 */
@Component
public class ExcelValidator {

    /**
     * Validates an Excel file structure and returns information about it.
     * 
     * @param file The Excel file to validate
     * @return A map containing information about the Excel file
     * @throws IOException If there's an error reading the file
     */
    public Map<String, Object> validateExcelFile(File file) throws IOException {
        Map<String, Object> result = new HashMap<>();
        
        result.put("exists", file.exists());
        result.put("size", file.length());
        
        if (!file.exists()) {
            result.put("error", "File does not exist");
            return result;
        }
        
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            result.put("sheetCount", workbook.getNumberOfSheets());
            
            if (workbook.getNumberOfSheets() > 0) {
                Sheet sheet = workbook.getSheetAt(0);
                result.put("sheetName", workbook.getSheetName(0));
                result.put("rowCount", sheet.getLastRowNum());
                
                // Get header information
                Row headerRow = sheet.getRow(0);
                if (headerRow != null) {
                    Map<String, Integer> headers = new HashMap<>();
                    for (Cell cell : headerRow) {
                        headers.put(cell.getStringCellValue(), cell.getColumnIndex());
                    }
                    result.put("headers", headers);
                    
                    // Check for required columns
                    boolean hasItemName = headers.containsKey("Item name*");
                    boolean hasStockQuantity = headers.containsKey("Current stock quantity");
                    result.put("hasRequiredColumns", hasItemName && hasStockQuantity);
                }
                
                // Get sample data from first row
                if (sheet.getLastRowNum() > 0) {
                    Row dataRow = sheet.getRow(1);
                    if (dataRow != null) {
                        Map<Integer, Object> firstRowData = new HashMap<>();
                        for (Cell cell : dataRow) {
                            switch (cell.getCellType()) {
                                case STRING:
                                    firstRowData.put(cell.getColumnIndex(), cell.getStringCellValue());
                                    break;
                                case NUMERIC:
                                    firstRowData.put(cell.getColumnIndex(), cell.getNumericCellValue());
                                    break;
                                case BOOLEAN:
                                    firstRowData.put(cell.getColumnIndex(), cell.getBooleanCellValue());
                                    break;
                                default:
                                    firstRowData.put(cell.getColumnIndex(), null);
                            }
                        }
                        result.put("sampleData", firstRowData);
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * Prints information about an Excel file to the console.
     * This method provides similar functionality to the original ExcelTester
     * but as a method that can be called from other components.
     * 
     * @param file The Excel file to analyze
     * @throws IOException If there's an error reading the file
     */
    public void printExcelFileInfo(File file) throws IOException {
        System.out.println("File exists: " + file.exists());
        System.out.println("File size: " + file.length() + " bytes");
        
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            System.out.println("Successfully opened workbook");
            System.out.println("Number of sheets: " + workbook.getNumberOfSheets());
            
            Sheet sheet = workbook.getSheetAt(0);
            System.out.println("Sheet name: " + workbook.getSheetName(0));
            System.out.println("Number of rows: " + sheet.getLastRowNum());
            
            // Print header row
            Row headerRow = sheet.getRow(0);
            if (headerRow != null) {
                System.out.println("\nHeader row:");
                for (Cell cell : headerRow) {
                    System.out.print(cell.getStringCellValue() + " | ");
                }
                System.out.println();
            }
            
            // Print first data row
            Row dataRow = sheet.getRow(1);
            if (dataRow != null) {
                System.out.println("\nFirst data row:");
                for (Cell cell : dataRow) {
                    switch (cell.getCellType()) {
                        case STRING:
                            System.out.print(cell.getStringCellValue() + " | ");
                            break;
                        case NUMERIC:
                            System.out.print(cell.getNumericCellValue() + " | ");
                            break;
                        case BOOLEAN:
                            System.out.print(cell.getBooleanCellValue() + " | ");
                            break;
                        default:
                            System.out.print("[EMPTY] | ");
                    }
                }
            }
        }
    }
}