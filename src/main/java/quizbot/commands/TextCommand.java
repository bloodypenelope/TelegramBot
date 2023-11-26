package quizbot.commands;

import quizbot.users.UserSession;

import java.io.IOException;
import java.sql.SQLException;

public interface TextCommand {
    boolean canBeApply(UserSession session, String text);
    CommandResult execute(UserSession session, String text) throws SQLException, IOException;
}
