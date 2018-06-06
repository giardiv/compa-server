package compa.exception;

import compa.app.Exception;
import javafx.util.Pair;

public class LocationException extends Exception{
    /**
     * @apiDefine FriendIsGhosted
     * @apiError FriendIsGhosted The friend is in ghost mode
     */
    public static final Pair<Integer, String> FRIEND_IS_GHOST = new Pair<>(7001, "👻");

    public LocationException(Pair<Integer, String> message, String... values) {
        super(message, values);
    }
}
