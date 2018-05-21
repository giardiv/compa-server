package compa.daos;

import compa.app.MongoUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import compa.app.Container;
import compa.exception.RegisterException;
import compa.models.User;
import compa.app.DAO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO extends DAO<User, ObjectId> {

    public static final int PASSWORD_MIN_LENGTH = 8;
    private Logger logger = Logger.getLogger("user_dao");

    public UserDAO(Container container){
        super(User.class, container);
    }

    public void getByLoginAndPassword(String login, String password, Handler<AsyncResult<User>> resultHandler){
        vertx.executeBlocking( future -> {
            future.complete(this.createQuery().filter("login", login).filter("password", password).get());
        }, resultHandler);
    }

    public void addUser(String login, String password, Handler<AsyncResult<User>> resultHandler) {

        vertx.executeBlocking( future -> {
            User user = this.createQuery().filter("login", login).get();

            if(password.length() < PASSWORD_MIN_LENGTH)
                future.fail(new RegisterException(RegisterException.PASSWORD_TOO_SHORT));

            if(user != null)
                future.fail(new RegisterException(RegisterException.USER_ALREADY_EXIST));

            user = new User(login, password);
            this.save(user);
            future.complete(user);

        }, resultHandler);

    }

    public void findOne(String key, Object value, Handler<AsyncResult<User>> resultHandler){

        vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "Looking for user by key {0} and value {1}", new Object[]{key, value});
            User u = super.findOne(key, value);
            logger.log(Level.INFO, "User {0} found", u == null ? "not" : "");
            future.complete(u);

        }, resultHandler);

    }

    public void findById(String id, Handler<AsyncResult<User>> resultHandler) {

        vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "Looking for user {0}", id);
            User u = super.findById(id);
            logger.log(Level.INFO, "User {0} found", u == null ? "not" : "");
            future.complete(u);
        }, resultHandler);

    }

    public void updatePassword(User user, String newPassword, Handler<AsyncResult<User>> resultHandler ){

        vertx.executeBlocking( future -> {
            UpdateOperations<User> update = this.createUpdateOperations().set("password", newPassword);
            this.getDatastore().update(user, update);
            future.complete(user);
        }, resultHandler);

    }


}
