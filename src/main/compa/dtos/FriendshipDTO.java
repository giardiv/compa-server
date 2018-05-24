package compa.dtos;

import compa.models.Friendship;
import compa.models.User;
import java.util.Date;

public class FriendshipDTO {

    private String id;
    private String status;
    private UserDTO me;
    private FriendshipDTO sister;
    private Date datetime;

    public FriendshipDTO(Friendship friendship, User user){
        this.id = friendship.getId().toString();
        this.status = friendship.getStatus().toString();
        this.me = new UserDTO(user);
        this.sister = new FriendshipDTO(friendship.getSister(),friendship.getFriend(),0);

    }

    private FriendshipDTO(Friendship friendship, User user, int depth){
        this.id = friendship.getId().toString();
        this.status = friendship.getStatus().toString();
        this.me = new UserDTO(user);
        if(depth == 0 )
            this.sister = new FriendshipDTO(friendship.getSister(),friendship.getFriend(), depth++);

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

    public UserDTO getMe() {
        return me;
    }

    public void setMe(UserDTO me) {
        this.me = me;
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
