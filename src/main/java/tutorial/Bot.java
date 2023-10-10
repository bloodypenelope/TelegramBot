package tutorial;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Key;
import java.sql.*;
import java.util.*;

import static java.sql.DriverManager.getConnection;

public class Bot extends TelegramLongPollingBot {
    private ReplyKeyboardMarkup replyKeyboardMarkup;
    private List<KeyboardRow> keyboardRows;
    private KeyboardRow keyboardRow;
    private boolean running = false;

    public Bot() {
        initKeyboard();
    }

    @Override
    public String getBotUsername() {
        return "dadootest_bot";
    }

    @Override
    public String getBotToken() {
        return "6632092453:AAEddNkIrrhoEaDPMXpKD7OWQPalkEAOBwI";
    }

    @Override
    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
        var user = msg.getFrom();
        var id = user.getId();

        String url = "jdbc:mysql://localhost:3306/test";
        String user1 = "root";
        String password = "Vhfghbdtn1~";

        var response = "";

        try (Connection conn = DriverManager.getConnection(url, user1, password)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM staff");

            while (rs.next()) {
                response = rs.getInt("id") + " " + rs.getString("name");
                sendText(id, response);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        if (!running) {
            if (msg.getText().equals("/start")) {


                SendMessage test = new SendMessage();
                test.setChatId(msg.getChatId());
                test.setText("Выберите опцию");
                test.setReplyMarkup(replyKeyboardMarkup);

                try {
                    execute(test);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }

            if (msg.getText().equals("/game") || msg.getText().equals("Начать игру")) {
                if (running) return;
                running = true;
            }
        }
    }

    public void sendText(Long who, String what){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void initKeyboard() {
        replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        keyboardRows = new ArrayList<KeyboardRow>();
        keyboardRow = new KeyboardRow();
        keyboardRows.add(keyboardRow);
        keyboardRow.add(new KeyboardButton("Начать игру"));
        keyboardRow.add(new KeyboardButton("Посмотреть счёт"));
        replyKeyboardMarkup.setKeyboard(keyboardRows);
    }
}