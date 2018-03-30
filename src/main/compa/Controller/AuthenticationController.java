package main.compa.Controller;

import com.google.gson.Gson;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import main.compa.App.Controller;
import main.compa.Model.User;

public class AuthenticationController extends Controller {

    public AuthenticationController(){
        super();
        this.registerRoute(HttpMethod.POST, "/login", this::login, "application/json");
    }


    private void login(RoutingContext routingContext){
        String login = routingContext.request().getParam("login");
        String password = routingContext.request().getParam("password");
        String token = User.checkAuth(login, password);
        Object content = token == null ? "error " : token; //TODO DEFINE STRUCTURE OF RETURNED JSON
        routingContext.response().end(new Gson().toJson(content));
    }
}