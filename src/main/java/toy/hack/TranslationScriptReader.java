package toy.hack;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import toy.util.Util;


public class TranslationScriptReader {
	
//	public static void main(String[] args) throws UnsupportedEncodingException {
//		new TranslationScriptReader().readGBK("{02f0}{03f0B}从不同沸点的混合液体中分离{br}出一种液体{end}", new Callback() {
//
//			@Override
//			public void onReadedUnit(boolean isCtrl, byte[] ctrls, String text) {
//				System.out.println(text);
//			}
//		});
//	}
	
	public interface Callback{
		/**
		 * 
		 * @param isCtrl 是否控制符
		 * @param ctrls 控制符的字节形式
		 * @param text 
		 * @param length 该unit的字节长度,各个控制符,图标的长度都不同
		 */
		void onReadedUnit(boolean isCtrl, byte[] ctrls, String text, int length);
	}
	
	public void readGBK(String text, Callback cb) {
		DataInputStream is=null;
		try {
			is = new DataInputStream(new ByteArrayInputStream(text.getBytes("gbk")));
		} catch (UnsupportedEncodingException e) {
		}
		StringBuilder unit = null;
		int mode = 0; //0=normal, 控制符=1, 特殊字(图标)=2;
		while(true) {
			try {
				byte b = is.readByte();
				if(mode==1) {
					unit.append((char)b);
					if(b=='}') {
						mode=0;
						byte[] _bs = toBytes(unit.toString());
						cb.onReadedUnit(true, _bs, unit.toString(), _bs.length);
					}
				} else if(mode==2){
					unit.append((char)b);
					if(b==']') {
						mode=0;
						cb.onReadedUnit(false, null, unit.toString(), 2);
					}
				} else {
					if(b=='{') {
						mode=1;
						unit=new StringBuilder();
						unit.append((char)b);
					} else if(b=='['){
						mode=2;
						unit=new StringBuilder();
						unit.append((char)b);
					} else {
						String char_ = null;
						if(b>=0&&b<=0x7f) {	//ascii编码下直接转换
							char_ = String.valueOf((char)b);
						} else {	//超出ascii编码时,再读一个字节拼接起来
							char_ = new String(new byte[]{b, is.readByte()}, "gbk");
						}
						cb.onReadedUnit(false, null, char_, 2);	//游戏中一个字固定使用2个字节
					}
				}
			} catch(EOFException e1) {
				break;
			} catch(IOException e2) {
				break;
			}
		}
		Util.close(is);
	}
	
	public byte[] toBytes(String ctrl) {
		ByteBuffer buf = ByteBuffer.allocate(6);
		if(ctrl.equals("{03f0B}")) {
			buf.putShort((short) 0x03f0);
			buf.putShort((short) 0);
		} else if(ctrl.equals("{03f0E}")) {
			buf.putShort((short) 0x03f0);
			buf.putShort((short) 0xffff);
		} else if(ctrl.equals("{ }")) {
			buf.putShort((short) 0x00f0);
		} else if(ctrl.equals("{br}")) {
			buf.putShort((short) 0x01f0);
		} else if(ctrl.equals("{02f0}")) {
			buf.putShort((short) 0x02f0);
		} else if(ctrl.equals("{wait}")) {
			buf.putShort((short) 0x04f0);
		} else if(ctrl.equals("{end}")) {
			buf.putShort((short) 0xffff);
		} else if(ctrl.startsWith("{06f0")) {
			buf.putShort((short) 0x06f0);
			buf.putShort((short)Integer.parseInt(ctrl.substring(5,9), 16));
		} else if(ctrl.startsWith("{icon")) {
			buf.putShort((short) 0x08f0);
			buf.putShort((short) 0);
			buf.putShort(Short.parseShort(ctrl.substring(5,9), 16));
		} else {
			throw new RuntimeException("无法识别的控制符:"+ctrl);
		}
		return Arrays.copyOfRange(buf.array(), 0, buf.capacity()-buf.remaining());
	}

}
