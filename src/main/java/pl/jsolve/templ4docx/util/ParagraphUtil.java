package pl.jsolve.templ4docx.util;


import org.apache.poi.xwpf.usermodel.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This class supply some methods for paragraph processing.
 *
 * @author Keyon
 */

public class ParagraphUtil {
    /**
     * Moved from pl.jsolve.templ4docx.meta.DocumentMetaProcessor
     * @param table
     * @return All table paragraphs
     */
    protected static List<XWPFParagraph> getParagraphs(XWPFTable table) {
        List<XWPFParagraph> paragraphs = new ArrayList<XWPFParagraph>();
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                paragraphs.addAll(cell.getParagraphs());
                for (XWPFTable cellTable : cell.getTables()) {
                    paragraphs.addAll(getParagraphs(cellTable));
                }
            }
        }
        return paragraphs;
    }

    /**
     * @param document
     * @param includeTables if include paragraphs in tables.
     * @return All document paragraphs, includes header and footer
     */
    public static List<XWPFParagraph> getAllParagraphs(XWPFDocument document, boolean includeTables) {
        List<XWPFParagraph> paragraphs = new ArrayList<XWPFParagraph>();
        paragraphs.addAll(document.getParagraphs());
        if (includeTables) {
            for (XWPFTable table : document.getTables()) {
                paragraphs.addAll(getParagraphs(table));
            }
        }

        for (XWPFHeader header : document.getHeaderList()) {
            paragraphs.addAll(header.getParagraphs());
            if (includeTables) {
                for (XWPFTable table : header.getTables()) {
                    paragraphs.addAll(getParagraphs(table));
                }
            }
        }

        for (XWPFFooter footer : document.getFooterList()) {
            paragraphs.addAll(footer.getParagraphs());
            if (includeTables) {
                for (XWPFTable table : footer.getTables()) {
                    paragraphs.addAll(getParagraphs(table));
                }
            }
        }

        return paragraphs;
    }
}
