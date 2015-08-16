package pl.jsolve.templ4docx.variable;

import java.io.File;

public class ImageVariable implements Variable {

    private String key;
    private String imagePath;
    private File imageFile;
    private int width;
    private int height;
    private ImageType imageType;

    public ImageVariable(String key, String imagePath, int width, int height) {
        this.key = key;
        this.imagePath = imagePath;
        this.width = width;
        this.height = height;
        this.imageType = ImageType.findImageTypeForPath(imagePath);
    }

    public ImageVariable(String key, File imageFile, int width, int height) {
        this.key = key;
        this.imageFile = imageFile;
        this.width = width;
        this.height = height;
        this.imageType = ImageType.findImageTypeForPath(imagePath);
    }

    public ImageVariable(String key, String imagePath, ImageType imageType, int width, int height) {
        this.key = key;
        this.imagePath = imagePath;
        this.width = width;
        this.height = height;
        this.imageType = imageType;
    }

    public ImageVariable(String key, File imageFile, ImageType imageType, int width, int height) {
        this.key = key;
        this.imageFile = imageFile;
        this.width = width;
        this.height = height;
        this.imageType = imageType;
    }

    public String getKey() {
        return key;
    }

    public String getImagePath() {
        return imagePath;
    }

    public File getImageFile() {
        return imageFile;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ImageType getImageType() {
        return imageType;
    }

}
