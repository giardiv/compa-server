package controllers;

import app.Controller;
import com.google.gson.Gson;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import models.Friendship;
import models.Location;
import models.User;

import java.util.ArrayList;

public class FakeControler extends Controller{
    private static final String PREFIX = "/fake";

    public FakeControler(Router router) {
        super(PREFIX, router);
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
