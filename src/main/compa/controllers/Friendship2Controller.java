package compa.controllers;

import compa.app.Container;
import compa.app.Controller;
import compa.daos.Friendship2DAO;
import compa.daos.UserDAO;
import compa.exception.FriendshipException;
import compa.exception.ParameterException;
import compa.models.Friendship2;
import compa.models.User;
import compa.services.GsonService;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

public class Friendship2Controller extends Controller{

    private static final String PREFIX = "/friendship2";

    private Friendship2DAO friendshipDAO2;
    private UserDAO userDAO;

    public Friendship2Controller(Container container) {

        super(PREFIX, container);
        this.registerAuthRoute(HttpMethod.POST, "/request", this::addFriend, "application/json");

        friendshipDAO2 = (Friendship2DAO) container.getDAO(Friendship2.class);
        userDAO = (UserDAO) container.getDAO(User.class);
    }

    private void addFriend(User me, RoutingContext routingContext) {
        GsonService gson = (GsonService) this.get(GsonService.class);

        String friend_id = null;
        try {
            friend_id = (String) this.getParam(routingContext, "friend_id", true, paramMethod.JSON, String.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        userDAO.findById(friend_id, res1 -> {

            User friend = res1.result();

            if(friend == null){
                routingContext.response().setStatusCode(400).end(
                        gson.toJson(
                                new FriendshipException(FriendshipException.FRIEND_NOT_EXIST)));
                return;
            }

            if(friend.equals(me)){
                routingContext.response().setStatusCode(400).end(
                        gson.toJson(
                                new FriendshipException(FriendshipException.BEFRIEND_ME)));
                return;
            }

            friendshipDAO2.addFriendship(me, friend, res2 -> {
                if(res2.failed()){
                    routingContext.response().setStatusCode(418).end(gson.toJson(res2.cause()));
                }
                else{
                    routingContext.response().end(
                            gson.toJson(
                                    new FriendshipException(FriendshipException.FRIEND_NEW)));
                }
            });
        });

    }
}
