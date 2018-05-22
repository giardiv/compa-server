package compa.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import compa.services.CipherSecurity;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import compa.app.*;
import compa.models.User;
import compa.daos.UserDAO;

import compa.services.GsonService;


public class UserController extends Controller {

    private static final String PREFIX = "/user";

    private UserDAO userDAO;

    public UserController(Container container){
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
        String login = routingContext.request().getParam("login");
        String password = routingContext.request().getParam("password");

        userDAO.getByLoginAndPassword(login, password, res -> {
            User u = res.result();
            JsonObject content = new JsonObject();
            if(u != null){
                String token = u.getToken();
                if(token == null){
                    content.addProperty("error", "invalid");
                }
                else{
                    content.addProperty("token", token);
                }
            }
            else{
                content.addProperty("error", "invalid");
            }
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

        String login = routingContext.request().getParam("login");
        String password = routingContext.request().getParam("password");
        CipherSecurity cipherUtil = (CipherSecurity) this.get(CipherSecurity.class);
        String encryptedPassword = cipherUtil.encrypt(password);
        
        userDAO.addUser(login, encryptedPassword, res -> {
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


    private void updatePassword(User me, RoutingContext routingContext) {

        CipherSecurity cipherUtil = (CipherSecurity) this.get(CipherSecurity.class);

        if(!this.checkParams(routingContext, "new_password")){ //TODO ADD ADDITIONAL CHECK: USER NEEDS TO GIVE CURRENT PASSWORD
            routingContext.response().end("missing param"); //TODO FORMAT
            return;
        }

        String newPassword = routingContext.request().getParam("new_password");
        String encryptedNewPassword = cipherUtil.encrypt(newPassword);

        userDAO.updatePassword(me, encryptedNewPassword, res -> {
            User u = res.result();
            routingContext.response().end("updated"); //TODO FORMAT
        });

    }

}
