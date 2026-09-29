package za.co.swiftparcel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EconomyShipmentTest {

    @Test
    void deliveryFee_isRatePerKgTimesWeight() {
        EconomyShipment shipment = new EconomyShipment("ECO-001", 10.0);
        assertEquals(10.0 * EconomyShipment.RATE_PER_KG, shipment.deliveryFee(), 0.001);
    }

    @Test
    void estimatedDays_isFive() {
        assertEquals(5, new EconomyShipment("ECO-002", 3.0).estimatedDays());
    }

    @Test
    void getReference_returnsReferenceProvided() {
        assertEquals("ECO-003", new EconomyShipment("ECO-003", 1.0).getReference());
    }

    @Test
    void canBeUsedAsAShipment() {
        Shipment shipment = new EconomyShipment("ECO-004", 2.0);
        assertEquals(2.0, shipment.getWeightKg(), 0.001);
    }
}
