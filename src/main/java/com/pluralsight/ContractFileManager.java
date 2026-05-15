package com.pluralsight;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ContractFileManager {
    private static final String CONTRACTS_FILE = "src/main/resources/contracts.csv";

    public void saveContract(Contract contract) {
        if (contract == null) return;

        String line;
        if (contract instanceof SalesContract) {
            line = formatSale((SalesContract) contract);
        } else if (contract instanceof LeaseContract) {
            line = formatLease((LeaseContract) contract);
        } else {
            System.out.println("Unknown contract type — not saved.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONTRACTS_FILE, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving contract: " + e.getMessage());
        }
    }

    private String formatSale(SalesContract c) {
        Vehicle v = c.getVehicleSold();
        // SALE|date|name|email|vin|year|make|model|type|color|odo|price|tax|rec|proc|total|finance|monthly
        return String.format(
                "SALE|%s|%s|%s|%d|%d|%s|%s|%s|%s|%d|%.2f|%.2f|%.2f|%.2f|%.2f|%s|%.2f",
                c.getDate(),
                c.getCustomerName(),
                c.getCustomerEmail(),
                v.getVin(),
                v.getYear(),
                v.getMake(),
                v.getModel(),
                v.getVehicleType(),
                v.getColor(),
                v.getOdometer(),
                v.getPrice(),
                c.getSalesTaxAmount(),
                c.getRecordingFee(),
                c.getProcessingFee(),
                c.getTotalPrice(),
                c.isFinanced() ? "YES" : "NO",
                c.getMonthlyPayment()
        );
    }

    private String formatLease(LeaseContract c) {
        Vehicle v = c.getVehicleSold();
        // LEASE|date|name|email|vin|year|make|model|type|color|odo|price|endingValue|leaseFee|total|monthly
        return String.format(
                "LEASE|%s|%s|%s|%d|%d|%s|%s|%s|%s|%d|%.2f|%.2f|%.2f|%.2f|%.2f",
                c.getDate(),
                c.getCustomerName(),
                c.getCustomerEmail(),
                v.getVin(),
                v.getYear(),
                v.getMake(),
                v.getModel(),
                v.getVehicleType(),
                v.getColor(),
                v.getOdometer(),
                v.getPrice(),
                c.getExpectedEndingValue(),
                c.getLeaseFee(),
                c.getTotalPrice(),
                c.getMonthlyPayment()
        );
    }
}