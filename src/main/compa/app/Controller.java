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

    // Sarah
    public boolean checkParams(RoutingContext context, String... mandatoryParams) {
        for (String s : mandatoryParams)
            if (context.request().getParam(s) == null && context.request().getFormAttribute(s) == null) {
                return false;
            }
        return true;

    }

    private String checkParam(String param, String name) throws ParameterException {
        if(param == null){
            System.out.println(param);
            throw new ParameterException(ParameterException.PARAM_REQUIRED, name);
        }
        return param;
    }

    protected int getParam(RoutingContext context, String mandatoryParam) throws ParameterException {
        System.out.println(mandatoryParam);
        System.out.println(context.request().getParam("login"));
        String param = checkParam(context.request().getParam(mandatoryParam), mandatoryParam);
        int value;
        try {
            value = Integer.parseInt(param);
        } catch (NumberFormatException e){
            throw new ParameterException(ParameterException.PARAM_WRONG_FORMAT, param, Integer.class.toString());
        }
        return value;
    }

    protected String getParam(){
        return "";
    }

}
