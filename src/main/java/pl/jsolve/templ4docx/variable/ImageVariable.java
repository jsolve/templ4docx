package pl.jsolve.templ4docx.variable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

public class ImageVariable implements Variable {

    private String key;
    private InputStream imageStream;
    private String imagePath;
    private File imageFile;
    private int width;
    private int height;
    private ImageType imageType;

    public ImageVariable(String key, String imagePath, int width, int height) {
    	this(key, new File(imagePath), ImageType.findImageTypeForPath(imagePath), width, height);
    }

    public ImageVariable(String key, File imageFile, int width, int height) {
    	this(key, imageFile, ImageType.findImageTypeForPath(imageFile.getAbsolutePath()), width, height);
    }

    public ImageVariable(String key, String imagePath, ImageType imageType, int width, int height) {
    	this(key, new File(imagePath), imageType, width, height);
    }

    public ImageVariable(String key, File imageFile, ImageType imageType, int width, int height) {
    	try {
            this.key = key;
            this.imageFile = imageFile;
            this.imagePath = imageFile.getAbsolutePath();
            this.imageStream = new ByteArrayInputStream(FileUtils.readFileToByteArray(imageFile));
            this.width = width;
            this.height = height;
            this.imageType = imageType;
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    }

    public ImageVariable(String key, InputStream imageStream, ImageType imageType, int width, int height) {
        this.key = key;
        this.imageStream = imageStream;
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

    public InputStream getImageStream() {
    	return imageStream;
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
