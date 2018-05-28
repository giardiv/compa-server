package compa.exception;

import compa.app.Exception;
import javafx.util.Pair;

public class UserException extends Exception{

    /**
     * @apiDefine UserNotFound
     * @apiError UserNotFound 1501 : No user found with this id
     */
    public static final Pair<Integer, String> USER_NOT_FOUND = new Pair<>(1501, "Can not find this user with {0} : {1}");

    public UserException(Pair<Integer, String> message, String... values) {
        super(message, values);
    }
}
