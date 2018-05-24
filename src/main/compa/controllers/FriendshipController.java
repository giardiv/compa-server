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
import compa.services.GsonService;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

public class FriendshipController extends Controller{

    private static final String PREFIX = "/friendship";

    private FriendshipDAO friendshipDAO;
    private UserDAO userDAO;

    public FriendshipController(Container container) {
        super(PREFIX, container);
        this.registerAuthRoute(HttpMethod.POST, "/request", this::addFriend, "application/json");
        this.registerAuthRoute(HttpMethod.GET, "/friends/:status", this::getFriendshipByStatus, "application/json");
        friendshipDAO = (FriendshipDAO) container.getDAO(Friendship.class);
        userDAO = (UserDAO) container.getDAO(User.class);
    }


    public void getFriendshipByStatus(User me, RoutingContext routingContext){
        String status = null;

        try {
            status = this.getParam(routingContext, "status", true, ParamMethod.GET, String.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        Friendship.Status statusEnum = null;
        try{
            statusEnum = Friendship.Status.valueOf(status);
            if(statusEnum.equals(Friendship.Status.BLOCKED)){

                routingContext.response().setStatusCode(400).end(
                        gson.toJson(new FriendshipException(FriendshipException.INVALID_STATUS)));
                return;
            }

        } catch (IllegalArgumentException e){
            routingContext.response().setStatusCode(400).end(
                    gson.toJson(new FriendshipException(FriendshipException.INVALID_STATUS)));
            return;
        }

        friendshipDAO.findFriendshipsByStatus(me, statusEnum, res -> {
            List<Friendship> friendshipList = res.result();
        });
    }

    private void addFriend(User me, RoutingContext routingContext) {

        String friend_id = null;

        try {
            friend_id = this.getParam(routingContext, "friend_id", true, ParamMethod.JSON, String.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        userDAO.findById(friend_id, res1 -> {

            User friend = res1.result();

            if(friend == null){
                routingContext.response().setStatusCode(400).end(
                        gson.toJson(
                                new UserException(UserException.USER_NOT_FOUND)));
                return;
            }

            if(friend.equals(me)){
                routingContext.response().setStatusCode(400).end(gson.toJson(
                        new FriendshipException(FriendshipException.BEFRIEND_YOURSELF)));
                return;
            }

            friendshipDAO.findFriendshipByUsers(me, friend, res -> {

                if(!res.failed()){
                    routingContext.response().setStatusCode(400).end(gson.toJson(
                            new FriendshipException(FriendshipException.FRIENDSHIP_ALREADY_EXISTS)));

                    return;
                }

                friendshipDAO.addFriendship(me, friend, res2 -> {
                    if(res2.failed()){
                        routingContext.response().setStatusCode(418).end(gson.toJson(res2.cause())); //SHOULD NEVER HAPPEN
                    }
                    else{
                        routingContext.response().end(
                                gson.toJson(
                                        new FriendshipException(FriendshipException.FRIEND_NEW)));
                    }
                });

            });

        });

    }
}