package compa.controllers;

import compa.app.Container;
import compa.app.Controller;
import com.google.gson.Gson;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import compa.models.Friendship;
import compa.models.Location;
import compa.models.User;

import java.util.ArrayList;

public class FakeController extends Controller{
    private static final String PREFIX = "/fake";

    public FakeController(Container container) {
        super(PREFIX, container);
        this.registerRoute(HttpMethod.GET, "/friend", this::addFakeFriend, "application/json");
        this.registerRoute(HttpMethod.GET, "/user", this::getFakeUser, "application/json");
        this.registerRoute(HttpMethod.GET, "/location/:id", this::getFakeLocation, "application/json");
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

    /**
     *
     * @param routingContext
     */
    private void addFakeFriend(RoutingContext routingContext) {
        ArrayList<Friendship> fakeFriends = null;
        routingContext.response().end(new Gson().toJson(fakeFriends));
    }

}
