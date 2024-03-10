package edu.java.scrapper.domain;

import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
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
    @DisplayName("Проверка запуска миграций")
    void runMigrations_shouldCreateTables() {
        try (Connection connection = DriverManager.getConnection(
            POSTGRES.getJdbcUrl(),
            POSTGRES.getUsername(),
            POSTGRES.getPassword()
        )) {
            String insertQuery = "insert into chat values(1, '2007-12-03T10:15:30+01:00')";
            String selectQuery = "select * from chat";
            String deleteQuery = "delete from chat";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);

            assertDoesNotThrow(() -> insertStatement.execute());
            assertDoesNotThrow(() -> selectStatement.execute());
            assertDoesNotThrow(() -> deleteStatement.execute());
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
