package com.pluralsight;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class UserInterface {
    private Dealership dealership;
    private final DealershipFileManager dealershipFileManager;
    private final ContractFileManager contractFileManager;
    private final Scanner scanner;

    public UserInterface() {
        this.dealershipFileManager = new DealershipFileManager();
        this.contractFileManager = new ContractFileManager();
        this.scanner = new Scanner(System.in);
    }

    private void init() {
        dealership = dealershipFileManager.getDealership();
        if (dealership == null) {
            System.out.println("Could not load dealership. Make sure inventory.csv exists in the project root.");
        }
    }

    public void display() {
        init();
        if (dealership == null) return;

        System.out.println("\n=== Welcome to " + dealership.getName() + " ===");
        System.out.println("    " + dealership.getAddress() + "  |  " + dealership.getPhone());

        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": processGetByPriceRequest();
                break;
                case "2": processGetByMakeModelRequest();
                break;
                case "3": processGetByYearRequest();
                break;
                case "4": processGetByColorRequest();
                break;
                case "5": processGetByMileageRequest();
                break;
                case "6": processGetByVehicleTypeRequest();
                break;
                case "7": processGetAllVehiclesRequest();
                break;
                case "8": processAddVehicleRequest();
                break;
                case "9": processRemoveVehicleRequest();
                break;
                case "10": processSellOrLeaseRequest();
                break;
                case "99":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n----- Main Menu -----");
        System.out.println(" 1  - Find vehicles within a price range");
        System.out.println(" 2  - Find vehicles by make / model");
        System.out.println(" 3  - Find vehicles by year range");
        System.out.println(" 4  - Find vehicles by color");
        System.out.println(" 5  - Find vehicles by mileage range");
        System.out.println(" 6  - Find vehicles by type (car, truck, SUV, van)");
        System.out.println(" 7  - List ALL vehicles");
        System.out.println(" 8  - Add a vehicle");
        System.out.println(" 9  - Remove a vehicle");
        System.out.println(" 10 - SELL / LEASE a vehicle");
        System.out.println(" 99 - Quit");
        System.out.print("Select an option: ");
    }

    private void displayVehicles(List<Vehicle> vehicles) {
        if (vehicles == null || vehicles.isEmpty()) {
            System.out.println("No vehicles found.");
            return;
        }
        System.out.printf("%n%-7s %-6s %-12s %-12s %-10s %-10s %-9s %s%n",
                "VIN", "YEAR", "MAKE", "MODEL", "TYPE", "COLOR", "ODOMETER", "PRICE");
        System.out.println("------------------------------------------------------------------------------------");
        for (Vehicle v : vehicles) {
            System.out.println(v);
        }
    }

// search-

    private void processGetByPriceRequest() {
        double min = readDouble("Minimum price: ");
        double max = readDouble("Maximum price: ");
        displayVehicles(dealership.getVehiclesByPrice(min, max));
    }

    private void processGetByMakeModelRequest() {
        System.out.print("Make: ");
        String make = scanner.nextLine();
        System.out.print("Model: ");
        String model = scanner.nextLine();
        displayVehicles(dealership.getVehiclesByMakeModel(make, model));
    }

    private void processGetByYearRequest() {
        int min = readInt("Minimum year: ");
        int max = readInt("Maximum year: ");
        displayVehicles(dealership.getVehiclesByYear(min, max));
    }

    private void processGetByColorRequest() {
        System.out.print("Color: ");
        String color = scanner.nextLine();
        displayVehicles(dealership.getVehiclesByColor(color));
    }

    private void processGetByMileageRequest() {
        int min = readInt("Minimum mileage: ");
        int max = readInt("Maximum mileage: ");
        displayVehicles(dealership.getVehiclesByMileage(min, max));
    }

    private void processGetByVehicleTypeRequest() {
        System.out.print("Vehicle type (car/truck/SUV/van): ");
        String type = scanner.nextLine();
        displayVehicles(dealership.getVehiclesByType(type));
    }

    private void processGetAllVehiclesRequest() {
        displayVehicles(dealership.getAllVehicles());
    }

    private void processAddVehicleRequest() {
        int vin = readInt("VIN: ");
        int year = readInt("Year: ");
        System.out.print("Make: ");   String make = scanner.nextLine();
        System.out.print("Model: ");  String model = scanner.nextLine();
        System.out.print("Type: ");   String type = scanner.nextLine();
        System.out.print("Color: ");  String color = scanner.nextLine();
        int odo = readInt("Odometer: ");
        double price = readDouble("Price: ");

        Vehicle v = new Vehicle(vin, year, make, model, type, color, odo, price);
        dealership.addVehicle(v);
        dealershipFileManager.saveDealership(dealership);
        System.out.println("Vehicle added.");
    }

    private void processRemoveVehicleRequest() {
        int vin = readInt("VIN of vehicle to remove: ");
        Vehicle v = dealership.findByVin(vin);
        if (v == null) {
            System.out.println("No vehicle with that VIN.");
            return;
        }
        dealership.removeVehicle(v);
        dealershipFileManager.saveDealership(dealership);
        System.out.println("Vehicle removed.");
    }


    // building sell

    private void processSellOrLeaseRequest() {
        int vin = readInt("VIN of the vehicle: ");
        Vehicle vehicle = dealership.findByVin(vin);
        if (vehicle == null) {
            System.out.println("No vehicle with VIN " + vin + " was found in inventory.");
            return;
        }

        System.out.println("Selected: " + vehicle);

        System.out.print("Customer name: ");
        String name = scanner.nextLine();
        System.out.print("Customer email: ");
        String email = scanner.nextLine();

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        System.out.print("Is this a (S)ale or (L)ease? ");
        String kind = scanner.nextLine().trim().toUpperCase();

        Contract contract;
        if (kind.startsWith("L")) {
            int age = LocalDate.now().getYear() - vehicle.getYear();
            if (age > 3) {
                System.out.println("Sorry — you can't lease a vehicle over 3 years old (this one is "
                        + age + " years old).");
                return;
            }
            contract = new LeaseContract(date, name, email, vehicle);
        } else if (kind.startsWith("S")) {
            System.out.print("Would you like to finance this purchase? (Y/N): ");
            boolean financed = scanner.nextLine().trim().toUpperCase().startsWith("Y");
            contract = new SalesContract(date, name, email, vehicle, financed);
        } else {
            System.out.println("Unknown option — contract not created.");
            return;
        }

        contractFileManager.saveContract(contract);

        // Remove the sold/leased vehicle from inventory
        dealership.removeVehicle(vehicle);
        dealershipFileManager.saveDealership(dealership);

        System.out.println();
        System.out.println("------ Contract Summary ------");
        System.out.println("Type:            " + (contract instanceof LeaseContract ? "LEASE" : "SALE"));
        System.out.println("Customer:        " + name + "  (" + email + ")");
        System.out.println("Vehicle:         " + vehicle.getYear() + " " + vehicle.getMake()
                + " " + vehicle.getModel() + " (VIN " + vehicle.getVin() + ")");
        System.out.printf ("Total price:     $%,.2f%n", contract.getTotalPrice());
        System.out.printf ("Monthly payment: $%,.2f%n", contract.getMonthlyPayment());
        System.out.println("Saved to contracts.csv. Vehicle removed from inventory.");
    }

    // --------------------------------------------------------------------
    // Input helpers
    // --------------------------------------------------------------------

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try { return Integer.parseInt(line); }
            catch (NumberFormatException e) { System.out.println("Please enter a whole number."); }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try { return Double.parseDouble(line); }
            catch (NumberFormatException e) { System.out.println("Please enter a number."); }
        }
    }
}