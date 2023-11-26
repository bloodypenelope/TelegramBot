package quizbot.users;

public interface UserId {
    Long id();

    boolean equals(Object o);

    int hashCode();
}
