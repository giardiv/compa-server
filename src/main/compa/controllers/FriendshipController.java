package main.compa.controllers;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import main.compa.app.Controller;
import main.compa.app.ModelManager;
import main.compa.daos.FriendshipDAO;

public class FriendshipController extends Controller{
    private static final String PREFIX = "/friendship";

    private FriendshipDAO friendshipDAO;

    public FriendshipController(Router router, ModelManager modelManager) {
        super(PREFIX, router);
        this.registerRoute(HttpMethod.GET, "/", this::test, "application/json");
    }

    private void test(RoutingContext routingContext){

    }
}
