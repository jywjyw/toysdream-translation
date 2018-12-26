package toy.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;

import toy.TranslationDict;
import toy.common.ExcelParser;
import toy.common.ExcelParser.RowEditCallback;

public class ExcelReplacer {
	
	public static void main(String[] args) throws IOException {
		String fromDir = "D:\\Workspace\\toysdream\\src\\main\\resources\\translation\\";
//		replace(new File(fromDir + "event-original.xlsx"), new File(fromDir+"event.xlsx"));
//		replaceOneColumn(new File(fromDir + "event.xlsx"), new File(fromDir+"event-replace.xlsx"), 2);
//		replaceOneColumn(new File(fromDir + "npc.xlsx"), new File(fromDir+"npc-replace.xlsx"), 2);
		replaceOneColumn(new File(fromDir + "mission-v0.xlsx"), new File(fromDir+"mission-replace.xlsx"), 3);
		
	}	
	
	public static void replaceOneColumn(File src, File to, int column)throws IOException{
		TranslationDict dict = new TranslationDict();
		byte[] bs = new ExcelParser(src).writeReplica(1, new RowEditCallback() {
			@Override
			public void doInRow(Row row, List<String> strs, int rowNum) {
				try {
					String s = dict.replace(strs.get(column));
					row.getCell(column).setCellValue(s);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		FileOutputStream fos = new FileOutputStream(to);
		fos.write(bs);
		fos.flush();
		fos.close();
	}
	
	public static void replace(File src, File to) throws IOException {
		TranslationDict dict = new TranslationDict();
		byte[] bs = new ExcelParser(src).writeReplica(1, new RowEditCallback() {
			@Override
			public void doInRow(Row row, List<String> strs, int rowNum) {
				try {
					String s2 = dict.replace(strs.get(2));
					if(containWarningChar(s2)) {
						System.err.println(s2);
					}
					row.getCell(2).setCellValue(s2);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					String s4 = dict.replace(strs.get(4));
					if(containWarningChar(s4)) {
						System.err.println(s4);
					}
					row.getCell(4).setCellValue(s4);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		FileOutputStream fos = new FileOutputStream(to);
		fos.write(bs);
		fos.flush();
		fos.close();
	}
	
	public static boolean containWarningChar(String s) {
		char[] cs = "あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもゃゅょらりるれろわをんうがぎぐげござじずぜぞだぢづでどばびぶべぼぱぴぷぺぽぁぃぅぇぉっゃゅょアイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲンヴガギグゲゴザジズゼゾダヂヅデドバビブベボパピプペポァィゥェォッャュョーDEFGHIKLMNOPQUVWXYabcdefghijklmnopqrstuvwxyz".toCharArray();
		s = s.replace("{wait}", "").replace("{02f0}", "").replace("{03f0B}", "").replace("{03f0E}", "")
				.replace("{br}", "").replace("{end}", "")
				.replace("[wine]", "").replace("[art]", "").replace("[equip", "").replace("[paper]", "")
				.replace("[design]", "").replace("[chemistry]", "").replace("[elec]", "").replace("[energy]", "")
				.replace("[instrum]", "").replace("[material]", "").replace("[machine]", "").replace("[money]", "")
				.replaceAll("\\{icon[a-zA-Z0-9]{4}\\}", "").replaceAll("\\{06f0\\w{4}\\}", "")
				;
		for(char c : cs){
			if(s.contains(String.valueOf(c))) return true;
		}
		return false;
	}

}
