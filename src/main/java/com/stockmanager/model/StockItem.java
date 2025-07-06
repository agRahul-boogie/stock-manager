package com.stockmanager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockItem {
    private String itemName;
    private double currentStockQuantity;
    private String baseUnit;
    private String secondaryUnit;
    private double conversionRate;
    
    // Derived field for display purposes
    public String getFormattedStockQuantity() {
        return String.format("%.2f", currentStockQuantity);
    }
    
    public String getShortBaseUnit() {
        return shortName(baseUnit);
    }
    
    public String getShortSecondaryUnit() {
        return shortName(secondaryUnit);
    }
    
    private String shortName(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        
        switch (str.toUpperCase()) {
            case "PIECES": return "Pcs";
            case "KILOGRAMS": return "Kgs";
            default: return !str.isEmpty() ? 
                    str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase() : str;
        }
    }
    
    // Calculate secondary unit quantity based on conversion rate
    public double getSecondaryUnitQuantity() {
        if (conversionRate <= 0) {
            return 0;
        }
        return currentStockQuantity * conversionRate;
    }
    
    // Format secondary unit quantity for display
    public String getFormattedSecondaryUnitQuantity() {
        return String.format("%.2f", getSecondaryUnitQuantity());
    }
}