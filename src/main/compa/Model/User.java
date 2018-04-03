package main.compa.Model;

import main.compa.App.Container;
import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.List;


@Entity("user")
@Indexes({
        @Index(value = "login", fields = @Field("login"), unique = true),
})
public class User {

    @Id
    private ObjectId id;

    private String login;
    private String password;
    private String token;

    @Reference
    private List<Location> locations;

    public User(){

    }

    public User(String login, String password, String token){
        this.login = login;
        this.password = password; //TO CHANGE
        //token to null when first registrating?
    }

    private void setToken(String token){
        this.token = token;
    }

    public static String checkAuth(String login, String password){
        User user = Container.getInstance().getDataStore().createQuery(User.class).
                                                            filter("login", login).
                                                            filter("password", password).
                                                            get();
        if(user == null)
            return null;

        String token =  RandomStringUtils.random(16);
        user.setToken(token);
        return token;
    }

}
