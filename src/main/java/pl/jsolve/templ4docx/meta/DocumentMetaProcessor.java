package pl.jsolve.templ4docx.meta;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkupRange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.core.VariablePattern;
import pl.jsolve.templ4docx.extractor.KeyExtractor;
import pl.jsolve.templ4docx.extractor.VariablesExtractor;
import pl.jsolve.templ4docx.util.Key;
import pl.jsolve.templ4docx.util.ParagraphUtil;
import pl.jsolve.templ4docx.variable.Variables;

/**
 * <p>
 * {@link DocumentMetaProcessor} allow reuse generated from template files for repeated usage as templates. So you can
 * generate file from template, edit this generated file (in text processor for example) and update variables values
 * after editing. Then edit file again and update values again, and again.
 * </p>
 *
 * <p>
 * It is possible by injecting meta information into generated docx-file. Every text occurrence of variable
 * ({@code ${var1}} for example) will be moved to separate {@code Run} object and this {@code Run} object will be marked
 * by bookmark ({@code w:bookmarkStart} before {@code w:r} and {@code w:bookmarkEnd} after them).
 * </p>
 *
 * <p>
 * Bookmark name looks like this: {@code var_1_0DE5D44A9BA046A9A70C921BCFBCBAB7}, where "var" is a prefix, 1 is bookmark
 * id and "0DE5D44A9BA046A9A70C921BCFBCBAB7" is a md5 hash code of variable name ("${var1}" in this case). Bookmark name
 * contain id, this hack will be allow use one variable more times in same document (because of bookmark name must be
 * unique).
 * </p>
 *
 * <p>
 * Before every of fill of template by variable values {@link DocumentMetaProcessor} will search such bookmarks and
 * replace any text of {@code Run} object in this bookmarks by variable name ("${var1}" in this case). Then
 * {@code DocumentExecutor} will update variable values as usual.
 * </p>
 *
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class DocumentMetaProcessor {

    public static final String VAR_BOOKMARK_PREFIX = "var";

    protected Pattern bookmarkNamePattern = Pattern.compile("^\\Q" + VAR_BOOKMARK_PREFIX + "\\E_(\\d{1,3})_(\\w{32})$");

    protected KeyExtractor keyExtractor = new KeyExtractor();
    protected VariablesExtractor extractor = new VariablesExtractor();

    public void processMetaInformation(Docx docx, Variables variables, VariablePattern variablePattern) {
        List<Key> keys = keyExtractor.extractKeys(variables);
        KeysHolder keysHolder = new KeysHolder(keys);
        XWPFDocument document = docx.getXWPFDocument();
        List<XWPFParagraph> paragraphs = ParagraphUtil.getAllParagraphs(document, true);
        for (XWPFParagraph paragraph : paragraphs) {
            moveVariablesToSeparateRun(paragraph, keysHolder, variablePattern);
            markVariablesByBookmarks(paragraph, keysHolder, variablePattern);
            clearVariablesInRunByBookmarks(paragraph, keysHolder);
        }
    }

    /**
     * Split every variable into separate run.
     *
     * @param paragraph
     * @param keysHolder
     * @param variablePattern
     */
    protected void moveVariablesToSeparateRun(XWPFParagraph paragraph, KeysHolder keysHolder,
            VariablePattern variablePattern) {
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
    protected int splitRunByVariable(XWPFParagraph paragraph, XWPFRun run, int runIndex, String varName) {
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

    protected void cloneRun(XWPFRun source, XWPFRun target) {
        applyStyle(source, target);
        target.setText(source.getText(0), 0);
    }

    protected void applyStyle(XWPFRun source, XWPFRun target) {
        applyRPr(target, source.getCTR().getRPr());
    }

    protected void applyRPr(XWPFRun run, CTRPr rPr) {
        CTRPr sourceRPr = run.getCTR().isSetRPr() ? run.getCTR().getRPr() : run.getCTR().addNewRPr();
        sourceRPr.set(rPr);
    }

    /**
     * Every variable in separate run will by marked by bookmark.
     *
     * @param paragraph
     * @param keysHolder
     * @param variablePattern
     */
    protected void markVariablesByBookmarks(XWPFParagraph paragraph, KeysHolder keysHolder,
            VariablePattern variablePattern) {
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
            String varName = run.getText(0);
            if (keysHolder.containsKeyByName(varName)) {
                List<VariableBookmark> varBookmarks = getVariableBookmarks(run, keysHolder);
                if (varBookmarks.isEmpty()) {
                    markVarInRunByBookmark(paragraph, run);
                } else {
                    // after editing document in office (Libre, MS) id in bookmark name will be differ from actual
                    // bookmark id, so bookmark name must be updated
                    for (VariableBookmark varBookmark : varBookmarks) {
                        CTBookmark bookmarkStart = varBookmark.getBookmarkStart();
                        setEncodedBookmarkName(bookmarkStart, varBookmark.getVarName());
                    }
                }
            }
        }

        String newParagraphText = paragraph.getText();
        if (!newParagraphText.equals(paragraphText))
            throw new IllegalStateException(
                    String.format("Paragraph text is changed from \"%s\" to \"%s\"!", paragraphText, newParagraphText));
    }

    /**
     * Find bookmarks used as meta information for variables near the {@code run}.
     *
     * @param run
     * @param keysHolder
     * @return
     */
    protected List<VariableBookmark> getVariableBookmarks(XWPFRun run, KeysHolder keysHolder) {
        List<VariableBookmark> varBookmarks = new ArrayList<VariableBookmark>();

        // paragraph
        IRunBody parent = run.getParent();
        if (parent instanceof XWPFParagraph == false)
            return varBookmarks;
        XWPFParagraph paragraph = (XWPFParagraph) parent;

        // bookmarkStartByNode
        Map<Node, CTBookmark> bookmarkStartByNode = new HashMap<Node, CTBookmark>();
        for (CTBookmark bookmark : paragraph.getCTP().getBookmarkStartList()) {
            Node node = bookmark.getDomNode();
            bookmarkStartByNode.put(node, bookmark);
        }

        // bookmarkEndByNode
        Map<Node, CTMarkupRange> bookmarkEndByNode = new HashMap<Node, CTMarkupRange>();
        for (CTMarkupRange bookmark : paragraph.getCTP().getBookmarkEndList()) {
            Node node = bookmark.getDomNode();
            bookmarkEndByNode.put(node, bookmark);
        }

        Node node = run.getCTR().getDomNode();

        // bookmarkStartById
        Map<BigInteger, CTBookmark> bookmarkStartById = new LinkedHashMap<BigInteger, CTBookmark>();
        {
            Node prevNode = node.getPreviousSibling();
            while (prevNode != null) {
                CTBookmark bookmarkStart = bookmarkStartByNode.get(prevNode);
                if (bookmarkStart != null) {
                    bookmarkStartById.put(bookmarkStart.getId(), bookmarkStart);
                }
                prevNode = prevNode.getPreviousSibling();
            }
        }

        // bookmarkEndById
        Map<BigInteger, CTMarkupRange> bookmarkEndById = new HashMap<BigInteger, CTMarkupRange>();
        {
            Node nextNode = node.getNextSibling();
            while (nextNode != null) {
                CTMarkupRange bookmarkEnd = bookmarkEndByNode.get(nextNode);
                if (bookmarkEnd != null) {
                    bookmarkEndById.put(bookmarkEnd.getId(), bookmarkEnd);
                }
                nextNode = nextNode.getNextSibling();
            }
        }

        // retain only ended bookmarks
        bookmarkStartById.keySet().retainAll(bookmarkEndById.keySet());

        // collect variable bookmarks
        for (Entry<BigInteger, CTBookmark> e : bookmarkStartById.entrySet()) {
            BigInteger id = e.getKey();
            CTBookmark bookmarkStart = e.getValue();
            CTMarkupRange bookmarkEnd = bookmarkEndById.get(id);

            if (bookmarkStart == null)
                continue;
            if (bookmarkEnd == null)
                continue;

            String varName = getDecodedVarName(bookmarkStart, bookmarkEnd, keysHolder);
            if (varName == null)
                continue;

            VariableBookmark varBookmark = new VariableBookmark(varName, id, bookmarkStart, bookmarkEnd);
            varBookmarks.add(varBookmark);
        }

        return varBookmarks;
    }

    /**
     * Find bookmarks used as meta information for variables into the {@code paragraph}.
     *
     * @param paragraph
     * @param keysHolder
     * @return
     */
    protected List<VariableBookmark> getVariableBookmarks(XWPFParagraph paragraph, KeysHolder keysHolder) {
        List<VariableBookmark> varBookmarks = new ArrayList<VariableBookmark>();

        // bookmarkStartById
        Map<BigInteger, CTBookmark> bookmarkStartById = new LinkedHashMap<BigInteger, CTBookmark>();
        for (CTBookmark bookmark : paragraph.getCTP().getBookmarkStartList()) {
            BigInteger id = bookmark.getId();
            bookmarkStartById.put(id, bookmark);
        }

        // bookmarkEndById
        Map<BigInteger, CTMarkupRange> bookmarkEndById = new HashMap<BigInteger, CTMarkupRange>();
        for (CTMarkupRange bookmark : paragraph.getCTP().getBookmarkEndList()) {
            BigInteger id = bookmark.getId();
            bookmarkEndById.put(id, bookmark);
        }

        // retain only ended bookmarks
        bookmarkStartById.keySet().retainAll(bookmarkEndById.keySet());

        // collect variable bookmarks
        for (Entry<BigInteger, CTBookmark> e : bookmarkStartById.entrySet()) {
            BigInteger id = e.getKey();
            CTBookmark bookmarkStart = e.getValue();
            CTMarkupRange bookmarkEnd = bookmarkEndById.get(id);

            if (bookmarkStart == null)
                continue;
            if (bookmarkEnd == null)
                continue;

            String varName = getDecodedVarName(bookmarkStart, bookmarkEnd, keysHolder);
            if (varName == null)
                continue;

            VariableBookmark varBookmark = new VariableBookmark(varName, id, bookmarkStart, bookmarkEnd);
            varBookmarks.add(varBookmark);
        }

        return varBookmarks;
    }

    /**
     * @param document
     * @return Max bookmark id in document
     */
    protected BigInteger getMaxBookmarkId(XWPFDocument document) {
        BigInteger maxId = new BigInteger("0");
        for (XWPFParagraph paragraph : ParagraphUtil.getAllParagraphs(document, true)) {
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
     */
    protected void markVarInRunByBookmark(XWPFParagraph paragraph, XWPFRun run) {

        String varName = run.getText(0);

        // getting new id for bookmark
        BigInteger id = getMaxBookmarkId(paragraph.getDocument()).add(new BigInteger("1"));

        // add bookmark to end of paragraph
        CTP ctp = paragraph.getCTP();
        CTBookmark bookmarkStart = ctp.addNewBookmarkStart();
        bookmarkStart.setId(id);
        setEncodedBookmarkName(bookmarkStart, varName);

        CTMarkupRange bookmarkEnd = ctp.addNewBookmarkEnd();
        bookmarkEnd.setId(id);

        alignNodes(paragraph, run, bookmarkStart, bookmarkEnd);
    }

    /**
     * Move bookmark to actual run.
     *
     * @param paragraph
     * @param run
     * @param bookmarkStart
     * @param bookmarkEnd
     */
    protected void alignNodes(XWPFParagraph paragraph, XWPFRun run, CTBookmark bookmarkStart,
            CTMarkupRange bookmarkEnd) {
        Node paragraphNode = paragraph.getCTP().getDomNode();
        Node runNode = run.getCTR().getDomNode();
        Node bookmarkStartNode = bookmarkStart.getDomNode();
        Node bookmarkEndNode = bookmarkEnd.getDomNode();

        paragraphNode.insertBefore(bookmarkStartNode, runNode);
        paragraphNode.insertBefore(bookmarkEndNode, runNode.getNextSibling());
    }

    /**
     * Replace content of variable run between all {@code w:bookmarkStart} and {@code w:bookmarkEnd} with the variable
     * name. All other runs inside variable bookmarks will be removed.
     *
     * @param paragraph
     * @param keysHolder
     */
    protected void clearVariablesInRunByBookmarks(XWPFParagraph paragraph, KeysHolder keysHolder) {
        List<VariableBookmark> varBookmarks = getVariableBookmarks(paragraph, keysHolder);
        for (VariableBookmark varBookmark : varBookmarks) {
            clearVariableBookmark(paragraph, varBookmark, keysHolder);
        }
    }

    /**
     * Replace content of variable run between {@code w:bookmarkStart} and {@code w:bookmarkEnd} with the variable name.
     * All other runs inside variable bookmark will be removed.
     *
     * @param paragraph
     * @param varBookmark
     * @param keysHolder
     */
    protected void clearVariableBookmark(XWPFParagraph paragraph, VariableBookmark varBookmark, KeysHolder keysHolder) {
        Node paragraphNode = paragraph.getCTP().getDomNode();
        if (!containsInNode(paragraphNode, varBookmark))
            return;

        CTBookmark bookmarkStart = varBookmark.getBookmarkStart();
        CTMarkupRange bookmarkEnd = varBookmark.getBookmarkEnd();

        // find or create variable run
        XWPFRun varRun;
        boolean needNodeAlign = false;
        {
            int varRunIndex = getVariableRunIndex(paragraph, varBookmark);
            int firstRunIndex = getFirstRunIndex(paragraph, varBookmark);
            if (firstRunIndex != -1) {
                // varRun must be first run inside bookmark
                List<XWPFRun> runs = paragraph.getRuns();
                if (varRunIndex == -1) {
                    throw new IllegalStateException("Incorrect index of variable Run");
                }
                varRun = runs.get(varRunIndex);
                XWPFRun firstRun = runs.get(firstRunIndex);
                if (varRunIndex != firstRunIndex) {
                    applyStyle(varRun, firstRun);
                }
                varRun = firstRun;

                // remove all runs except run with variable
                for (int i = runs.size() - 1; i >= 0; i--) {
                    XWPFRun r = runs.get(i);
                    if (r.equals(varRun))
                        continue;
                    Node n = r.getCTR().getDomNode();
                    if (containsInVariableBookmark(varBookmark, n))
                        paragraph.removeRun(i);
                }
            } else {
                // bookmark doesn't contains any run, so we must create new
                if (varRunIndex != -1) {
                    throw new IllegalStateException("Incorrect index of variable Run");
                }
                int nextRunIndex = getNextRunIndex(paragraph, varBookmark);
                varRun = paragraph.insertNewRun(nextRunIndex);
                int prevRunIndex = getPrevRunIndex(paragraph, varBookmark);
                if (prevRunIndex != -1) {
                    XWPFRun prevRun = paragraph.getRuns().get(prevRunIndex);
                    applyStyle(prevRun, varRun);
                }
                needNodeAlign = true;
            }
        }

        // after editing document in office (Libre, MS) id in bookmark name will be differ from actual
        // bookmark id, so bookmark name must be updated too
        String varName = varBookmark.getVarName();
        setEncodedBookmarkName(bookmarkStart, varName);
        varRun.setText(varName, 0);

        if (needNodeAlign) {
            alignNodes(paragraph, varRun, bookmarkStart, bookmarkEnd);
        }
    }

    protected boolean containsInNode(Node node, VariableBookmark varBookmark) {
        CTBookmark bookmarkStart = varBookmark.getBookmarkStart();
        CTMarkupRange bookmarkEnd = varBookmark.getBookmarkEnd();
        Node bookmarkStartNode = bookmarkStart.getDomNode();
        Node bookmarkEndNode = bookmarkEnd.getDomNode();

        if (!containsInNode(node, bookmarkStartNode))
            return false;
        if (!containsInNode(node, bookmarkEndNode))
            return false;

        return true;
    }

    protected boolean containsInNode(Node container, Node node) {
        NodeList childNodes = container.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.equals(node))
                return true;
            if (containsInNode(child, node))
                return true;
        }
        return false;
    }

    protected boolean containsInVariableBookmark(VariableBookmark varBookmark, Node node) {
        CTBookmark bookmarkStart = varBookmark.getBookmarkStart();
        CTMarkupRange bookmarkEnd = varBookmark.getBookmarkEnd();
        Node bookmarkStartNode = bookmarkStart.getDomNode();
        Node bookmarkEndNode = bookmarkEnd.getDomNode();

        if (isBeforeNode(bookmarkStartNode, node))
            return false;
        if (isAfterNode(bookmarkEndNode, node))
            return false;

        return true;
    }

    protected boolean isBeforeNode(Node source, Node target) {
        Node prevNode = source.getPreviousSibling();
        while (prevNode != null) {
            if (prevNode.equals(target))
                return true;
            prevNode = prevNode.getPreviousSibling();
        }
        return false;
    }

    protected boolean isAfterNode(Node source, Node target) {
        Node nextNode = source.getNextSibling();
        while (nextNode != null) {
            if (nextNode.equals(target))
                return true;
            nextNode = nextNode.getNextSibling();
        }
        return false;
    }

    protected int getVariableRunIndex(XWPFParagraph paragraph, VariableBookmark varBookmark) {
        String varName = varBookmark.getVarName();

        int index = -1;
        List<XWPFRun> runs = paragraph.getRuns();
        for (int i = 0; i < runs.size(); i++) {
            XWPFRun run = runs.get(i);
            Node node = run.getCTR().getDomNode();
            if (!containsInVariableBookmark(varBookmark, node))
                continue;
            if (index == -1) {
                index = i;
            } else {
                String text = run.getText(0);
                if (varName.equals(text)) {
                    index = i;
                    break;
                }
            }
        }

        return index;
    }

    protected int getFirstRunIndex(XWPFParagraph paragraph, VariableBookmark varBookmark) {
        List<XWPFRun> runs = paragraph.getRuns();
        for (int i = 0; i < runs.size(); i++) {
            XWPFRun run = runs.get(i);
            Node node = run.getCTR().getDomNode();
            if (!containsInVariableBookmark(varBookmark, node))
                continue;
            return i;
        }
        return -1;
    }

    protected int getNextRunIndex(XWPFParagraph paragraph, VariableBookmark varBookmark) {
        Map<Node, XWPFRun> runByNode = new HashMap<Node, XWPFRun>();
        Map<XWPFRun, Integer> runIndexes = new HashMap<XWPFRun, Integer>();
        List<XWPFRun> runs = paragraph.getRuns();
        for (int i = 0; i < runs.size(); i++) {
            XWPFRun run = runs.get(i);
            Node node = run.getCTR().getDomNode();
            runByNode.put(node, run);
            runIndexes.put(run, i);
        }
        Node bookmarkEndNode = varBookmark.getBookmarkEnd().getDomNode();
        Node nextNode = bookmarkEndNode.getNextSibling();
        while (nextNode != null) {
            XWPFRun run = runByNode.get(nextNode);
            if (run != null) {
                int index = runIndexes.get(run).intValue();
                return index;
            }
            nextNode = nextNode.getNextSibling();
        }
        return runs.size();
    }

    protected int getPrevRunIndex(XWPFParagraph paragraph, VariableBookmark varBookmark) {
        Map<Node, XWPFRun> runByNode = new HashMap<Node, XWPFRun>();
        Map<XWPFRun, Integer> runIndexes = new HashMap<XWPFRun, Integer>();
        List<XWPFRun> runs = paragraph.getRuns();
        for (int i = 0; i < runs.size(); i++) {
            XWPFRun run = runs.get(i);
            Node node = run.getCTR().getDomNode();
            runByNode.put(node, run);
            runIndexes.put(run, i);
        }
        Node bookmarkStartNode = varBookmark.getBookmarkStart().getDomNode();
        Node prevNode = bookmarkStartNode.getPreviousSibling();
        while (prevNode != null) {
            XWPFRun run = runByNode.get(prevNode);
            if (run != null) {
                int index = runIndexes.get(run).intValue();
                return index;
            }
            prevNode = prevNode.getPreviousSibling();
        }
        return -1;
    }

    /**
     * Extract decoded variable name from bookmark name.
     *
     * @param bookmarkStart
     * @param keysHolder
     * @return
     */
    protected String getDecodedVarName(CTBookmark bookmarkStart, CTMarkupRange bookmarkEnd, KeysHolder keysHolder) {
        if (bookmarkStart == null)
            return null;
        if (bookmarkEnd == null)
            return null;

        BigInteger bookmarkId = bookmarkStart.getId();
        if (!bookmarkId.equals(bookmarkEnd.getId()))
            return null;

        String bookmarkName = bookmarkStart.getName();
        Matcher matcher = bookmarkNamePattern.matcher(bookmarkName);
        if (matcher.matches()) {
            String encodedVarName = matcher.group(2);
            if (keysHolder.containsKeyByMD5Name(encodedVarName)) {
                String decodedVarName = keysHolder.getKeyNameByMD5Name(encodedVarName);
                return decodedVarName;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Encode bookmark name. Bookmark name will be contain id. This hack will be allow use one variable more times in
     * same document.
     *
     * @param bookmark
     * @param varName
     */
    protected void setEncodedBookmarkName(CTBookmark bookmark, String varName) {
        String encodedVarName = DigestUtils.md5Hex(varName).toUpperCase();
        String encodedBookmarkName = String.format("%s_%d_%s", VAR_BOOKMARK_PREFIX, bookmark.getId(), encodedVarName);
        bookmark.setName(encodedBookmarkName);
    }

}
