package com.stockmanager.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.stockmanager.model.StockItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class PdfGenerationService {

    @Value("${app.output.dir}")
    private String outputDir;

    public File generatePdf(List<StockItem> stockItems, String originalFileName) throws IOException {
        Path outputPath = Paths.get(outputDir);
        
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String outputFileName = originalFileName.replace(".xlsx", "") + "_" + timestamp + ".pdf";
        Path pdfPath = outputPath.resolve(outputFileName);
        
        try (PdfWriter writer = new PdfWriter(pdfPath.toFile());
             PdfDocument pdfDocument = new PdfDocument(writer);
             Document document = new Document(pdfDocument)) {
            
            // Add title
            Paragraph title = new Paragraph("Stock Report")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(16);
            document.add(title);
            
            // Add timestamp
            Paragraph timestampPara = new Paragraph("Generated on: " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10);
            document.add(timestampPara);
            
            document.add(new Paragraph("\n"));
            
            // Create table
            Table table = new Table(UnitValue.createPercentArray(new float[]{20, 15, 10, 15, 10, 15}))
                    .useAllAvailableWidth();
            
            // Add headers
            table.addHeaderCell(createHeaderCell("Item Name"));
            table.addHeaderCell(createHeaderCell("Primary Quantity"));
            table.addHeaderCell(createHeaderCell("Base Unit"));
            table.addHeaderCell(createHeaderCell("Secondary Quantity"));
            table.addHeaderCell(createHeaderCell("Secondary Unit"));
            table.addHeaderCell(createHeaderCell("Conversion Rate"));
            
            // Add data rows
            for (StockItem item : stockItems) {
                table.addCell(createCell(item.getItemName()));
                table.addCell(createCell(item.getFormattedStockQuantity()));
                table.addCell(createCell(item.getShortBaseUnit()));
                table.addCell(createCell(item.getFormattedSecondaryUnitQuantity()));
                table.addCell(createCell(item.getShortSecondaryUnit()));
                table.addCell(createCell(String.valueOf(item.getConversionRate())));
            }
            
            document.add(table);
        }
        
        return pdfPath.toFile();
    }
    
    private Cell createHeaderCell(String text) {
        Cell cell = new Cell().add(new Paragraph(text));
        cell.setTextAlignment(TextAlignment.CENTER);
        cell.setBold();
        return cell;
    }
    
    private Cell createCell(String text) {
        Cell cell = new Cell().add(new Paragraph(text != null ? text : ""));
        cell.setTextAlignment(TextAlignment.CENTER);
        return cell;
    }
}