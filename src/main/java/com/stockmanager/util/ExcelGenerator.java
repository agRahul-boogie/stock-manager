package com.stockmanager.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelGenerator {
    
    public static void main(String[] args) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Stock Items");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            createCell(headerRow, 0, "Item name*");
            createCell(headerRow, 1, "Current stock quantity");
            createCell(headerRow, 2, "Base Unit (x)");
            createCell(headerRow, 3, "Secondary Unit (y)");
            createCell(headerRow, 4, "Conversion Rate (n)");
            
            // Create data rows
            createDataRow(sheet, 1, "Rice", 100.0, "KILOGRAMS", "GRAMS", 1000.0);
            createDataRow(sheet, 2, "Sugar", 50.0, "KILOGRAMS", "GRAMS", 1000.0);
            createDataRow(sheet, 3, "Salt", 25.0, "KILOGRAMS", "GRAMS", 1000.0);
            createDataRow(sheet, 4, "Flour", 75.0, "KILOGRAMS", "GRAMS", 1000.0);
            createDataRow(sheet, 5, "Apples", 200.0, "PIECES", "", 0.0);
            createDataRow(sheet, 6, "Oranges", 150.0, "PIECES", "", 0.0);
            createDataRow(sheet, 7, "Milk", 30.0, "LITERS", "MILLILITERS", 1000.0);
            createDataRow(sheet, 8, "Eggs", 120.0, "PIECES", "", 0.0);
            createDataRow(sheet, 9, "Bread", 45.0, "PIECES", "", 0.0);
            createDataRow(sheet, 10, "Butter", 20.0, "KILOGRAMS", "GRAMS", 1000.0);
            
            // Auto-size columns
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream("/workspace/Trial-new/sample-stock.xlsx")) {
                workbook.write(fileOut);
                System.out.println("Sample Excel file created successfully!");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void createCell(Row row, int column, String value) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
    }
    
    private static void createDataRow(Sheet sheet, int rowNum, String itemName, double quantity, 
                                     String baseUnit, String secondaryUnit, double conversionRate) {
        Row row = sheet.createRow(rowNum);
        createCell(row, 0, itemName);
        row.createCell(1).setCellValue(quantity);
        createCell(row, 2, baseUnit);
        createCell(row, 3, secondaryUnit);
        row.createCell(4).setCellValue(conversionRate);
    }
}