package compa.controllers;

import compa.app.Container;
import compa.app.Controller;
import com.google.gson.Gson;
import compa.daos.FriendshipDAO;
import compa.daos.UserDAO;
import compa.dtos.FriendshipDTO;
import compa.dtos.UserDTO;
import compa.models.Friendship;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import compa.models.Location;
import compa.models.User;

import java.util.ArrayList;
import java.util.List;

public class FakeController extends Controller{
    private static final String PREFIX = "/fake";

    FriendshipDAO friendshipDAO;
    UserDAO userDAO;

    public FakeController(Container container) {
        super(PREFIX, container);
        this.registerAuthRoute(HttpMethod.GET, "/friend", this::getFriends, "application/json");
        this.registerRoute(HttpMethod.GET, "/user", this::getFakeUser, "application/json");
        this.registerRoute(HttpMethod.GET, "/location/:id", this::getFakeLocation, "application/json");
        friendshipDAO = (FriendshipDAO) container.getDAO(Friendship.class);
        userDAO = (UserDAO) container.getDAO(User.class);
    }

    /**
     *
     * @param routingContext
     */
    private void getFriends(User me, RoutingContext routingContext) {
        List<Friendship> friendships = friendshipDAO.findAll();
        List<FriendshipDTO> friends = friendshipDAO.toDTO(friendships);
        routingContext.response().end(gson.toJson(friends));
    }

    /**
     * @api {get} /location Get fake data location
     * @param routingContext
     */
    private void getFakeLocation(RoutingContext routingContext) {
        ArrayList<Location> fakeLocation = null;
        routingContext.response().end(new Gson().toJson(fakeLocation));
    }

    /**
     *
     * @param routingContext
     */
    private void getFakeUser(RoutingContext routingContext) {
        User fakeUser = null;
        routingContext.response().end(new Gson().toJson(fakeUser));
    }

}
