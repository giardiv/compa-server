package compa.controllers;

import compa.app.Container;
import compa.app.Controller;
import compa.daos.FriendshipDAO;
import compa.daos.UserDAO;
import compa.exception.FriendshipException;
import compa.exception.ParameterException;
import compa.exception.UserException;
import compa.models.Friendship;
import compa.models.User;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FriendshipController extends Controller{

    private static final String PREFIX = "/friend";
    private FriendshipDAO friendshipDAO;
    private UserDAO userDAO;


    public FriendshipController(Container container) {
        super(PREFIX, container);
        this.registerAuthRoute(HttpMethod.POST, "", this::addFriend, "application/json");
        this.registerAuthRoute(HttpMethod.GET, "/search", this::searchFriends, "application/json");
        this.registerAuthRoute(HttpMethod.GET, "/:status", this::getFriendsByStatus, "application/json");
        this.registerAuthRoute(HttpMethod.GET, "/:user_id/:status", this::getFriendsByStatusById, "application/json");
        this.registerAuthRoute(HttpMethod.DELETE, "", this::deleteFriendship, "application/json");
        this.registerAuthRoute(HttpMethod.PUT, "", this::setStatus, "application/json");
        this.registerAuthRoute(HttpMethod.GET, "all/all_friendOfFriend", this::suggestFriend, "application/json");
        friendshipDAO = (FriendshipDAO) container.getDAO(Friendship.class);
        userDAO = (UserDAO) container.getDAO(User.class);
    }

    //TODO MAP all the user and limit the result.
    private void suggestFriend(User me, RoutingContext routingContext){
        Friendship.Status status = Friendship.Status.ACCEPTED;

        friendshipDAO.findFriendsByStatus(me, status, res -> {
            List<User> friendList = res.result();
            friendshipDAO.suggestFriend(me,status,friendList, resf -> {
                List<User> allFriends = resf.result();

                System.out.println("size : " + allFriends.size());
                List<User> result = allFriends.stream().filter(aObject -> {
                    return !friendList.contains(aObject);
                }).collect(Collectors.toList());
                System.out.println("result : " + result.size());

                routingContext.response().end(gson.toJson(userDAO.toDTO(result)));

            });

        });
    }


    /**
     * @api {get} /friend/:status Get list of friends filtered by status
     * @apiName GetByStatus
     * @apiGroup Friendship
     *
     * @apiParam {String} status      The Friendship.Status
     *
     * @apiSuccess Return a list of User
     */
    private void getFriendsByStatus(User me, RoutingContext routingContext){
        Friendship.Status status;
        Integer size;

        try {
            status = this.getParam(routingContext, "status", true, ParamMethod.GET, Friendship.Status.class);
            size = this.getParam(routingContext, "size", false, ParamMethod.GET, Integer.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        if(status == Friendship.Status.BLOCKED || status == Friendship.Status.REFUSED)
            routingContext.response().end(gson.toJson(FriendshipException.INVALID_STATUS));


        friendshipDAO.findFriendsByStatus(me, status, res -> {
            List<User> friendList = res.result();

            if(size != null)
                routingContext.response().end(gson.toJson(userDAO.toDTO(friendList, size, size)));
            else
                routingContext.response().end(gson.toJson(userDAO.toDTO(friendList)));
        });
    }

    /**
     * @api {get} /friend/:user_id/:status Get list of friends bu user_id, filtered by status
     * @apiName GetByStatusByid
     * @apiGroup Friendship
     *
     * @apiParam {String} status      The Friendship.Status
     * @apiParam {String} uder_id     String
     *
     * @apiSuccess Return a list of User
     */
    private void getFriendsByStatusById(User me, RoutingContext routingContext){
        Friendship.Status status;
        Integer size;
        String user_id;

        try {
            user_id = this.getParam(routingContext, "user_id", false, ParamMethod.GET, String.class);
            status = this.getParam(routingContext, "status", true, ParamMethod.GET, Friendship.Status.class);
            size = this.getParam(routingContext, "size", false, ParamMethod.GET, Integer.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        if(status == Friendship.Status.BLOCKED || status == Friendship.Status.REFUSED)
            routingContext.response().end(gson.toJson(FriendshipException.INVALID_STATUS));


        userDAO.findById(user_id, res1 -> {
            User user = res1.result();

            if (user == null) {
                routingContext.response().setStatusCode(404).end(gson.toJson(
                        new UserException(UserException.USER_NOT_FOUND, "id", user.toString())));
                return;
            }

            friendshipDAO.findFriendsByStatus(user, status, res -> {
                List<User> friendList = res.result();
                if (size != null)
                    routingContext.response().end(gson.toJson(userDAO.toDTO(friendList, size, size)));
                else
                    routingContext.response().end(gson.toJson(userDAO.toDTO(friendList)));
            });
        });
    }


    /**
     * @api {put} /friend
     *
     * us Update the status of a friendship
     * @apiName SetStatus
     * @apiGroup Friendship
     *
     * @apiParam {String} friend_id      The <code>_id</code> of the friend matching with the wanted friendship
     *
     * @apiSuccess Return 200 without body
     */
    private void setStatus(User me, RoutingContext routingContext){
        final Friendship.Status status;
        final String friend_id;

        try {
            status = this.getParam(routingContext, "status", true, ParamMethod.JSON, Friendship.Status.class);
            friend_id = this.getParam(routingContext, "friend_id", true, ParamMethod.JSON, String.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        userDAO.findById(friend_id, res1 -> {
            User friend = res1.result();
            System.out.println("friend: " + friend);

            if (friend == null) {
                routingContext.response().setStatusCode(404).end(gson.toJson(
                        new UserException(UserException.USER_NOT_FOUND, "id", friend_id.toString())));
                return;
            }
            friendshipDAO.findFriendshipByUsers(me, friend, res -> {
                Friendship fs = res.result();

                if (fs == null) {
                    routingContext.response().setStatusCode(404).end(gson.toJson(
                            new UserException(FriendshipException.NOT_FRIEND)));
                    return;
                }

                boolean meIsA = me.getId().equals(fs.getUserA().getId());

                if (
                        (meIsA && !Friendship.validStatusChange(fs.getStatusA(), status)) ||
                                (!meIsA && !Friendship.validStatusChange(fs.getStatusB(), status))
                        ) {
                    routingContext.response().setStatusCode(404).end(gson.toJson(
                            new FriendshipException(FriendshipException.NOT_CHANGE_STATUS)));
                    return;
                }

                friendshipDAO.updateFriendship(fs, status, meIsA, res2 -> routingContext.response().end("{}"));


            });
        });
    }

    /**
     * @api {delete} /friend Remove a friendship
     * @apiName Delete
     * @apiGroup Friendship
     *
     * @apiParam {String} friend_id      The <code>_id</code> of the friend to be removed
     *
     * @apiSuccess Return 200 without body
     */
    private void deleteFriendship(User me, RoutingContext routingContext){
        final String friend_id;

        try {
            friend_id = this.getParam(routingContext, "friend_id", true, ParamMethod.JSON, String.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        userDAO.findById(friend_id, res1 -> {
            User friend = res1.result();
            if(friend == null){
                routingContext.response().setStatusCode(404).end(gson.toJson(
                        new UserException(UserException.USER_NOT_FOUND, "id", friend_id)));
                return;
            }
            if(friend.equals(me)){
                routingContext.response().setStatusCode(400).end(gson.toJson(
                        new FriendshipException(FriendshipException.BEFRIEND_YOURSELF)));
                return;
            }
            friendshipDAO.findFriendshipByUsers(me, friend, res -> {
                Friendship fs = res.result();

                if(fs == null){
                    routingContext.response().setStatusCode(400).end(gson.toJson(
                            new FriendshipException(FriendshipException.NOT_FRIEND)));
                    return;
                }
                friendshipDAO.deleteFriendship(fs, res2 -> {
                    routingContext.response().end("{}");
                });
            });
        });
    }

    /**
     * @api {get} /friend/search Get list of users with <code>login == tag</code>
     * @apiName Search
     * @apiGroup Friendship
     *
     * @apiParam {String} tag      Currently, the login of searched user
     *
     * @apiSuccess Return a User
     */
    private void searchFriends(User me, RoutingContext routingContext){
        System.out.println("In search");
        String tag;

        try {
            tag = this.getParam(routingContext, "tag", true, ParamMethod.GET, String.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        userDAO.search(tag, res -> {
            List<User> u = res.result();
            if(u != null){
                routingContext.response().end(gson.toJson(userDAO.toDTO(u)));
            } else {
                routingContext.response().setStatusCode(404).end(gson.toJson(
                        new UserException(UserException.USER_NOT_FOUND, "login", tag)));
            }
        });
    }

    /**
     * @api {post} /friend Create a new friendship
     * @apiName GetMe
     * @apiGroup Friendship
     *
     * @apiParam {String} friend_id      The <code>_id</code> of the new friend
     *
     * @apiSuccess Return 200 without body
     */
    private void addFriend(User me, RoutingContext routingContext) {
        final String friend_id;

        try {
            friend_id = this.getParam(routingContext, "friend_id", true, ParamMethod.JSON, String.class);

        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        userDAO.findById(friend_id, res1 -> {
            User friend = res1.result();
            if(friend == null){
                routingContext.response().setStatusCode(404).end(gson.toJson(
                        new UserException(UserException.USER_NOT_FOUND, "id", friend_id)));
                return;
            }
            if(friend.equals(me)){
                routingContext.response().setStatusCode(400).end(gson.toJson(
                        new FriendshipException(FriendshipException.BEFRIEND_YOURSELF)));
                return;
            }

            friendshipDAO.findFriendshipByUsers(me, friend, res -> {
                Friendship fs = res.result();

                if(fs != null){

                    Friendship.Status statusToCheck = fs.getUserA().equals(me) ? fs.getStatusA() : fs.getStatusB();
                    boolean meIsA = fs.getUserA().equals(me) ? true : false;

                    if(statusToCheck == Friendship.Status.SORRY){
                        friendshipDAO.updateFriendship(fs, Friendship.Status.PENDING, meIsA, res2 -> routingContext.response().end("{}"));
                        return;
                    }
                    routingContext.response().setStatusCode(400).end(gson.toJson(
                            new FriendshipException(FriendshipException.FRIENDSHIP_ALREADY_EXISTS)));
                    return;
                }
                friendshipDAO.addFriendship(me, friend, res2 -> routingContext.response().end("{}"));
            });
        });

    }
}