package main.compa.exception;

import com.google.gson.annotations.Expose;
import javafx.util.Pair;
import main.compa.models.JSONisable;

public class RegisterException extends Exception implements JSONisable
{
    public static final Pair<Integer, String> USER_ALREADY_EXIST = new Pair<>(1001, "Be more original ! Your login is already used");
    public static final Pair<Integer, String> PASSWORD_TOO_SHORT = new Pair<>(1002, "I'm sure you can do better, gimme a real password");

    @Expose
    private Integer code;

    public RegisterException(Pair<Integer, String> message)
    {
        super(message.getValue());
        this.code = message.getKey();
    }
}