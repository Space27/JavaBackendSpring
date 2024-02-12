package edu.java.bot;

import com.pengrad.telegrambot.model.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import java.net.URI;
import static org.assertj.core.api.Assertions.assertThat;

class MessageParserTest {

    @ParameterizedTest
    @NullSource
    @DisplayName("Команда от Null")
    void getCommand_shouldReturnNullForNullMessage(Message message) {
        String command = MessageParser.getCommand(message);

        assertThat(command)
            .isNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Команда от пустого или null сообщения")
    void getCommand_shouldReturnNullForEmptyMessage(String content) {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.text()).thenReturn(content);

        String command = MessageParser.getCommand(message);

        assertThat(command)
            .isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"track https:", "/ frfrrf", "  ", "list", "/"})
    @DisplayName("В сообщении нет команды")
    void getCommand_shouldReturnNullIfMessageHasNoCommand(String content) {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.text()).thenReturn(content);

        String command = MessageParser.getCommand(message);

        assertThat(command)
            .isNull();
    }

    @ParameterizedTest
    @CsvSource({"/track https:/  frfr,/track", "/list,/list", "/h,/h", "/help smth,/help"})
    @DisplayName("В сообщении есть команда")
    void getCommand_shouldReturnCommandIfMessageHasCommand(String content, String answer) {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.text()).thenReturn(content);

        String command = MessageParser.getCommand(message);

        assertThat(command)
            .isEqualTo(answer);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("Адрес от Null")
    void getURI_shouldReturnNullForNullMessage(Message message) {
        URI uri = MessageParser.getURI(message);

        assertThat(uri)
            .isNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Адрес от пустого или null сообщения")
    void getURI_shouldReturnNullForEmptyMessage(String content) {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.text()).thenReturn(content);

        URI uri = MessageParser.getURI(message);

        assertThat(uri)
            .isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"https:/", "https://githu", "https://github.c", "https:/github.com", "htps://github.com", " "})
    @DisplayName("В сообщении нет адреса")
    void getURI_shouldReturnNullIfMessageHasNoURI(String content) {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.text()).thenReturn(content);

        URI uri = MessageParser.getURI(message);

        assertThat(uri)
            .isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://github.com", "/track https://github.com", "https://github.com /track"})
    @DisplayName("В сообщении есть адрес")
    void getURI_shouldReturnNotNullIfMessageHasURI(String content) {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.text()).thenReturn(content);

        URI uri = MessageParser.getURI(message);

        assertThat(uri)
            .isNotNull();
    }
}
