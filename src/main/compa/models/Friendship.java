package compa.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.Date;

@Entity(value = "friendship", noClassnameStored = true)
@Indexes({
        // @Index(value = "login", fields = @Field("login"), unique = true),
})
public class Friendship {

    @Embedded
    public enum Status {
        PENDING,
        ACCEPTED,
        REFUSED,
        BLOCKED,
        BLOCKER,
        AWAITING
    };

    @Id
    private ObjectId id;

    @Reference
    private User friend;

    @Reference
    private Friendship sister ;

    private Status status;

    public Friendship(){}

    public Friendship(User friend){
        this.friend= friend;
        this.status = Status.PENDING;//TODO change the status
    }

    public ObjectId getId() {
        return id;
    }

    public User getFriend() {
        return friend;
    }

    public User setFriend(User friend) {
        return this.friend = friend;
    }

    public Friendship getSister() {
        return sister;
    }

    public void setSister(Friendship sister) {
        this.sister = sister;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}