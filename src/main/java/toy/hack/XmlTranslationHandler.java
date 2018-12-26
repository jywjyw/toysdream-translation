package toy.hack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import toy.CharStat;
import toy.Resource;
import toy.dump.Scripts;
import toy.hack.TranslationScriptReader.Callback;
import toy.util.Util;


public class XmlTranslationHandler {
	
	/**
	 * 从译文中抽取用到的所有字符
	 * @return
	 */
	public void drawAllChars(String rscName, Resource resource, Map<String,CharStat> char_count) {
		
		Len len = new Len();
		Element root;
		InputStream xml=null;
		try {
			xml = new FileInputStream(System.getProperty("user.dir") + "/translation/"+rscName+".xml");
			root = new SAXReader().read(xml).getRootElement();
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}
		for(Object o : root.elements()) {
			Element e = (Element)o;
			String gbk = e.elementTextTrim("zh");
			new TranslationScriptReader().readGBK(gbk, new Callback() {
				
				@Override
				public void onReadedUnit(boolean isCtrl, byte[] ctrls, String text, int length) {
					len.plus(length);
					if(!isCtrl) {
						CharStat count = char_count.get(text);
						if(count!=null) {
							char_count.put(text, count.plus().addLocation(rscName));
						} else {
							char_count.put(text, new CharStat().addLocation(rscName));
						}
					}
				}
			});
		}
		Util.close(xml);
		int lenLimit = resource.getLengthByName(rscName);
		if(len.i > lenLimit) {
			throw new RuntimeException(String.format("%s文本超长了,可用长度%d,实际长度%d", rscName, lenLimit, len.i));
		}
		len.reset();
	}
	
	
	class Len {
		public int i;
		public void plus(int len) {
			this.i+=len;
		}
		public void reset() {
			this.i = 0;
		}
	}
	
	/**
	 * 根据xml译文, 重建文本指针和译文码
	 * @param fonts
	 * @param rscName
	 * @param resource
	 * @throws IOException
	 */
	public void rebuildSingle(Map<String,FontData> fonts, String rscName, File targetFile) throws IOException {
		InputStream xml=null;
		Element root;
		try {
			xml = new FileInputStream(System.getProperty("user.dir") + "/translation/"+rscName+".xml");
			root = new SAXReader().read(xml).getRootElement();
		} catch (DocumentException e1) {
			throw new RuntimeException(e1);
		}
		List<byte[]> texts = new ArrayList<>();
		for(Object o : root.elements()) {
			Element e = (Element)o;
			String gbk = e.elementTextTrim("zh");
			ByteBuffer buf = ByteBuffer.allocate(50000);
			new TranslationScriptReader().readGBK(gbk, new Callback() {
				
				@Override
				public void onReadedUnit(boolean isCtrl, byte[] ctrls, String text, int length) {
					if(isCtrl) {
						buf.put(ctrls);
					} else {
						FontData newfont = fonts.get(text);
						buf.put(newfont.getLittleEndianCode());
					}
				}
			});
			texts.add(Arrays.copyOfRange(buf.array(), 0, buf.capacity()-buf.remaining()));
		}
		Util.close(xml);
		
		byte[] scriptFile= new Scripts().rebuild(texts);
		ByteBuffer buf = ByteBuffer.allocate((int) targetFile.length());	//和原来的文件脚本要相同大小,原因不明
		buf.put(scriptFile);
		Util.writeFile(targetFile, buf.array());
	}

}
