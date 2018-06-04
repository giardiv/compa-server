package compa.dtos;

import compa.models.Location;
import compa.models.User;
import java.util.List;


public class UserDTO {

    private String email,id, login, name, profilePicId;
    private LocationDTO lastLocation;
    private boolean ghostMode;

    /**
     * @apiDefine UserDTO
     * @apiSuccess {String} id                    The current User Id
     * @apiSuccess {String} login                 User login
     * @apiSuccess {String} name                  Name
     * @apiSuccess {String} email                 User e-mail
     * @apiSuccess {Boolean} ghostMode            If ghost more is enable
     * @apiSuccess {LocationDTO} lastLocation     The current User Id
     */
    public UserDTO(User user){
        this.id = user.getId().toString();
        this.login = user.getUsername();
        this.name = user.getName();
        this.ghostMode = user.getGhostMode();
        this.email = user.getEmail();
        List<Location> locs = user.getLocations();
        Location loc = (locs.size() > 0 && !ghostMode)? locs.get(locs.size() - 1) : null; //TODO CHANGE THIS DEFINETELY
        this.lastLocation = loc == null ? null : new LocationDTO(loc);
        this.profilePicId = user.getProfilePic() == null ? null : user.getProfilePic().getPublicId();
    }
}
