public abstract class Vehicle {
    private String licensePlate;
    private String make;
    private String model;
    private int year;
    private VehicleStatus status;

    public enum VehicleStatus { Available, Held, Rented, UnderMaintenance, OutOfService }
    
    // Fixes String Behaviour as per Task 1.5
    private String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
    
 // Validates license plate: 3 letters followed by 3 digits
    private boolean isValidPlate(String plate) {

        if (plate == null) {
            return false;
        }

        plate = plate.trim();

        if (plate.length() != 6) {
            return false;
        }

        // First 3 must be letters
        if (!Character.isLetter(plate.charAt(0))) return false;
        if (!Character.isLetter(plate.charAt(1))) return false;
        if (!Character.isLetter(plate.charAt(2))) return false;

        // Last 3 must be numbers
        if (!Character.isDigit(plate.charAt(3))) return false;
        if (!Character.isDigit(plate.charAt(4))) return false;
        if (!Character.isDigit(plate.charAt(5))) return false;

        return true;
    }
    
    
    
    // Refactored Constructor and added capitalize method

    public Vehicle(String make, String model, int year) {
        this.make = capitalize(make);
        this.model = capitalize(model);
        this.year = year;
        this.status = VehicleStatus.Available;
        this.licensePlate = null;
    }

    public Vehicle() {
        this(null, null, 0);
    }

    public void setLicensePlate(String plate) {

        if (!isValidPlate(plate)) {
            throw new IllegalArgumentException("Plate must be 3 letters then 3 digits.");
        }
        // Make everything uppercase
        this.licensePlate = plate.toUpperCase();
    }

    public void setStatus(VehicleStatus status) {
    	this.status = status;
    }

    public String getLicensePlate() { return licensePlate; }

    public String getMake() { return make; }

    public String getModel() { return model;}

    public int getYear() { return year; }

    public VehicleStatus getStatus() { return status; }

    public String getInfo() {
        return "| " + licensePlate + " | " + make + " | " + model + " | " + year + " | " + status + " |";
    }

}
