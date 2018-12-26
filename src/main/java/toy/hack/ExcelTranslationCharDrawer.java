package toy.hack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import toy.CharStat;
import toy.common.ExcelParser;
import toy.common.ExcelParser.RowCallback;
import toy.hack.TranslationScriptReader.Callback;
import toy.util.Util;

public class ExcelTranslationCharDrawer {
	
	private String lastTitle;
	private StringBuilder perEvent = new StringBuilder();
	
	public void drawAllChars(String rscName, Map<String,CharStat> char_count) throws IOException {
		File rscFile = ExcelFileFinder.getNewestTranslation(rscName);
		InputStream excel = new FileInputStream(rscFile);
		new ExcelParser(excel, rscFile.getName()).parse(1, new RowCallback() {
			
			@Override
			public void doInRow(List<String> strs, int rowNum) {
				String thisTitle = strs.get(0);
				if(lastTitle!=null && !thisTitle.equals(lastTitle)) {
					plusCharCount(perEvent, char_count, rscName);
					perEvent = new StringBuilder();
				}
				if(strs.size()>2){
					perEvent.append(strs.get(2));
				}
				if(strs.size()>4){
					perEvent.append(strs.get(4));
				}
				lastTitle = thisTitle;
			}
		});
		plusCharCount(perEvent, char_count, rscName);
		
		Util.close(excel);
	}
	
	private void plusCharCount(StringBuilder perEvent, Map<String,CharStat> char_count, String location) {
		new TranslationScriptReader().readGBK(perEvent.toString(), new Callback() {
			@Override
			public void onReadedUnit(boolean isCtrl, byte[] ctrls, String text, int length) {
				if(!isCtrl) {
					CharStat count = char_count.get(text);
					if(count!=null) {
						char_count.put(text, count.plus().addLocation(location));
					} else {
						char_count.put(text, new CharStat().addLocation(location));
					}
				}
			}
		});
	}

}
