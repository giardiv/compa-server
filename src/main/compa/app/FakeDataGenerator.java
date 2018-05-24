package compa.app;

import compa.models.Friendship;
import compa.models.Location;
import compa.models.User;
import compa.services.AuthenticationService;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.UpdateOperations;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FakeDataGenerator {

    public static void main(String... args){
        ClassFinder cf = new ClassFinder();
        Container c = new Container(null);
        c.run(cf);

        List<User> users = new ArrayList<>();;

        Datastore datastore = c.getMongoUtil().getDatastore();

        datastore.getCollection(User.class).drop();
        datastore.getCollection(Location.class).drop();
        datastore.getCollection(Friendship.class).drop();

        Location cityTest = new Location(51.509865, -0.118092);

        double baseLatitude = Math.round((cityTest.getLatitude() - 0.007)*1000)/1000;
        double baseLongitude = Math.round((cityTest.getLongitude() - 0.008)*1000)/1000;

        int offset = -10000;
        int userNb = 20;
        int locationsPerUser = 5;

        for(int i = 0; i < userNb; ++i){
            String salt = AuthenticationService.getSalt();
            String encPassword = AuthenticationService.encrypt("password" + i, salt);
            User u = new User("user" + i, "Name "+ i, encPassword, salt);
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
            User friend = users.get(friendId);
            Friendship fs_me = new Friendship(me);
            Friendship fs_friend = new Friendship(friend);
            datastore.save(fs_friend);
            fs_me.setSister(fs_friend);
            datastore.save(fs_me);
            fs_friend.setSister(fs_me);
            UpdateOperations<Friendship> ops = datastore.createUpdateOperations(Friendship.class).addToSet("sister",fs_me );
            datastore.update(fs_friend, ops);
        }
    }

}
