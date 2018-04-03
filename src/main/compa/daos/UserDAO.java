package main.compa.daos;

import main.compa.Model.User;
import main.compa.App.AbstractDAO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;

public class UserDAO extends AbstractDAO<User, ObjectId> {

    public UserDAO(Datastore ds){
        super(User.class, ds);
    }

    public User getByLoginAndPassword(String login, String password){
        return this.createQuery().filter("login", login).
                filter("password", password).get();
    }
}
