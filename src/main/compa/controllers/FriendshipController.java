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

import java.util.List;

public class FriendshipController extends Controller{

    private static final String PREFIX = "/friend";
    private FriendshipDAO friendshipDAO;
    private UserDAO userDAO;

    public FriendshipController(Container container) {
        super(PREFIX, container);
        this.registerAuthRoute(HttpMethod.POST, "", this::addFriend, "application/json");
        this.registerAuthRoute(HttpMethod.GET, "/search", this::searchFriends, "application/json");
        this.registerAuthRoute(HttpMethod.GET, "/:status", this::getFriendsByStatus, "application/json");
        this.registerAuthRoute(HttpMethod.DELETE, "", this::deleteFriendship, "application/json");
        this.registerAuthRoute(HttpMethod.PUT, "", this::setStatus, "application/json");
        friendshipDAO = (FriendshipDAO) container.getDAO(Friendship.class);
        userDAO = (UserDAO) container.getDAO(User.class);
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

        try {
            status = this.getParam(routingContext, "status", true, ParamMethod.GET, Friendship.Status.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        //TODO if status equals "blocked", we cant access it ??? perhaps even "sorry"

        friendshipDAO.findFriendsByStatus(me, status, res -> {
            List<User> friendList = res.result();
            routingContext.response().end(gson.toJson(userDAO.toDTO(friendList)));
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

        System.out.println("status : " + status);
        System.out.println("friend id : " + friend_id);
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
        System.out.println("In addFriend");
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
                    routingContext.response().setStatusCode(400).end(gson.toJson(
                            new FriendshipException(FriendshipException.FRIENDSHIP_ALREADY_EXISTS)));
                    return;
                }
                friendshipDAO.addFriendship(me, friend, res2 -> routingContext.response().end("{}"));
            });
        });

    }
}