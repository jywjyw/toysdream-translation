package hanhua;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;

public class Event {
	public static void main(String[] args) throws IOException {
		File bin = new File("D:\\ps3\\hanhua\\toysdream\\FILELINK.BIN");
		File tbl = new File("D:\\ps3\\hanhua\\toysdream\\toys-dream.tbl"); 
		new Event().export(bin, tbl);
	}
	
	public void export(File filelinkbin, File charTbl) throws IOException {
		RandomAccessFile text = new RandomAccessFile(filelinkbin, "r");
		text.seek(0x0286c0e4);	
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("D:\\ps3\\hanhua\\toysdream\\event.txt"), "utf-8");
		
		CharTable charTable = new CharTable(charTbl);
		
		int bufT=0, lastT=0;
		try {
		while(true) {
			bufT=text.readUnsignedShort();
			if(lastT==0x03F0){
				if(bufT==0x0000) {
					writer.write("{03f0B}");
				} else if(bufT==0xffff){
					writer.write("{03f0E}");
				}
			} else if(lastT==0&&bufT==0) {
				writer.write("\n");
			} else if(bufT==0x03f0) {
			} else if(bufT==0x00f0) {
				writer.write("{ }");
			} else if(bufT==0x01f0) {
				writer.write("{br}");
			} else if(bufT==0x02f0){
				writer.write("{02f0}");
			} else if(bufT==0x04f0) {
				writer.write("{wait}");
			} else if(bufT==0xFFFF) {
				writer.write("{end}");
			} else if(charTable.containChar(bufT)){
				writer.write(charTable.getChar(bufT));
			} else {
				writer.write(String.format("[%04X]", bufT));
			}
			lastT = bufT;
		}
		}catch(EOFException e) {
			e.printStackTrace();
		}
		text.close();
		writer.flush();
		writer.close();
	}
	
	private int hilo(int i) {
		return i>>>24|i>>>8&0xff00|i<<8&0xff0000|i<<24&0xff000000;
	}

}
