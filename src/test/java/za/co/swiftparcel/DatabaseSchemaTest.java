package za.co.swiftparcel;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseSchemaTest {

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = Database.connect(":memory:");
        DatabaseSchema.createSchema(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    // ---- customers -----------------------------------------------------

    @Test
    void customers_columnTypes() throws SQLException {
        Map<String, String> types = columnTypes("customers");
        assertEquals("INTEGER", types.get("id"));
        assertEquals("TEXT", types.get("full_name"));
        assertEquals("TEXT", types.get("email"));
    }

    @Test
    void customers_hasIdAsPrimaryKey() throws SQLException {
        assertTrue(columnFlags("customers", "pk").get("id"));
    }

    @Test
    void customers_fullNameAndEmailAreNotNull() throws SQLException {
        Map<String, Boolean> notNull = columnFlags("customers", "notnull");
        assertTrue(notNull.get("full_name"));
        assertTrue(notNull.get("email"));
    }

    @Test
    void customers_emailMustBeUnique() throws SQLException {
        execute("INSERT INTO customers (full_name, email) VALUES ('Thandi Mokoena', 'thandi@example.com')");
        assertThrows(SQLException.class, () ->
                execute("INSERT INTO customers (full_name, email) VALUES ('Someone Else', 'thandi@example.com')"));
    }

    // ---- parcels -------------------------------------------------------

    @Test
    void parcels_columnTypes() throws SQLException {
        Map<String, String> types = columnTypes("parcels");
        assertEquals("INTEGER", types.get("id"));
        assertEquals("TEXT", types.get("tracking_no"));
        assertEquals("REAL", types.get("weight_kg"));
        assertEquals("TEXT", types.get("service_level"));
        assertEquals("INTEGER", types.get("customer_id"));
    }

    @Test
    void parcels_hasIdAsPrimaryKey() throws SQLException {
        assertTrue(columnFlags("parcels", "pk").get("id"));
    }

    @Test
    void parcels_requiredColumnsAreNotNull() throws SQLException {
        Map<String, Boolean> notNull = columnFlags("parcels", "notnull");
        assertTrue(notNull.get("tracking_no"));
        assertTrue(notNull.get("weight_kg"));
        assertTrue(notNull.get("service_level"));
        assertTrue(notNull.get("customer_id"));
    }

    @Test
    void parcels_trackingNumberMustBeUnique() throws SQLException {
        insertCustomer();
        execute("INSERT INTO parcels (tracking_no, weight_kg, customer_id) VALUES ('SP00000001', 2.0, 1)");
        assertThrows(SQLException.class, () ->
                execute("INSERT INTO parcels (tracking_no, weight_kg, customer_id) VALUES ('SP00000001', 3.0, 1)"));
    }

    @Test
    void parcels_serviceLevelDefaultsToStandard() throws SQLException {
        insertCustomer();
        execute("INSERT INTO parcels (tracking_no, weight_kg, customer_id) VALUES ('SP00000001', 2.0, 1)");
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT service_level FROM parcels")) {
            assertTrue(rs.next());
            assertEquals("STANDARD", rs.getString("service_level"));
        }
    }

    @Test
    void parcels_weightMustBePositive() throws SQLException {
        insertCustomer();
        assertThrows(SQLException.class, () ->
                execute("INSERT INTO parcels (tracking_no, weight_kg, customer_id) VALUES ('SP00000001', 0, 1)"));
    }

    @Test
    void parcels_customerIdReferencesCustomers() throws SQLException {
        assertForeignKey("parcels", "customer_id", "customers", "id");
    }

    @Test
    void parcels_cannotReferenceACustomerThatDoesNotExist() {
        assertThrows(SQLException.class, () ->
                execute("INSERT INTO parcels (tracking_no, weight_kg, customer_id) VALUES ('SP00000001', 2.0, 99)"));
    }

    // ---- tracking_events -----------------------------------------------

    @Test
    void trackingEvents_columnTypes() throws SQLException {
        Map<String, String> types = columnTypes("tracking_events");
        assertEquals("INTEGER", types.get("id"));
        assertEquals("INTEGER", types.get("parcel_id"));
        assertEquals("TEXT", types.get("status"));
        assertEquals("TEXT", types.get("location"));
        assertEquals("TEXT", types.get("recorded_at"));
    }

    @Test
    void trackingEvents_hasIdAsPrimaryKey() throws SQLException {
        assertTrue(columnFlags("tracking_events", "pk").get("id"));
    }

    @Test
    void trackingEvents_everyColumnExceptIdIsNotNull() throws SQLException {
        Map<String, Boolean> notNull = columnFlags("tracking_events", "notnull");
        assertTrue(notNull.get("parcel_id"));
        assertTrue(notNull.get("status"));
        assertTrue(notNull.get("location"));
        assertTrue(notNull.get("recorded_at"));
    }

    @Test
    void trackingEvents_parcelIdReferencesParcels() throws SQLException {
        assertForeignKey("tracking_events", "parcel_id", "parcels", "id");
    }

    // ---- helpers -------------------------------------------------------

    private void insertCustomer() throws SQLException {
        execute("INSERT INTO customers (full_name, email) VALUES ('Thandi Mokoena', 'thandi@example.com')");
    }

    private void execute(String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private void assertForeignKey(String table, String from, String refTable, String to) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("PRAGMA foreign_key_list(" + table + ")")) {
            assertTrue(rs.next(), "expected " + table + " to declare a foreign key");
            assertEquals(refTable, rs.getString("table"));
            assertEquals(from, rs.getString("from"));
            assertEquals(to, rs.getString("to"));
            assertFalse(rs.next(), "expected exactly one foreign key on " + table);
        }
    }

    private Map<String, String> columnTypes(String table) throws SQLException {
        Map<String, String> types = new HashMap<>();
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (rs.next()) {
                types.put(rs.getString("name"), rs.getString("type").toUpperCase());
            }
        }
        return types;
    }

    private Map<String, Boolean> columnFlags(String table, String flagColumn) throws SQLException {
        Map<String, Boolean> flags = new HashMap<>();
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (rs.next()) {
                flags.put(rs.getString("name"), rs.getInt(flagColumn) > 0);
            }
        }
        return flags;
    }
}
