package compa.exception;

import compa.app.Exception;
import javafx.util.Pair;

public class ImageException extends Exception{

    /**
     * @apiDefine ImageUpload
     * @apiError ImageUpload Upload Problem
     */
    public static final Pair<Integer, String> IO_EXCEPTION = new Pair<>(5001, "IO Exception");

    /**
     * @apiDefine UnacceptedFormat
     * @apiError UnacceptedFormat The format isnot accepted, i.e different than png or jpeg
     */
    public static final Pair<Integer, String> UNACCEPTED_FORMAT = new Pair<>(5002, "Format {0} is unacceptable !");

    public ImageException(Pair<Integer, String> message, String... values) {
        super(message, values);
    }
}
