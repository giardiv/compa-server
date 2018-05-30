package compa.app;

import compa.exception.ParameterException;
import compa.models.Friendship;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;

import io.vertx.ext.web.RoutingContext;
import compa.models.User;
import compa.services.AuthenticationService;
import compa.services.GsonService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


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

    protected enum ParamMethod {
        JSON,
        GET
    }

    protected <T> T getParam(RoutingContext context, String mandatoryParam, boolean required, ParamMethod method, Class<T> type) throws ParameterException {

        String value = method == ParamMethod.JSON ?
                (String) context.getBodyAsJson().getValue(mandatoryParam) :
                //considering there is no nesting of objects
                context.request().getParam(mandatoryParam);

        if(required && value == null) {
            throw new ParameterException(ParameterException.PARAM_REQUIRED, mandatoryParam, method.toString());
        }

        if(type.equals(Integer.class)) {
            try {
                return type.cast(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                throw new ParameterException(ParameterException.PARAM_WRONG_FORMAT, value, Integer.class.toString());
            }
        }
        else if (type.equals(String.class)) {
            return type.cast(value);
        }
        else if (type.equals(Boolean.class)) {
            if(value.equals("true"))
                return type.cast(true);
            else if(value.equals("false"))
                return type.cast(false);
            else
                throw new ParameterException(ParameterException.PARAM_WRONG_FORMAT, value, Boolean.class.toString());
        }
        else if(type.equals(Date.class)){
            try{
                return type.cast(new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss").parse(value));
            }
            catch(ParseException e){
                throw new ParameterException(ParameterException.PARAM_WRONG_FORMAT, value, Date.class.toString());
            }
        }
        else if(type.equals(Double.class)) {
            try {
                return type.cast(Double.parseDouble(value));
            } catch (NumberFormatException e) {
                throw new ParameterException(ParameterException.PARAM_WRONG_FORMAT, value, Double.class.toString());

            }
        }
        else if(type.equals(Friendship.Status.class)){
            try{
                return (T) Friendship.Status.valueOf(value.toUpperCase());
            }
            catch(IllegalArgumentException e){
                throw new ParameterException(ParameterException.PARAM_WRONG_FORMAT, value, Friendship.Status.class.toString());

            }
        }
        else{
            System.err.println("Unaccepted class : " + type.toString());
            return null;
        }
    }
}
