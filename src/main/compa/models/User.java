package main.compa.models;

import com.google.gson.annotations.Expose;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.ArrayList;
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

    @Expose
    @Reference
    private List<Token> tokens;


    private List<Friendship> friendships;

    @Reference
    private List<Location> locations;

    public User(){
    }

    public User(String login, String password){
        this.login = login;
        this.password = password;
        this.tokens = new ArrayList<>();
        this.friendships = new ArrayList<>();
    }

    public void addToken(Token token){
        this.tokens.add(token);
    }

    public String getLogin() {
        return login;
    }
}
