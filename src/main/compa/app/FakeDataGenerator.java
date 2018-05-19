package main.compa.app;

import main.compa.Main;
import main.compa.models.Friendship;
import main.compa.models.Location;
import main.compa.models.User;
import org.mongodb.morphia.Datastore;

import javax.jws.soap.SOAPBinding;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FakeDataGenerator {

    public static void main(String... args){
        Main.main(args);
        Datastore datastore = Main.c.getMongoUtil().getDatastore();

        datastore.getCollection(User.class).drop();
        datastore.getCollection(Location.class).drop();
        datastore.getCollection(Friendship.class).drop();

        users = new ArrayList<>();
        friendships = new ArrayList<>();


        Location cityTest = new Location(51.509865, -0.118092);

        double baseLatitude = Math.round((cityTest.getLatitude() - 0.007)*1000)/1000;
        double baseLongitude = Math.round((cityTest.getLongitude() - 0.008)*1000)/1000;

        int offset = -10000;
        int userNb = 20;
        int locationsPerUser = 5;

        for(int i = 0; i < userNb; ++i){
            User u = new User("user" + i, "password" + i);
            for(int j = 0; j < locationsPerUser; ++j){

                LocalDateTime date = LocalDateTime.now().minus(offset, ChronoUnit.SECONDS);

                Location l = new Location(
                        baseLatitude + randomCoordinate(),
                        baseLongitude + randomCoordinate(),
                        java.sql.Timestamp.valueOf(date));

                datastore.save(l);
                u.addLocation(l);

                offset += 100;
            }

            users.add(u);

        }


        for(User u : users)
            datastore.save(u);


       Random r = new Random();

        for(int i = 0; i < userNb - 1; ++i){
            //loop again? multiple friendships
            int min = i+1;
            int max = userNb - 1;
            int friendId = r.nextInt((max - min) + 1) + min;
            User me = users.get(i);
            User other = users.get(friendId);
            Friendship f = new Friendship(me, other);
            me.addFriendship(f);
            other.addFriendship(f);
            friendships.add(f);
        }

        for(Friendship f : friendships)
            datastore.save(f);


    }

    private static List<User> users;
    private static List<Friendship> friendships;
    private static double randomCoordinate(){
        return new Random().nextInt(140) * 0.0001;
    }



}
