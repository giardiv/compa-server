package main.compa.app;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import main.compa.models.User;

import java.util.ArrayList;
import java.util.List;

public abstract class Controller {

    private String prefix;
    private Container container;

    public Controller(String prefix, Container container){
        this.prefix = prefix;
        this.container = container;
    }

    protected void registerRoute(HttpMethod method, String route, Handler<RoutingContext> handler, String produces){
        container.getRouter().route(method,prefix + route).produces(produces).handler(handler);
    }

    protected Service get(String name){
        return container.getServices().get(name);
    }

    public boolean checkParams(RoutingContext context, String[] mandatoryParams){
        for(String s : mandatoryParams)
            if(context.request().getParam(s) == null) //TODO CHECK IT ACTUALLY RETURNS NULL WHEN NON-EXISTENT...
                return false;
        return true;
    }


}
