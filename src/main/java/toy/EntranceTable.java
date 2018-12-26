package toy;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import toy.util.Util;

public class EntranceTable {
	
	byte[] entranceHead;
	public List<EntrancePointer> pointers = new ArrayList<>();
	
	public EntranceTable(String bin){
		try {
			RandomAccessFile binfile = new RandomAccessFile(bin, "r");
			entranceHead = new byte[0xc];
			binfile.read(entranceHead);
			for(int i=0;i<Conf.ENTRANCE_COUNT;i++){
				EntrancePointer p = new EntrancePointer();
				p.addr = Util.hilo(binfile.readInt());
				p.size = Util.hilo(binfile.readInt());
				p.unknown = binfile.readInt();
				pointers.add(p);
			}
			binfile.close();
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public EntrancePointer find(int addr){
		for(EntrancePointer p : pointers){
			if(p.addr == addr) {
				return p;
			}
		}
		System.err.println("no pointer...");
		return null;
	}
	
	public byte[] rebuild(){
		ByteBuffer buf = ByteBuffer.allocate(0xc+Conf.ENTRANCE_COUNT*12);
		buf.put(entranceHead);
		for(EntrancePointer p : pointers){
			buf.put(p.tobytes());
		}
		return buf.array();
	}

}
