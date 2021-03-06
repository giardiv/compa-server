package compa.exception;

import compa.app.Exception;
import javafx.util.Pair;

public class UserException extends Exception{

    /**
     * @apiDefine UserNotFound
     * @apiError UserNotFound 1501 : No user found with this id
     */
    public static final Pair<Integer, String> USER_NOT_FOUND = new Pair<>(2001, "Can not find this user with {0} : {1}");

    public static final Pair<Integer, String> USER_ALREADY_EXIST = new Pair<>(2002, "User with {0} : {1} exist");

    public UserException(Pair<Integer, String> message, String... values) {
        super(message, values);
    }
}
