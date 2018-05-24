package compa.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

@Entity(value = "friendship2", noClassnameStored = true)
@Indexes({
        // @Index(value = "login", fields = @Field("login"), unique = true),
})
public class Friendship {

    @Embedded
    public enum Status {
        PENDING,
        ACCEPTED,
        REUSED,
        BLOCKED
    };

    @Id
    private ObjectId id;

    @Reference
    private User me;

    @Reference
    private Friendship sister ;

    private Status status;

    public Friendship(){}

    public Friendship(User me, User friend){
        this.me = me;
        this.status = Status.PENDING;//TODO change the status
        //this.sister  = new Friendship(friend,this);
    }

//    public Friendship(User me, Friendship asker){
//        this.me = me;
//        this.status = Status.PENDING;//TODO change the status
//        this.sister  = asker;
//    }
    public ObjectId getId() {
        return id;
    }

    public User getMe() {
        return me;
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
