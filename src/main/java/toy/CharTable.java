package toy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class CharTable {
	Map<Integer,String> code_char = new HashMap<>();
	Map<String,Integer> char_code = new HashMap<>();
	
	public CharTable() {
		BufferedReader reader = null;
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("chartable.tbl");
		try {
			reader  = new BufferedReader(new InputStreamReader(is, "gbk"));
			String l = null;
			while((l=reader.readLine())!=null && l.length()>0){
				String[] arr = l.split("=",2);
				int code = Integer.parseInt(arr[0], 16);
				code_char.put(code,arr[1]);
				char_code.put(arr[1], code);
			}
		} catch (IOException e) {
			throw new RuntimeException();
		} finally{
			try {
				reader.close();
			} catch (IOException e) {
			}
		}
	}
	
	public String getChar(int code) {
		return code_char.get(code);
	}
	
	public boolean containChar(int code) {
		return code_char.containsKey(code);
	}
	
	public Integer getCode(String char_) {
		return char_code.get(char_);
	}
	
}
