package hanhua;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class Batch {
	
	public static void main(String[] args) throws Exception {
//		System.out.println((short)Integer.parseInt("ffff", 16));
//		new Batch().export();
		new Batch().rebuild();
		new Batch().import_();
	}
	
	class Config {
		public int hasDivider;
		long pointerStartPos;
		int pointerInitVal;
		int pointerCount;
		long textStartPos, textEndPos;
	}
	
	public Map<String,Config> getConfig() throws IOException {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("address.prop");
		Properties prop = new Properties();
		prop.load(is);
		is.close();
		Map<String,Config> confs = new HashMap<>();
		for(Object key : prop.keySet()) {
			String[] arr = prop.getProperty(String.valueOf(key)).split(",");
			Config conf = new Config();
			conf.hasDivider = Integer.parseInt(arr[0]); 
			conf.pointerStartPos = Long.parseLong(arr[1].replace("0x", ""),16);
			conf.pointerInitVal = Integer.parseInt(arr[2].replace("0x", ""),16);
			conf.pointerCount = Integer.parseInt(arr[3]);
			conf.textStartPos = Long.parseLong(arr[4].replace("0x", ""),16);
			conf.textEndPos = Long.parseLong(arr[5].replace("0x", ""),16);
			confs.put(String.valueOf(key), conf);
		}
		return confs;
	}
	
	public void export() throws IOException {
		String dir = "D:\\ps3\\hanhua\\toysdream\\";
		File bin = new File(dir+"FILELINK.BIN");
		File tbl = new File(dir+"toys-dream.tbl");
		
		Map<String,Config> confs = getConfig();
		for(Entry<String,Config> e : confs.entrySet()) {
			Config conf = e.getValue();
			if(conf.hasDivider==1) {
				byte[] bs = new PointerTable1().export(bin,tbl,conf.pointerStartPos, conf.textStartPos);	
				save(dir+e.getKey()+".xml", bs);
			} else {
				byte[] bs = new PointerTable2().export(bin,tbl,conf.pointerStartPos, conf.textStartPos);	
				save(dir+e.getKey()+".xml", bs);
			}
		}
		
		System.out.println("over...");
	}
	
	private static void save(String file, byte[] bs) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(bs);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<byte[]> rebuild() throws Exception {
		String dir = "D:\\ps3\\hanhua\\toysdream\\";
		File bin = new File("D:\\ps3\\hanhua\\isopatcher\\FILELINK.BIN");
		File tbl = new File(dir+"toys-dream.tbl"); 
		Map<String,Config> confs = getConfig();
		Config conf = confs.get("menu");
		return Util.rebuild(bin, tbl, new File(dir+"menu.xml"), conf.pointerCount, conf.textStartPos, conf.textEndPos);
	}
	
	public void import_() throws Exception {
		String dir = "D:\\ps3\\hanhua\\toysdream\\";
		File bin = new File("D:\\ps3\\hanhua\\isopatcher\\FILELINK.BIN");
		File tbl = new File(dir+"toys-dream.tbl"); 
		Map<String,Config> confs = getConfig();
		Config conf = confs.get("menu");
		List<byte[]> rebuild =  Util.rebuild(bin, tbl, new File(dir+"menu.xml"), conf.pointerCount, conf.textStartPos, conf.textEndPos);
		new PointerTable1().import_(rebuild, bin, conf.pointerStartPos, conf.pointerInitVal, conf.pointerCount);
	}
	
}
