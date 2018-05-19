package main.compa.controllers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import main.compa.app.*;
import com.google.gson.Gson;
import main.compa.daos.FriendshipDAO;
import main.compa.daos.UserDAO;
import main.compa.dtos.UserDTO;
import main.compa.exception.FriendshipException;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import main.compa.app.Controller;
import main.compa.app.ModelManager;
import main.compa.app.ServiceManager;
import main.compa.daos.FriendshipDAO;
import main.compa.daos.UserDAO;
import main.compa.exception.FriendshipException;
import main.compa.exception.RegisterException;
import main.compa.models.Friendship;
import main.compa.models.User;
import main.compa.models.Friendship;
import main.compa.models.User;
import org.mongodb.morphia.query.UpdateOperations;

import javax.management.Query;
import java.util.List;

public class FriendshipController extends Controller {
    private static final String PREFIX = "/friend";

    private FriendshipDAO friendshipDAO;
    private UserDAO userDAO;

    //TODO CHANGE AUTH TOKEN TO THE HEADER AND NOT THE BODY

    public FriendshipController(ServiceManager serviceManager, Router router, ModelManager modelManager) {
        super(serviceManager, PREFIX, router);
        this.registerRoute(HttpMethod.POST, "/", this::addFriendship, "application/json");
        this.registerRoute(HttpMethod.GET, "/getFriend", this::getFriends, "application/json");

        friendshipDAO = (FriendshipDAO) modelManager.getDAO(Friendship.class);
        userDAO = (UserDAO) modelManager.getDAO(User.class);
    }

    /**
     * @api {post} /friendship Add a new friendship
     * @apiName AddFriendship
     * @apiGroup Friendship
     * @apiParam {String} friend_id : the id of the user you want to become friends with
     * @apiParam {String} token : your auth token
     * @apiUse FriendshipAlreadyExist
     */
    private void addFriendship(RoutingContext routingContext) {
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

        try {
            friendshipDAO.addFriendship(me, friend);
            routingContext.response().end();
        } catch (FriendshipException e) {
            routingContext.response().setStatusCode(418).end(new Gson().toJson(e));
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
                List<Friendship> friendships = friendshipDAO.getFriendshipsByUserId(other.id.toString());
                List<UserDTO> friends = friendshipDAO.toDTO(friendships, other);
                routingContext.response().end(new Gson().toJson(friends));
                return;
            }
        }
        else{
            List<Friendship> friendships = friendshipDAO.getFriendshipsByUserId(me.id.toString());
            List<UserDTO> friends = friendshipDAO.toDTO(friendships, me);
            routingContext.response().end(new Gson().toJson(friends));
            return;
        }
    }

}
