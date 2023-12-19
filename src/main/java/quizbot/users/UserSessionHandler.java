package quizbot.users;

import quizbot.sql.SQLConnector;

import java.io.IOException;
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

    @Override
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

    @Override
    public void createQuestions(String difficulty) throws SQLException, IOException {
        setDifficulty(difficulty);
        SQLConnector connector = SQLConnector.getInstance();
        this.quiz = connector.getQuestions(difficulty);
        quiz.next();
        setQuestion(quiz.getString("question") + "\n\n" + quiz.getString("opt_a")
                + "\n" + quiz.getString("opt_b") + "\n" + quiz.getString("opt_c")
                + "\n" + quiz.getString("opt_d"));
        setAnswer(quiz.getString("ans"));
    }

    @Override
    public boolean nextQuestion() throws SQLException {
        if (quiz.next()) {
            setQuestion(quiz.getString("question") + "\n\n" + quiz.getString("opt_a")
                    + "\n" + quiz.getString("opt_b") + "\n" + quiz.getString("opt_c")
                    + "\n" + quiz.getString("opt_d"));
            setAnswer(quiz.getString("ans"));
            return true;
        } else return false;
    }

    @Override
    public boolean validateAnswer(String answer) {
        return this.getAnswer().equals(answer.toLowerCase());
    }
}
