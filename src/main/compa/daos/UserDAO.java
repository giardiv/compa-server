package main.compa.daos;

import main.compa.models.User;
import main.compa.app.CustomDAO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;

public class UserDAO extends CustomDAO<User, ObjectId> {

    public UserDAO(Datastore ds){
        super(User.class, ds);
    }

    public User getByLoginAndPassword(String login, String password){
        return this.createQuery().filter("login", login).
                filter("password", password).get();
    }
}
