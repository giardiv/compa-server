package compa.models;

import com.google.gson.annotations.Expose;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.Date;

@Entity(value = "friendship2", noClassnameStored = true)
@Indexes({
        // @Index(value = "login", fields = @Field("login"), unique = true),
})
public class Friendship2 {

    @Embedded
    public enum Status {
        PENDING,
        ACCEPTED,
        REUSED,
        BLOCKED,
        BLOCKED_Asked,
        BLOCKED_Asker
    };

    @Id
    private ObjectId id;

    @Reference
    private User me;

    @Reference
    private Friendship2 sister ;

    private Status status;

    public Friendship2(){}

    public Friendship2(User me, User friend){
        this.me = me;
        this.status = Status.PENDING;//TODO change the status
        this.sister  = new Friendship2(friend,this);
    }

    public Friendship2(User me, Friendship2 asker){
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

    public Friendship2 getSister() {
        return sister;
    }

    public void setSister(Friendship2 sister) {
        this.sister = sister;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
