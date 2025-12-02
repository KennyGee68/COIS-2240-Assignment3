import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.File;



public class RentalSystem

{
	// singleton instance
	private static RentalSystem instance; 
	// Refactored for constructor 
    private List<Vehicle> vehicles;
    private List<Customer> customers;
    private RentalHistory rentalHistory;
    
    
    
    
    
    
    //Constructor
    
    private RentalSystem() 
    {
        vehicles = new ArrayList<>();
        customers = new ArrayList<>();
        rentalHistory = new RentalHistory();
        loadData();// added method for loading the data
        
    }
    // method to load data from files
    
    private void loadData() {
        loadVehiclesFromFile();
        loadCustomersFromFile();
        loadRentalRecordsFromFile();
    }


    
    // method to load vehicles, checks to see if file exists, before loading
    private void loadVehiclesFromFile() {
        File file = new File("vehicles.txt");
        if (!file.exists()) return;

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 6) continue;

                String type  = parts[0];
                String plate = parts[1];
                String make  = parts[2];
                String model = parts[3];
                int year     = Integer.parseInt(parts[4]);
                String statusString = parts[5];

                Vehicle vehicle = null;
                
                

                
                
                if (type.equalsIgnoreCase("Car")) {
                    vehicle = new Car(make, model, year, 0); 
                } 
                else if (type.equalsIgnoreCase("Minibus")) {
                    vehicle = new Minibus(make, model, year, false);
                } 
                else if (type.equalsIgnoreCase("PickupTruck")) {
                    vehicle = new PickupTruck(make, model, year, 1.0, false); 
                }

