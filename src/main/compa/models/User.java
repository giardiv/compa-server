package compa.models;


import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.ArrayList;
import java.util.List;


@Entity(value = "user", noClassnameStored = true)
@Indexes({
        @Index(value = "login", fields = @Field("login"), unique = true),
})
public class User {
    private final static int TOKEN_COUNT = 16;

    @Id
    public ObjectId id;

    private String login, password, token, name, salt;

    @Reference
    private List<Friendship> friendships;

    @Reference
    private List<Friendship2> friendships2;

    @Reference
    private List<Location> locations;

    private boolean ghostMode;

    public User(){
    }

    public User(String login, String name, String password, String salt){
        this.login = login;
        this.password = password;
        this.locations = new ArrayList<>();
        this.friendships = new ArrayList<>();
        this.friendships2 = new ArrayList<>();
        this.salt = salt;
        this.name = name;
        this.ghostMode = false;
        this.setToken();
    }

    public void addLocation(Location l){
        locations.add(l);
    }

    public String getToken(){
        return this.token;
    }

    public void setToken(){
        this.token = RandomStringUtils.randomAscii(TOKEN_COUNT);
    }

    public String getLogin() {
        return login;
    }

    public void addFriendship(Friendship f){
        friendships.add(f);
    }

    public void addFriendship2(Friendship2 f){
        friendships2.add(f);
    }

    public List<Friendship> getFriendships() {
        return friendships;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public String getName() {
        return name;
    }

    public ObjectId getId() {
        return id;
    }

    public String getSalt() { return salt; }

    public boolean getGhostMode() { return ghostMode; }

    public boolean isPassword(String password) {
        return this.password.equals(password);
    }

    @Override
    public boolean equals(Object obj){
        return this.id.toString().equals(((User) obj).id.toString());
    }
}
