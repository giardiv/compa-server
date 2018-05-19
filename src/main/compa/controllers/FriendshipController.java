package main.compa.controllers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import main.compa.app.*;
import com.google.gson.Gson;
import main.compa.daos.FriendshipDAO;
import main.compa.daos.UserDAO;
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

public class FriendshipController extends Controller {
    private static final String PREFIX = "/friend";

    private FriendshipDAO friendshipDAO;
    private UserDAO userDAO;

    // TODO: maybe create a service manager
    public FriendshipController(ServiceManager serviceManager, Router router, ModelManager modelManager) {
        super(serviceManager, PREFIX, router);
        this.registerRoute(HttpMethod.POST, "/", this::addFriendship, "application/json");
        this.registerRoute(HttpMethod.GET, "/", this::getAll, "application/json");
        this.registerRoute(HttpMethod.GET, "/getFriend", this::getFriends, "application/json");

        friendshipDAO = (FriendshipDAO) modelManager.getDAO(Friendship.class);
        userDAO = (UserDAO) modelManager.getDAO(User.class);
    }

    /**
     * @api {post} /friendship Add a new friendship
     * @apiName AddFriendship
     * @apiGroup Friendship
     *
     * @apiParam {String} friend_id
     *
     * @apiUse FriendshipAlreadyExist
     */
    private void addFriendship(RoutingContext routingContext) {
        String[] params = {"friend_id"};

        if(!this.checkParams(routingContext, params)){
            routingContext.response().end(); //TODO RETURN APPROPRIATE ERROR CODE & MSG
        }

        String friendId = routingContext.request().getParam("friend_id");

        User me = userDAO.findById("5affe51a210883070cbca779");
        User friend = userDAO.findById(friendId);

        try {
            friendshipDAO.addFriendship(me, friend);
            routingContext.response().end();
        } catch (FriendshipException e) {
            routingContext.response().setStatusCode(418).end(new Gson().toJson(e));
        }
    }

    /**
     *{get} /friends Get all friends
     * @param routingContext
     */
    private void getFriends(RoutingContext routingContext){
        String userId = routingContext.request().getParam("user_id");
        String userToken = routingContext.request().getParam("user_tokens");
        routingContext.response().end(new Gson().toJson(friendshipDAO.getFriendshipByUserId(userId)));
    }

    /**
     * @api {get} /friendship Get all friendships
     * @apiName GetFriendships
     * @apiGroup Friendship
     */
    private void getAll(RoutingContext routingContext){
        routingContext.response().end(new Gson().toJson(friendshipDAO.findAll()));
    }
}
