package compa.daos;

import compa.app.MongoUtil;
import compa.dtos.UserDTO;
import compa.services.AuthenticationService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import compa.app.Container;
import compa.exception.RegisterException;
import compa.models.User;
import compa.app.DAO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class UserDAO extends DAO<User, ObjectId> {
    private Logger logger = Logger.getLogger("user_dao");

    public UserDAO(Container container){
        super(User.class, container);
    }

    public void getByLoginAndPassword(String login, String password, Handler<AsyncResult<User>> resultHandler){
        vertx.executeBlocking( future -> {
            User u = this.createQuery().filter("login", login).get();
            if(u == null){
                future.complete(null);
                return;
            }
            String encPassword = AuthenticationService.encrypt(password, u.getSalt());
            if(u.isPassword(encPassword)) {
                future.complete(u);
            } else {
                future.complete(null);
            }
        }, resultHandler);
    }

    public void addUser(String email, String name, String login, String password, String salt, Handler<AsyncResult<User>> resultHandler) {
        vertx.executeBlocking( future -> {
            User user = this.createQuery().filter("login", login).get();

            if(user != null) {
                future.fail(new RegisterException(RegisterException.USER_ALREADY_EXIST));
                return;
            }

            user = new User(email, name, login, password, salt);
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
            logger.log(Level.INFO, "User {0}found", u == null ? "not " : "");
            future.complete(u);
        }, resultHandler);

    }

    public void findById(ObjectId id, Handler<AsyncResult<User>> resultHandler) {

        vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "Looking for user {0}", id);
            User u = super.findOne("id", id);
            logger.log(Level.INFO, "User {0}found", u == null ? "not " : "");
            future.complete(u);
        }, resultHandler);

    }

    public void updatePassword(User user, String newEncryptedPassword, Handler<AsyncResult<User>> resultHandler ){

        vertx.executeBlocking( future -> {
            UpdateOperations<User> update = this.createUpdateOperations().set("password", newEncryptedPassword);
            this.getDatastore().update(user, update);
            user.setToken();
            this.save(user);
            future.complete(user);
        }, resultHandler);

    }

    public void setGhostMode(User user, boolean mode, Handler<AsyncResult<User>> resultHandler ){

        vertx.executeBlocking( future -> {
            UpdateOperations<User> update = this.createUpdateOperations().set("ghostMode", mode);
            this.getDatastore().update(user, update);
            future.complete();
        }, resultHandler);

    }

    public UserDTO toDTO(User me){
        return new UserDTO(me);
    }

    public List<UserDTO> toDTO(List<User> users){
        return users.stream().map(UserDTO::new).collect(Collectors.toList());
    }
}
