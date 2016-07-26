package pl.jsolve.templ4docx.cleaner;

import java.util.List;

import pl.jsolve.sweetener.collection.Collections;
import pl.jsolve.templ4docx.insert.ParagraphInsert;

/**
 * Container class which contains information about paragraphs intended to remove.
 * @author Lukasz Stypka
 */
public class ParagraphCleaner {

    private List<ParagraphInsert> paragraphs;

    public ParagraphCleaner() {
        this.paragraphs = Collections.newArrayList();
    }

    /**
     * Add new paragraph which should be removed from document
     * @param paragraph ParagraphInsert
     */
    public void add(ParagraphInsert paragraph) {
        this.paragraphs.add(paragraph);
    }

    /**
     * Get list of paragraphs which should be removed from document
     * @return List of paragraphs
     */
    public List<ParagraphInsert> getParagraphs() {
        return paragraphs;
    }

}
