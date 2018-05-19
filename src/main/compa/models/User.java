package main.compa.models;


import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.ArrayList;
import java.util.List;


@Entity("user")
@Indexes({
        @Index(value = "login", fields = @Field("login"), unique = true),
})
public class User {

    @Id
    public ObjectId id;

    private String login, password, token, name;

    @Reference
    private List<Friendship> friendships;

    @Reference
    private List<Location> locations;

    public User(){
    }

    public User(String login, String password){
        this.login = login;
        this.password = password;
        this.locations = new ArrayList<>();
        this.friendships = new ArrayList<>();
        this.token = RandomStringUtils.random(16);
    }

    public void addLocation(Location l){
        locations.add(l);
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

    public void addFriendship(Friendship f){
        friendships.add(f);
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

    @Override
    public boolean equals(Object obj){
        return this.id.toString().equals(((User) obj).id.toString());
    }
}
