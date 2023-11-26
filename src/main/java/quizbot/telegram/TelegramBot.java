package quizbot.telegram;

import quizbot.commands.CommandResult;
import quizbot.commands.TextCommandHandler;
import quizbot.users.UserProvider;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class TelegramBot extends TelegramLongPollingBot {
    private final String name;
    private TextCommandHandler textHandler = null;
    private TextCommandHandler buttonHandler = null;

    public TelegramBot(String token, String name) {
        super(token);
        this.name = name;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var message = update.getMessage();
            var chatId = update.getMessage().getChatId();
            var session = UserProvider.getInstance().findUserById(new TelegramUserId(chatId));
            if (textHandler!=null) {
                var result = textHandler.processCommand(session, message.getText());
                response(result, chatId);
            }
        }
        if (update.hasCallbackQuery()) {
            var callbackQuery = update.getCallbackQuery();
            var chatId = callbackQuery.getMessage().getChatId();
            var session = UserProvider.getInstance().findUserById(new TelegramUserId(chatId));
            var command = callbackQuery.getData();
            if (buttonHandler!=null) {
                var result = buttonHandler.processCommand(session, command);
                response(result, chatId);
            }
        }
    }

    private void response(CommandResult result, Long chatId) {
        SendMessage response = new SendMessage();
        response.setChatId(chatId);
        response.setText(result.getResult());
        if(result.hasButtons()) {
            InlineKeyboardMarkup markupInline = getInlineKeyboardMarkup(result);
            response.setReplyMarkup(markupInline);
        }
        try {
            execute(response);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup(CommandResult result) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        for (String text : result.getButtons()) {
            var button = new InlineKeyboardButton();
            button.setText(text);
            button.setCallbackData(text);
            rowInline.add(button);
        }
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    public void addTextHandler(TextCommandHandler textHandler) {
        this.textHandler = textHandler;
    }

    public void addButtonHandler(TextCommandHandler buttonHandler) {
        this.buttonHandler = buttonHandler;
    }
}