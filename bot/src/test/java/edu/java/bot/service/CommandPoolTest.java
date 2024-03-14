package edu.java.bot.service;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.command.Command;
import edu.java.bot.service.command.EndCommand;
import edu.java.bot.service.command.HelpCommand;
import edu.java.bot.service.command.ListCommand;
import edu.java.bot.service.command.StartCommand;
import edu.java.bot.service.command.TrackCommand;
import edu.java.bot.service.command.UntrackCommand;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

class CommandPoolTest {

    CommandPool commandPool;
    LinkService linkService;
    ChatService chatService;

    @BeforeEach
    void init() {
        linkService = Mockito.mock(LinkService.class);
        chatService = Mockito.mock(ChatService.class);

        List<Command> tmpCommands = new ArrayList<>(List.of(
            new StartCommand(chatService),
            new EndCommand(chatService),
            new TrackCommand(linkService),
            new UntrackCommand(linkService),
            new ListCommand(linkService)
        ));
        tmpCommands.addLast(new HelpCommand(tmpCommands));

        commandPool = new CommandPool(tmpCommands);
    }

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

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "track", "help", "/", "/ "})
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
        Mockito.when(linkService.getLinks(0L)).thenReturn(List.of(URI.create("first"), URI.create("second")));
        Update update = generateUpdateWithIdAndContent("/list", 0L);

        SendMessage result = commandPool.process(update);

        SendMessage expected = new SendMessage(0L, "*Список ссылок*\n\n- first\n- second")
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
            .map(botCommand -> String.format("%s - %s", botCommand.command(), botCommand.description()))
            .collect(Collectors.joining("\n"));
        SendMessage expected = new SendMessage(0L, "*Список команд*\n\n" + expectedContent)
            .parseMode(ParseMode.Markdown);

        assertThat(result.toWebhookResponse())
            .isEqualTo(expected.toWebhookResponse());
    }

    @Test
    @DisplayName("/start для незарегистрированного пользователя")
    void process_shouldReturnDefaultStartMessage() {
        Mockito.when(chatService.register(any())).thenReturn(true);
        Update update = generateUpdateWithIdAndContent("/start", 0L);
        Mockito.when(update.message().chat().firstName()).thenReturn("name");

        SendMessage result = commandPool.process(update);

        SendMessage expected = new SendMessage(0L, "Поздравляю, name, Вы можете начинать отслеживание ссылок!");

        assertThat(result.toWebhookResponse())
            .isEqualTo(expected.toWebhookResponse());
    }

    @Test
    @DisplayName("/end для незарегистрированного пользователя")
    void process_shouldReturnErrorMessageForEndUnregisteredUser() {
        Update update = generateUpdateWithIdAndContent("/end", 0L);
        Mockito.when(update.message().chat().firstName()).thenReturn("name");

        SendMessage result = commandPool.process(update);

        SendMessage expected = new SendMessage(0L, "Вы не зарегистрированы в системе!");

        assertThat(result.toWebhookResponse())
            .isEqualTo(expected.toWebhookResponse());
    }

    @Test
    @DisplayName("/end для зарегистрированного пользователя")
    void process_shouldReturnDefaultMessageForEndRegisteredUser() {
        Mockito.when(chatService.unregister(any())).thenReturn(true);
        Update update = generateUpdateWithIdAndContent("/end", 0L);
        Mockito.when(update.message().chat().firstName()).thenReturn("name");

        SendMessage result = commandPool.process(update);

        SendMessage expected = new SendMessage(0L, "Работа с ботом завершена. Ждём вас снова!");

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
        Mockito.when(linkService.addLink(any(), any())).thenReturn(LinkService.Response.OK);
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
        Mockito.when(linkService.removeLink(0L, URI.create("https://www.youtube.com/watch?v=4i2ifGa5n0o"))).thenReturn(
            LinkService.Response.OK);
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
        Mockito.when(linkService.removeLink(any(), any())).thenReturn(LinkService.Response.LINK_NOT_TRACKING);
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
