package edu.java.scrapper.service.linkUpdateService.clientUpdate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

public class ClientUpdaterTest {

    ClientUpdater clientUpdater;
    ClientUpdateService updateService;

    @BeforeEach
    void init() {
        updateService = Mockito.mock(ClientUpdateService.class);
        clientUpdater = new ClientUpdater(List.of(updateService));
    }

    @Test
    @DisplayName("Возврат нескольких сообщений")
    void handle_shouldReturnSortedByTimeMap() {
        Map<String, OffsetDateTime> messages = Map.of(
            "a", OffsetDateTime.now(),
            "b", OffsetDateTime.now().minusDays(1),
            "c", OffsetDateTime.now().minusDays(2),
            "d", OffsetDateTime.now()
        );
        Mockito.when(updateService.handle(any(), any())).thenReturn(messages);

        LinkedHashMap<String, OffsetDateTime> result =
            clientUpdater.handle(URI.create("https://stackoverflow.com/"), OffsetDateTime.now());

        assertThat(result)
            .hasSize(messages.size())
            .containsAllEntriesOf(messages);
        assertThat(result.values().stream().toList())
            .isEqualTo(result.values().stream().sorted().toList());
    }

    @Test
    @DisplayName("Ссылка поддерживается")
    void supports_shouldReturnTrueForSupportedLink(){
        Mockito.when(updateService.supports(any())).thenReturn(true);

        boolean result = clientUpdater.supports(URI.create("https://stackoverflow.com/"));

        assertThat(result)
            .isTrue();
    }

    @Test
    @DisplayName("Ссылка не поддерживается")
    void supports_shouldReturnFalseForUnsupportedLink(){
        Mockito.when(updateService.supports(any())).thenReturn(false);

        boolean result = clientUpdater.supports(URI.create("https://stackoverflow.com/"));

        assertThat(result)
            .isFalse();
    }
}
