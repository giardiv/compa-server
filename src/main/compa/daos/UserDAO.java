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
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import sun.rmi.server.UnicastServerRef;

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
        System.out.println("In getByLoginAndPassword DAO");

        vertx.executeBlocking( future -> {
            Query<User> query = this.createQuery();

            query.or(
                    query.criteria("email").equal(login),
                    query.criteria("login").equal(login)
            ).and(
                    query.criteria("password").equal(password)
            );
            User u = this.findOne(query);

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
            //User user = this.createQuery().filter("login", login).get();
            Query<User> query = this.createQuery();

            query.or(
                    query.criteria("email").equal(email),
                    query.criteria("login").equal(login)
            );

            User user = this.findOne(query);

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
            logger.log(Level.INFO, "Looking for user with id {0}", id);
            User u = super.findById(id);
            logger.log(Level.INFO, "User {0}found", u == null ? "not " : "");
            future.complete(u);
        }, resultHandler);

    }

    public void findById(ObjectId id, Handler<AsyncResult<User>> resultHandler) {

        vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "Looking for user with ObjectId {0}", id);
            User u = super.findOne("id", id);
            logger.log(Level.INFO, "User {0}found", u == null ? "not " : "");
            future.complete(u);
        }, resultHandler);

    }

    public void updatePassword(User user, String newEncryptedPassword, Handler<AsyncResult<User>> resultHandler ){

        vertx.executeBlocking( future -> {
            UpdateOperations<User> update = this.createUpdateOperations().set("password", newEncryptedPassword);
            this.getDatastore().update(user, update);
            user.generate_token();
            this.save(user);
            future.complete(user);
        }, resultHandler);

    }

    public void updateProfile(User user, String name, String email, Handler<AsyncResult<User>> resultHandler ){

        vertx.executeBlocking( future -> {
            UpdateOperations<User> update = this.createUpdateOperations();
            System.out.println("Avant  name: " + user.getName());

            if(name != user.getName()){
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
            this.getDatastore().update(user, update);
            System.out.println(" après name: " + user.getName());
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

    public void logout(User user, Handler<AsyncResult<User>> resultHandler ){
        vertx.executeBlocking( future -> {
            UpdateOperations<User> update = this.createUpdateOperations().set("token", "");
            this.getDatastore().update(user, update);
            user.setToken(null);
            this.save(user);
            System.out.println(" token : " + user.getToken());

            future.complete(user);
        }, resultHandler);

    }

    public UserDTO toDTO(User me){
        return new UserDTO(me);
    }

    public List<UserDTO> toDTO(List<User> users){
        return users.stream().map(UserDTO::new).collect(Collectors.toList());
    }

    @Override
    public void init(Map<Class, DAO> daos) {

    }
}
