package quizbot;

import quizbot.commands.*;
import quizbot.telegram.TelegramBot;
import quizbot.users.UserState;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;

public class BotInitializer {
    public static void main(String[] args) throws IOException, TelegramApiException, SQLException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        var data = Files.readAllLines(new File("src\\main\\resources\\telegramKey").toPath());
        String token = data.get(0);
        var name = data.get(1);
        var bot = new TelegramBot(token, name);

        var textHandler = new TextCommandHandler();
        textHandler.addCommand(new SimpleTextCommand(UserState.NEW_BEE,
                "Hi, it's a Java Quiz Bot! ",
                ButtonHelper.readyStateButtons,
                (s, t) -> s.setState(UserState.READY)));
        bot.addTextHandler(textHandler);

        var buttonHandler = new TextCommandHandler();
        buttonHandler.addCommand(new SimpleButtonReadyStateCommand("Start Game",
                "Select difficulty",
                ButtonHelper.difficultyButtons,
                (s, t) -> s.setState(UserState.SELECTING)));
        buttonHandler.addCommand(new ShowStatsCommand(UserState.READY));
        buttonHandler.addCommand(new SelectDifficultyCommand(UserState.SELECTING));
        buttonHandler.addCommand(new AnswerCommand(UserState.INGAME));
        bot.addButtonHandler(buttonHandler);

        telegramBotsApi.registerBot(bot);
    }
}
