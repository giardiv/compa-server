package compa.services;

import com.cloudinary.utils.ObjectUtils;
import com.cloudinary.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import compa.app.Container;
import compa.app.Service;
import compa.daos.ImageDAO;
import compa.exception.ImageException;
import compa.models.Image;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.FileUpload;
import org.apache.tika.Tika;

public class ImageService extends Service {
    // TODO: to secure values
    private final static String CLOUD_NAME = "compadant";
    private final static String API_KEY = "929366632823379";
    private final static String API_SECRET = "KWoShyvRRVgSgKm2_OFDZHbRSMc";

    public final static String[] ACCEPTED_FORMAT = {"image/jpeg", "image/png"};

    private Cloudinary cloudinary;
    private Vertx vertx;
    private ImageDAO imageDAO;

    public ImageService(Container container) {
        super(container);
        this.cloudinary = getNewCloudinary();
        this.vertx = container.getVertx();
        this.imageDAO = (ImageDAO) container.getDAO(Image.class);
    }

    public Cloudinary getCloudinary(){
        return this.cloudinary;
    }

    private static Cloudinary getNewCloudinary(){
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", CLOUD_NAME,
                "api_key", API_KEY,
                "api_secret", API_SECRET));
    }

    public void upload(FileUpload uploadedFile, Handler<AsyncResult<Image>> resultHandler){
        // TODO: add constraint
        // TODO: work this file directly
        vertx.executeBlocking( future -> {
            Map result = null;
            String localPath = "profile-images/" + UUID.randomUUID().toString();
            String ext;

            Tika tika = new Tika();

            File file = new File(uploadedFile.uploadedFileName());

            try {
                String type = tika.detect(file);
                if(!Arrays.asList(ACCEPTED_FORMAT).contains(type))
                    throw new ImageException(ImageException.UNACCEPTED_FORMAT, type);

                file.createNewFile();
                result = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());

                ext = (String) result.get("format");
                localPath += "." + ext;
                File localCopy = new File(localPath);
                Files.copy(file.toPath(), localCopy.toPath());

                new File(uploadedFile.uploadedFileName()).delete();
            } catch (IOException e) {
                e.printStackTrace();
                future.fail(new ImageException(ImageException.IO_EXCEPTION));
                return;
            } catch (ImageException e){
                future.fail(e);
                return;
            }

            imageDAO.addImage((String) result.get("public_id"), localPath, ext, res -> {
                System.out.println(this.getRawUrl(res.result()));
                System.out.println(this.getThumbnailUrl(res.result()));
                future.complete(res.result());
            });
        }, resultHandler);
    }

    public String getThumbnailUrl(Image image){
        return cloudinary.url().transformation(
                new Transformation()
                        .width(90)
                        .height(90)
                        .crop("fill"))
                .generate(image.getPublicId());
    }

    public static String getUrl(int width, int height, Image image){
        return getNewCloudinary().url().transformation(
                new Transformation()
                        .width(width)
                        .height(height)
                        .crop("fill"))
                .generate(image.getPublicId());
    }

    public String getRawUrl(Image image){
        return cloudinary.url().generate(image.getPublicId());
    }
}
