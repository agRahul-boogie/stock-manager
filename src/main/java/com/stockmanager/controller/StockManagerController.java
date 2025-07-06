package com.stockmanager.controller;

import com.stockmanager.model.StockItem;
import com.stockmanager.service.BatchProcessingService;
import com.stockmanager.service.ExcelProcessingService;
import com.stockmanager.service.PdfGenerationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StockManagerController {

    private final ExcelProcessingService excelProcessingService;
    private final PdfGenerationService pdfGenerationService;
    private final BatchProcessingService batchProcessingService;
    
    @PostConstruct
    public void init() {
        excelProcessingService.init();
    }
    
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, 
                             RedirectAttributes redirectAttributes,
                             Model model,
                             HttpSession session) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/";
        }
        
        try {
            // Save the uploaded file
            File savedFile = excelProcessingService.saveUploadedFile(file);
            
            // Process the Excel file
            List<StockItem> stockItems = excelProcessingService.processExcelFile(savedFile);
            
            // Add the processed data to the model and session
            model.addAttribute("stockItems", stockItems);
            model.addAttribute("fileName", file.getOriginalFilename());
            session.setAttribute("stockItems", stockItems);
            session.setAttribute("fileName", file.getOriginalFilename());
            
            return "result";
        } catch (Exception e) {
            log.error("Error processing file: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error processing file: " + e.getMessage());
            return "redirect:/";
        }
    }
    
    @GetMapping("/generate-pdf")
    public ResponseEntity<Resource> generatePdf(HttpSession session) {
        try {
            @SuppressWarnings("unchecked")
            List<StockItem> stockItems = (List<StockItem>) session.getAttribute("stockItems");
            String fileName = (String) session.getAttribute("fileName");
            
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
    
    @PostMapping("/process-batch")
    public String processBatch(@RequestParam("directory") String directory,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        try {
            // Process the batch directory
            Map<String, Object> result = batchProcessingService.processBatchDirectory(directory);
            
            if (result.containsKey("error")) {
                redirectAttributes.addFlashAttribute("error", result.get("error"));
                return "redirect:/";
            }
            
            // Add the results to the model
            model.addAttribute("batchResults", result);
            return "batch-result";
        } catch (Exception e) {
            log.error("Error in batch processing: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error in batch processing: " + e.getMessage());
            return "redirect:/";
        }
    }
}