package test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import toy.Conf;

public class OriginalFontColor {
	
	public static void main(String[] args) throws IOException {
		RandomAccessFile bin = new RandomAccessFile(Conf.bin, "r");
		bin.seek(0x2B5E0+0x7c);
		Map<Integer,Integer> counts = new HashMap<>();
		for(int i=0;i<16;i++){
			counts.put(i, 0);
		}
		
		int len=0x100*0x100*2;
		for(int i=0;i<len;i++){
			byte b = bin.readByte();
			int i1=b>>>4&0xf;
			counts.put(i1, counts.get(i1)+1);
			int i2=b&0xf;
			counts.put(i2, counts.get(i2)+1);
		}
		
		for(Entry<Integer,Integer> e:counts.entrySet()){
			System.out.println(e.getKey()+"="+e.getValue());
		}
		bin.close();
	}
	
}
