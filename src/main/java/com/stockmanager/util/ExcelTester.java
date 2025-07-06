package com.stockmanager.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * This is a simple utility class to test Excel file structure.
 * It's not using JUnit framework, but rather a standalone application
 * that can be run to verify Excel file structure and content.
 * This helps with debugging and understanding the Excel file format.
 */
public class ExcelTester {
    
    public static void main(String[] args) {
        try {
            File file = new File("/workspace/Trial-new/sample-stock.xlsx");
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
        } catch (IOException e) {
            System.err.println("Error reading Excel file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}