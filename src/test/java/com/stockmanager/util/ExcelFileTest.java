package com.stockmanager.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for verifying Excel file structure and content.
 * This replaces the standalone ExcelTester utility with proper JUnit tests.
 */
@SpringBootTest
public class ExcelFileTest {

    @Test
    public void testExcelFileStructure() throws IOException {
        // Try to find the sample file in several possible locations
        File file = null;
        String[] possiblePaths = {
            "classpath:sample-stock.xlsx",
            "sample-stock.xlsx",
            "../sample-stock.xlsx"
        };
        
        for (String path : possiblePaths) {
            try {
                file = ResourceUtils.getFile(path);
                if (file.exists()) {
                    break;
                }
            } catch (Exception e) {
                // Continue to next path
            }
        }
        
        // If file not found in predefined paths, look in the workspace
        if (file == null || !file.exists()) {
            file = new File("/workspace/Trial-new/sample-stock.xlsx");
        }
        
        // Verify file exists
        assertTrue(file.exists(), "Sample Excel file should exist");
        assertTrue(file.length() > 0, "File should not be empty");
        
        // Test reading the workbook
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            assertNotNull(workbook, "Workbook should be successfully opened");
            assertTrue(workbook.getNumberOfSheets() > 0, "Workbook should have at least one sheet");
            
            Sheet sheet = workbook.getSheetAt(0);
            assertNotNull(sheet, "First sheet should exist");
            
            // Verify header row exists
            Row headerRow = sheet.getRow(0);
            assertNotNull(headerRow, "Header row should exist");
            
            // Verify expected columns exist
            boolean hasItemNameColumn = false;
            boolean hasStockQuantityColumn = false;
            
            for (Cell cell : headerRow) {
                String headerValue = cell.getStringCellValue();
                if (headerValue.contains("Item name")) {
                    hasItemNameColumn = true;
                } else if (headerValue.contains("Current stock quantity")) {
                    hasStockQuantityColumn = true;
                }
            }
            
            assertTrue(hasItemNameColumn, "Excel should have 'Item name' column");
            assertTrue(hasStockQuantityColumn, "Excel should have 'Current stock quantity' column");
            
            // Verify at least one data row exists
            assertTrue(sheet.getLastRowNum() > 0, "Excel should have at least one data row");
            
            // Verify first data row has content
            Row dataRow = sheet.getRow(1);
            assertNotNull(dataRow, "First data row should exist");
        }
    }
}