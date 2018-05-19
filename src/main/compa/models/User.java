package main.compa.models;

import com.google.gson.annotations.Expose;
import main.compa.app.JSONisable;
import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.List;


@Entity("user")
@Indexes({
        @Index(value = "login", fields = @Field("login"), unique = true),
})
public class User implements JSONisable {
    static final long serialVersionUID = 42L;

    @Id
    @Expose
    public ObjectId id;

    @Expose
    private String login;
    private String password;

    @Expose
    //@Reference
    private String token; //List<Token> tokens;


    private List<Friendship> friendships;

    @Reference
    private List<Location> locations;

    public User(){
    }

    public User(String login, String password){
        this.login = login;
        this.password = password;
        this.friendships = new ArrayList<>();
        this.token = RandomStringUtils.random(16); 
    }

    public String getToken(){
        return this.token;
    }

    public void setToken(){
        this.token = RandomStringUtils.random(16);
    }

    public String getLogin() {
        return login;
    }
}
