package com.pluralsight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DealershipFileManager {
    private static final String INVENTORY_FILE = "src/main/resources/inventory.csv";

    public Dealership getDealership() {
        Dealership dealership = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(INVENTORY_FILE))) {
            String header = reader.readLine();
            if (header != null) {
                String[] info = header.split("\\|");
                dealership = new Dealership(info[0], info[1], info[2]);
            }
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] f = line.split("\\|");
                Vehicle v = new Vehicle(
                        Integer.parseInt(f[0].trim()),
                        Integer.parseInt(f[1].trim()),
                        f[2].trim(),
                        f[3].trim(),
                        f[4].trim(),
                        f[5].trim(),
                        Integer.parseInt(f[6].trim()),
                        Double.parseDouble(f[7].trim())
                );
                dealership.addVehicle(v);
            }
        } catch (IOException e) {
            System.out.println("Error reading inventory file: " + e.getMessage());
        }
        return dealership;
    }

    public void saveDealership(Dealership dealership) {
        if (dealership == null) return;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(INVENTORY_FILE))) {
            writer.write(dealership.getName() + "|" + dealership.getAddress() + "|" + dealership.getPhone());
            writer.newLine();
            for (Vehicle v : dealership.getAllVehicles()) {
                writer.write(String.format("%d|%d|%s|%s|%s|%s|%d|%.2f",
                        v.getVin(), v.getYear(), v.getMake(), v.getModel(),
                        v.getVehicleType(), v.getColor(), v.getOdometer(), v.getPrice()));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving inventory file: " + e.getMessage());
        }
    }
}
