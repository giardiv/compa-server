package compa.models;

import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Entity(value = "user", noClassnameStored = true)
@Indexes({
        @Index(value = "username", fields = @Field("username"), unique = true),
})
public class User {
    private final static int TOKEN_SIZE = 16;

    @Id
    public ObjectId id;

    @Transient
    private Location lastLocation;

    private String email, name, username, password, token, salt;

    @Reference(lazy=true)
    private List<Location> locations;

    private boolean ghostMode;

    private Image profilePic;

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
        this.generateToken();

    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public void addLocation(Location l){
        locations.add(l);
    }

    public List<Location> getLocations() {
        return locations;
    }


    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getToken(){
        return this.token;
    }

    public void setToken(String token){this.token = token;}

    public void generateToken(){this.token = RandomStringUtils.randomAlphanumeric(TOKEN_SIZE);}

    public String getUsername() {
        return username;
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

    public Image getProfilePic(){
        return this.profilePic;
    }

    public void setProfilePic(Image image){
        this.profilePic = image;
    }
}
