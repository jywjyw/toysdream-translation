package hanhua;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class CopyPartFile {
	
	public static void main(String[] args) throws IOException {
		RandomAccessFile in = new RandomAccessFile(new File("D:\\ps3\\hanhua\\toysdream\\FILELINK.BIN"), "r");
		FileOutputStream out = new FileOutputStream("D:\\ps3\\hanhua\\toysdream\\TOWN.BIN");
		long start=0x0012ffae, end=0x00131286;	//end要加1， 100~1000即复制第100个字节到999个字节
		in.seek(start);
		byte[] buf = new byte[1];
		while(true) {
			if(start>=end) break;
			in.read(buf);
			out.write(buf);
			start++;
		}
		in.close();
		out.close();
	}

}
