package main.compa.models;

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

    public void setToken(String token){
        this.token = token;
    }

}
