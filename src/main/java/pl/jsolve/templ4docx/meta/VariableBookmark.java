package pl.jsolve.templ4docx.meta;

import java.math.BigInteger;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkupRange;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class VariableBookmark {

    String varName;
    BigInteger id;
    CTBookmark bookmarkStart;
    CTMarkupRange bookmarkEnd;

    public VariableBookmark(String varName, BigInteger id, CTBookmark bookmarkStart, CTMarkupRange bookmarkEnd) {
        super();
        this.varName = varName;
        this.id = id;
        this.bookmarkStart = bookmarkStart;
        this.bookmarkEnd = bookmarkEnd;
    }

    public String getVarName() {
        return varName;
    }

    public BigInteger getId() {
        return id;
    }

    public CTBookmark getBookmarkStart() {
        return bookmarkStart;
    }

    public CTMarkupRange getBookmarkEnd() {
        return bookmarkEnd;
    }

}
