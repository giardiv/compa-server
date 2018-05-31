package compa.models;


import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


@Entity(value = "user", noClassnameStored = true)
@Indexes({
        @Index(value = "username", fields = @Field("username"), unique = true),
})
public class User {
    private final static int TOKEN_COUNT = 16;

    @Id
    public ObjectId id;

    private String email, name, username, password, token, salt;

    @Reference
    private List<Location> locations;

    private boolean ghostMode;

    public User(){
    }


    public User(String email, String name, String username, String password, String salt){
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.locations = new ArrayList<>();
        this.salt = salt;
        this.ghostMode = false;
        this.generate_token();
    }

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public void addLocation(Location l){
        locations.add(l);
    }

    public String getToken(){
        return this.token;
    }

    public void generate_token(){
        this.token = RandomStringUtils.randomAlphanumeric(TOKEN_COUNT);
    }

    public void setToken(String token){this.token = token;}

    public String getUsername() {
        return username;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public Location getLastLocation() {
        return this.getLocations().size() > 0 ?
                this.getLocations().stream().max(Comparator.comparing(Location::getDatetime)).get() :
                null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {this.name = name;}

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
