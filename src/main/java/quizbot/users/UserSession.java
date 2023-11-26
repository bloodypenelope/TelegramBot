package quizbot.users;

import java.io.IOException;
import java.sql.SQLException;

public interface UserSession {
    Long getId();

    UserState getState();

    String getDifficulty();

    void setDifficulty(String difficulty);

    int getScore();

    void setScore(int score);

    void incrementScore();

    void setState(UserState state);

    String getQuestion();

    void setQuestion(String question);

    String getAnswer();

    void setAnswer(String answer);

    void createQuestions(String difficulty) throws SQLException, IOException;

    boolean nextQuestion() throws SQLException;

    boolean validateAnswer(String answer);
}
