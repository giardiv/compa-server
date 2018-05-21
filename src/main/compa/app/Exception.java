package compa.app;

import com.google.gson.annotations.Expose;
import javafx.util.Pair;

public class Exception extends java.lang.Exception {

    private Integer code;

    public Exception(){}

    public Exception(Pair<Integer, String> message)
    {
        super(message.getValue());
        this.code = message.getKey();
    }
}
