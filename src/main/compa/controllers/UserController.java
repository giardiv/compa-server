package compa.controllers;

import com.google.gson.Gson;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import main.compa.app.*;
import main.compa.models.Friendship;
import main.compa.models.User;
import main.compa.daos.UserDAO;
import main.compa.exception.RegisterException;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.Date;

import compa.services.GsonService;


public class UserController extends Controller {

    private static final String PREFIX = "/user";

    private UserDAO userDAO;

    public UserController(Container container){
        super(PREFIX, container);
        this.registerRoute(HttpMethod.POST, "/login", this::login, "application/json");
        this.registerRoute(HttpMethod.POST, "/register", this::register, "application/json");
        this.registerRoute(HttpMethod.GET, "/updatePassword", this::updatePWD, "application/json"); //auth route probs
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

        String login = routingContext.request().getParam("login");
        String password = routingContext.request().getParam("password");
        CipherSecurity cipherUtil = new CipherSecurity();
        String encryptedString = cipherUtil.encrypt(password + String.valueOf(new Date().getTime()));
        
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


    private void updatePWD(RoutingContext routingContext) {
    
        //to asyncify + some of the code needs to be moved in the dao
        CipherSecurity cipherUtil = new CipherSecurity();

        //TODO : change login to user_id
        String login = routingContext.request().getParam("login");
        String password = routingContext.request().getParam("password");
        String encryptedPWD = cipherUtil.encrypt(password);

        String newPwd = routingContext.request().getParam("NewPassword");
        String encryptedNewPWD = cipherUtil.decrypt(newPwd);

        Query<User> query = MongoUtil.getDatastore().find(User.class);
        query.or(
                query.criteria("login").equal(login),
                query.criteria("password").equal(encryptedPWD)
        );
        if(query != null){
            UpdateOperations<User> update = MongoUtil.getDatastore().createUpdateOperations(User.class).set("password", encryptedNewPWD);
            MongoUtil.getDatastore().update(query, update);
        }
        System.out.println("query  : " + query);
        routingContext.response().setStatusCode(418).end();
    }

}