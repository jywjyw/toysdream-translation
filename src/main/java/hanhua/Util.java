package hanhua;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Util {
	
	public static TxtResult readText(RandomAccessFile text, long startPos, CharTable charTable) throws IOException {
		int prevT=0, prev2T=0, bufT=0;
		StringBuilder s = new StringBuilder();
		long pos = startPos;
		while(true) {
			bufT=text.readUnsignedShort();
			pos+=2;
			if(prev2T==0x08f0 && prevT==0x0000) {
				s.append(String.format("{icon%04X}", bufT));
			} else if(prevT==0x08f0){
			} else if(prevT==0x03F0){
				if(bufT==0x0000) {
					s.append("{03f0B}");
				} else if(bufT==0xffff){
					s.append("{03f0E}");
				}
			} else if(prevT==0x06F0){
				s.append(String.format("{06f0%04x}", bufT));
			} else if(bufT==0x03f0 || bufT==0x06f0 || bufT==0x08f0) {
			} else if(bufT==0x00f0) {
				s.append("{ }");
			} else if(bufT==0x01f0) {
				s.append("{br}");
			} else if(bufT==0x02f0){
				s.append("{02f0}");
			} else if(bufT==0x04f0) {
				s.append("{wait}");
			} else if(bufT==0xFFFF) {
				s.append("{end}");
				break;
			} else if(charTable.containChar(bufT)){
				s.append(charTable.getChar(bufT));
			} else {
				s.append(String.format("[%04X]", bufT));
			}
			prev2T=prevT;
			prevT=bufT;
		}
		TxtResult result = new TxtResult();
		result.text = s.toString();
		result.textFilePos = pos;
		result.readLen = pos-startPos;
		return result;
	}
	
	public static void ctrlToBytes(ByteBuffer buf, String ctrl) {
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
			buf.putShort(Short.parseShort(ctrl.substring(5,9), 16));
		} else {
			throw new RuntimeException(ctrl);
		}
	}
	
	public static List<byte[]> rebuild(File filelinkbin, File charTbl, File textFile,
			int pointerCount, long textStartPos, long textEndPos) throws Exception {
		
		CharTable charTable = new CharTable(charTbl);
		Element xml = new SAXReader().read(textFile).getRootElement();
		if(xml.elements().size()!=pointerCount) throw new RuntimeException();
		List<byte[]> textArray = new ArrayList<>();
		Set<String> illegalChar = new HashSet<>();
		ByteBuffer buf = ByteBuffer.allocate(50000);
		for(int i=1;i<=pointerCount;i++) {
			Element e = xml.element("_"+i);
			String chinese = e.elementTextTrim("chinese");
			DataInputStream is =null;
			try {
				is = new DataInputStream(new ByteArrayInputStream(chinese.getBytes("gbk")));
			} catch (UnsupportedEncodingException e3) {}
			
			StringBuilder specialChar = null;
			int mode = 0; //0=normal, 控制符=1, 特殊字=2;
			while(true) {
				try {
					byte b = is.readByte();
					if(mode==1) {
						specialChar.append((char)b);
						if(b=='}') {
							Util.ctrlToBytes(buf, specialChar.toString());
							mode=0;
						}
					} else if(mode==2){
						specialChar.append((char)b);
						if(b==']') {
							Integer code = charTable.getCode(specialChar.toString());
							buf.putShort(code.shortValue());
							mode=0;
						}
					} else {
						if(b=='{') {
							mode=1;
							specialChar=new StringBuilder();
							specialChar.append((char)b);
						} else if(b=='['){
							mode=2;
							specialChar=new StringBuilder();
							specialChar.append((char)b);
						} else {
							String char_ = null;
							if(b>=0&&b<=0x7f) {
								char_ = String.valueOf((char)b);
							} else {
								char_ = new String(new byte[]{b, is.readByte()}, "gbk");
							}
							Integer code = charTable.getCode(char_);
							if(code!=null) {
								buf.putShort(code.shortValue());
							} else {
								illegalChar.add(char_);
							}
						}
					}
				} catch(EOFException e1) {
					break;
				} catch(IOException e2) {
					break;
				}
			}
			
			byte[] bytes = Arrays.copyOf(buf.array(), buf.position());
//			System.out.println(chinese);
//			for(byte b : bytes) {
//				System.out.printf("%02X ", b);
//			}
//			System.out.println();
			textArray.add(bytes);
			buf.clear();
			buf.position(0);
		}
		if(!illegalChar.isEmpty()) {
			throw new RuntimeException("illegal char");
		}
		if(textArray.size()!=pointerCount) {
			throw new RuntimeException("not eq pointer size");
		}
		
		FileOutputStream fis = new FileOutputStream("D:\\ps3\\hanhua\\toysdream\\item.bin");
		for(byte[] bs : textArray) {
			fis.write(bs);
		}
		fis.close();
		long totalLen = 0;
		for(byte[] bs : textArray) {
			totalLen+=bs.length;
		}
		if(totalLen>textEndPos-textStartPos) {
			throw new RuntimeException("too long");
		} 
		return textArray;
		
	}
	
	public static int hilo(int i) {
		return i>>>24|i>>>8&0xff00|i<<8&0xff0000|i<<24&0xff000000;
	}

}
