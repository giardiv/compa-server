package compa.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import compa.exception.LoginException;
import compa.exception.RegisterException;
import compa.services.AuthenticationService;
import compa.helpers.CipherSecurity;
import compa.exception.ParameterException;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

import compa.app.*;
import compa.models.User;
import compa.daos.UserDAO;

import compa.services.GsonService;


public class AuthController extends Controller {

    private static final String PREFIX = "";

    private UserDAO userDAO;

    public AuthController(Container container){
        super(PREFIX, container);
        this.registerRoute(HttpMethod.POST, "/login", this::login, "application/json");
        this.registerRoute(HttpMethod.POST, "/register", this::register, "application/json");
        this.registerAuthRoute(HttpMethod.PUT, "/updatePassword", this::updatePassword, "application/json"); //auth route probs
        userDAO = (UserDAO) container.getDAO(User.class);
    }

    /**
     * @api {post} /login Get token from user / password
     * @apiName Login
     * @apiGroup User
     *
     * @apiParam {String} login      User's email
     * @apiParam {String} password   Users's raw password
     *
     * @apiUse IncorrectCredentials
     *
     * @apiSuccess {String} Token    Token is returned
     */
    private void login(RoutingContext routingContext){
        String login = null;
        String password = null;
        try {
            login = (String) this.getParam(routingContext, "login", true, paramMethod.JSON, String.class);
            password = (String) this.getParam(routingContext, "password", true, paramMethod.JSON, String.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        userDAO.getByLoginAndPassword(login, password, res -> {
            User u = res.result();
            if(u != null){
                u.setToken();
                routingContext.response().end(
                        gson.toJson(
                                AuthenticationService.getJsonFromToken(u.getToken())));
            } else {
                routingContext.response().setStatusCode(400).end(
                        gson.toJson(
                                new LoginException(LoginException.INCORRECT_CREDENTIALS)));
            }
        });
    }

    /**
     * @api {post} /register Register a new user
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
        String  login = null;
        String password = null;
        try {
            login = (String) this.getParam(routingContext, "login", true, paramMethod.JSON, String.class);
            password = (String) this.getParam(routingContext, "password", true, paramMethod.JSON, String.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        if(!AuthenticationService.isAcceptablePassword(password)){
            routingContext.response().setStatusCode(400).end(
                    gson.toJson(
                            new RegisterException(compa.exception.RegisterException.PASSWORD_TOO_SHORT)));
            return;
        }

        String salt = AuthenticationService.getSalt();
        String encryptedPassword = AuthenticationService.encrypt(password, salt);

        userDAO.addUser(login, encryptedPassword, salt, res -> {
            if(res.failed()){
                // TODO: log
                System.out.println("fail");
                routingContext.response().end(gson.toJson(res.cause()));
                return;
            } else {
                // TODO: log
                System.out.println("ok");
                // TODO: send mail ?
                User user = res.result();
                routingContext.response().end(gson.toJson(AuthenticationService.getJsonFromToken(user.getToken())));
                return;
            }
        });
    }

    /**
     * @api {post} /updatePassword Update the password
     * @apiName Update Password
     * @apiGroup User
     *
     * @apiParam {String} password   Users's raw password
     *
     * @apiUse PasswordTooShort
     *
     * @apiSuccess {String} Token    A new token is returned
     */
    private void updatePassword(User me, RoutingContext routingContext) {
        String password = null;
        try {
            password = (String) this.getParam(routingContext, "new_password", true, paramMethod.JSON, String.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        if(!me.isPassword(AuthenticationService.encrypt(password, me.getSalt()))){
            routingContext.response().setStatusCode(400).end(gson.toJson(new LoginException(LoginException.INCORRECT_CREDENTIALS)));
            return;
        }

        if(!AuthenticationService.isAcceptablePassword(password)){
            routingContext.response().setStatusCode(400).end(
                    gson.toJson(
                            new RegisterException(compa.exception.RegisterException.PASSWORD_TOO_SHORT)));
            return;
        }

        String encryptedNewPassword = AuthenticationService.encrypt(password, me.getSalt());

        userDAO.updatePassword(me, encryptedNewPassword, res -> {
            User u = res.result();
            routingContext.response().end(gson.toJson(AuthenticationService.getJsonFromToken(u.getToken()))); //TODO FORMAT
        });
    }
}
