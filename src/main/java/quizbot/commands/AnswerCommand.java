package quizbot.commands;

import quizbot.sql.SQLConnector;
import quizbot.users.UserSession;
import quizbot.users.UserState;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.function.BiConsumer;

public class AnswerCommand implements TextCommand {
    private final SQLConnector connector;
    private final UserState acceptableState;
    private final BiConsumer<UserSession, String> action;

    public AnswerCommand(UserState acceptableState) throws IOException, SQLException {
        this.acceptableState = acceptableState;
        this.action = (s, t) -> s.setState(UserState.READY);
        this.connector = SQLConnector.getInstance();
    }

    @Override
    public boolean canBeApply(UserSession session, String text) {
        return session.getState() == this.acceptableState && text.matches("^A|B|C|D$");
    }

    @Override
    public CommandResult execute(UserSession session, String text) throws SQLException {
        if (session.validateAnswer(text))
            session.incrementScore();
        String result;
        List<String> buttons;
        if (session.nextQuestion()) {
            result = session.getQuestion();
            buttons = ButtonHelper.optionButtons;
        } else {
            connector.updateStats(session);
            result = String.format("Your score is: %d", session.getScore());
            buttons = ButtonHelper.readyStateButtons;
            session.setScore(0);
            this.action.accept(session, text);
        }
        return new CommandResult(result, buttons);
    }
}
