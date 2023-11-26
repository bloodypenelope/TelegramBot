package quizbot.commands;

import quizbot.users.UserSession;
import quizbot.users.UserState;

import java.util.List;
import java.util.function.BiConsumer;

public class SimpleButtonReadyStateCommand extends SimpleTextCommand{
    private final String data;

    @SuppressWarnings("unused")
    public SimpleButtonReadyStateCommand(String data, String resultText, BiConsumer<UserSession, String> action) {
        super(UserState.READY, resultText, action);
        this.data = data;
    }

    public SimpleButtonReadyStateCommand(String data, String resultText, List<String> buttons, BiConsumer<UserSession, String> action) {
        super(UserState.READY, resultText, buttons, action);
        this.data = data;
    }

    @SuppressWarnings("unused")
    public SimpleButtonReadyStateCommand(String data, String resultText, UserState newState) {
        super(UserState.READY, resultText, newState);
        this.data = data;
    }

    @SuppressWarnings("unused")
    public SimpleButtonReadyStateCommand(String data, String resultText, List<String> buttons) {
        super(UserState.READY, resultText, buttons);
        this.data = data;
    }

    @Override
    public boolean canBeApply(UserSession session, String text) {
        return super.canBeApply(session, text) && text.equals(data);
    }
}