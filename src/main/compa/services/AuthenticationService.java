package compa.services;

import com.google.gson.JsonObject;
import compa.exception.LoginException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import compa.app.Container;
import compa.app.Service;
import compa.daos.UserDAO;
import compa.models.User;
import org.mindrot.jbcrypt.BCrypt;

public class AuthenticationService extends Service {
    private final static int SALT_ROUND = 12;
    public static final int PASSWORD_MIN_LENGTH = 8;

    private UserDAO userDAO;

    public AuthenticationService(Container container){
        super(container);
        userDAO = (UserDAO) container.getDAO(User.class);
    }

    public void checkAuth(HttpServerRequest request, Handler<AsyncResult<User>> resultHandler){
        String token = request.getHeader("Authorization");

        if(token == null) {
            Future<User> f = Future.failedFuture(new LoginException(LoginException.INCORRECT_TOKEN));
            f.setHandler(resultHandler);
        } else {
            userDAO.findOne("token", token, res -> {
                User u = res.result();
                Future<User> f = (res.result() == null)
                        ? Future.failedFuture(new LoginException(LoginException.INCORRECT_TOKEN))
                        : Future.succeededFuture(u);
                f.setHandler(resultHandler);
            });
        }
    }

    public static JsonObject getJsonFromToken(String token){
        JsonObject content = new JsonObject();
        content.addProperty("token", token);
        return content;
    }

    public static String encrypt(String rawPassword, String salt)
    {
        return BCrypt.hashpw(rawPassword, salt);
    }

    public static String getSalt()
    {
        return BCrypt.gensalt(SALT_ROUND);
    }

    public static boolean isNotAcceptablePassword(String rawPassword){
        return rawPassword.length() < PASSWORD_MIN_LENGTH;
    }
}
