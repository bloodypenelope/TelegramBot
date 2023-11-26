package quizbot.users;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;

public class UserSessionHandler implements UserSession {
    private final UserId id;
    private UserState state = UserState.NEW_BEE;
    private String difficulty;
    private int score = 0;
    private ResultSet quiz;
    private String question;
    private String answer;

    UserSessionHandler(UserId id) {
        this.id = id;
    }

    public Long getId() {
        return id.id();
    }

    @Override
    public UserState getState() {
        return state;
    }

    @Override
    public String getDifficulty() {
        return this.difficulty;
    }

    @Override
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public void incrementScore() {
        this.score++;
    }

    @Override
    public void setState(UserState state) {
        this.state = state;
    }

    @Override
    public String getQuestion() {
        return question;
    }

    @Override
    public void setQuestion(String question) {
        this.question = question;
    }

    @Override
    public String getAnswer() {
        return answer;
    }

    @Override
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void createQuestions(String difficulty) throws SQLException, IOException {
        var data = Files.readAllLines(new File("src\\main\\resources\\sqlData").toPath());
        var url = data.get(0);
        var user = data.get(1);
        var password = data.get(2);
        Connection conn = DriverManager.getConnection(url, user, password);
        Statement stmt = conn.createStatement();
        setDifficulty(difficulty);
        this.quiz = stmt.executeQuery("SELECT * FROM " + difficulty.toLowerCase() + "_t ORDER BY RAND() LIMIT 10");
        quiz.next();
        setQuestion(quiz.getString(2) + "\n\n" + quiz.getString(3)
            + "\n" + quiz.getString(4) + "\n" + quiz.getString(5)
            + "\n" + quiz.getString(6));
        setAnswer(quiz.getString(7));
    }

    public boolean nextQuestion() throws SQLException {
        if (quiz.next()) {
            setQuestion(quiz.getString(2) + "\n\n" + quiz.getString(3)
                    + "\n" + quiz.getString(4) + "\n" + quiz.getString(5)
                    + "\n" + quiz.getString(6));
            setAnswer(quiz.getString(7));
            return true;
        } else return false;
    }

    @Override
    public boolean validateAnswer(String answer) {
        return this.getAnswer().equals(answer.toLowerCase());
    }
}