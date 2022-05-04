package dev.simmons.utilities.connection;

import dev.simmons.utilities.connection.PostgresConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionTests {
    @Test
    public void canConnect() {
        // Made a mistake but might be able to turn this into
        //  some kind of singleton?
        try (Connection connection = PostgresConnection.getConnection()) {
            Assertions.assertEquals("bank", connection.getCatalog());
            Assertions.assertNotNull(connection);
        } catch (SQLException se) {
            Assertions.fail(se.getMessage());
        }


    }
}
