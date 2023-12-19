package quizbot.commands;

import quizbot.users.UserSession;
import quizbot.users.UserState;

import java.io.IOException;
import java.sql.SQLException;
import java.util.function.BiConsumer;

public class SelectDifficultyCommand implements TextCommand {
    private final UserState acceptableState;
    private final BiConsumer<UserSession, String> action;

    public SelectDifficultyCommand(UserState acceptableState) {
        this.acceptableState = acceptableState;
        this.action = (s, t) -> s.setState(UserState.INGAME);
    }


    @Override
    public boolean canBeApply(UserSession session, String text) {
        return this.acceptableState == session.getState() && text.matches("^Easy|Medium|Hard$");
    }

    @Override
    public CommandResult execute(UserSession session, String text) throws SQLException, IOException {
        session.createQuestions(text.toLowerCase());
        this.action.accept(session, text);
        var output = session.getQuestion();
        return new CommandResult(output, ButtonHelper.optionButtons);
    }
}
