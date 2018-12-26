package toy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
/**
 * 拆分FILELINK.BIN
 * 入口表的格式:位于0000000c开始的位置,每12字节为一个指针, 指针的前4位代表子文件的起始地址,中间4位代表子文件的长度,最后4位未知
 */
public class BinSplitter {
	
	public static void main(String[] args) throws IOException {
		new BinSplitter().split(true);
	}
	
	public void split(boolean addrAsName) throws IOException {
		Resource p = new Resource();
		RandomAccessFile pointer = new RandomAccessFile(new File(Conf.bin), "r");
		RandomAccessFile copySrc = new RandomAccessFile(new File(Conf.bin), "r");
		byte[] buf = new byte[12];
		for(int pos=0;pos<0x5358;pos+=12) {
			pointer.read(buf);
			int entrance = buf[3]<<24&0xff000000|buf[2]<<16&0xff0000|buf[1]<<8&0xff00|buf[0]&0xff;
			int length = buf[7]<<24&0xff000000|buf[6]<<16&0xff0000|buf[5]<<8&0xff00|buf[4]&0xff;
			String filename=String.format("%08X", entrance);
			if(!addrAsName && p.containAddress(entrance)) {
				filename = p.getName(entrance);
			}
			copy(copySrc, filename, entrance, length);
		}
		pointer.close();
		copySrc.close();
	}
	
	private void copy(RandomAccessFile copySrc, String filename, int entrance, int length) throws IOException {
		copySrc.seek(entrance);
		File file = new File(Conf.export+"split\\"+filename);
		if(!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		FileOutputStream out = new FileOutputStream(file);
		byte[] buf = new byte[length];
		copySrc.read(buf);
		out.write(buf);
		out.flush();
		out.close();
	}
	
}
