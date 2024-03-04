package edu.java.scrapper.repository;

import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SimpleMigrationTest extends IntegrationTest {

    @Test
    void contextLoads() {
        assertThat(POSTGRES.isRunning())
            .isTrue();
    }

    @Test
    void simpleTest() {
        try (Connection connection = DriverManager.getConnection(
            POSTGRES.getJdbcUrl(),
            POSTGRES.getUsername(),
            POSTGRES.getPassword()
        )) {
            String insertQuery = "insert into chat values(1, '2007-12-03T10:15:30+01:00')";
            String selectQuery = "select * from chat";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);

            assertDoesNotThrow(() -> insertStatement.execute());
            assertDoesNotThrow(() -> selectStatement.execute());
            ResultSet resultSet = selectStatement.getResultSet();
            resultSet.next();

            assertThat(resultSet.getInt("id"))
                .isEqualTo(1);
            assertThat(resultSet.getObject("created_at", OffsetDateTime.class))
                .isEqualTo(OffsetDateTime.parse("2007-12-03T10:15:30+01:00"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
