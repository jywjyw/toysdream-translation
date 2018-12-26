package toy.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Row;

import toy.common.ExcelParser;
import toy.common.FanyiClient;
import toy.common.ExcelParser.RowCallback;
import toy.common.ExcelParser.RowEditCallback;

public class ExcelAutoTranslator {
	
	public static void batchTranslate(String excel) throws IOException {
		Map<Integer,String> zh = new HashMap<>(), jp = new HashMap<>();
		new ExcelParser(new File(excel)).parse(1, new RowCallback() {
			@Override
			public void doInRow(List<String> strs, int rowNum) {
				if(strs.size()>3) {
					String jp = strs.get(3);
					if(jp!=null) zh.put(rowNum, jp);
				}
			}
		});
		
		List<Integer> indexes = new ArrayList<>();
		List<String> texts = new ArrayList<>();
		for(Iterator<Entry<Integer,String>> it=zh.entrySet().iterator();it.hasNext();) {
			Entry<Integer,String> obj = it.next();
			indexes.add(obj.getKey());
			texts.add(obj.getValue());
			if(indexes.size()>=40) {
				List<String> jps = FanyiClient.translate(texts); 
				for(int i=0;i<indexes.size();i++) {
					jp.put(indexes.get(i), jps.get(i));
				}
				indexes.clear();
				texts.clear();
			}
		}
		if(indexes.size()>0) {
			List<String> jps = FanyiClient.translate(texts); 
			for(int i=0;i<indexes.size();i++) {
				jp.put(indexes.get(i), jps.get(i));
			}
			indexes.clear();
			texts.clear();
		}
		
		byte[] newExcel = new ExcelParser(new File(excel)).writeReplica(1, new RowEditCallback() {
			
			@Override
			public void doInRow(Row row, List<String> strs, int rowNum) {
				if(jp.containsKey(rowNum)) {
					row.createCell(4).setCellValue(jp.get(rowNum));
				}
			}
		});
		FileOutputStream fos = new FileOutputStream(excel);
		fos.write(newExcel);
		fos.flush();
		fos.close();
	}

}
