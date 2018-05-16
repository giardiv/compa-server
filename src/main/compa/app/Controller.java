package main.compa.app;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.List;

public abstract class Controller {
    private String prefix;
    private List<Route> routes;
    private Router router;

    public Controller(String prefix, Router router){
        this.prefix = prefix;
        this.routes = new ArrayList<>();
        this.router = router;
    }

    public Controller(Router router) {
        this("", router);
    }

    protected void registerRoute(HttpMethod method, String route, Handler<RoutingContext> handler, String produces){
        this.routes.add(router.route(method,prefix + route).produces(produces).handler(handler));
    }
}
