package pl.jsolve.templ4docx.util;

import java.util.List;

import org.apache.poi.xwpf.usermodel.IBody;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;

public class XWPFTableCellWrapper extends XWPFTableCell{

    public XWPFTableCellWrapper(CTTc cell, XWPFTableRow tableRow, IBody part) {
        super(cell, tableRow, part);
    }

    public List<IBodyElement> getBodyElements(){
        return bodyElements;
      }
}
