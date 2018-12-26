package toy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TranslationDict {
	
	private Map<String,String> map = new LinkedHashMap<>();
	private List<String> sortKeys;
	
	public TranslationDict() {
		BufferedReader reader = null;
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("translate_dict.prop");
		try {
			reader  = new BufferedReader(new InputStreamReader(is, "gbk"));
			String l = null;
			while((l=reader.readLine())!=null){
				if(!l.startsWith("#") && l.length()>0) {
					String[] arr = l.split("=",2);
					map.put(arr[0], arr[1]);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException();
		} finally{
			try {
				reader.close();
			} catch (IOException e) {
			}
		}
		sortKeys = new ArrayList<>(map.keySet());
		Collections.sort(sortKeys, new Comparator<String>(){
			@Override
			public int compare(String o1, String o2) {
				return o2.length()-o1.length();	//串长的排前头,先替换
			}
		});
	}
	
	public String replace(String txt) {
		String replaced = txt;
		for(String key : sortKeys){
			replaced = replaced.replace(key, map.get(key));
		}
		return replaced;
	}

}
