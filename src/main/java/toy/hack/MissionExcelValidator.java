package toy.hack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import toy.common.ExcelParser;
import toy.common.ExcelParser.RowCallback;
import toy.util.Util;

public class MissionExcelValidator {
	
	private Integer lastPointer;
	private StringBuilder textPerPointer= new StringBuilder();
	
	public void validate() throws IOException{
		File rscFile = ExcelFileFinder.getNewestTranslation("mission");
		InputStream excel = new FileInputStream(rscFile);
		new ExcelParser(excel, rscFile.getName()).parse(1, new RowCallback() {
			
			@Override
			public void doInRow(List<String> strs, int rowNum) {
				int thisPointer = Integer.parseInt(strs.get(1));
				if(lastPointer!=null && thisPointer!=lastPointer.intValue()){
					doValidate(textPerPointer.toString(), lastPointer);
					textPerPointer = new StringBuilder();
				}
				if(strs.size()>2){
					textPerPointer.append(strs.get(2));
				}
				if(strs.size()>4){
					textPerPointer.append(strs.get(4));
				}
				lastPointer = thisPointer;
			}
		});
		doValidate(textPerPointer.toString(), lastPointer);
		Util.close(excel);
	}
	
	private void doValidate(String textPerPointer, int pointer){
		if(pointer>=210&&pointer<=416) {
			int count = Util.substringCount(textPerPointer, "\\{br\\}");
			if(count>9) {
				System.err.println("mission文本行数超了,最大9行: "+pointer+"="+count);
			} 
			if(count<9){
				System.err.println("mission文本行数不够了,需要9行: "+pointer+"="+count);
			}
		}
	}

}
