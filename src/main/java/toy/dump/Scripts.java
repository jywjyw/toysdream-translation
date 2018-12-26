package toy.dump;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import toy.CharTable;
import toy.dump.BinScriptReader.Callback;
import toy.util.Util;

/**
 * ROM中的脚本文件
 */
public class Scripts {
	
	public static void main(String[] args) throws IOException {
		List<byte[]> texts = new ArrayList<>();
		for(int i=0;i<20;i++){
			byte[] bs=new byte[100];
			Arrays.fill(bs, (byte)i);
			texts.add(bs);
		}
		byte[] bs = new Scripts().rebuild(texts);
		FileOutputStream fos = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\xx.bin");
		fos.write(bs);
		fos.close();
	}
	
	public void exportAsEvent(File filelinkbin, CharTable charTable, int startPos, Callback callback) throws IOException {
		RandomAccessFile pointer = new RandomAccessFile(filelinkbin, "r");
		pointer.seek(startPos);
		RandomAccessFile text = new RandomAccessFile(filelinkbin, "r");
		
		Integer firstOffset = null;
		int pos=startPos,p=0;
		while(true) {
			p=Util.hilo(pointer.readInt());
			pos+=4;
			if(firstOffset==null) firstOffset=p;
			
			new BinScriptReader(text, startPos+p, charTable).readUntilFF(callback);
			callback.newIndex();
			if(pos>=startPos+firstOffset) break;
		}
		pointer.close();
		text.close();
	}
	
	public List<String> exportAsString(File filelinkbin, CharTable charTable, int startPos) throws IOException {
		RandomAccessFile pointer = new RandomAccessFile(filelinkbin, "r");
		pointer.seek(startPos);
		RandomAccessFile text = new RandomAccessFile(filelinkbin, "r");
		List<String> txts = new ArrayList<>();
		
		Integer firstOffset = null;
		int pos=startPos,p=0;
		while(true) {
			p=Util.hilo(pointer.readInt());
			pos+=4;
			if(firstOffset==null) firstOffset=p;
			
			String str = new BinScriptReader(text, startPos+p,charTable).readTextUntil04ff();
			txts.add(str);
			
			if(pos>=startPos+firstOffset) break;
		}
		pointer.close();
		text.close();
		
		return txts;
	}
	
	
	/**
	 * 根据该脚本文件的正文内容,重建整个脚本文件.包括指针表和正文内容
	 * @param texts
	 * @return
	 */
	public byte[] rebuild(List<byte[]> texts) {
		int totalTextLen=0;
		for(byte[] bs : texts){
			totalTextLen+=bs.length;
		}
		
		ByteBuffer buf = ByteBuffer.allocate(4*texts.size()+totalTextLen);
		int prev = 4*texts.size();
		for(int i=0;i<texts.size();i++) {
			buf.putInt(Util.hilo(prev));
			prev+=texts.get(i).length;
		}
		for(byte[] bs : texts){
			buf.put(bs);
		}
		return buf.array();
	}
	
	

}
