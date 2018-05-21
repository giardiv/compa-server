package compa.controllers;

import com.google.gson.Gson;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import compa.app.Container;
import compa.app.Controller;
import compa.models.User;
import compa.daos.UserDAO;
import compa.exception.RegisterException;
import compa.services.GsonService;

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

        userDAO.getByLoginAndPassword(login, password, res -> {
            User u = res.result();
            String token = u.getToken();
            Object content = token == null ? "error" : token; //TODO DEFINE STRUCTURE OF RETURNED JSON
            routingContext.response().end(new Gson().toJson(content));
        });


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

        GsonService gson = (GsonService) this.get(GsonService.class);

        if(!this.checkParams(routingContext, "login", "password")) {
            routingContext.response().end("missing param"); //TODO FORMAT
            return;
        }

        String login = routingContext.request().getParam("login");
        String password = routingContext.request().getParam("password");

        userDAO.addUser(login, password, res -> {
            if(res.failed()){
                routingContext.response().end(gson.toJson(res.cause()));
            }
            else{
                User user = res.result();
                userDAO.save(user);
                routingContext.response().end(gson.toJson(user.getToken()));
            }
        });

    }

}