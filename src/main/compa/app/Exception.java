package compa.app;

import javafx.util.Pair;
import java.text.MessageFormat;

public class Exception extends java.lang.Exception {

    private Integer code;

    public Exception(){}

    public Exception(Pair<Integer, String> message, String... values)
    {
        super(MessageFormat.format(message.getValue(), values));
        this.code = message.getKey();
    }

    public int getCode(){
        return this.code;
    }
}
