package main.compa.exception;

import javafx.util.Pair;
import main.compa.app.Exception;

public class LoginException extends Exception {
    /**
     * @apiDefine IncorrectCredentials
     * @apiError IncorrectCredentials Incorrect login or password
     */
    public static final Pair<Integer, String> INCORRECT_CREDENTIALS = new Pair<>(3001, "WRONG !");
}
