package toy.dump;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TextureMeta {
	// 纹理在显存中的x,y坐标, 在显存中的宽高(即16位下的宽,通常图片是4位), 在split file中的起始位置
	public int x, y, w, h, pos;

	public void validate(long fileSize) {
		if (x > 1024 || w <= 0 || w > 1024 || y > 512 || h <= 0 || h > 512)
			throw new UnsupportedOperationException("out of vram,not img");
		if (pos < 0 || pos > fileSize)
			throw new UnsupportedOperationException("out of file range,not img");
	}
	
	public int get4bitWidth(){
		return w*4;	//4bit下的图片宽度
	}
	
	//占多少字节
	public int getSize(){
		return 10;
	}
	
	public byte[] rebuild(){
		ByteBuffer buf = ByteBuffer.allocate(12);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putShort((short) x);
		buf.putShort((short) y);
		buf.putShort((short) w);
		buf.putShort((short) h);
		buf.putInt(pos);
		return buf.array();
	}
	
	public static void main(String[] args) {
		TextureMeta t = new TextureMeta();
		t.x = 100;t.y = 200;t.w = 300;t.h = 400;t.pos = 500;
		for(byte b:t.rebuild()){
			System.out.printf("%02X ", b);
		}
	}
}