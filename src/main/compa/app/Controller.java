package compa.app;

import compa.exception.ParameterException;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;

import io.vertx.ext.web.RoutingContext;
import compa.models.User;
import compa.services.AuthenticationService;
import compa.services.GsonService;


public abstract class Controller {

    private String prefix;
    private Container container;
    protected GsonService gson;

    public Controller(String prefix, Container container){
        this.prefix = prefix;
        this.container = container;
        this.gson = (GsonService) this.get(GsonService.class);
    }

    protected void registerRoute(HttpMethod method, String route, Handler<RoutingContext> handler, String produces){
        container.getRouter().route(method,prefix + route).produces(produces).handler(handler);
    }

    protected void registerAuthRoute(HttpMethod method, String route,
                                     AuthenticatedHandler<User, RoutingContext> handler, String produces){

        container.getRouter().route(method,prefix + route).produces(produces).handler(context -> {

            ((AuthenticationService) this.get(AuthenticationService.class)).checkAuth(context.request(), res -> {

                if(res.failed()){
                    context.response().setStatusCode(401).end(gson.toJson(res.cause()));
                } else {
                    handler.handle(res.result(), context);
                }
            });

        });

    }

    protected Service get(Class service){
        return container.getServices().get(service);
    }

    // OUTDATED
    public boolean checkParams(RoutingContext context, String... mandatoryParams) {
        for (String s : mandatoryParams)
            if (context.request().getParam(s) == null && context.request().getFormAttribute(s) == null) {
                return false;
            }
        return true;
    }

    protected enum ParamMethod {
        JSON,
        GET
    }

    protected Object getParam(RoutingContext context, String mandatoryParam, boolean required, ParamMethod method, Class type) throws ParameterException {
        if(required) {

            Object value = method == ParamMethod.JSON ?
                    context.getBodyAsJson().getValue(mandatoryParam) :
                    context.request().getParam(mandatoryParam);

          if(value == null){
                throw new ParameterException(ParameterException.PARAM_REQUIRED, mandatoryParam, method.toString());
            }
            try {
                if(type == Integer.class) {
                    value = Integer.parseInt((String) value);
                } else if (type == String.class) {
                    value = (String) value;
                } else if (type == Boolean.class){
                    value = (boolean) value;
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
