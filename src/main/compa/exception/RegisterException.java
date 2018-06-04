package compa.exception;

import javafx.util.Pair;
import compa.app.Exception;

public class RegisterException extends Exception
{
    /**
     * @apiDefine UserAlreadyExist
     * @apiError UserAlreadyExist 1001 : The <code>login</code> is already used.
     */
    public static final Pair<Integer, String> USER_ALREADY_EXIST = new Pair<>(1001, "Be more original ! Your login is already used");

    /**
     * @apiDefine PasswordTooShort
     * @apiError PasswordTooShort 1002 : The password require at least {@value compa.daos.UserDAO#PASSWORD_MIN_LENGTH}
     */
    public static final Pair<Integer, String> PASSWORD_TOO_SHORT = new Pair<>(1002, "I'm sure you can do better, gimme a real password");

    /**
     * @apiDefine SamePassword
     * @apiError SamePassword 1003 : The <code>password</code> is the same as the old password.
     */
    public static final Pair<Integer, String> SAME_PASSWORD = new Pair<>(1003, "It's the same password");

    /**
     * @apiDefine NotValidEmail
     * @apiError NotValidEmail 1004 : The <code>email</code> is not valid email.
     */
    public static final Pair<Integer, String> NOT_VALID_EMAIL = new Pair<>(1004, "Error in email format");


    public RegisterException(Pair<Integer, String> message, String... values) {
        super(message, values);
    }
}