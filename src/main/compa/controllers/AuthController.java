package compa.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import compa.exception.LoginException;
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
        GsonService gson = (GsonService) this.get(GsonService.class);

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
            // TODO: first check if user exist
            User u = res.result();
            JsonObject content = new JsonObject();
            if(u != null){
                u.setToken();
                routingContext.response().end(
                        new Gson().toJson(
                                AuthenticationService.getJsonFromToken(u.getToken())));
            }
            else{
                content.addProperty("error", "invalid");
                routingContext.response().setStatusCode(400).end(
                        new Gson().toJson(
                                new LoginException(LoginException.INCORRECT_CREDENTIALS)));
            }
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
        AuthenticationService AS = (AuthenticationService) this.get(AuthenticationService.class);
        GsonService gson = (GsonService) this.get(GsonService.class);

        String  login = null;
        String password = null;
        try {
            login = (String) this.getParam(routingContext, "login", true, paramMethod.JSON, String.class);
            password = (String) this.getParam(routingContext, "password", true, paramMethod.JSON, String.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        // TODO
        byte[] salt = AuthenticationService.getSalt();
        String encryptedPassword = AuthenticationService.encrypt(password, salt);
        System.out.println(encryptedPassword);

        userDAO.addUser(login, password, salt.toString(), res -> {
            if(res.failed()){
                // TODO: log
                System.out.println("fail");
                routingContext.response().end(gson.toJson(res.cause()));
                return;
            } else {
                // TODO: log
                System.out.println("ok");
                User user = res.result();
                routingContext.response().end(gson.toJson(AS.getJsonFromToken(user.getToken())));
                return;
            }
        });
    }

    private void updatePassword(User me, RoutingContext routingContext) {

        //CipherSecurity cipherUtil = (CipherSecurity) this.get(CipherSecurity.class);

        if(!this.checkParams(routingContext, "new_password")){ //TODO ADD ADDITIONAL CHECK: USER NEEDS TO GIVE CURRENT PASSWORD
            routingContext.response().end("missing param"); //TODO FORMAT
            return;
        }

        String newPassword = routingContext.request().getParam("new_password");
        String encryptedNewPassword = "";//cipherUtil.encrypt(newPassword);

        userDAO.updatePassword(me, encryptedNewPassword, res -> {
            User u = res.result();
            routingContext.response().end("updated"); //TODO FORMAT
        });
    }
}
