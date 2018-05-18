package main.compa.models;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.Date;

@Entity("token")
@Indexes({
})
public class Token {
    @Id
    private ObjectId id;

    @Expose
    private Date beginAt;

    @Expose
    private Date expiredAt;

    @Expose
    private String value;

    public Token(){
        this.beginAt = new Date();

        //TODO changer expired at
        this.expiredAt = new Date();

        this.value =  RandomStringUtils.random(16);
    }
}
