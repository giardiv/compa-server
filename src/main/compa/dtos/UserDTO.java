package compa.dtos;

import compa.models.Location;
import compa.models.User;
import compa.services.ImageService;

import java.util.List;


public class UserDTO {

    private String id, login, name, profilePicUrl;
    private LocationDTO lastLocation;
    private boolean ghostMode;

    public static int DEFAULT_PP_WIDTH = 100;
    public static int DEFAULT_PP_HEIGHT = 100;

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
        this.login = user.getUsername();
        this.name = user.getName();
        this.ghostMode = user.getGhostMode();
        List<Location> locs = user.getLocations();
        Location loc = (locs.size() > 0 && !ghostMode)? locs.get(locs.size() - 1) : null; //TODO CHANGE THIS DEFINETELY
        this.lastLocation = loc == null ? null : new LocationDTO(loc);
        this.profilePicUrl = ImageService.getUrl(DEFAULT_PP_WIDTH, DEFAULT_PP_HEIGHT, user.getProfilePic());
    }

    public UserDTO(User user, int width, int height){
        this(user);
        this.profilePicUrl = ImageService.getUrl(width, height, user.getProfilePic());
    }
}
