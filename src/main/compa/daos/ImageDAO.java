package compa.daos;

import compa.app.Container;
import compa.app.DAO;
import compa.models.Image;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.bson.types.ObjectId;

import java.util.Map;

public class ImageDAO extends DAO<Image, ObjectId>  {

    public ImageDAO(Container container) {
        super(Image.class, container);
    }

    @Override
    public void init(Map<Class, DAO> daos) {
    }

    public void addImage(String publicId, String localPath, String format, Handler<AsyncResult<Image>> resultHandler) {
        vertx.executeBlocking( future -> {
            Image image = new Image(publicId, localPath, format);
            this.save(image);
            future.complete(image);
        }, resultHandler);
    }
}
