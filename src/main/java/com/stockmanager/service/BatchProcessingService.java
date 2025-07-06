package com.stockmanager.service;

import com.stockmanager.model.StockItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchProcessingService {

    private final ExcelProcessingService excelProcessingService;
    private final PdfGenerationService pdfGenerationService;

    public Map<String, Object> processBatchDirectory(String directoryPath) {
        Map<String, Object> result = new HashMap<>();
        List<String> processedFiles = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();
        
        try {
            Path dirPath = Paths.get(directoryPath);
            if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
                throw new IllegalArgumentException("Invalid directory path: " + directoryPath);
            }
            
            // Get all Excel files in the directory
            List<File> excelFiles = Files.list(dirPath)
                    .filter(path -> path.toString().toLowerCase().endsWith(".xlsx"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
                    
            log.info("Found {} Excel files in directory: {}", excelFiles.size(), directoryPath);
            
            if (excelFiles.isEmpty()) {
                result.put("message", "No Excel files found in the directory");
                return result;
            }
            
            // Process each Excel file
            for (File excelFile : excelFiles) {
                try {
                    // Process Excel file
                    List<StockItem> stockItems = excelProcessingService.processExcelFile(excelFile);
                    
                    // Generate PDF
                    File pdfFile = pdfGenerationService.generatePdf(stockItems, excelFile.getName());
                    
                    processedFiles.add(excelFile.getName() + " -> " + pdfFile.getName());
                } catch (Exception e) {
                    log.error("Error processing file {}: {}", excelFile.getName(), e.getMessage());
                    failedFiles.add(excelFile.getName() + " (Error: " + e.getMessage() + ")");
                }
            }
            
            result.put("processedFiles", processedFiles);
            result.put("failedFiles", failedFiles);
            result.put("totalProcessed", processedFiles.size());
            result.put("totalFailed", failedFiles.size());
            result.put("message", "Processed " + processedFiles.size() + " files, " + failedFiles.size() + " failed");
            
        } catch (IOException e) {
            log.error("Error accessing directory: {}", e.getMessage());
            result.put("error", "Error accessing directory: " + e.getMessage());
        }
        
        return result;
    }
}