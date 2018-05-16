package main.compa.daos;

import main.compa.exception.RegisterException;
import main.compa.models.User;
import main.compa.app.DAO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;

public class UserDAO extends DAO<User, ObjectId> {

    public static final int PASSWORD_MIN_LENGTH = 8;

    public UserDAO(Datastore ds){
        super(User.class, ds);
    }

    public User getByLoginAndPassword(String login, String password){
        return this.createQuery().filter("login", login).
                filter("password", password).get();
    }

    public User addUser(String login, String password) throws RegisterException {
        User user = this.createQuery().filter("login", login).get();
        if(password.length() < this.PASSWORD_MIN_LENGTH)
            throw new RegisterException(RegisterException.PASSWORD_TOO_SHORT);
        if(user != null)
            throw new RegisterException(RegisterException.USER_ALREADY_EXIST);

        user = new User(login, password);

        this.save(user);

        return user;
    }
}
