package pl.jsolve.templ4docx.strategy;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import pl.jsolve.templ4docx.insert.Insert;
import pl.jsolve.templ4docx.insert.TextInsert;
import pl.jsolve.templ4docx.variable.TextVariable;
import pl.jsolve.templ4docx.variable.Variable;

public class TextInsertStrategy implements InsertStrategy {

    @Override
	public void insert(Insert insert, Variable variable) {
		if (!(insert instanceof TextInsert)) {
			return;
		}
		if (!(variable instanceof TextVariable)) {
			return;
		}

		TextInsert textInsert = (TextInsert) insert;
		TextVariable textVariable = (TextVariable) variable;

		XWPFParagraph paragraph = textInsert.getParagraph();
		TextSegement found = paragraph.searchText(textInsert.getKey().getKey(), new PositionInParagraph());

		if (found != null) {
			if (found.getBeginRun() == found.getEndRun()) {

				for (XWPFRun run : paragraph.getRuns()) {
					String runText = run.getText(0);
					if (StringUtils.contains(runText, textInsert.getKey().getKey())) {
						runText = StringUtils.replace(runText, textVariable.getKey(), textVariable.getValue());
						run.setText(runText, 0);
					}
				}
			} else {
				StringBuilder b = new StringBuilder();
				for (int runPos = found.getBeginRun(); runPos <= found.getEndRun(); runPos++) {
					XWPFRun run = paragraph.getRuns().get(runPos);
					b.append(run.getText(run.getTextPosition()));
				}
				String connectedRuns = b.toString();

				if (StringUtils.contains(connectedRuns, textInsert.getKey().getKey())) {
					connectedRuns = StringUtils.replace(connectedRuns, textVariable.getKey(), textVariable.getValue());
					XWPFRun partOne = paragraph.getRuns().get(found.getBeginRun());
					partOne.setText(connectedRuns, 0);
				}

				for (int runPos = found.getBeginRun() + 1; runPos <= found.getEndRun(); runPos++) {
					XWPFRun partNext = paragraph.getRuns().get(runPos);
					partNext.setText("", 0);
				}
			}
		}

	}
}
