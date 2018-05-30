package compa.dtos;

import compa.models.Image;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageDTO {

    private String publicId, format, date;

    public ImageDTO(Image image){
        this.publicId = image.getPublicId();
        this.format = image.getFormat();
        this.date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(image.getCreatedAt());
    }
}
