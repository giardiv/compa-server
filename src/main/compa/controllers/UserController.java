package main.compa.controllers;

import com.google.gson.Gson;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import main.compa.app.Controller;
import main.compa.app.ModelManager;
import main.compa.models.User;
import main.compa.daos.UserDAO;
import org.apache.commons.lang3.RandomStringUtils;

public class UserController extends Controller {

    private UserDAO userDAO;

    public UserController(Router router, ModelManager modelManager){
        super(router);
        this.registerRoute(HttpMethod.POST, "/login", this::login, "application/json");
        userDAO = (UserDAO) modelManager.getDAO(User.class);
    }

    private void login(RoutingContext routingContext){
        String login = routingContext.request().getParam("login");
        String password = routingContext.request().getParam("password");
        String token = checkAuth(login, password);
        Object content = token == null ? "error " : token; //TODO DEFINE STRUCTURE OF RETURNED JSON
        routingContext.response().end(new Gson().toJson(content));
    }

    //TODO MOVE IT ELSEWHERE
    private String checkAuth(String login, String password){
            User user = userDAO.getByLoginAndPassword(login, password);
            if(user == null)
                return null;

            String token =  RandomStringUtils.random(16);
            user.setToken(token);
            return token;
    }
}