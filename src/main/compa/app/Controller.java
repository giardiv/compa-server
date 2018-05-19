package main.compa.app;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import main.compa.models.User;

import java.util.ArrayList;
import java.util.List;

public abstract class Controller {
    private ServiceManager serviceManager;

    private String prefix;
    private List<Route> routes;
    private Router router;

    public Controller(ServiceManager serviceManager, String prefix, Router router){
        this.serviceManager = serviceManager;
        this.prefix = prefix;
        this.routes = new ArrayList<>();
        this.router = router;
    }

    protected void registerRoute(HttpMethod method, String route, Handler<RoutingContext> handler, String produces){
        this.routes.add(router.route(method,prefix + route).produces(produces).handler(handler));
    }

    private Service get(String name){
        return serviceManager.get(name);
    }

    public boolean checkParams(RoutingContext context, String[] mandatoryParams){
        for(String s : mandatoryParams)
            if(context.request().getParam(s) == null) //TODO CHECK IT ACTUALLY RETURNS NULL WHEN NON-EXISTENT...
                return false;
        return true;
    }
}
