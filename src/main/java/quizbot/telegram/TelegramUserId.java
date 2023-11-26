package quizbot.telegram;

import quizbot.users.UserId;

import java.util.Objects;

public record TelegramUserId(Long id) implements UserId {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TelegramUserId that = (TelegramUserId) o;
        return Objects.equals(id, that.id);
    }
}