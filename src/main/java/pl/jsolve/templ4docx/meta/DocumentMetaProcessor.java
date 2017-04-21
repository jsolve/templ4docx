package pl.jsolve.templ4docx.meta;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.impl.xb.xmlschema.SpaceAttribute;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkupRange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.w3c.dom.Node;

import com.indvd00m.codec.ABCCodec;

import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.core.VariablePattern;
import pl.jsolve.templ4docx.extractor.KeyExtractor;
import pl.jsolve.templ4docx.extractor.VariablesExtractor;
import pl.jsolve.templ4docx.util.Key;
import pl.jsolve.templ4docx.variable.Variables;

/**
 * @author indvd00m
 *
 */
public class DocumentMetaProcessor {

    public static final String VAR_BOOKMARK_PREFIX = "var";

    Pattern bookmarkNamePattern = Pattern.compile("\\Q" + VAR_BOOKMARK_PREFIX + "\\E(\\d+)(.*)");
    ABCCodec abcCodec = new ABCCodec('_', "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890");

    KeyExtractor keyExtractor = new KeyExtractor();
    VariablesExtractor extractor = new VariablesExtractor();

    public void processMetaInformation(Docx docx, Variables variables, VariablePattern variablePattern) {
        List<Key> keys = keyExtractor.extractKeys(variables);
        KeysHolder keysHolder = new KeysHolder(keys);
        XWPFDocument document = docx.getXWPFDocument();
        List<XWPFParagraph> paragraphs = getParagraphs(document);
        for (XWPFParagraph paragraph : paragraphs) {
            moveVariablesToSeparateRun(paragraph, keysHolder, variablePattern);
            markVariablesByBookmarks(paragraph, keysHolder, variablePattern);
            clearVariablesInRunByBookmarks(paragraph, keysHolder, variablePattern);
        }
    }

    /**
     * @param document
     * @return All document paragraphs (including paragraphs in nested tables)
     */
    List<XWPFParagraph> getParagraphs(XWPFDocument document) {
        List<XWPFParagraph> paragraphs = new ArrayList<XWPFParagraph>();
        paragraphs.addAll(document.getParagraphs());
        for (XWPFTable table : document.getTables()) {
            paragraphs.addAll(getParagraphs(table));
        }
        return paragraphs;
    }

