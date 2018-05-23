package compa.app;

import compa.Main;
import compa.models.Friendship;
import compa.models.Location;
import compa.models.User;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.UpdateOperations;

import javax.jws.soap.SOAPBinding;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FakeDataGenerator {

    public static List<User> users;

    public static void main(String... args){
        ClassFinder cf = new ClassFinder();
        Container c = new Container(null);
        c.run(cf);

        Datastore datastore = c.getMongoUtil().getDatastore();

        datastore.getCollection(User.class).drop();
        datastore.getCollection(Location.class).drop();
        datastore.getCollection(Friendship.class).drop();

        users = new ArrayList<>();
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
                        baseLatitude + new Random().nextInt(140) * 0.0001,
                        baseLongitude + new Random().nextInt(140) * 0.0001,
                        java.sql.Timestamp.valueOf(date));

                datastore.save(l);
                u.addLocation(l);

                offset += 100;
            }

            users.add(u);
            datastore.save(u);
        }


       Random r = new Random();

        for(int i = 0; i < userNb - 1; ++i){
            User me = users.get(i);
            int min = i+1;
            int max = userNb - 1;
            int friendId = r.nextInt((max - min) + 1) + min;
            LocalDateTime date = LocalDateTime.now().minus(offset, ChronoUnit.SECONDS);
            User other = users.get(friendId);
            Friendship f = new Friendship(me, other, java.sql.Timestamp.valueOf(date));
            me.addFriendship(f);
            other.addFriendship(f);
            datastore.save(f);
            UpdateOperations ops = datastore.createUpdateOperations(User.class).addToSet("friendships", f);
            datastore.update(me, ops);
            datastore.update(other, ops);
        }

    }

}
