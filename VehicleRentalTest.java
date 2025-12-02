import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
}