    /**
     * @param table
     * @return All table paragraphs (including paragraphs in nested tables)
     */
    List<XWPFParagraph> getParagraphs(XWPFTable table) {
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
     * Split every variable into separate run.
     *
     * @param paragraph
     * @param keys
     * @param variablePattern
     */
    void moveVariablesToSeparateRun(XWPFParagraph paragraph, KeysHolder keysHolder, VariablePattern variablePattern) {
        String paragraphText = paragraph.getText();

        List<XWPFRun> runs = new ArrayList<XWPFRun>(paragraph.getRuns());
        int lastProcessedRunIndex = -1;
        for (XWPFRun run : runs) {
            lastProcessedRunIndex++;
            String text = run.getText(0);
            if (text == null)
                continue;
            XWPFRun lastProcessedRun = run;
            List<String> varNames = extractor.extract(text, variablePattern);
            for (String varName : varNames) {
                if (keysHolder.containsKeyByName(varName)) {
                    lastProcessedRunIndex = splitRunByVariable(paragraph, lastProcessedRun, lastProcessedRunIndex,
                            varName);
                    lastProcessedRun = paragraph.getRuns().get(lastProcessedRunIndex);
                }
            }
        }

        String newParagraphText = paragraph.getText();
        if (!newParagraphText.equals(paragraphText))
            throw new IllegalStateException(
                    String.format("Paragraph text is changed from \"%s\" to \"%s\"!", paragraphText, newParagraphText));
    }

    /**
     * @param paragraph
     * @param run
     * @param runIndex
     *            current run index
     * @param varName
     * @return index of a last new run after splitting source run and creating new runs
     */
    int splitRunByVariable(XWPFParagraph paragraph, XWPFRun run, int runIndex, String varName) {
        String text = run.getText(0);
        if (text == null)
            return runIndex;
        if (text.equals(varName)) {
            return runIndex;
        }

        int varIndex = text.indexOf(varName);
        if (varIndex == -1) {
            return runIndex;
        }

        int start = varIndex;
        int end = varIndex + varName.length();
        String beforeText = text.substring(0, start);
        String currentText = varName;
        String afterText = text.substring(end);

        // before
        if (beforeText.length() > 0) {
            XWPFRun beforeRun = paragraph.insertNewRun(runIndex++);
            applyStyle(run, beforeRun);
            beforeRun.setText(beforeText, 0);
        }
        // current
        {
            run.setText(currentText, 0);
        }
        // after
        if (afterText.length() > 0) {
            XWPFRun afterRun = paragraph.insertNewRun(++runIndex);
            applyStyle(run, afterRun);
            afterRun.setText(afterText, 0);
        }

        return runIndex;
    }

    void cloneRun(XWPFRun source, XWPFRun target) {
        applyStyle(source, target);
        target.setText(source.getText(0), 0);
    }

    void applyStyle(XWPFRun source, XWPFRun target) {
        applyRPr(target, source.getCTR().getRPr());
    }

    void applyRPr(XWPFRun run, CTRPr rPr) {
        CTRPr sourceRPr = run.getCTR().isSetRPr() ? run.getCTR().getRPr() : run.getCTR().addNewRPr();
        sourceRPr.set(rPr);
    }

    /**
     * Every variable in separate run will by marked by bookmark.
     *
     * @param paragraph
     * @param keys
     * @param variablePattern
     */
    void markVariablesByBookmarks(XWPFParagraph paragraph, KeysHolder keysHolder, VariablePattern variablePattern) {
        String paragraphText = paragraph.getText();

        List<CTBookmark> bookmarkStartList = paragraph.getCTP().getBookmarkStartList();
        Map<Node, CTBookmark> bookmarkStartByNode = new HashMap<Node, CTBookmark>();
        for (CTBookmark bookmark : bookmarkStartList) {
            Node node = bookmark.getDomNode();
            bookmarkStartByNode.put(node, bookmark);
        }

        List<CTMarkupRange> bookmarkEndList = paragraph.getCTP().getBookmarkEndList();
        Map<Node, CTMarkupRange> bookmarkEndByNode = new HashMap<Node, CTMarkupRange>();
        for (CTMarkupRange bookmark : bookmarkEndList) {
            Node node = bookmark.getDomNode();
            bookmarkEndByNode.put(node, bookmark);
        }

        for (XWPFRun run : paragraph.getRuns()) {
            String text = run.getText(0);
            if (keysHolder.containsKeyByName(text)) {
                Node node = run.getCTR().getDomNode();
                Node prevNode = node.getPreviousSibling();
                Node nextNode = node.getNextSibling();
                CTBookmark startBookmark = bookmarkStartByNode.get(prevNode);
                CTMarkupRange endBookmark = bookmarkEndByNode.get(nextNode);
                fixIdInBookmarkNameIfNeed(startBookmark, variablePattern);
                if (!isVarInRunMarkedByBookmark(run, startBookmark, endBookmark, variablePattern)) {
                    markVarInRunByBookmark(paragraph, run, variablePattern);
                }
            }
        }

        String newParagraphText = paragraph.getText();
        if (!newParagraphText.equals(paragraphText))
            throw new IllegalStateException(
                    String.format("Paragraph text is changed from \"%s\" to \"%s\"!", paragraphText, newParagraphText));
    }

    /**
     * After editing document in office (Libre, MS) id in bookmark name will be differ from actual bookmark id, so
     * bookmark name must be fixed.
     *
     * @param bookmark
     */
    void fixIdInBookmarkNameIfNeed(CTBookmark bookmark, VariablePattern variablePattern) {
        if (bookmark == null)
            return;
        String name = getDecodedBookmarkName(bookmark, variablePattern);
        BigInteger id = bookmark.getId();

        String varName = bookmarkNameToVarName(name, variablePattern);
        BigInteger idInName = bookmarkNameToId(name);
        if (varName != null && idInName != null) {
            // this is bookmark for variable
            if (!idInName.equals(id)) {
                // actual bookmark id differ from id in bookmark name
                String fixedName = varNameToBookmarkName(varName, id, variablePattern);
                setEncodedBookmarkName(bookmark, fixedName, variablePattern);
            }
        }
    }

    /**
     * Return {@code true}, if run contains variable and this is run is marked by bookmark as variable.
     *
     * @param run
     * @param startBookmark
     * @param endBookmark
     * @param variablePattern
     * @return
     */
    boolean isVarInRunMarkedByBookmark(XWPFRun run, CTBookmark startBookmark, CTMarkupRange endBookmark,
            VariablePattern variablePattern) {
        if (startBookmark == null)
            return false;
        if (endBookmark == null)
            return false;

        String varName = run.getText(0);
        if (!varName.startsWith(variablePattern.getOriginalPrefix()))
            return false;
        if (!varName.endsWith(variablePattern.getOriginalSuffix()))
            return false;

        BigInteger bookmarkId = startBookmark.getId();
        if (!bookmarkId.equals(endBookmark.getId()))
            return false;

        String name = getDecodedBookmarkName(startBookmark, variablePattern);
        String expectedName = varNameToBookmarkName(varName, bookmarkId, variablePattern);

        return expectedName.equals(name);
    }

    /**
     * Return {@code true}, if run is marked by bookmark as variable.
     *
     * @param run
     * @param startBookmark
     * @param endBookmark
     * @param variablePattern
     * @return
     */
    boolean isRunMarkedAsVariableByBookmark(XWPFRun run, CTBookmark startBookmark, CTMarkupRange endBookmark,
            VariablePattern variablePattern) {
        if (startBookmark == null)
            return false;
        if (endBookmark == null)
            return false;

        BigInteger bookmarkId = startBookmark.getId();
        if (!bookmarkId.equals(endBookmark.getId()))
            return false;

        String bookmarkName = getDecodedBookmarkName(startBookmark, variablePattern);

        String varName = bookmarkNameToVarName(bookmarkName, variablePattern);
        if (varName == null)
            return false;

        return true;
    }

    /**
     * Bookmark name will be contain id. This hack will be allow use one variable more times in same document.
     *
     * @param varName
     * @param id
     * @return
     */
    String varNameToBookmarkName(String varName, BigInteger id, VariablePattern variablePattern) {
        String prefix = variablePattern.getOriginalPrefix();
        String suffix = variablePattern.getOriginalSuffix();
        if (varName.startsWith(prefix) && varName.endsWith(suffix)) {
            varName = varName.substring(prefix.length(), varName.length() - suffix.length());
        }
        return String.format("%s%d%s", VAR_BOOKMARK_PREFIX, id, varName);
    }

    /**
     * Extract variable name from bookmark name.
     *
     * @param bookmarkName
     * @return
     */
    String bookmarkNameToVarName(String bookmarkName, VariablePattern variablePattern) {
        Matcher matcher = bookmarkNamePattern.matcher(bookmarkName);
        if (!matcher.matches())
            return null;
        String varName = matcher.group(2);
        varName = variablePattern.getOriginalPrefix() + varName + variablePattern.getOriginalSuffix();
        return varName;
    }

    /**
     * Extract bookmark id from bookmark name.
     *
     * @param bookmarkName
     * @return
     */
    BigInteger bookmarkNameToId(String bookmarkName) {
        Matcher matcher = bookmarkNamePattern.matcher(bookmarkName);
        if (!matcher.matches())
            return null;
        String sId = matcher.group(1);
        BigInteger id = new BigInteger(sId);
        return id;
    }

    /**
     * @param document
     * @return Max bookmark id in document
     */
    BigInteger getMaxBookmarkId(XWPFDocument document) {
        BigInteger maxId = new BigInteger("0");
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            CTP ctp = paragraph.getCTP();
            for (CTBookmark bookmark : ctp.getBookmarkStartList()) {
                BigInteger id = bookmark.getId();
                if (maxId.compareTo(id) < 0) {
                    maxId = id;
                }
            }
        }
        return maxId;
    }

