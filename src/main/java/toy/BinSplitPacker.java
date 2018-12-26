package toy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BinSplitPacker {
	
	public static BinSplitPacker newInstance(){
		String splitDir = System.getProperty("java.io.tmpdir");
		if(!splitDir.endsWith(File.separator))
			splitDir+=File.separator;
		splitDir+="toys"+File.separator;
		return new BinSplitPacker(splitDir);
	}
	
	public Map<File,Integer> entrancePointer = new LinkedHashMap<>();
	private EntranceTable table;
	private String splitDir;
	
	public BinSplitPacker(String splitDir) {
		this.splitDir = splitDir;
	}

	public void split(String bin) throws IOException{
		table = new EntranceTable(bin);
		RandomAccessFile binfile = new RandomAccessFile(bin, "r");
		
		for(EntrancePointer p : table.pointers){
			String filename=String.format("%08X", p.addr);
			File target = new File(splitDir+filename);
			if(!target.getParentFile().exists()) {
				target.getParentFile().mkdirs();
			}
			entrancePointer.put(target, p.unknown);
			FileOutputStream fos = new FileOutputStream(target);
			byte[] buf = new byte[p.size];
			binfile.seek(p.addr);	 
			binfile.read(buf);
			fos.write(buf);
			fos.close();
		}
		binfile.close();
	}
	
	public File findFile(String addr){
		if(addr.length()!=8) throw new IllegalArgumentException("addr格式不对:"+addr); 
		for(File k : entrancePointer.keySet()){
			if(k.getName().equals(addr)){
				return k;
			}
		}
		throw new IllegalArgumentException("没找到split文件:"+addr); 
	}
	
	public void pack(String targetBin) throws IOException{
		decreaseFile();
		FileOutputStream bin = new FileOutputStream(targetBin);
		byte[] buf = new byte[1024];
		int len=0;
		int nextAddr = table.pointers.get(0).addr;
		table.pointers = new ArrayList<>();
		for(Entry<File,Integer> e : entrancePointer.entrySet()){
			File src = e.getKey();
			EntrancePointer p = new EntrancePointer();
			p.addr = nextAddr;
			p.size = (int) src.length();
			p.unknown = e.getValue();
			table.pointers.add(p);
			nextAddr += p.size;
		}
		bin.write(table.rebuild());
		
		for(Entry<File,Integer> e : entrancePointer.entrySet()){
			File src = e.getKey();
			FileInputStream fis = new FileInputStream(src);
			while((len=fis.read(buf))!=-1){
				bin.write(buf, 0, len);
			}
			fis.close();
		}
		bin.close();
	}
	
	
	/**
	 * 由于字库文件变大,必须要缩小另外一些文件,让BIN文件大小保持不变.
	 * 否则要使用http://www.theisozone.com/downloads/playstation/tools/cd-dvd-generator-200/重建光盘
	 */
	private void decreaseFile(){
		long totalSize=0;
		for(Entry<File,Integer> e : entrancePointer.entrySet()){
			totalSize+=e.getKey().length();
		}
		long decreaseLen=totalSize-(Conf.BIN_LEN-table.pointers.get(0).addr);
			
		try {
			File dummyFile = findFile("012CCC58");	//体验版中的图片
			int zeroCount = (int) (dummyFile.length()-decreaseLen);
			FileOutputStream fos = new FileOutputStream(dummyFile);
			byte[] buf = new byte[zeroCount];
			fos.write(buf);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void dispose(){
		for(Entry<File,Integer> e : entrancePointer.entrySet()){
			e.getKey().delete();
		}
		new File(splitDir).delete();
	}
	
	
	public String getSplitDir(){
		return splitDir;
	}

}
