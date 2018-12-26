package toy.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import toy.util.Util;

public class FanyiClient {
	
	private static final String APPID, PWD;
	public static void main(String[] args) throws IOException {
		List<String> ss = translate(Arrays.asList("一日も無駄に过ごせない"));
		for(String s:ss)
		System.out.println(s);
	}
	
	static {
		try {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("secret.prop");
			Properties p = new Properties();
			p.load(is);
			APPID = p.getProperty("baidu.appid");
			PWD = p.getProperty("baidu.pwd");
			is.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static List<String> translate(List<String> q) throws IOException {
		String url = "http://fanyi-api.baidu.com/api/trans/vip/translate";
		Map<String,String> par = new HashMap<>();
		String qstr = Util.join(q, "\n");
		par.put("q", qstr);
		par.put("from", "jp");
		par.put("to", "zh");
		par.put("appid", APPID);
		String salt = new Random().nextInt(Integer.MAX_VALUE)+"";
		par.put("salt", salt);
		par.put("sign", sign(qstr,salt));
		
		HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
		c.setDoOutput(true);
		OutputStream os = c.getOutputStream();		
		os.write(Util.paramMapToString(par, "utf-8").getBytes());
		os.flush();
		os.close();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), "utf-8"));
		StringBuffer sb = new StringBuffer();
		String s = null;
		while((s=br.readLine())!=null) {
			sb.append(s);
		}
		br.close();
//		System.out.println(sb.toString());
		JsonArray arr = new JsonParser().parse(sb.toString()).getAsJsonObject()
				.get("trans_result").getAsJsonArray();
		
		List<String> ret = new ArrayList<>();
		for(JsonElement e : arr) {
			ret.add(e.getAsJsonObject().get("dst").getAsString());
		}
		return ret;
	}
	
	private static String sign(String q, String salt) {
		String s = APPID+q+salt+PWD;
		try {
			return Util.toMd5(s.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

}
