package main.compa.dtos;

import main.compa.models.Location;
import main.compa.models.User;
import java.util.List;


public class UserDTO {

    private String id, login, name;
    private LocationDTO lastLocation;

    public UserDTO(User user){
        this.id = user.getId().toString();
        this.login = user.getLogin();
        this.name = user.getName();
        List<Location> locs = user.getLocations();
        Location loc = locs.size() > 0 ? locs.get(locs.size() - 1) : null;
        this.lastLocation = loc == null ? null : new LocationDTO(loc);
    }

}
