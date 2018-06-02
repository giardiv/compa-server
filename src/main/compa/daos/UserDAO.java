package compa.daos;

import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
import compa.app.MongoUtil;
import compa.dtos.UserDTO;
import compa.models.Image;
import compa.services.AuthenticationService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import compa.app.Container;
import compa.exception.RegisterException;
import compa.models.User;
import compa.app.DAO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import sun.rmi.server.UnicastServerRef;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static compa.email.SendEmail.sendEmail;

public class UserDAO extends DAO<User, ObjectId> {
    private Logger logger = Logger.getLogger("user_dao");

    public UserDAO(Container container){
        super(User.class, container);
    }

    public void getByLoginAndPassword(String login, String password, Handler<AsyncResult<User>> resultHandler){
        vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "{0} is attempting to log in", login);

            Query<User> query = this.createQuery();

            query.or(
                    query.criteria("email").equal(login),
                    query.criteria("username").equal(login)
            ).and(
                    query.criteria("password").equal(password)
            );
            User u = this.findOne(query);

            if(u == null){
                logger.log(Level.INFO, "False credentials");
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

            Object[] params = new Object[]{login, email};

            logger.log(Level.INFO, "Looking for users with existing login {0} or existing email {1}", params);

            Query<User> query = this.createQuery();
            query.or(
                    query.criteria("email").equal(email),
                    query.criteria("username").equal(login)
            );

            User user = this.findOne(query);
            if(user != null) {
                logger.log(Level.INFO, "User with login {0} or email {1} already exists", params);
                future.fail(new RegisterException(RegisterException.USER_ALREADY_EXIST));
                return;
            }

            logger.log(Level.INFO, "No user with login {0} or email {1}, creating new user", params);
            user = new User(email, name, login, password, salt);
            this.save(user);
            logger.log(Level.INFO, "Created new user");

            future.complete(user);

        }, resultHandler);
    }

    public void findOne(String key, Object value, Handler<AsyncResult<User>> resultHandler){

        vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "Looking for user by {0} : {1} : ",  new Object[]{key, value});
            User u = super.findOne(key, value);
            logger.log(Level.INFO, u == null ? "No user found" : "User found");
            future.complete(u);
        }, resultHandler);

    }

    public void search(String value, Handler<AsyncResult<List<User>>> resultHandler){
        vertx.executeBlocking( future -> {

            logger.log(Level.INFO, "Looking for user containing {0} ", value);
            Query<User> query = this.createQuery();

            query.or(
                    query.criteria("username").contains(value),
                    query.criteria("name").contains(value)
            );

            List<User> users = query.asList();//new FindOptions().limit(30));
            logger.log(Level.INFO, "{0} user(s) found", users.size());
            future.complete(users);

        }, resultHandler);

    }

    public void findById(String id, Handler<AsyncResult<User>> resultHandler) {
        this.findById(new ObjectId(id), resultHandler);
    }

    public void findById(ObjectId id, Handler<AsyncResult<User>> resultHandler) {

        vertx.executeBlocking( future -> {

            logger.log(Level.INFO, "Looking for user with id {0}", id.toString());

            User u = super.findOne("id", id);

            if(u != null)
                logger.log(Level.INFO, "User {0} found", id.toString());
            else
                logger.log(Level.INFO, "User {0} not found", id.toString());

            future.complete(u);

        }, resultHandler);

    }

    public void updatePassword(User user, String newEncryptedPassword, Handler<AsyncResult<User>> resultHandler ){

        vertx.executeBlocking( future -> {

            logger.log(Level.INFO, "Updating {0}'s password", user.getUsername());

            user.generateToken();
            UpdateOperations<User> update = this.createUpdateOperations()
                    .set("password", newEncryptedPassword)
                    .set("token", user.getToken());

            this.getDatastore().update(user, update);

            logger.log(Level.INFO, "Updated {0}'s password", user.getUsername());

            future.complete(user);

        }, resultHandler);

    }
    public void updateProfile(User user, String name, String email, Handler<AsyncResult<User>> resultHandler ){

        vertx.executeBlocking( future -> {
            /*UpdateOperations<User> update = this.createUpdateOperations();
            if(name.equals(user.getName())){
                update.set("name", name);
                user.setName(name);
            }

            if(email != user.getEmail()){
                update.set("email", email);
                user.setEmail(email);
//                sendEmail(email,"titre", "message sans pièce joint", res1 -> {
//                    if(res1!=null)
//                        System.out.println("email Ok");
//                });
            }
            this.getDatastore().update(user, update);*/

            String msg = "The following changes were made to" + user.getUsername() + ":\n";

            if(name != null  && !name.equals(user.getName())){
                user.setName(name);
                msg += user.getName() + ": " + name + "\n";
            }

            if(email != null && !email.equals(user.getEmail())){
                user.setEmail(email);
                msg += user.getEmail() + ": " + email + "\n";
            }

            this.save(user);
            logger.log(Level.INFO, msg);

            future.complete(user);

        }, resultHandler);
    }

    public void setGhostMode(User user, boolean mode, Handler<AsyncResult<User>> resultHandler ){

        Object[] params = new Object[]{user.getUsername(), mode};

        vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "Changing {0}'s ghost mode to {1}", params);
            UpdateOperations<User> update = this.createUpdateOperations().set("ghostMode", mode);
            this.getDatastore().update(user, update);
            logger.log(Level.INFO, "Changed {0}'s ghost mode to {1}", params);
            future.complete();
        }, resultHandler);

    }

    public void setProfilePic(User user, Image image, Handler<AsyncResult<User>> resultHandler ){

        vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "Changing {0}'s profile pic", user.getUsername());
            UpdateOperations<User> update = this.createUpdateOperations().set("profilePic", image);
            this.getDatastore().update(user, update);
            logger.log(Level.INFO, "Changed {0}'s profile pic", user.getUsername());
            future.complete();
        }, resultHandler);

    }

    public void logout(User user, Handler<AsyncResult<User>> resultHandler ){
        vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "Logging out {0}", user.getUsername());
            UpdateOperations<User> update = this.createUpdateOperations().unset("token");
            this.getDatastore().update(user, update);
            logger.log(Level.INFO, "{0} is logged out", user.getUsername());
            future.complete(user);
        }, resultHandler);

    }

    public UserDTO toDTO(User me){
        return new UserDTO(me);
    }

    public List<UserDTO> toDTO(List<User> users){
        return users != null ? users.stream().map(UserDTO::new).collect(Collectors.toList()) : new ArrayList<>();
    }

    @Override
    public void init(Map<Class, DAO> daos) {

    }
}
