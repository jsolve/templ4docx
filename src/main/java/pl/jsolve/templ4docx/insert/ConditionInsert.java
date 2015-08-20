package pl.jsolve.templ4docx.insert;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;

public class ConditionInsert {

    private XWPFParagraph startParagraph;
    private Integer startIndex;
    private XWPFParagraph endParagraph;
    private Integer endIndex;
    private boolean found;

    public ConditionInsert() {
    }

    public ConditionInsert(XWPFParagraph startParagraph, Integer startIndex, XWPFParagraph endParagraph,
            Integer endIndex) {
        this.startParagraph = startParagraph;
        this.startIndex = startIndex;
        this.endParagraph = endParagraph;
        this.endIndex = endIndex;
    }

    public XWPFParagraph getStartParagraph() {
        return startParagraph;
    }

    public void setStartParagraph(XWPFParagraph startParagraph) {
        this.startParagraph = startParagraph;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public XWPFParagraph getEndParagraph() {
        return endParagraph;
    }

    public void setEndParagraph(XWPFParagraph endParagraph) {
        this.endParagraph = endParagraph;
    }

    public Integer getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(Integer endIndex) {
        this.endIndex = endIndex;
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

}
