package za.co.swiftparcel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExpressShipmentTest {

    @Test
    void deliveryFee_isBaseFeePlusRatePerKg() {
        ExpressShipment shipment = new ExpressShipment("EXP-001", 5.0);
        assertEquals(ExpressShipment.BASE_FEE + 5.0 * ExpressShipment.RATE_PER_KG, shipment.deliveryFee(), 0.001);
    }

    @Test
    void estimatedDays_isOne() {
        assertEquals(1, new ExpressShipment("EXP-002", 3.0).estimatedDays());
    }

    @Test
    void getWeightKg_returnsWeightProvided() {
        assertEquals(7.5, new ExpressShipment("EXP-003", 7.5).getWeightKg(), 0.001);
    }

    @Test
    void feeIsAvailableThroughTheShipmentType() {
        Shipment shipment = new ExpressShipment("EXP-004", 1.0);
        assertTrue(shipment.deliveryFee() > 0);
    }
}
