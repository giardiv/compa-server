package compa.dtos;

import compa.models.Friendship;
import compa.models.User;
import java.util.Date;

public class FriendshipDTO {

    private String id;
    private String status;
    private UserDTO userAscked;
    private Date datetime;

    public FriendshipDTO(Friendship friendship, User user){
        this.id = friendship.getId().toString();
        this.status = friendship.getStatus().toString();
        this.userAscked = new UserDTO(user);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserDTO getUserAscked() {
        return userAscked;
    }

    public void setUserAscked(UserDTO userascked) {
        this.userAscked = userascked;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

}