                if (vehicle != null) {
                    vehicle.setLicensePlate(plate);
                    vehicle.setStatus(Vehicle.VehicleStatus.valueOf(statusString));
                    vehicles.add(vehicle);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading vehicles: " + e.getMessage());
        }
    }
    
    // same as above, but for customers
    private void loadCustomersFromFile() {
        File file = new File("customers.txt");
        if (!file.exists()) return;

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 2) continue;

                int id = Integer.parseInt(parts[0]);
                String name = parts[1];

                customers.add(new Customer(id, name));
            }
        } catch (Exception e) {
            System.out.println("Error loading customers: " + e.getMessage());
        }
    }
    
    
    
    // same as above but for records
    private void loadRentalRecordsFromFile() {
        File file = new File("rental_records.txt");
        if (!file.exists()) return;

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 5) continue;

                String type = parts[0];
                String plate = parts[1];
                int customerId = Integer.parseInt(parts[2]);
                LocalDate date = LocalDate.parse(parts[3]);
                double amount = Double.parseDouble(parts[4]);

                Vehicle vehicle = findVehicleByPlate(plate);
                Customer customer = findCustomerById(customerId);

                if (vehicle != null && customer != null) {
                    rentalHistory.addRecord(
                        new RentalRecord(vehicle, customer, date, amount, type)
                    );
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading rental records: " + e.getMessage());
        }
    }
    
    
    
    
    
 /*
  * 
  * 
  *
  *
  *
  *
  *
  *
  *
  */
    



	//  method to get instance
    public static RentalSystem getInstance() 
    {
        if (instance == null) {
            instance = new RentalSystem();
        }
        return instance;
    }
    
    
    
    
    // Refactored to make it possible to save instances 

    public boolean addVehicle(Vehicle vehicle) {
        // Check for duplicate license plate
        Vehicle existing = findVehicleByPlate(vehicle.getLicensePlate());
        if (existing != null) {
            System.out.println("A vehicle with plate " + vehicle.getLicensePlate() + " already exists. Vehicle not added.");
            return false; 
        }

        // If no duplicate then add to list
        vehicles.add(vehicle);
        saveVehicle(vehicle);

        return true; 
    }

    // Same thing but for customer ID
    public boolean addCustomer(Customer customer) {
        // Check for duplicate customer ID
        Customer existing = findCustomerById(customer.getCustomerId());
        if (existing != null) {
            System.out.println("A customer with ID " + customer.getCustomerId() + " already exists. Customer not added.");
            return false;  
        }

        customers.add(customer);
        saveCustomer(customer);

        return true; 
    }
    
    
    
    
    
    
    
    
    
    // Refactor and fix rent and return to save instances
    
    public boolean rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);

            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(record);

            saveRecord(record); 

            System.out.println("Vehicle rented to " + customer.getCustomerName());
            return true;
        } else {
            System.out.println("Vehicle is not available for renting.");
            return false;
        }
    }

    public boolean returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
            vehicle.setStatus(Vehicle.VehicleStatus.Available);

            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(record);

            saveRecord(record); 

            System.out.println("Vehicle returned by " + customer.getCustomerName());
            return true;
        } else {
            System.out.println("Vehicle is not rented.");
            return false;
        }
    }
    
    
    
    
    
    

    public void displayVehicles(Vehicle.VehicleStatus status) {
        // Display appropriate title based on status
        if (status == null) {
            System.out.println("\n=== All Vehicles ===");
        } else {
            System.out.println("\n=== " + status + " Vehicles ===");
        }
        
        // Header with proper column widths
        System.out.printf("|%-16s | %-12s | %-12s | %-12s | %-6s | %-18s |%n", 
            " Type", "Plate", "Make", "Model", "Year", "Status");
        System.out.println("|--------------------------------------------------------------------------------------------|");
    	  
        boolean found = false;
        for (Vehicle vehicle : vehicles) {
            if (status == null || vehicle.getStatus() == status) {
                found = true;
                String vehicleType;
                if (vehicle instanceof Car) {
                    vehicleType = "Car";
                } else if (vehicle instanceof Minibus) {
                    vehicleType = "Minibus";
                } else if (vehicle instanceof PickupTruck) {
                    vehicleType = "Pickup Truck";
                } else {
                    vehicleType = "Unknown";
                }
                System.out.printf("| %-15s | %-12s | %-12s | %-12s | %-6d | %-18s |%n", 
                    vehicleType, vehicle.getLicensePlate(), vehicle.getMake(), vehicle.getModel(), vehicle.getYear(), vehicle.getStatus().toString());
            }
        }
        if (!found) {
            if (status == null) {
                System.out.println("  No Vehicles found.");
            } else {
                System.out.println("  No vehicles with Status: " + status);
            }
        }
        System.out.println();
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }
    
    public void displayRentalHistory() {
        if (rentalHistory.getRentalHistory().isEmpty()) {
            System.out.println("  No rental history found.");
        } else {
            // Header with proper column widths
            System.out.printf("|%-10s | %-12s | %-20s | %-12s | %-12s |%n", 
                " Type", "Plate", "Customer", "Date", "Amount");
            System.out.println("|-------------------------------------------------------------------------------|");
            
            for (RentalRecord record : rentalHistory.getRentalHistory()) {                
                System.out.printf("| %-9s | %-12s | %-20s | %-12s | $%-11.2f |%n", 
                    record.getRecordType(), 
                    record.getVehicle().getLicensePlate(),
                    record.getCustomer().getCustomerName(),
                    record.getRecordDate().toString(),
                    record.getTotalAmount()
                );
            }
            System.out.println();
        }
    }
    
    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }
    
    public Customer findCustomerById(int id) {
        for (Customer c : customers)
            if (c.getCustomerId() == id)
                return c;
        return null;
    }
    
   
    
    
    
    
    
    
    
    // Add methods related to saving files
    private void saveVehicle(Vehicle vehicle) {
        String type;

        if (vehicle instanceof Car) {
            type = "Car";
        } else if (vehicle instanceof Minibus) {
            type = "Minibus";
        } else if (vehicle instanceof PickupTruck) {
            type = "PickupTruck";
        } else {
            type = "Vehicle";
        }

        
        // ensure vehicles were saved properly
        try (FileWriter fw = new FileWriter("vehicles.txt", true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println(
                type + "," +
                vehicle.getLicensePlate() + "," +
                vehicle.getMake() + "," +
                vehicle.getModel() + "," +
                vehicle.getYear() + "," +
                vehicle.getStatus()
            );

        } catch (IOException e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    }
    
    
    
    
    
    
    // Save File 
    
    private void saveCustomer(Customer customer) {
        try (FileWriter fw = new FileWriter("customers.txt", true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println(customer.getCustomerId() + "," + customer.getCustomerName());

        } catch (IOException e) {
            System.out.println("Error saving customer: " + e.getMessage());
        }
    }
    
    
    // Save the record 
    private void saveRecord(RentalRecord record) {
        try (FileWriter fw = new FileWriter("rental_records.txt", true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println(
                record.getRecordType() + "," +
                record.getVehicle().getLicensePlate() + "," +
                record.getCustomer().getCustomerId() + "," +
                record.getRecordDate().toString() + "," +
                record.getTotalAmount()
            );

        } catch (IOException e) {
            System.out.println("Error saving rental record: " + e.getMessage());
        }
        
        
    }
    
   
    
}