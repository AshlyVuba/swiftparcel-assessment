package za.co.swiftparcel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ParcelJsonTest {

    @Test
    void toJson_includesTheParcelFields() {
        Parcel parcel = new Parcel("SP00000001", 2.5, true, 1200.0, Zone.LOCAL);

        String json = ParcelJson.toJson(parcel);

        assertTrue(json.contains("\"trackingNo\":\"SP00000001\""));
        assertTrue(json.contains("\"weightKg\":2.5"));
        assertTrue(json.contains("\"fragile\":true"));
        assertTrue(json.contains("\"zone\":\"LOCAL\""));
    }
}
