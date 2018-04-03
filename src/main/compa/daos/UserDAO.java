package main.compa.daos;

import main.compa.App.Container;
import main.compa.Model.User;
import main.compa.mongodb.AbstractDAO;
import org.bson.types.ObjectId;

public class UserDAO extends AbstractDAO<User, ObjectId> {

    public UserDAO(){
        super(User.class, Container.getInstance().getDataStore());
    }

    public User getByLoginAndPassword(String login, String password){
        return this.createQuery().filter("login", login).
                filter("password", password).get();
    }
}
