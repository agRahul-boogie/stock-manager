package com.stockmanager.controller;

import com.stockmanager.model.StockItem;
import com.stockmanager.service.ExcelProcessingService;
import com.stockmanager.service.PdfGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class StockManagerApiController {

    private final ExcelProcessingService excelProcessingService;
    private final PdfGenerationService pdfGenerationService;

    @PostMapping("/upload")
    public ResponseEntity<List<StockItem>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Save the uploaded file
            File savedFile = excelProcessingService.saveUploadedFile(file);
            
            // Process the Excel file
            List<StockItem> stockItems = excelProcessingService.processExcelFile(savedFile);
            
            return ResponseEntity.ok(stockItems);
        } catch (Exception e) {
            log.error("Error processing file: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/generate-pdf")
    public ResponseEntity<Resource> generatePdf(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<StockItem> stockItems = (List<StockItem>) request.get("stockItems");
            String fileName = (String) request.get("fileName");
            
            if (stockItems == null || stockItems.isEmpty() || fileName == null) {
                return ResponseEntity.badRequest().build();
            }
            
            // Generate PDF
            File pdfFile = pdfGenerationService.generatePdf(stockItems, fileName);
            
            // Return the PDF file as a download
            Resource resource = new FileSystemResource(pdfFile);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pdfFile.getName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("Error generating PDF: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}