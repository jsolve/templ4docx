package pl.jsolve.templ4docx.strategy;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import pl.jsolve.templ4docx.insert.ImageInsert;
import pl.jsolve.templ4docx.insert.Insert;
import pl.jsolve.templ4docx.variable.ImageVariable;
import pl.jsolve.templ4docx.variable.Variable;

public class ImageInsertStrategy implements InsertStrategy {

    @Override
    public void insert(Insert insert, Variable variable) {
        if (!(insert instanceof ImageInsert)) {
            return;
        }
        if (!(variable instanceof ImageVariable)) {
            return;
        }

        ImageInsert imageInsert = (ImageInsert) insert;
        ImageVariable imageVariable = (ImageVariable) variable;
        for (XWPFRun run : imageInsert.getParagraph().getRuns()) {
            String text = run.getText(0);
            if (StringUtils.contains(text, imageInsert.getKey().getKey())) {
                insertPicture(run, imageVariable);
                text = StringUtils.replace(text, imageInsert.getKey().getKey(), "");
                run.setText(text, 0);
            }
        }
    }

    private void insertPicture(XWPFRun r, ImageVariable imageVariable) {
        try {
            r.addPicture(imageVariable.getImageStream(),
                    imageVariable.getImageType().getImageType(), imageVariable.getKey(),
                    Units.toEMU(imageVariable.getWidth()), Units.toEMU(imageVariable.getHeight()));
            imageVariable.getImageStream().reset();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
