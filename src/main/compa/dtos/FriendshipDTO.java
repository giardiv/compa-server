package compa.dtos;

import compa.models.Friendship;
import compa.models.User;
import java.util.Date;

public class FriendshipDTO {

    private String id;
    private String status;
    private UserDTO friend;
    private FriendshipDTO sister;
    private Date datetime;

    public FriendshipDTO(Friendship friendship){
        this(friendship, 1);
    }

    public FriendshipDTO(Friendship friendship, int depth){
        this.id = friendship.getId().toString();
        this.status = friendship.getStatus().toString();
        this.friend = new UserDTO(friendship.getFriend());
        if(depth > 0){
            this.sister = new FriendshipDTO(friendship.getSister(), 0);
        }
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

    public UserDTO getFriend() {
        return friend;
    }

    public void setFriend(UserDTO friend) {
        this.friend = friend;
    }

    public FriendshipDTO getSister() {
        return sister;
    }

    public void setSister(FriendshipDTO sister) {
        this.sister = sister;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

}
