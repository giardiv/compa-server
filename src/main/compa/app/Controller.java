package compa.app;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;

import io.vertx.ext.web.RoutingContext;
import compa.models.User;
import compa.services.AuthenticationService;
import compa.services.GsonService;


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

    protected void registerAuthRoute(HttpMethod method, String route,
                                     AuthenticatedHandler<User, RoutingContext> handler, String produces){

        container.getRouter().route(method,prefix + route).produces(produces).handler(context -> {

            ((AuthenticationService) this.get(AuthenticationService.class)).checkAuth(context.request(), res -> {

                if(res.failed()){
                    GsonService gson = (GsonService) this.get(GsonService.class);
                    context.response().end(gson.toJson(res.cause()));
                }
                else{
                    handler.handle(res.result(), context);
                }
            });

        });

    }

    protected Service get(Class service){
        return container.getServices().get(service);
    }

    public boolean checkParams(RoutingContext context, String... mandatoryParams){
        for(String s : mandatoryParams)
            if(context.request().getParam(s) == null && context.request().getFormAttribute(s) == null){
                return false;
            }
        return true;
    }


}
