package toy;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import toy.hack.FontData;
import toy.util.Util;

public class FixedCharModel {
	
	private LinkedHashMap<String,FontData> map = new LinkedHashMap<>();
	
	public FixedCharModel(){
		try {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("charmodel.xml");
			Element root = new SAXReader().read(is).getRootElement();
			for(Object o : root.elements()){
				Element c=(Element)o;
				FontData f = new FontData();
				f.char_=c.attributeValue("id");
				f.imgdata = Base64.decodeBase64(c.attributeValue("img"));
				String pos=c.attributeValue("position");
				if(pos!=null) {
					f.position = Integer.parseInt(pos);
				}
				map.put(f.char_, f);
			}
			Util.close(is);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Map<String,FontData> getClone() {
		return (Map<String, FontData>) map.clone();
	}
	
}
