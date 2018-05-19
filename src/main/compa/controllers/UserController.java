package main.compa.controllers;

import com.google.gson.Gson;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import main.compa.app.Container;
import main.compa.app.Controller;
import main.compa.models.User;
import main.compa.daos.UserDAO;
import main.compa.exception.RegisterException;

public class UserController extends Controller {

    private static final String PREFIX = "/user";

    private UserDAO userDAO;

    public UserController(Container container){
        super(PREFIX, container);
        this.registerRoute(HttpMethod.POST, "/login", this::login, "application/json");
        this.registerRoute(HttpMethod.POST, "/register", this::register, "application/json");
        userDAO = (UserDAO) container.getDAO(User.class);
    }

    /**
     * @api {post} /user/login Get token from user / password
     * @apiName Login
     * @apiGroup User
     *
     * @apiParam {String} login      User's email
     * @apiParam {String} password   Users's raw password
     *
     * @apiUse IncorrectCredentials
     */
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

        //Token token = new Token();
        //user.addToken(token);

        return null;
        //return token;
    }

    /**
     * @api {post} /user/register Register a new user
     * @apiName Register
     * @apiGroup User
     *
     * @apiParam {String} login      User's email
     * @apiParam {String} password   Users's raw password
     *
     * @apiUse UserAlreadyExist
     * @apiUse PasswordTooShort
     *
     * @apiSuccess {String} Token    Token is returned
     */
    private void register(RoutingContext routingContext){
        System.out.println("In register");
        // TODO : manage null values > return bad request
        String login = routingContext.request().getParam("login");
        String password = routingContext.request().getParam("password");
        try {
            User user = userDAO.addUser(login, password);
            //Token token = new Token();
            //user.addToken(token);
            userDAO.save(user);
            routingContext.response().end(new Gson().toJson(user.getToken()));
        } catch (RegisterException e) {
            routingContext.response().setStatusCode(418).end(new Gson().toJson(e));
        }
    }

}