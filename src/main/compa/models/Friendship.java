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
        BLOCKED,
        BLOCKED_Asked,
        BLOCKED_Asker
    };


    @Id
    private ObjectId id;

    private Status status;

    @Reference
    private User userAsker;

    @Reference
    private User userAsked ;

    private Date datetime;


    public Friendship(){}
    public Friendship(User a, User b){
        this(a, b, null);
    }

    public Friendship(User a, User b, Date date){
        this.status = Status.PENDING;
        this.userAsker = a;
        this.userAsked  = b;
        this.datetime = date;
    }

    public void setStatus(Status s){
        this.status = s;
    }

    public boolean isAccepted(){
        return true;//this.getStatus().getKey() == Status.ACCEPTED;
    }

    public Status getStatus(){ ///Map<Date, Status> getStatus(){
        return this.status;
    }

    public User getUserAsker() {
        return userAsker;
    }

    public User getUserAsked () {
        return userAsked ;
    }

    public ObjectId getId() {return id;}

    public void setId(ObjectId id) {this.id = id;}
}
