package main.compa.models;

import com.google.gson.annotations.Expose;
import javafx.util.Pair;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.Date;

@Entity("friendship")
@Indexes({
        @Index(value = "login", fields = @Field("login"), unique = true),
})
public class Friendship implements JSONisable{
    private enum Status{
        PENDING,
        ACCEPTED,
        REUSED,
        BLOCKED
    };

    @Id
    private ObjectId id;
    @Expose
    private Pair<Status, Date> status;

    @Expose
    @Reference
    private User friendLeft;
    @Expose
    @Reference
    private User friendRight;

    public Friendship(){}

    public Friendship(User a, User b){
        this.status = new Pair<>(Status.PENDING, new Date());
        this.friendLeft = a;
        this.friendRight = b;
    }

    public boolean isAccepted(){
        return this.getStatus().getKey() == Status.ACCEPTED;
    }

    public Pair<Status, Date> getStatus(){
        return this.status;
    }
}
