package edu.java.bot.service;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.repository.LinkRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

class CommandPoolTest {

    private final static CommandPool commandPool =
        CommandPool.standardPool(generateStorageWithLinks(Collections.emptyList()));

    private static Update generateUpdateWithIdAndContent(String content, Long id) {
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        Chat chat = Mockito.mock(Chat.class);

        Mockito.when(chat.id()).thenReturn(id);
        Mockito.when(message.text()).thenReturn(content);
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(update.message()).thenReturn(message);

        return update;
    }

    private static LinkRepository generateStorageWithLinks(List<String> links) {
        LinkRepository linkRepository = Mockito.mock(LinkRepository.class);

        Mockito.when(linkRepository.get(any())).thenReturn(links);
        Mockito.when(linkRepository.contains(any())).thenReturn(links != null);

        return linkRepository;
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "track", "help", "/end", "/", "/ "})
    @DisplayName("Сообщения не содержат поддерживаемых команд")
    void process_shouldReturnSpecialMessageIfMessageHasNoCommand(String content) {
        Update update = generateUpdateWithIdAndContent(content, 0L);

        SendMessage result = commandPool.process(update);

        SendMessage expected = new SendMessage(0L, "Команда не поддерживается или написана неправильно!");

        assertThat(result.toWebhookResponse())
            .isEqualTo(expected.toWebhookResponse());
    }

    @Test
    @DisplayName("/list с пустым списком")
    void process_shouldReturnSpecialMessageForListIfLinkListEmpty() {
        Update update = generateUpdateWithIdAndContent("/list", 0L);

        SendMessage result = commandPool.process(update);

        SendMessage expected = new SendMessage(0L, "У Вас нет отслеживаемых ссылок!")
            .disableWebPagePreview(true)
            .parseMode(ParseMode.Markdown);

        assertThat(result.toWebhookResponse())
            .isEqualTo(expected.toWebhookResponse());
    }

    @Test
    @DisplayName("/list с непустым списком")
    void process_shouldReturnMessageWithLinkListForNotEmptyList() {
        CommandPool commandPool = CommandPool.standardPool(generateStorageWithLinks(List.of(
            "first",
            "second"
        )));
        Update update = generateUpdateWithIdAndContent("/list", 0L);

        SendMessage result = commandPool.process(update);

        SendMessage expected = new SendMessage(0L, "*Список ссылок*\n\n- first\n- second\n")
            .disableWebPagePreview(true)
            .parseMode(ParseMode.Markdown);

        assertThat(result.toWebhookResponse())
            .isEqualTo(expected.toWebhookResponse());
    }

    @Test
    @DisplayName("/help")
    void process_shouldReturnCommandListForHelp() {
        Update update = generateUpdateWithIdAndContent("/help", 0L);

        SendMessage result = commandPool.process(update);

        String expectedContent = commandPool.getBotCommands().stream()
            .map(botCommand -> String.format("%s - %s\n", botCommand.command(), botCommand.description()))
            .collect(Collectors.joining());
        SendMessage expected = new SendMessage(0L, "*Список команд*\n\n" + expectedContent)
            .parseMode(ParseMode.Markdown);

        assertThat(result.toWebhookResponse())
            .isEqualTo(expected.toWebhookResponse());
    }

    @Test
    @DisplayName("/start для незарегистрированного пользователя")
    void process_shouldReturnDefaultStartMessage() {
        CommandPool commandPool = CommandPool.standardPool(generateStorageWithLinks(null));
        Update update = generateUpdateWithIdAndContent("/start", 0L);
        Mockito.when(update.message().chat().firstName()).thenReturn("name");

        SendMessage result = commandPool.process(update);

        SendMessage expected = new SendMessage(0L, "Поздравляю, name, Вы можете начинать отслеживание ссылок!");

        assertThat(result.toWebhookResponse())
            .isEqualTo(expected.toWebhookResponse());
    }

    @Test
    @DisplayName("/start для зарегистрированного пользователя")
    void process_shouldReturnSpecialStartMessageForRegisteredUser() {
        Update update = generateUpdateWithIdAndContent("/start", 0L);

        SendMessage result = commandPool.process(update);

        SendMessage expected = new SendMessage(0L, "Вы уже зарегистрированы в системе!");

        assertThat(result.toWebhookResponse())
            .isEqualTo(expected.toWebhookResponse());
    }

    @Test
    @DisplayName("/track для корректной ссылки")
    void process_shouldReturnDefaultMessageForTrackingValidLink() {
        Update update = generateUpdateWithIdAndContent("/track https://www.youtube.com/watch?v=4i2ifGa5n0o", 0L);

        SendMessage result = commandPool.process(update);

        SendMessage expected =
            new SendMessage(0L, "Начато отслеживание ссылки https://www.youtube.com/watch?v=4i2ifGa5n0o")
                .disableWebPagePreview(true);

        assertThat(result.toWebhookResponse())
            .isEqualTo(expected.toWebhookResponse());
    }

    @Test
    @DisplayName("/track для некорректной ссылки")
    void process_shouldReturnSpecialMessageForTrackingNotValidLink() {
        Update update = generateUpdateWithIdAndContent("/track https://www.youtube.o/watch?v=4i2ifGa5n0o", 0L);

        SendMessage result = commandPool.process(update);

        SendMessage expected = new SendMessage(0L, "Ссылка введена в неправильном формате!")
            .disableWebPagePreview(true);

        assertThat(result.toWebhookResponse())
            .isEqualTo(expected.toWebhookResponse());
    }

    @Test
    @DisplayName("/untrack для ссылки в списке")
    void process_shouldReturnDefaultMessageForUntrackingTrackingLink() {
        CommandPool commandPool =
            CommandPool.standardPool(generateStorageWithLinks(List.of("https://www.youtube.com/watch?v=4i2ifGa5n0o")));
        Update update = generateUpdateWithIdAndContent("/untrack https://www.youtube.com/watch?v=4i2ifGa5n0o", 0L);

        SendMessage result = commandPool.process(update);

        SendMessage expected =
            new SendMessage(0L, "Прекращено отслеживание ссылки https://www.youtube.com/watch?v=4i2ifGa5n0o")
                .disableWebPagePreview(true);

        assertThat(result.toWebhookResponse())
            .isEqualTo(expected.toWebhookResponse());
    }

    @Test
    @DisplayName("/untrack для ссылки не списке")
    void process_shouldReturnSpecialMessageForUntrackingNotTrackingLink() {
        Update update = generateUpdateWithIdAndContent("/untrack https://www.youtube.com/watch?v=4i2ifGa5n0o", 0L);

        SendMessage result = commandPool.process(update);

        SendMessage expected =
            new SendMessage(0L, "Данная ссылка не отслеживается!")
                .disableWebPagePreview(true);

        assertThat(result.toWebhookResponse())
            .isEqualTo(expected.toWebhookResponse());
    }

    @Test
    @DisplayName("/untrack для некорректной ссылки")
    void process_shouldReturnSpecialMessageForUntrackingNotValidLink() {
        Update update = generateUpdateWithIdAndContent("/untrack https://www.youtube.om/watch?v=4i2ifGa5n0o", 0L);

        SendMessage result = commandPool.process(update);

        SendMessage expected =
            new SendMessage(0L, "Ссылка введена в неправильном формате!")
                .disableWebPagePreview(true);

        assertThat(result.toWebhookResponse())
            .isEqualTo(expected.toWebhookResponse());
    }
}
