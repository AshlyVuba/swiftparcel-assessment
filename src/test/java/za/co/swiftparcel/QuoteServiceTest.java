package za.co.swiftparcel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuoteServiceTest {

    private QuoteService service;

    @BeforeEach
    void setUp() {
        service = new QuoteService();
    }

    private Parcel parcel(double weightKg, boolean fragile, double declaredValue, Zone zone) {
        return new Parcel("SP00000001", weightKg, fragile, declaredValue, zone);
    }

    private Depot openDepot() {
        return new Depot("Johannesburg Hub", null, 30.0, true, 10);
    }

    // ---- baseRate / calculateQuote -------------------------------------

    @Test
    void baseRate_differsByZone() {
        assertEquals(8.0, service.baseRate(Zone.LOCAL), 0.001);
        assertEquals(14.0, service.baseRate(Zone.REGIONAL), 0.001);
        assertEquals(22.0, service.baseRate(Zone.NATIONAL), 0.001);
    }

    @Test
    void calculateQuote_plainLocalParcelHasNoSurcharge() {
        assertEquals(20.0, service.calculateQuote(parcel(2.5, false, 1000, Zone.LOCAL)), 0.001);
    }

    @Test
    void calculateQuote_addsSurchargeToBaseCharge() {
        // 22 * 25 = 550, plus 150 (fragile, high value) plus 40 (over 20 kg)
        assertEquals(740.0, service.calculateQuote(parcel(25, true, 6000, Zone.NATIONAL)), 0.001);
    }

    // ---- calculateSurcharge --------------------------------------------

    @Test
    void surcharge_fragileHighValueIs150() {
        assertEquals(150.0, service.calculateSurcharge(parcel(5, true, 6000, Zone.LOCAL)), 0.001);
    }

    @Test
    void surcharge_fragileLowValueIs60() {
        assertEquals(60.0, service.calculateSurcharge(parcel(5, true, 1000, Zone.LOCAL)), 0.001);
    }

    @Test
    void surcharge_nonFragileHighValueIs90() {
        assertEquals(90.0, service.calculateSurcharge(parcel(5, false, 6000, Zone.LOCAL)), 0.001);
    }

    @Test
    void surcharge_nonFragileLowValueIsZero() {
        assertEquals(0.0, service.calculateSurcharge(parcel(5, false, 1000, Zone.LOCAL)), 0.001);
    }

    @Test
    void surcharge_valueOfExactly5000IsNotHighValue() {
        assertEquals(60.0, service.calculateSurcharge(parcel(5, true, 5000, Zone.LOCAL)), 0.001);
        assertEquals(0.0, service.calculateSurcharge(parcel(5, false, 5000, Zone.LOCAL)), 0.001);
    }

    @Test
    void surcharge_heavyParcelAddsFlat40() {
        assertEquals(40.0, service.calculateSurcharge(parcel(20.5, false, 100, Zone.LOCAL)), 0.001);
        assertEquals(100.0, service.calculateSurcharge(parcel(20.5, true, 100, Zone.LOCAL)), 0.001);
        assertEquals(130.0, service.calculateSurcharge(parcel(20.5, false, 9000, Zone.LOCAL)), 0.001);
        assertEquals(190.0, service.calculateSurcharge(parcel(20.5, true, 9000, Zone.LOCAL)), 0.001);
    }

    @Test
    void surcharge_exactly20KgIsNotHeavy() {
        assertEquals(0.0, service.calculateSurcharge(parcel(20, false, 100, Zone.LOCAL)), 0.001);
    }

    @Test
    void surcharge_rejectsMissingParcel() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> service.calculateSurcharge(null));
        assertEquals("Parcel is required", ex.getMessage());
    }

    @Test
    void surcharge_rejectsZeroOrNegativeWeight() {
        IllegalArgumentException zero = assertThrows(IllegalArgumentException.class,
                () -> service.calculateSurcharge(parcel(0, false, 100, Zone.LOCAL)));
        assertEquals("Weight must be positive", zero.getMessage());
        assertThrows(IllegalArgumentException.class,
                () -> service.calculateSurcharge(parcel(-1, true, 9000, Zone.LOCAL)));
    }

    // ---- canAccept -----------------------------------------------------

    @Test
    void canAccept_whenEveryConditionIsMet() {
        assertTrue(service.canAccept(parcel(10, true, 100, Zone.LOCAL), openDepot()));
    }

    @Test
    void canAccept_deniedWhenDepotIsClosed() {
        Depot depot = openDepot();
        depot.close();
        assertFalse(service.canAccept(parcel(10, false, 100, Zone.LOCAL), depot));
    }

    @Test
    void canAccept_deniedWhenDepotHasNoSpaces() {
        Depot full = new Depot("Full Hub", null, 30.0, true, 0);
        assertFalse(service.canAccept(parcel(10, false, 100, Zone.LOCAL), full));
    }

    @Test
    void canAccept_deniedWhenParcelIsTooHeavy() {
        Depot small = new Depot("Small Hub", null, 15.0, true, 5);
        assertFalse(service.canAccept(parcel(15.1, false, 100, Zone.LOCAL), small));
    }

    @Test
    void canAccept_allowedWhenWeightEqualsDepotLimit() {
        Depot small = new Depot("Small Hub", null, 15.0, true, 5);
        assertTrue(service.canAccept(parcel(15.0, false, 100, Zone.LOCAL), small));
    }

    @Test
    void canAccept_deniedForFragileParcelAtDepotThatRefusesFragile() {
        Depot noFragile = new Depot("Rough Hub", null, 30.0, false, 5);
        assertFalse(service.canAccept(parcel(5, true, 100, Zone.LOCAL), noFragile));
    }

    @Test
    void canAccept_allowedForNonFragileParcelAtDepotThatRefusesFragile() {
        Depot noFragile = new Depot("Rough Hub", null, 30.0, false, 5);
        assertTrue(service.canAccept(parcel(5, false, 100, Zone.LOCAL), noFragile));
    }

    @Test
    void canAccept_deniedWhenZoneDoesNotMatch() {
        Depot capeTown = new Depot("Cape Town Hub", Zone.REGIONAL, 30.0, true, 5);
        assertFalse(service.canAccept(parcel(5, false, 100, Zone.NATIONAL), capeTown));
    }

    @Test
    void canAccept_allowedWhenZoneMatches() {
        Depot capeTown = new Depot("Cape Town Hub", Zone.REGIONAL, 30.0, true, 5);
        assertTrue(service.canAccept(parcel(5, false, 100, Zone.REGIONAL), capeTown));
    }
}
