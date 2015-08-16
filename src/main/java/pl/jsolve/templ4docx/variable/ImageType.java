package pl.jsolve.templ4docx.variable;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import pl.jsolve.templ4docx.exception.UnsupportedImageTypeException;

/**
 * Class represents supported image types. The supported image types are: .emf, .wmf, .pict, .jpeg, .jpg, .png, .dib,
 * .gif, .tiff, .eps, .bmp, .wpg
 * @author Lukasz Stypka
 */
public enum ImageType {

    EMF(XWPFDocument.PICTURE_TYPE_EMF, "emf"), WMF(XWPFDocument.PICTURE_TYPE_WMF, "wmf"), PICT(
            XWPFDocument.PICTURE_TYPE_PICT, "pict"), JPEG(XWPFDocument.PICTURE_TYPE_JPEG, "jpeg"), JPG(
            XWPFDocument.PICTURE_TYPE_JPEG, "jpg"), PNG(XWPFDocument.PICTURE_TYPE_PNG, "png"), DIB(
            XWPFDocument.PICTURE_TYPE_DIB, "dib"), GIF(XWPFDocument.PICTURE_TYPE_GIF, "gif"), TIFF(
            XWPFDocument.PICTURE_TYPE_TIFF, "tiff"), EPS(XWPFDocument.PICTURE_TYPE_EPS, "eps"), BMP(
            XWPFDocument.PICTURE_TYPE_BMP, "bmp"), WPG(XWPFDocument.PICTURE_TYPE_WPG, "wpg");

    ImageType(int imageType, String extension) {
        this.imageType = imageType;
        this.extension = extension;
    }

    int imageType;
    String extension;

    public int getImageType() {
        return imageType;
    }

    public String getExtension() {
        return extension;
    }

    public static ImageType findImageTypeForPath(String filePath) {
        filePath = filePath.toLowerCase();
        for (ImageType imageType : ImageType.values()) {
            if (filePath.endsWith(imageType.extension)) {
                return imageType;
            }
        }

        String[] splitPath = filePath.split("\\.");
        throw new UnsupportedImageTypeException(String.format("The %s extension is unsupported",
                splitPath[splitPath.length - 1]));
    }

}
