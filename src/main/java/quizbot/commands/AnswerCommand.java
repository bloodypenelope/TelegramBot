package quizbot.commands;

import quizbot.users.UserSession;
import quizbot.users.UserState;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.function.BiConsumer;

public class AnswerCommand implements TextCommand {
    private final UserState acceptableState;
    private final BiConsumer<UserSession, String> action;
    private final Statement stmt;

    public AnswerCommand(UserState acceptableState) throws IOException, SQLException {
        this.acceptableState = acceptableState;
        this.action = (s, t) -> s.setState(UserState.READY);
        var data = Files.readAllLines(new File("src\\main\\resources\\sqlData").toPath());
        var url = data.get(0);
        var user = data.get(1);
        var password = data.get(2);
        Connection conn = DriverManager.getConnection(url, user, password);
        this.stmt = conn.createStatement();
    }

    @Override
    public boolean canBeApply(UserSession session, String text) {
        return session.getState() == acceptableState && text.matches("^A|B|C|D$");
    }

    @Override
    public CommandResult execute(UserSession session, String text) throws SQLException {
        if (session.validateAnswer(text))
            session.incrementScore();
        String output;
        List<String> buttons;
        if (session.nextQuestion()) {
            output = session.getQuestion();
            buttons = ButtonHelper.optionButtons;
        } else {
            var exists = stmt.executeQuery("SELECT EXISTS(SELECT * FROM stats WHERE id = " + session.getId() + ")");
            exists.next();
            if (exists.getInt(1) == 0)
                stmt.executeUpdate("INSERT INTO stats VALUES (" + session.getId() + ",0,0,0)");
            var score = stmt.executeQuery("SELECT * FROM stats WHERE id = " + session.getId());
            score.next();
            if (score.getInt(session.getDifficulty()) < session.getScore())
                stmt.executeUpdate("UPDATE stats SET " + session.getDifficulty() + " = " + session.getScore()
                        + " WHERE id = " + session.getId());
            output = "Your score is: " + session.getScore();
            buttons = ButtonHelper.readyStateButtons;
            session.setScore(0);
            this.action.accept(session, text);
        }
        return new CommandResult(output, buttons);
    }
}
