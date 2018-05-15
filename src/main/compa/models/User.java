package main.compa.models;

import com.google.gson.annotations.Expose;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.List;


@Entity("user")
@Indexes({
        @Index(value = "login", fields = @Field("login"), unique = true),
})
public class User implements JSONisable{

    @Id
    private ObjectId id;

    @Expose
    private String login;
    private String password;
    private String token;

    @Expose
    private List<Friendship> friendships;

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
