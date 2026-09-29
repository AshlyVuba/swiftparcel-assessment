package za.co.swiftparcel;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSchema {

    private DatabaseSchema() {
    }

    public static void createSchema(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // TODO: create the `customers`, `parcels` and `tracking_events`
            // tables described in resources/erd.png.
            //
            // Execute one CREATE TABLE statement per table, in an order that
            // lets each foreign key point at a table that already exists:
            //
            // statement.execute("""
            //         CREATE TABLE ... (
            //         )
            //         """);
        }
    }
}
