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
import main.compa.exception.RegisterException;

public class UserController extends Controller {

    private static final String PREFIX = "/user";

    private UserDAO userDAO;

    public UserController(Router router, ModelManager modelManager){
        super(PREFIX, router);
        this.registerRoute(HttpMethod.GET, "/", this::getAll, "application/json");
        this.registerRoute(HttpMethod.POST, "/login", this::login, "application/json");
        this.registerRoute(HttpMethod.POST, "/", this::register, "application/json");
        userDAO = (UserDAO) modelManager.getDAO(User.class);
    }

    // Return 200 ; 418
    private void login(RoutingContext routingContext){
        String login = routingContext.request().getParam("login");
        String password = routingContext.request().getParam("password");
        String token = checkAuth(login, password);
        Object content = token == null ? "error" : token; //TODO DEFINE STRUCTURE OF RETURNED JSON
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

    /**
     * @api {post} /user Add a new user
     * @apiName Register
     * @apiGroup User
     *
     * @apiUse UserAlreadyExist
     * @apiUse PasswordTooShort
     */
    private void register(RoutingContext routingContext){
        // TODO : manage null values > return bad request
        String login = routingContext.request().getParam("login");
        String password = routingContext.request().getParam("password");
        try{
            userDAO.addUser(login, password);
            routingContext.response().end();
        } catch (RegisterException e) {
            routingContext.response().setStatusCode(418).end(new Gson().toJson(e));
        }
    }

    /**
     * @api {get} /user Get all users
     * @apiName GetUsers
     * @apiGroup User
     */
    // Return 200
    private void getAll(RoutingContext routingContext){
        routingContext.response().end(new Gson().toJson(userDAO.findAll()));
    }

    /**
     * @api {get} /user/:id Request User information
     * @apiName GetUser
     * @apiGroup User
     */
    // Return 200 ; 404
    private void getUser(RoutingContext routingContext){
        routingContext.response().end();
    }

    private void getFriendships(RoutingContext routingContext){

    }
}