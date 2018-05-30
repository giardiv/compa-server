package compa.exception;

import compa.app.Exception;
import javafx.util.Pair;

public class ParameterException extends Exception{
    /**
     * @apiDefine ParamIsRequired
     * @apiError ParamIsRequired The param is required.
     */
    public static final Pair<Integer, String> PARAM_REQUIRED = new Pair<>(4001, "Missing parameter: {0} with method {1}");

    /**
     * @apiDefine FormatException
     * @apiError FormatException The param is required.
     */
    public static final Pair<Integer, String> PARAM_WRONG_FORMAT = new Pair<>(4002, "Cannot convert parameter: {0} to {1}");

    public static final Pair<Integer, String> ERROR_PARSING_JSON = new Pair<>(4003, "Couldn't parse the body");

    public ParameterException(Pair<Integer, String> message, String... values) {
        super(message, values);
    }
}
