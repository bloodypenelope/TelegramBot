package quizbot.sql;

import quizbot.users.UserSession;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;

public class SQLConnector {
    private static SQLConnector instance;
    private final Connection connection;

    private SQLConnector() throws SQLException, IOException {
        var data = Files.readAllLines(new File("src\\main\\resources\\sqlData").toPath());
        var url = data.get(0);
        var user = data.get(1);
        var password = data.get(2);
        this.connection = DriverManager.getConnection(url, user, password);
    }

    public static SQLConnector getInstance() throws SQLException, IOException {
        if (instance == null)
            instance = new SQLConnector();
        return instance;
    }

    private void initializeStats(UserSession session) throws SQLException {
        PreparedStatement stmt;
        var checkStatsStmt = "SELECT EXISTS(SELECT * FROM stats WHERE id = ?)";
        var updateStatsStmt = "INSERT INTO stats VALUES (?,0,0,0)";
        stmt = this.connection.prepareStatement(checkStatsStmt);
        stmt.setLong(1, session.getId());
        var exists = stmt.executeQuery();
        exists.next();
        if (exists.getInt(1) == 0) {
            stmt = this.connection.prepareStatement(updateStatsStmt);
            stmt.setLong(1, session.getId());
            stmt.executeUpdate();
        }
    }

    public ResultSet getStats(UserSession session) throws SQLException {
        this.initializeStats(session);
        var statsStmt = "SELECT * FROM stats WHERE id = ?";
        PreparedStatement stmt = this.connection.prepareStatement(statsStmt);
        stmt.setLong(1, session.getId());
        var stats = stmt.executeQuery();
        stats.next();
        return stats;
    }

    public void updateStats(UserSession session) throws SQLException {
        var difficulty = session.getDifficulty();
        var stats = this.getStats(session);
        var updateStatsStmt = "UPDATE stats SET easy = ?, medium = ?, hard = ? WHERE id = ?";
        PreparedStatement stmt = this.connection.prepareStatement(updateStatsStmt);
        stmt.setInt(1, difficulty.equals("easy") && stats.getInt(difficulty) < session.getScore() ?
                session.getScore() : stats.getInt("easy"));
        stmt.setInt(2, difficulty.equals("medium") && stats.getInt(difficulty) < session.getScore() ?
                session.getScore() : stats.getInt("medium"));
        stmt.setInt(3, difficulty.equals("hard") && stats.getInt(difficulty) < session.getScore() ?
                session.getScore() : stats.getInt("hard"));
        stmt.setLong(4, session.getId());
        stmt.executeUpdate();
    }

    public ResultSet getQuestions(String difficulty) throws SQLException {
        var getQuestionsStmt = String.format("SELECT * FROM %s_t ORDER BY RAND() LIMIT 10", difficulty);
        PreparedStatement stmt = this.connection.prepareStatement(getQuestionsStmt);
        return stmt.executeQuery();
    }

    @SuppressWarnings("unused")
    public void closeConnection() throws SQLException {
        connection.close();
    }
}
