package main.compa.daos;

import main.compa.dtos.UserDTO;
import main.compa.exception.RegisterException;
import main.compa.models.User;
import main.compa.app.DAO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO extends DAO<User, ObjectId> {

    public static final int PASSWORD_MIN_LENGTH = 8;
    Logger logger = Logger.getLogger("user_dao");

    public UserDAO(Datastore ds){
        super(User.class, ds);
    }

    public User getByLoginAndPassword(String login, String password){
        return this.createQuery().filter("login", login).
                filter("password", password).get();
    }

    public User addUser(String login, String password) throws RegisterException {
        User user = this.createQuery().filter("login", login).get();

        if(password.length() < PASSWORD_MIN_LENGTH)
            throw new RegisterException(RegisterException.PASSWORD_TOO_SHORT);

        if(user != null)
            throw new RegisterException(RegisterException.USER_ALREADY_EXIST);

        user = new User(login, password);
        this.save(user);
        return user;
    }

    @Override
    public User findOne(String key, Object value){
        logger.log(Level.INFO, "Looking for user by key {0} and value {1}", new Object[]{key, value});
        User u = super.findOne(key, value);
        logger.log(Level.INFO, "User {0} found", u == null ? "not" : "");
        return u;
    }

    @Override
    public User findById(String id) {
        logger.log(Level.INFO, "Looking for user {0}", id);
        User u = super.findById(id);
        logger.log(Level.INFO, "User {0} found", u == null ? "not" : "");
        return u;
    }

}
