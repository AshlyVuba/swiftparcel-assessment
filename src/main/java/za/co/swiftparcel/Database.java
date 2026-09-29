package za.co.swiftparcel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private Database() {
    }

    /**
     * Opens a SQLite connection. SQLite does not enforce foreign keys unless
     * asked to, so enforcement is switched on for every connection.
     */
    public static Connection connect(String path) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }
}
