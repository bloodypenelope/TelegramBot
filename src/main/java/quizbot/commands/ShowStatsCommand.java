package quizbot.commands;

import quizbot.users.UserSession;
import quizbot.users.UserState;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;

public class ShowStatsCommand implements TextCommand {
    private final UserState acceptableState;
    private final Statement stmt;

    public ShowStatsCommand(UserState acceptableState) throws IOException, SQLException {
        this.acceptableState = acceptableState;
        var data = Files.readAllLines(new File("src\\main\\resources\\sqlData").toPath());
        var url = data.get(0);
        var user = data.get(1);
        var password = data.get(2);
        Connection conn = DriverManager.getConnection(url, user, password);
        this.stmt = conn.createStatement();
    }

    @Override
    public boolean canBeApply(UserSession session, String text) {
        return session.getState() == this.acceptableState && "Show Stats".equals(text);
    }

    @Override
    public CommandResult execute(UserSession session, String text) throws SQLException {
        var exists = stmt.executeQuery("SELECT EXISTS(SELECT * FROM stats WHERE id = " + session.getId() + ")");
        exists.next();
        if (exists.getInt(1) == 0)
            stmt.executeUpdate("INSERT INTO stats VALUES (" + session.getId() + ",0,0,0)");
        var stats = stmt.executeQuery("SELECT * FROM stats WHERE id = " + session.getId());
        stats.next();
        var resultText = "Your high scores:\nEasy: " + stats.getInt(2)
                + "\nMedium: " + stats.getInt(3)
                + "\nHard: " + stats.getInt(4);
        return new CommandResult(resultText, ButtonHelper.readyStateButtons);
    }
}