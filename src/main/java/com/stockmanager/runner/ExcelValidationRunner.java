package com.stockmanager.runner;

import com.stockmanager.util.ExcelValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.io.File;

/**
 * Configuration class that provides a CommandLineRunner for validating Excel files.
 * This runner is only activated when the property "app.validate-excel" is set to true.
 */
@Configuration
public class ExcelValidationRunner {

    @Autowired
    private ExcelValidator excelValidator;
    
    /**
     * Creates a CommandLineRunner bean that validates a sample Excel file.
     * This provides similar functionality to the original ExcelTester but within
     * the Spring Boot application lifecycle.
     * 
     * @return A CommandLineRunner that validates an Excel file
     */
    @Bean
    @Order(2) // Run after other critical initialization
    @ConditionalOnProperty(name = "app.validate-excel", havingValue = "true")
    public CommandLineRunner validateExcelRunner() {
        return args -> {
            System.out.println("Running Excel validation...");
            
            // Try to find the sample file in several possible locations
            File file = new File("sample-stock.xlsx");
            if (!file.exists()) {
                file = new File("../sample-stock.xlsx");
            }
            if (!file.exists()) {
                file = new File("/workspace/Trial-new/sample-stock.xlsx");
            }
            
            if (file.exists()) {
                excelValidator.printExcelFileInfo(file);
            } else {
                System.err.println("Sample Excel file not found. Skipping validation.");
            }
        };
    }
}