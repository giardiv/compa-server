package compa.controllers;

import com.google.gson.JsonElement;
import compa.app.Container;
import compa.app.Controller;
import compa.daos.UserDAO;
import compa.models.User;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

public class UserController extends Controller {
    private static final String PREFIX = "/user";

    private UserDAO userDAO;

    public UserController(Container container){
        super(PREFIX, container);
        this.registerAuthRoute(HttpMethod.PUT, "/", this::getProfile, "application/json");

        userDAO = (UserDAO) container.getDAO(User.class);
    }

    public void getProfile(User me, RoutingContext routingContext){
        JsonElement tempEl = this.gson.toJsonTree(userDAO.toDTO(me));
        //to do add friendships
        routingContext.response().end(gson.toJson(tempEl));
    }
}
