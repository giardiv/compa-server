package compa.models;


import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.Date;

@Entity(value = "image", noClassnameStored = true)
@Indexes({
        //@Index(value = "login", fields = @Field("login"), unique = true),
})
public class Image {
    @Id
    public ObjectId id;

    private String publicId;

    private String localPath;

    private String format;

    private Date createdAt;

    public Image(){}

    public Image(String publicId, String localPath, String format){
        this.publicId = publicId;
        this.localPath = localPath;
        this.format = format;
        this.createdAt = new Date();
    }

    public String getPublicId() {
        return publicId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getFormat() {
        return format;

    }
}
