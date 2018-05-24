package compa.models;

import com.google.gson.annotations.Expose;
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
        this.sister  = new Friendship(friend,this);
    }

    public Friendship(User me, Friendship asker){
        this.me = me;
        this.status = Status.PENDING;//TODO change the status
        this.sister  = asker;
    }
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public User getMe() {
        return me;
    }

    public void setMe(User me) {
        this.me = me;
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