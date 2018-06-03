package compa.dtos;

import compa.models.Friendship;

import java.util.Date;

public class FriendshipDTO {

    private String id, statusA, statusB, friendAId, friendBId;
    private Date datetime;

    public FriendshipDTO(Friendship friendship){
        this.id = friendship.getId().toString();
        this.statusA = friendship.getStatusA().toString();
        this.statusB = friendship.getStatusB().toString();
        this.friendAId = friendship.getUserA().getId().toString();
        this.friendBId = friendship.getUserB().getId().toString();
    }
}
