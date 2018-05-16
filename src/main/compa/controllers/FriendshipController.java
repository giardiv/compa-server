package main.compa.controllers;

import com.google.gson.Gson;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import main.compa.app.Controller;
import main.compa.app.ModelManager;
import main.compa.daos.FriendshipDAO;
import main.compa.daos.UserDAO;
import main.compa.exception.FriendshipException;
import main.compa.exception.RegisterException;
import main.compa.models.Friendship;
import main.compa.models.User;

public class FriendshipController extends Controller {
    private static final String PREFIX = "/friendship";

    private FriendshipDAO friendshipDAO;
    private UserDAO userDAO;

    // TODO: maybe create a service manager
    public FriendshipController(Router router, ModelManager modelManager) {
        super(PREFIX, router);
        this.registerRoute(HttpMethod.POST, "/", this::addFriendship, "application/json");
        this.registerRoute(HttpMethod.GET, "/", this::getAll, "application/json");

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
        String friendId = routingContext.request().getParam("friend_id");
        User me = this.userDAO.findById("5afbc9f5d914cc19b21805d6");
        User friend = this.userDAO.findById("5afb06cad914cc158e86e4c8");
        try {
            friendshipDAO.addFriendship(me, friend);
            routingContext.response().end();
        } catch (FriendshipException e) {
            routingContext.response().setStatusCode(418).end(new Gson().toJson(e));
        }
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
