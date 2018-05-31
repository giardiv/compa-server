package compa.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.Date;

import static compa.models.Friendship.Status.*;

@Entity(value = "friendship", noClassnameStored = true)
@Indexes({
        // @Index(value = "login", fields = @Field("login"), unique = true),
})
public class Friendship {

    @Embedded
    public enum Status {
        PENDING,
        AWAITING,

        SORRY,
        REFUSED,

        ACCEPTED,

        BLOCKED,
        BLOCKER
    };

    @Id
    private ObjectId id;

    @Reference
    private User friend;

    @Reference(lazy = true)
    private Friendship sister;

    private Status status;

    public Friendship(){}

    public Friendship(User me, User friend){
        this.id = ObjectId.get();
        this.friend = friend;
        this.status = PENDING;
        this.sister = new Friendship(me, this);
    }

    public Friendship(User friend, Friendship fs){
        this.id = ObjectId.get();
        this.friend= friend;
        this.status = AWAITING;
        this.sister = fs;
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

    public void setStatus(Status status, boolean recursive) {
        if(!recursive)
            return;
        this.status = status;
        sister.setStatus(Friendship.getRiprocalStatus(status), false);
    }

    public static Status getRiprocalStatus(Status s){
        Status reciprocal = null;
        switch (s){
            case PENDING:
                reciprocal = AWAITING;
                break;
            case AWAITING:
                reciprocal = PENDING;
                break;
            case BLOCKED:
                reciprocal = BLOCKER;
                break;
            case BLOCKER:
                reciprocal = BLOCKED;
                break;
            case REFUSED:
                reciprocal = SORRY;
                break;
            case SORRY:
                reciprocal = REFUSED;
                break;
            default:
                reciprocal = s;
        }
        return reciprocal;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
