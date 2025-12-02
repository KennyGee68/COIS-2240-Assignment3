import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

public class VehicleRentalTest {

    @Test
    public void testLicensePlate() {
    	
    	// Valid Plates
        Vehicle v1 = new Car("Toyota", "Camry", 2012, 5);
        Vehicle v2 = new Car("Honda", "Civic", 2015, 5);
        Vehicle v3 = new Car("Ford", "Focus", 2021, 5);

        assertDoesNotThrow(() -> v1.setLicensePlate("AAA100"));
        assertEquals("AAA100", v1.getLicensePlate());

        assertDoesNotThrow(() -> v2.setLicensePlate("ABC567"));
        assertEquals("ABC567", v2.getLicensePlate());

        assertDoesNotThrow(() -> v3.setLicensePlate("ZZZ999"));
        assertEquals("ZZZ999", v3.getLicensePlate());

        // Invalid Plates
        Vehicle invalid1 = new Car("Toyota", "Corolla", 2018, 4);

        assertThrows(IllegalArgumentException.class,
            () -> invalid1.setLicensePlate(""));

        assertThrows(IllegalArgumentException.class,
            () -> invalid1.setLicensePlate(null));

        assertThrows(IllegalArgumentException.class,
            () -> invalid1.setLicensePlate("AAA1000"));

        assertThrows(IllegalArgumentException.class,
            () -> invalid1.setLicensePlate("ZZZ99"));
    }
    
    
    
     
    @Test
    public void testRentAndReturnVehicle() {

        RentalSystem system = RentalSystem.getInstance();

        Vehicle vehicle = new Car("Toyota", "Corolla", 2019, 5);
        vehicle.setLicensePlate("AAA100");
        vehicle.setStatus(Vehicle.VehicleStatus.Available);

        Customer customer = new Customer(1, "George");

        // starts available
        assertEquals(Vehicle.VehicleStatus.Available, vehicle.getStatus());

        // first rent should work
        boolean firstRent = system.rentVehicle(vehicle, customer, LocalDate.now(), 100.0);
        assertTrue(firstRent);

        // now rented
        assertEquals(Vehicle.VehicleStatus.Rented, vehicle.getStatus());

        // second rent should fail (already rented)
        boolean secondRent = system.rentVehicle(vehicle, customer, LocalDate.now(), 100.0);
        assertFalse(secondRent);

        // first return should work
        boolean firstReturn = system.returnVehicle(vehicle, customer, LocalDate.now(), 0.0);
        assertTrue(firstReturn);

        // now available again
        assertEquals(Vehicle.VehicleStatus.Available, vehicle.getStatus());

        // second return should fail (not rented)
        boolean secondReturn = system.returnVehicle(vehicle, customer, LocalDate.now(), 0.0);
        assertFalse(secondReturn);
        
    }

    

}