package toy.hack;

import java.nio.ByteBuffer;

public class FontPointerTable {
	
	public static byte[] rebuild5page(int count){
		return rebuild(count, new byte[]{0x1f,0x1e,0x1d,0x1c,0x1b});
	}
	public static byte[] rebuild4page(int count){
		return rebuild(count, new byte[]{0x1e,0x1d,0x1c,0x1b});
	}
	
	public static byte[] rebuild(int count, byte[] pageName){
		if(count>pageName.length*21*21) {
			throw new IllegalArgumentException("count超出范围");
		}
		ByteBuffer buf = ByteBuffer.allocate(4+4*count);
		buf.put(new byte[]{0x3f,0x32,0xc,0xc});	//unknown head
		int total=0;
		for(byte page:pageName){
			for(int y=0;y<=0xf0;y+=0xc) {
				for(int x=0;x<=0xf0;x+=0xc){
					buf.put(page);
					buf.put((byte)0);
					buf.put((byte) (x&0xff));
					buf.put((byte) (y&0xff));
					total++;
					if(total>=count) 
						return buf.array();
				}
			}
		}
		throw new RuntimeException();
	}
	
}
