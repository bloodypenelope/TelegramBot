package quizbot.users;

import java.util.HashMap;
import java.util.Map;

public class UserProviderHandler implements UserProvider {
    private final Map<UserId, UserSession> data = new HashMap<>();

    @Override
    public UserSession findUserById(UserId userId) {
        return data.computeIfAbsent(userId, UserSessionHandler::new);
    }
}
