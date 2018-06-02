package compa.dtos;

import compa.models.Friendship;

import java.util.Date;

public class FriendshipDTO {

    private String id, statusA, statusB, friendAId, friendBId;
    private Date datetime;

    public FriendshipDTO(Friendship friendship){
        this(friendship, 1);
    }

    public FriendshipDTO(Friendship friendship, int depth){
        this.id = friendship.getId().toString();
        this.statusA = friendship.getStatusA().toString();
        this.statusB = friendship.getStatusB().toString();
        this.friendAId = friendship.getFriendA().getId().toString();
        this.friendBId = friendship.getFriendB().getId().toString();
    }
}
