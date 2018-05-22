package compa.services;

import com.google.gson.JsonObject;
import compa.exception.LoginException;
import compa.helpers.CipherSecurity;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import compa.app.Container;
import compa.app.Service;
import compa.daos.UserDAO;
import compa.models.User;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.SecureRandom;

public class AuthenticationService extends Service {
    private UserDAO userDAO;

    public AuthenticationService(Container container){
        super(container);
        userDAO = (UserDAO) container.getDAO(User.class);
    }

    public void checkAuth(HttpServerRequest request, Handler<AsyncResult<User>> resultHandler){
        String token = request.getHeader("token");

        if(token == null) {
            Future<User> f = Future.failedFuture(new LoginException(LoginException.INCORRECT_TOKEN));
            f.setHandler(resultHandler);
        } else {
            ((UserDAO) container.getDAO(User.class)).findOne("token", token, res -> {
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

    public static String encrypt(String rawPassword, byte[] salt)
    {
        int iterations = 1000;
        char[] chars = rawPassword.toCharArray();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return iterations + ":" + toHex(salt) + ":" + toHex(hash);
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] getSalt()
    {
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            byte[] salt = new byte[16];
            sr.nextBytes(salt);
            return salt;
        }catch (Exception e){
            return null;
        }
    }

    private static String toHex(byte[] array)
    {
        try {
            BigInteger bi = new BigInteger(1, array);
            String hex = bi.toString(16);
            int paddingLength = (array.length * 2) - hex.length();
            if (paddingLength > 0) {
                return String.format("%0" + paddingLength + "d", 0) + hex;
            } else {
                return hex;
            }
        } catch (Exception e){
            return null;
        }
    }
}
