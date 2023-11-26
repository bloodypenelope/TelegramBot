package quizbot.users;

public interface UserProvider {
    class Helper {
        private static final UserProvider instance = new UserProviderHandler();
    }

    static UserProvider getInstance() {
        return Helper.instance;
    }

    UserSession findUserById(UserId userId);
}