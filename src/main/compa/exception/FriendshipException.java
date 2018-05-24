package compa.exception;

import compa.app.Exception;
import javafx.util.Pair;

public class FriendshipException extends Exception {
    /**
     * @apiDefine FriendshipAlreadyExist
     * @apiError FriendshipAlreadyExist The friendship is already defined.
     */
    public static final Pair<Integer, String> FRIENDSHIP_ALREADY_EXISTS = new Pair<>(6001, "You're already related in some way");

    /**
     * @apiDefine FriendshipNotExist
     * @apiError FriendshipNotExist The friendship is not defined.
     */
    public static final Pair<Integer, String> FRIENDSHIP_NOT_FOUND = new Pair<>(6002, "Can't find your friend");

    /**
     * @apiDefine CanNotBefriendYourself
     * @apiError CanNotBefriendYourself The friendship is not defined.
     */
    public static final Pair<Integer, String> BEFRIEND_YOURSELF = new Pair<>(6003, "You can't befriend yourself");

    public static final Pair<Integer, String> FRIEND_NEW = new Pair<>(6004, "Now, you are friends");

    public static final Pair<Integer, String> NOT_FRIEND = new Pair<>(6005, "You aren't friends");

    public static final Pair<Integer, String> INVALID_STATUS = new Pair<>(6006, "Invalid status requested");

    public static final Pair<Integer, String> FRIEND_NOT_EXIST = new Pair<>(6002, "Can't find your friend");

    public static final Pair<Integer, String> BEFRIEND_ME = new Pair<>(6003, "You can't befriend yourself");

    public static final Pair<Integer, String> User_NOT_EXIST = new Pair<>(6004, "Can't find user whose friend list is requested");


    public FriendshipException(Pair<Integer, String> message) {
        super(message);
    }
}