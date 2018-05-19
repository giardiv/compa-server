package main.compa.controllers;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import main.compa.app.Container;
import main.compa.daos.FriendshipDAO;
import main.compa.daos.UserDAO;
import main.compa.dtos.UserDTO;
import main.compa.exception.FriendshipException;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import main.compa.app.Controller;
import main.compa.models.Friendship;
import main.compa.models.User;

import java.util.List;

public class FriendshipController extends Controller {
    private static final String PREFIX = "/friendship";

    private FriendshipDAO friendshipDAO;
    private UserDAO userDAO;

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    //TODO CHANGE AUTH TOKEN TO THE HEADER AND NOT THE BODY

    public FriendshipController(Container container) {
        super(PREFIX, container);
        this.registerRoute(HttpMethod.POST, "/request", this::requestFriendship, "application/json");
        this.registerRoute(HttpMethod.POST, "/response", this::respondToRequest, "application/json");
        this.registerRoute(HttpMethod.GET, "/friend_list", this::getFriends, "application/json");
        this.registerRoute(HttpMethod.GET, "/pending", this::getPendingFriendships, "application/json");

        friendshipDAO = (FriendshipDAO) container.getDAO(Friendship.class);
        userDAO = (UserDAO) container.getDAO(User.class);
    }

    /**
     * @api {post} /friendship Add a new friendship
     * @apiName Request Friendship
     * @apiGroup Friendship
     * @apiParam {String} friend_id : the id of the user you want to become friends with
     * @apiParam {String} token : your auth token
     * @apiUse FriendshipAlreadyExist
     */
    private void requestFriendship(RoutingContext routingContext) {
        String[] params = {"friend_id", "token"};

        if(!this.checkParams(routingContext, params)){
            routingContext.response().end("missing parameter"); //TODO FORMAT
            return;
        }

        User me = userDAO.findOne("token", routingContext.request().getParam("token"));

        if(me == null){
            routingContext.response().end("can't find authenticated user"); //TODO FORMAT
            return;
        }

        User friend = userDAO.findById(routingContext.request().getParam("friend_id"));

        if(friend == null){
            routingContext.response().end("can't find your friend"); //TODO FORMAT
            return;
        }

        if(friend.equals(me)){
            routingContext.response().end("you can't befriend yourself"); //TODO FORMAT
            return;
        }

        try {
            friendshipDAO.addFriendship(me, friend);
            routingContext.response().end("you are now friends"); //TODO FORMAT
        } catch (FriendshipException e) {
            routingContext.response().setStatusCode(418).end(gson.toJson(e));
        }
    }

    /**
     * @api {get} /friends Get the friends of the user
     * @apiName GetFriendship
     * @apiGroup Friendship
     * @apiParam {String} token : auth token of person making the request
     * @apiParam {String} user_id : id of user whose friend list is request
     */
    private void getFriends(RoutingContext routingContext){
        String[] params = {"user_id", "token"};

        if(!this.checkParams(routingContext, params)) {
            routingContext.response().end("missing param"); //TODO FORMAT
            return;
        }

        User me = userDAO.findOne("token", routingContext.request().getParam("token"));

        if(me == null){
            routingContext.response().end("can't find authenticated user"); //TODO FORMAT
            return;
        }

        User other = userDAO.findById(routingContext.request().getParam("user_id"));

        if(other == null){
            routingContext.response().end("can't find user whose friend list is requested"); //TODO FORMAT
            return;
        }

        if(!me.equals(other)){
            if(friendshipDAO.getFriendshipByFriends(me, other) == null){
                routingContext.response().end("can't see this user's friends : you aren't friends"); //TODO FORMAT
                return;
            }
            else{
                List<Friendship> friendships = friendshipDAO.getFriendshipsByUser(other);
                List<UserDTO> friends = friendshipDAO.toDTO(friendships, other);
                routingContext.response().end(gson.toJson(friends));
                return;
            }
        }
        else{
            List<Friendship> friendships = friendshipDAO.getFriendshipsByUser(me);
            List<UserDTO> friends = friendshipDAO.toDTO(friendships, me);
            routingContext.response().end(gson.toJson(friends));
            return;
        }
    }

    private void respondToRequest(RoutingContext routingContext){

    }

    private void getPendingFriendships(RoutingContext routingContext){
        String[] params = {"token"};

        if(!this.checkParams(routingContext, params)){
            routingContext.response().end("missing parameter"); //TODO FORMAT
            return;
        }
    }

}
