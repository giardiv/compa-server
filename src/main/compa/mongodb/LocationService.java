package main.compa.mongodb;

import main.compa.Model.Location;
import main.compa.Model.User;

import java.util.List;

public interface LocationService {
    Location save(double latitude, double longitude);

    Location find(String latitude, String longitude);

    List<Location> findAll();

    void remove(String latitude, String longitude);
}
