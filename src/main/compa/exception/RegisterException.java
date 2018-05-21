package compa.exception;

import javafx.util.Pair;
import compa.app.Exception;

public class RegisterException extends Exception
{
    /**
     * @apiDefine UserAlreadyExist
     * @apiError UserAlreadyExist The <code>login</code> is already used.
     */
    public static final Pair<Integer, String> USER_ALREADY_EXIST = new Pair<>(1001, "Be more original ! Your login is already used");

    /**
     * @apiDefine PasswordTooShort
     * @apiError PasswordTooShort The password require at least {@value compa.daos.UserDAO#PASSWORD_MIN_LENGTH}
     */
    public static final Pair<Integer, String> PASSWORD_TOO_SHORT = new Pair<>(1002, "I'm sure you can do better, gimme a real password");

    public RegisterException(Pair<Integer, String> message) {
        super(message);
    }
}