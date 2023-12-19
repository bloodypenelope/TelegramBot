package quizbot.commands;

import quizbot.sql.SQLConnector;
import quizbot.users.UserSession;
import quizbot.users.UserState;

import java.io.IOException;
import java.sql.*;

public class ShowStatsCommand implements TextCommand {
    private final SQLConnector connector;
    private final UserState acceptableState;

    public ShowStatsCommand(UserState acceptableState) throws IOException, SQLException {
        this.acceptableState = acceptableState;
        this.connector = SQLConnector.getInstance();
    }

    @Override
    public boolean canBeApply(UserSession session, String text) {
        return session.getState() == this.acceptableState && "Show Stats".equals(text);
    }

    @Override
    public CommandResult execute(UserSession session, String text) throws SQLException {
        var stats = this.connector.getStats(session);
        var result = String.format("Your stats:\nEasy: %d\nMedium: %d\nHard: %d",
                stats.getInt("easy"), stats.getInt("medium"),
                stats.getInt("hard"));
        return new CommandResult(result, ButtonHelper.readyStateButtons);
    }
}
