package compa.models;


import com.mongodb.gridfs.GridFSDBFile;
import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


@Entity(value = "user", noClassnameStored = true)
@Indexes({
        @Index(value = "login", fields = @Field("login"), unique = true),
})
public class User {
    private final static int TOKEN_COUNT = 16;

    @Id
    public ObjectId id;

    private String login, email, password, token, name, salt;

    @Reference
    private List<Location> locations;

    private boolean ghostMode;

    public User(){
    }

    public User(String login, String name, String password, String salt){
        this.name = name;
        this.name = name;
        this.login = login;
        this.password = password;
        this.locations = new ArrayList<>();
        this.salt = salt;
        this.ghostMode = false;
        this.generateToken();
    }

    public void addLocation(Location l){
        locations.add(l);
    }

    public String getToken(){
        return this.token;
    }

    public void generateToken(){
        this.token = RandomStringUtils.randomAlphanumeric(TOKEN_COUNT);
    }

    public String getLogin() {
        return login;
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

    public void setName(String name) {
        this.name=name;
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