    /**
     * Create {@code w:bookmarkStart} before run with variable and {@code w:bookmarkEnd} after run with variable.
     *
     * @param paragraph
     * @param run
     * @param variablePattern
     */
    void markVarInRunByBookmark(XWPFParagraph paragraph, XWPFRun run, VariablePattern variablePattern) {

        // check variable name with pattern
        String varName = run.getText(0);
        if (!varName.startsWith(variablePattern.getOriginalPrefix()))
            return;
        if (!varName.endsWith(variablePattern.getOriginalSuffix()))
            return;

        // getting new id and name for bookmark
        BigInteger id = getMaxBookmarkId(paragraph.getDocument()).add(new BigInteger("1"));
        String name = varNameToBookmarkName(varName, id, variablePattern);

        // add bookmark to end of paragraph
        CTP ctp = paragraph.getCTP();
        CTBookmark startBookmark = ctp.addNewBookmarkStart();
        startBookmark.setId(id);
        setEncodedBookmarkName(startBookmark, name, variablePattern);

        CTMarkupRange endBookmark = ctp.addNewBookmarkEnd();
        endBookmark.setId(id);

        // move bookmark to actual run
        Node paragraphNode = ctp.getDomNode();
        Node runNode = run.getCTR().getDomNode();
        Node startBookmarkNode = startBookmark.getDomNode();
        Node endBookmarkNode = endBookmark.getDomNode();

        paragraphNode.insertBefore(startBookmarkNode, runNode);
        paragraphNode.insertBefore(endBookmarkNode, runNode.getNextSibling());
    }

