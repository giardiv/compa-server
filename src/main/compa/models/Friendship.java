package compa.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

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
    }

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

    private Friendship(User friend, Friendship fs){
        this.id = ObjectId.get();
        this.friend = friend;
        this.status = AWAITING;
        this.sister = fs;
    }

    public ObjectId getId() {
        return id;
    }

    public User getFriend() {
        return friend;
    }

    public Friendship getSister() {
        return sister;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status, boolean recursive) {
        // TODO: to test
        this.status = status;
        if(!recursive)
            return;
        sister.setStatus(Friendship.getReciprocalStatus(status), false);
    }

    public static Status getReciprocalStatus(Status s){
        switch (s){
            case PENDING:
                return AWAITING;
            case AWAITING:
                return PENDING;
            case BLOCKED:
                return BLOCKER;
            case BLOCKER:
                return BLOCKED;
            case REFUSED:
                return SORRY;
            case SORRY:
                return REFUSED;
            case ACCEPTED:
                return ACCEPTED;
            default:
                System.err.println("Match hasn't been done yet");
                return s;
        }
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
