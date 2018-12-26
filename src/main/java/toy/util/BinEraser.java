package toy.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import toy.Conf;

public class BinEraser {
	
	public static void main(String[] args) {
		eraseByAddress(0x1433180,0x14336FC, (byte) 0);
//		eraseByLength(0x14336FC+0x7, 0xd);
	}
	
	public static void eraseByLength(long start, int len, byte b) {
		try {
			RandomAccessFile bin = new RandomAccessFile(new File(Conf.hackbin), "rw");
			bin.seek(start);
			byte[] bs = new byte[len];
			Arrays.fill(bs, b);
			bin.write(bs);
			bin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void eraseByAddress(long start, long end, byte b) {
		try {
			RandomAccessFile bin = new RandomAccessFile(new File(Conf.hackbin), "rw");
			bin.seek(start);
			byte[] bs = new byte[(int) (end-start)];
			Arrays.fill(bs, b);
			bin.write(bs);
			bin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

}
