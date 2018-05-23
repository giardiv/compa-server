package compa.dtos;

import compa.models.Location;
import compa.models.User;
import java.util.List;


public class UserDTO {

    private String id, login, name;
    private LocationDTO lastLocation;
    private boolean ghostMode;

    /**
     * @apiDefine UserDTO
     * @apiSuccess {String} id                    The current User Id
     * @apiSuccess {String} login                 User e-mail
     * @apiSuccess {String} name                  Name
     * @apiSuccess {Boolean} ghostMode            If ghost more is enable
     * @apiSuccess {LocationDTO} lastLocation     The current User Id
     */
    public UserDTO(User user){
        this.id = user.getId().toString();
        this.login = user.getLogin();
        this.name = user.getName();
        this.ghostMode = user.getGhostMode();
        List<Location> locs = user.getLocations();
        Location loc = (locs.size() > 0 && !ghostMode)? locs.get(locs.size() - 1) : null;
        this.lastLocation = loc == null ? null : new LocationDTO(loc);
    }
}