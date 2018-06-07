package compa.dtos;

import compa.models.Location;
import compa.models.User;
import compa.services.ImageService;

import java.util.List;


public class UserDTO {

    private String id, login, name, email, profilePicUrl;
    private LocationDTO lastLocation;
    private boolean ghostMode;

    public static int DEFAULT_PP_WIDTH = 100;
    public static int DEFAULT_PP_HEIGHT = 100;

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
        this.email = user.getEmail();
        this.name = user.getName();
        this.ghostMode = user.getGhostMode();
        this.email = user.getEmail();
        System.out.println(user.getLastLocation());
            this.lastLocation = new LocationDTO(user.getLastLocation());
        if(user.getProfilePic() != null)
            this.profilePicUrl = ImageService.getUrl(DEFAULT_PP_WIDTH, DEFAULT_PP_HEIGHT, user.getProfilePic());
    }

    public UserDTO(User user, Integer width, Integer height){
        this(user);
        if(width == null || height == null)
            return;
        if(user.getProfilePic() != null)
            this.profilePicUrl = ImageService.getUrl(width, height, user.getProfilePic());
    }
}
