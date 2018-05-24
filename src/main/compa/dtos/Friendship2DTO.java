package compa.dtos;

import compa.models.Friendship2;
import compa.models.User;
import java.util.Date;

public class Friendship2DTO {



    private String id;
    private String status;
    private UserDTO me;
    private Friendship2DTO sister;
    private Date datetime;

    public Friendship2DTO(Friendship2 friendship, User user){
        this.id = friendship.getId().toString();
        this.status = friendship.getStatus().toString();
        this.me = new UserDTO(user);
        this.sister = new Friendship2DTO(friendship.getSister(),friendship.getMe(),0);
    }

    private Friendship2DTO(Friendship2 friendship, User user, int depth){
        this.id = friendship.getId().toString();
        this.status = friendship.getStatus().toString();
        this.me = new UserDTO(user);
        if(depth == 0 )
            this.sister = new Friendship2DTO(friendship.getSister(),friendship.getMe(), depth++);

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

    public Friendship2DTO getSister() {
        return sister;
    }

    public void setSister(Friendship2DTO sister) {
        this.sister = sister;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

}


