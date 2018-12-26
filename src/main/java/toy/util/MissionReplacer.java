package toy.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Row;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import toy.Conf;
import toy.TranslationDict;
import toy.common.ExcelParser;
import toy.common.ExcelParser.RowEditCallback;

public class MissionReplacer {
	
	public static void main(String[] args) {
		new MissionReplacer().replace(new File(Conf.export+"/export/mission.xlsx"), 
				new File(Conf.export+"/export/mission-replace.xlsx"));
		
	}
		
	public void replace(File src, File to)  {
		Map<String,String> itemDict = buildDict();
		TranslationDict translationDict = new TranslationDict();
		try {
			byte[] bs = new ExcelParser(src).writeReplica(1, new RowEditCallback() {

				@Override
				public void doInRow(Row row, List<String> strs, int rowNum) {
					try {
						String s2 = translationDict.replace(replaceItem(itemDict, strs.get(2)));
						row.getCell(2).setCellValue(s2);
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						String s3 = translationDict.replace(replaceItem(itemDict, strs.get(3)));
						row.getCell(3).setCellValue(s3);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			});
			FileOutputStream fos = new FileOutputStream(to);
			fos.write(bs);
			fos.flush();
			fos.close();
			ExcelAutoTranslator.batchTranslate(to.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Map<String,String> buildDict() {
		InputStream item = Thread.currentThread().getContextClassLoader().getResourceAsStream("translation/item.xml");
		Map<String,String> dict = new HashMap<>();
		try {
			Element root = new SAXReader().read(item).getRootElement();
			for(int i=7;i<=512;i++) {
				Element e = root.element("_"+i);
				String jp = e.elementText("jp"), zh = e.elementText("zh");
				dict.put(filter(jp), filter(zh));
			}
			item.close();
		} catch (DocumentException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dict;
	}
	
	private String filter(String s) {
		return s.replaceAll("\\{\\w+\\}", "").replaceAll("\\[\\w+\\]", "");
	}
	
	private String replaceItem(Map<String,String> itemDict, String txt){
		String replaced = txt;
		for(Entry<String,String> e : itemDict.entrySet()){
			replaced = replaced.replace(e.getKey(), e.getValue());
		}
		return replaced;
	}
	
	

}
