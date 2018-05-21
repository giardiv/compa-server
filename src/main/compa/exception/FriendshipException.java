package compa.exception;

import compa.app.Exception;
import javafx.util.Pair;

public class FriendshipException extends Exception {
    /**
     * @apiDefine FriendshipAlreadyExist
     * @apiError FriendshipAlreadyExist The friendship is already defined.
     */
    public static final Pair<Integer, String> FRIEND_ALREADY_EXIST = new Pair<>(2001, "You're already friends, go outside");

    public FriendshipException(Pair<Integer, String> message) {
        super(message);
    }
}
