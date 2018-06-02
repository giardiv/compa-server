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

    // Author of the Friendship
    @Reference
    private User friendAId;

    private Status statusA;

    @Reference
    private User friendBId;

    private Status statusB;

    public Friendship(){}

    public Friendship(User asker, User asked){
        this.friendAId = asker;
        this.statusA = PENDING;
        this.friendBId = asked;
        this.statusB = AWAITING;
    }

    public ObjectId getId() {
        return id;
    }

    public User getFriendA() {
        return friendAId;
    }

    public User getFriendB() {
        return friendBId;
    }

    public Status getStatusA() {
        return statusA;
    }

    public Status getStatusB() {
        return statusB;
    }

    public void setStatusA(Status status) {
        this.statusA = status;
        this.statusB = Friendship.getReciprocalStatus(status);
    }

    public void setStatusB(Status status) {
        this.statusB = status;
        this.statusA = Friendship.getReciprocalStatus(status);
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
            default:
                return s;
        }
    }

    public static boolean validStatusChange(Status origin, Status change) {
        switch (origin){
            case PENDING:
                return false;
            case AWAITING:
                return change == ACCEPTED || change == REFUSED;
            case BLOCKED:
                return false;
            case BLOCKER:
                return change == ACCEPTED;
            case REFUSED:
                return false;
            case SORRY:
                return false;
            case ACCEPTED:
                return change == BLOCKER;
            default:
                return false;
        }
    }
}
