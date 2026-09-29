package za.co.swiftparcel;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * These tests seed a small database and run the queries in ParcelQueries.
 * They need a correct DatabaseSchema (Q7.2) to pass.
 *
 * Seed data:
 *   customers : 1 Thandi Mokoena, 2 Sipho Nel, 3 Aisha Patel
 *   parcels   : SP00000001, SP00000002, SP00000003 -> Thandi
 *               SP00000004                         -> Sipho
 *   events    : SP00000001 COLLECTED, IN_TRANSIT, DELIVERED
 *               SP00000002 COLLECTED, IN_TRANSIT
 *               SP00000003 (none)
 *               SP00000004 COLLECTED
 */
class ParcelQueriesTest {

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = Database.connect(":memory:");
        DatabaseSchema.createSchema(connection);
        seed();
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void parcelsForCustomer_returnsThatCustomersParcelsInTrackingOrder() throws SQLException {
        List<String> rows = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(ParcelQueries.PARCELS_FOR_CUSTOMER)) {
            ps.setInt(1, 1);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(rs.getString("tracking_no") + ":" + rs.getDouble("weight_kg"));
                }
            }
        }
        assertEquals(List.of("SP00000001:2.5", "SP00000002:12.0", "SP00000003:8.0"), rows);
    }

    @Test
    void parcelsForCustomer_isEmptyWhenCustomerHasNoParcels() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(ParcelQueries.PARCELS_FOR_CUSTOMER)) {
            ps.setInt(1, 3);
            try (ResultSet rs = ps.executeQuery()) {
                assertFalse(rs.next());
            }
        }
    }

    @Test
    void parcelCountPerCustomer_includesCustomersWithNoParcels() throws SQLException {
        List<String> rows = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(ParcelQueries.PARCEL_COUNT_PER_CUSTOMER)) {
            while (rs.next()) {
                rows.add(rs.getString("full_name") + ":" + rs.getInt("parcel_count"));
            }
        }
        assertEquals(List.of("Thandi Mokoena:3", "Sipho Nel:1", "Aisha Patel:0"), rows);
    }

    @Test
    void latestStatusPerParcel_usesTheMostRecentEventOnly() throws SQLException {
        List<String> rows = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(ParcelQueries.LATEST_STATUS_PER_PARCEL)) {
            while (rs.next()) {
                rows.add(rs.getString("tracking_no") + ":" + rs.getString("status"));
            }
        }
        assertEquals(List.of("SP00000001:DELIVERED", "SP00000002:IN_TRANSIT", "SP00000004:COLLECTED"), rows);
    }

    private void seed() throws SQLException {
        try (Statement s = connection.createStatement()) {
            s.execute("INSERT INTO customers (id, full_name, email) VALUES (1, 'Thandi Mokoena', 'thandi@example.com')");
            s.execute("INSERT INTO customers (id, full_name, email) VALUES (2, 'Sipho Nel', 'sipho@example.com')");
            s.execute("INSERT INTO customers (id, full_name, email) VALUES (3, 'Aisha Patel', 'aisha@example.com')");

            s.execute("INSERT INTO parcels (id, tracking_no, weight_kg, service_level, customer_id) VALUES (1, 'SP00000001', 2.5, 'STANDARD', 1)");
            s.execute("INSERT INTO parcels (id, tracking_no, weight_kg, service_level, customer_id) VALUES (2, 'SP00000002', 12.0, 'EXPRESS', 1)");
            s.execute("INSERT INTO parcels (id, tracking_no, weight_kg, service_level, customer_id) VALUES (3, 'SP00000003', 8.0, 'ECONOMY', 1)");
            s.execute("INSERT INTO parcels (id, tracking_no, weight_kg, service_level, customer_id) VALUES (4, 'SP00000004', 25.0, 'STANDARD', 2)");

            s.execute("INSERT INTO tracking_events (parcel_id, status, location, recorded_at) VALUES (1, 'COLLECTED', 'Johannesburg', '2026-09-01T08:00:00')");
            s.execute("INSERT INTO tracking_events (parcel_id, status, location, recorded_at) VALUES (1, 'IN_TRANSIT', 'Bloemfontein', '2026-09-02T10:00:00')");
            s.execute("INSERT INTO tracking_events (parcel_id, status, location, recorded_at) VALUES (1, 'DELIVERED', 'Cape Town', '2026-09-03T14:30:00')");
            s.execute("INSERT INTO tracking_events (parcel_id, status, location, recorded_at) VALUES (2, 'COLLECTED', 'Johannesburg', '2026-09-01T09:00:00')");
            s.execute("INSERT INTO tracking_events (parcel_id, status, location, recorded_at) VALUES (2, 'IN_TRANSIT', 'Durban', '2026-09-02T11:00:00')");
            s.execute("INSERT INTO tracking_events (parcel_id, status, location, recorded_at) VALUES (4, 'COLLECTED', 'Pretoria', '2026-09-05T07:45:00')");
        }
    }
}
