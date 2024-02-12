package edu.java.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.BaseResponse;
import edu.java.bot.configuration.ApplicationConfig;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Bot implements IBot {

    private final TelegramBot bot;
    private final CommandPool commandPool;

    @Autowired
    public Bot(ApplicationConfig applicationConfig, LinkStorage linkStorage) {
        bot = new TelegramBot(applicationConfig.telegramToken());
        bot.setUpdatesListener(this);

        commandPool = CommandPool.standardPool(linkStorage);
        execute(new SetMyCommands(commandPool.getBotCommands().toArray(new BotCommand[0])));
    }

    @Override
    public <T extends BaseRequest<T, R>, R extends BaseResponse>
    void execute(BaseRequest<T, R> request) {
        if (request != null) {
            bot.execute(request);
        }
    }

    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            execute(commandPool.process(update));
        }

        return CONFIRMED_UPDATES_ALL;
    }

    @Override
    public void close() {
        bot.shutdown();
    }
}
