package compa.app;

import com.google.gson.JsonObject;
import compa.exception.ParameterException;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;

import io.vertx.core.json.JsonArray;
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
                } else {
                    handler.handle(res.result(), context);
                }
            });

        });

    }

    protected Service get(Class service){
        return container.getServices().get(service);
    }

    // Sarah
    public boolean checkParams(RoutingContext context, String... mandatoryParams) {
        for (String s : mandatoryParams)
            if (context.request().getParam(s) == null && context.request().getFormAttribute(s) == null) {
                return false;
            }
        return true;

    }

    protected enum paramMethod {
        JSON,
        GET
    }

    protected Object getParam(RoutingContext context, String mandatoryParam, boolean required, paramMethod method, Class type) throws ParameterException {
        if(required) {
            Object value = context.getBodyAsJson().getValue(mandatoryParam);
            if(value == null){
                throw new ParameterException(ParameterException.PARAM_REQUIRED, mandatoryParam, method.toString());
            }
            try {
                if(type == Integer.class) {
                    value = Integer.parseInt((String) value);
                } else if (type == String.class) {
                    value = (String) value;
                } else {
                    System.err.println("Unaccepted class : " + type.toString());
                }
            } catch (NumberFormatException e) {
                throw new ParameterException(ParameterException.PARAM_WRONG_FORMAT, (String) value, Integer.class.toString());
            }
            return value;
        } else {
            return null;
        }
    }
}
