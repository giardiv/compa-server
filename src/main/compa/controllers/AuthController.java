package compa.controllers;

import compa.exception.LoginException;
import compa.exception.RegisterException;
import compa.exception.UserException;
import compa.services.AuthenticationService;
import compa.exception.ParameterException;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

import compa.app.*;
import compa.models.User;
import compa.daos.UserDAO;
import static compa.email.SendEmail.sendEmail;

import org.apache.commons.lang3.RandomStringUtils;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.apache.commons.validator.routines.EmailValidator;

public class AuthController extends Controller {

    private static final String PREFIX = "";
    private final static int PASSWORD_COUNT = 10;


    private UserDAO userDAO;

    public AuthController(Container container){
        super(PREFIX, container);
        this.registerRoute(HttpMethod.POST, "/login", this::login, "application/json");
        this.registerRoute(HttpMethod.POST, "/register", this::register, "application/json");
        this.registerAuthRoute(HttpMethod.PUT, "/updatePassword", this::updatePassword, "application/json");
        this.registerAuthRoute(HttpMethod.PUT, "/logout", this::logout, "application/json");
        this.registerRoute(HttpMethod.POST, "/forgotPassword", this::forgotPassword, "application/json");

        userDAO = (UserDAO) container.getDAO(User.class);
    }

    /**
     * @api {post} /login Get token from user / password
     * @apiName Login
     * @apiGroup Auth
     *
     * @apiParam {String} login      User's email
     * @apiParam {String} password   Users's raw password
     *
     * @apiUse IncorrectCredentials
     *
     * @apiSuccess {String} Token    Token is returned
     */
    private void login(RoutingContext routingContext){
        String login, password;

        try {
            login = this.getParam(routingContext, "login", true, ParamMethod.JSON, String.class);
            password = this.getParam(routingContext, "password", true, ParamMethod.JSON, String.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        userDAO.getByLoginAndPassword(login, password, res -> {
            User u = res.result();
            if(u != null){
                u.generateToken();
                userDAO.save(u);
                routingContext.response().end(gson.toJson(
                                AuthenticationService.getJsonFromToken(u.getToken())));
            } else {
                routingContext.response().setStatusCode(400).end(gson.toJson(
                                new LoginException(LoginException.INCORRECT_CREDENTIALS)));
            }
        });
    }

    /**
     * @api {post} /register Register a new user
     * @apiName Register
     * @apiGroup Auth
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
        final String name, email, login, password;

        try {
            name = this.getParam(routingContext, "name", true, ParamMethod.JSON, String.class);
            email = this.getParam(routingContext, "email", true, ParamMethod.JSON, String.class);
            login = this.getParam(routingContext, "login", true, ParamMethod.JSON, String.class);
            password = this.getParam(routingContext, "password", true, ParamMethod.JSON, String.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        if(!EmailValidator.getInstance(true).isValid(email)){
            routingContext.response().setStatusCode(400).end(gson.toJson(
                            new RegisterException(RegisterException.NOT_VALID_EMAIL)));
            return;
        }

        if(AuthenticationService.isNotAcceptablePassword(password)){
            routingContext.response().setStatusCode(400).end(gson.toJson(
                            new RegisterException(compa.exception.RegisterException.PASSWORD_TOO_SHORT)));
            return;
        }

        String salt = AuthenticationService.getSalt();
        String encryptedPassword = AuthenticationService.encrypt(password, salt);

        userDAO.addUser(email, name, login, encryptedPassword, salt, res -> {
            if(res.failed()){
                System.out.println("fail");
                routingContext.response().setStatusCode(400).end(gson.toJson(res.cause()));
                return;
            } else {
                User user = res.result();
                System.out.println("ok");
                sendEmail(email,"titre", "message sans pièce joint", res1 -> {
                    if(res1!=null)
                        System.out.println("email Ok");
                });
                routingContext.response().end(gson.toJson(
                        AuthenticationService.getJsonFromToken(user.getToken())));
            }
        });
    }

    /**
     * @api {post} /updatePassword Update the password
     * @apiName Update Password
     * @apiGroup Auth
     *
     * @apiParam {String} password   Users's raw password
     *
     * @apiUse PasswordTooShort
     *
     * @apiSuccess {String} Token    A new token is returned
     */
    private void updatePassword(User me, RoutingContext routingContext) {
        String password, newPassword;

        try {
            newPassword = this.getParam(routingContext, "new_password", true, ParamMethod.JSON, String.class);
            password = this.getParam(routingContext, "password", true, ParamMethod.JSON, String.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        if(!me.isPassword(AuthenticationService.encrypt(password, me.getSalt()))){
            routingContext.response().setStatusCode(400).end(gson.toJson(new LoginException(LoginException.INCORRECT_CREDENTIALS)));
            return;
        }

        if(AuthenticationService.isNotAcceptablePassword(newPassword)){
            routingContext.response().setStatusCode(400).end(
                    gson.toJson(
                            new RegisterException(compa.exception.RegisterException.PASSWORD_TOO_SHORT)));
            return;
        }
        String encryptedNewPassword = AuthenticationService.encrypt(newPassword, me.getSalt());

        if(password == encryptedNewPassword){
            routingContext.response().setStatusCode(400).end(
                    gson.toJson(
                            new RegisterException(RegisterException.SAME_PASSWORD)));
            return;
        }

        userDAO.updatePassword(me, encryptedNewPassword, res -> {
            User u = res.result();
            routingContext.response().end(gson.toJson(AuthenticationService.getJsonFromToken(u.getToken())));
        });
    }

    /**
     * @api {put} /logout Logout, put token to null
     * @apiName Logout
     * @apiGroup Auth
     *
     * @apiUse UserAlreadyExist
     *
     * @apiSuccess {String} String    Success true
     */
    public void logout(User me, RoutingContext routingContext) {
        userDAO.logout(me, res -> {
            routingContext.response().end("{}");
        });
    }

    /**
     * @api {post} /forgotPassword Forgot password, send a new password
     * @apiName Forgot Password
     * @apiGroup Auth
     *
     * @apiParam {String} email   Users's raw email
     *
     * @apiUse USER_NOT_FOUND
     * @apiSuccess {String} email    A new password is send by email
     */
    public void forgotPassword(RoutingContext routingContext){

        String email;
        try {
            email = this.getParam(routingContext, "email", true, ParamMethod.JSON, String.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        userDAO.findOne("email",email, res -> {
            User user = res.result();
            if(user != null){
                String newPassword = RandomStringUtils.randomAlphanumeric(PASSWORD_COUNT);
                sendEmail(email,"Forgot Password", "New password : " + newPassword, res1 -> {
                    if(res1!=null) {
                        System.out.println("email Ok");
                    }
                });

                String encryptedNewPassword = AuthenticationService.encrypt(newPassword, user.getSalt());

                userDAO.updatePassword(user, encryptedNewPassword, res2 -> {
                    User u = res2.result();
                });

                routingContext.response().end("{}");
            } else {
                routingContext.response().setStatusCode(404).end(gson.toJson(
                        new UserException(UserException.USER_NOT_FOUND, "email", email)));
            }
        });
    }
}
