package com.stockmanager.service;

import com.stockmanager.model.StockItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExcelProcessingService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.output.dir}")
    private String outputDir;

    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
            Files.createDirectories(Paths.get(outputDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directories!", e);
        }
    }

    public File saveUploadedFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String filename = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            fos.write(file.getBytes());
        }
        
        return filePath.toFile();
    }

    public List<StockItem> processExcelFile(File excelFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<StockItem> stockItems = new ArrayList<>();

            // Reading the header row
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columnMap = new LinkedHashMap<>(); // Maintain order
            
            for (Cell cell : headerRow) {
                String header = cell.getStringCellValue();
                if (header.equalsIgnoreCase("Item name*")) {
                    columnMap.put("Item name*", cell.getColumnIndex());
                } else if (header.equalsIgnoreCase("Current stock quantity")) {
                    columnMap.put("Current stock quantity", cell.getColumnIndex());
                } else if (header.equalsIgnoreCase("Base Unit (x)")) {
                    columnMap.put("Base Unit (x)", cell.getColumnIndex());
                } else if (header.equalsIgnoreCase("Secondary Unit (y)")) {
                    columnMap.put("Secondary Unit (y)", cell.getColumnIndex());
                } else if (header.equalsIgnoreCase("Conversion Rate (n)") || 
                           header.equalsIgnoreCase("Conversion Rate (n) (x = ny)")) {
                    columnMap.put("Conversion Rate (n)", cell.getColumnIndex());
                }
            }

            // Read the data rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    StockItem item = extractStockItem(row, columnMap);
                    if (item != null) {
                        stockItems.add(item);
                    }
                }
            }

            // Sort rows by "Current Stock Quantity" in descending order
            stockItems.sort(Comparator.comparing(StockItem::getCurrentStockQuantity).reversed());
            
            return stockItems;
        }
    }

    private StockItem extractStockItem(Row row, Map<String, Integer> columnMap) {
        try {
            String itemName = getCellValue(row.getCell(columnMap.get("Item name*")));
            
            // Skip rows with empty item names
            if (itemName == null || itemName.trim().isEmpty()) {
                return null;
            }
            
            String stockQuantityStr = getCellValue(row.getCell(columnMap.get("Current stock quantity")));
            double stockQuantity = stockQuantityStr.isEmpty() ? 0 : Double.parseDouble(stockQuantityStr);
            
            String baseUnit = getCellValue(row.getCell(columnMap.get("Base Unit (x)")));
            String secondaryUnit = getCellValue(row.getCell(columnMap.get("Secondary Unit (y)")));
            
            String conversionRateStr = getCellValue(row.getCell(columnMap.get("Conversion Rate (n)")));
            double conversionRate = conversionRateStr.isEmpty() ? 0 : Double.parseDouble(conversionRateStr);
            
            return StockItem.builder()
                    .itemName(itemName)
                    .currentStockQuantity(stockQuantity)
                    .baseUnit(baseUnit)
                    .secondaryUnit(secondaryUnit)
                    .conversionRate(conversionRate)
                    .build();
        } catch (Exception e) {
            log.error("Error extracting stock item from row: {}", e.getMessage());
            return null;
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return "";
        }
    }
}