    /**
     * Replace every run between {@code w:bookmarkStart} and {@code w:bookmarkEnd} with the variable name.
     *
     * @param paragraph
     * @param keys
     * @param variablePattern
     */
    void clearVariablesInRunByBookmarks(XWPFParagraph paragraph, KeysHolder keysHolder,
            VariablePattern variablePattern) {
        List<CTBookmark> bookmarkStartList = paragraph.getCTP().getBookmarkStartList();
        Map<Node, CTBookmark> bookmarkStartByNode = new HashMap<Node, CTBookmark>();
        for (CTBookmark bookmark : bookmarkStartList) {
            Node node = bookmark.getDomNode();
            bookmarkStartByNode.put(node, bookmark);
        }

        List<CTMarkupRange> bookmarkEndList = paragraph.getCTP().getBookmarkEndList();
        Map<Node, CTMarkupRange> bookmarkEndByNode = new HashMap<Node, CTMarkupRange>();
        for (CTMarkupRange bookmark : bookmarkEndList) {
            Node node = bookmark.getDomNode();
            bookmarkEndByNode.put(node, bookmark);
        }

        for (XWPFRun run : paragraph.getRuns()) {
            Node node = run.getCTR().getDomNode();
            Node prevNode = node.getPreviousSibling();
            Node nextNode = node.getNextSibling();
            CTBookmark startBookmark = bookmarkStartByNode.get(prevNode);
            CTMarkupRange endBookmark = bookmarkEndByNode.get(nextNode);
            fixIdInBookmarkNameIfNeed(startBookmark, variablePattern);
            if (isRunMarkedAsVariableByBookmark(run, startBookmark, endBookmark, variablePattern)) {
                String varName = bookmarkNameToVarName(getDecodedBookmarkName(startBookmark, variablePattern),
                        variablePattern);
                if (keysHolder.containsKeyByName(varName)) {
                    run.setText(varName, 0);
                }
            }
        }
    }

    void setTextWithPreserveSpace(XWPFRun run, String value) {
        CTR ctr = run.getCTR();
        int pos = 0;
        CTText t = (pos < ctr.sizeOfTArray() && pos >= 0) ? ctr.getTArray(pos) : ctr.addNewT();
        t.setStringValue(value);
        t.setSpace(SpaceAttribute.Space.PRESERVE);
    }

    String getDecodedBookmarkName(CTBookmark bookmark, VariablePattern variablePattern) {
        String name = bookmark.getName();
        Matcher matcher = bookmarkNamePattern.matcher(name);
        if (matcher.matches()) {
            String encodedVarName = matcher.group(2);
            if (abcCodec.isMatchesToAlphabet(encodedVarName)) {
                String decodedVarName = abcCodec.decode(encodedVarName);
                return varNameToBookmarkName(decodedVarName, bookmark.getId(), variablePattern);
            } else {
                return name;
            }
        } else {
            return name;
        }
    }

    void setEncodedBookmarkName(CTBookmark bookmark, String name, VariablePattern variablePattern) {
        Matcher matcher = bookmarkNamePattern.matcher(name);
        if (matcher.matches()) {
            String varName = matcher.group(2);
            String encodedVarName = abcCodec.encode(varName);
            String encodedBookmarkName = varNameToBookmarkName(encodedVarName, bookmark.getId(), variablePattern);
            bookmark.setName(encodedBookmarkName);
        } else {
            bookmark.setName(name);
        }
    }

}
