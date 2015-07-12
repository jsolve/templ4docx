package pl.jsolve.templ4docx.core;

import java.io.FileInputStream;
import java.io.Serializable;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import pl.jsolve.templ4docx.exception.OpenDocxException;

public class Docx implements Serializable {

	private static final long serialVersionUID = 1L;

	private String docxPath;
	private XWPFDocument xdoc = null;

	public Docx(XWPFDocument xdoc) {
		this.xdoc = xdoc;
	}

	public Docx(String docxPath) {
		this.docxPath = docxPath;
		open();
	}

	private void open() {
		try {
			FileInputStream fis = new FileInputStream(docxPath);
			xdoc = new XWPFDocument(fis);
		} catch (Exception ex) {
			throw new OpenDocxException(ex.getMessage(), ex.getCause());
		}
	}

	XWPFDocument getDocument() {
		return xdoc;
	}

}
