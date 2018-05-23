package compa.exception;

import javafx.util.Pair;
import compa.app.Exception;

public class LoginException extends Exception {
    /**
     * @apiDefine IncorrectCredentials
     * @apiError IncorrectCredentials Incorrect login or password
     */
    public static final Pair<Integer, String> INCORRECT_CREDENTIALS = new Pair<>(3001, "WRONG !");

    /**
     * @apiDefine IncorrectToken
     * @apiError IncorrectToken Incorrect token
     */
    public static final Pair<Integer, String> INCORRECT_TOKEN = new Pair<>(3002, "WRONG TOKEN !");

    public LoginException(Pair<Integer, String> message, String... values) {
        super(message, values);
    }
}
