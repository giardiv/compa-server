package main.compa.models;

import com.google.gson.annotations.Expose;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity("friendship")
@Indexes({
       // @Index(value = "login", fields = @Field("login"), unique = true),
})
public class Friendship implements JSONisable{
    @Embedded
    enum Status {
        PENDING,
        ACCEPTED,
        REUSED,
        BLOCKED
    };

    @Id
    private ObjectId id;

    @Expose
    //@Embedded
    //private Map<Date, Status> status;
    private Status status;

    @Expose
    @Reference
    private User friendLeft;

    @Expose
    @Reference
    private User friendRight;

    public Friendship(){}

    public Friendship(User a, User b){
        this.status = Status.PENDING; //new HashMap<Date, Status>();
        //this.status.put(new Date(), Status.PENDING);
        this.friendLeft = a;
        this.friendRight = b;
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
}
