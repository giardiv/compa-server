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

    @Expose
    @Reference
    private Friendship2 sister ;

    private Status status;

    private Date date;

    public Friendship2(){}

    public Friendship2(User me, User sister){
        this.me = me;
        this.status = Status.PENDING;
        this.sister  = new Friendship2(sister,me, 0);
    }

    public Friendship2(User me, User sister,int depth){
        this.me = me;
        this.status = Status.PENDING;
        if(depth == 0)
            this.sister  = new Friendship2(sister,me, depth++);
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
