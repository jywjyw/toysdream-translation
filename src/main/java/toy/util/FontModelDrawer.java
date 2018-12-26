package toy.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import org.apache.commons.codec.binary.Base64;

import toy.Conf;

/**
 * 抽取原始字库中的某些字模
 */
public class FontModelDrawer {
	
	public static void main(String[] args) throws IOException {
		new FontModelDrawer().batchGetDefaultFontBytes();
	}
	
	public void batchGetDefaultFontBytes() throws IOException {
		File bin = new File(Conf.bin);
		System.out.println("0="+toStr(getFontBytes(bin, "0000")));
		System.out.println("1="+toStr(getFontBytes(bin, "0100")));
		System.out.println("2="+toStr(getFontBytes(bin, "0200")));
		System.out.println("3="+toStr(getFontBytes(bin, "0300")));
		System.out.println("4="+toStr(getFontBytes(bin, "0400")));
		System.out.println("5="+toStr(getFontBytes(bin, "0500")));
		System.out.println("6="+toStr(getFontBytes(bin, "0600")));
		System.out.println("7="+toStr(getFontBytes(bin, "0700")));
		System.out.println("8="+toStr(getFontBytes(bin, "0800")));
		System.out.println("9="+toStr(getFontBytes(bin, "0900")));
		System.out.println("，="+toStr(getFontBytes(bin, "4600")));
		System.out.println("！="+toStr(getFontBytes(bin, "4900")));
		System.out.println("？="+toStr(getFontBytes(bin, "4a00")));
		System.out.println("。="+toStr(getFontBytes(bin, "f600")));
		System.out.println("[machine]="+toStr(getFontBytes(bin, "0805")));
		System.out.println("[energy]="+toStr(getFontBytes(bin, "0905")));
		System.out.println("[instrum]="+toStr(getFontBytes(bin, "0a05")));
		System.out.println("[elec]="+toStr(getFontBytes(bin, "0b05")));
		System.out.println("[chemistry]="+toStr(getFontBytes(bin, "0c05")));
		System.out.println("[design]="+toStr(getFontBytes(bin, "0d05")));
		System.out.println("[paper]="+toStr(getFontBytes(bin, "0e05")));
		System.out.println("[equip]="+toStr(getFontBytes(bin, "0f05")));
		System.out.println("[art]="+toStr(getFontBytes(bin, "1005")));
		System.out.println("[wine]="+toStr(getFontBytes(bin, "1105")));
		System.out.println("[material]="+toStr(getFontBytes(bin, "1205")));
		System.out.println("[money]="+toStr(getFontBytes(bin, "1305")));
		System.out.println("…="+toStr(getFontBytes(bin, "3705")));
		System.out.println("+="+toStr(getFontBytes(bin, "3e00")));
		System.out.println("-="+toStr(getFontBytes(bin, "3f00")));
		System.out.println("(="+toStr(getFontBytes(bin, "4300")));
		System.out.println(")="+toStr(getFontBytes(bin, "4400")));
		System.out.println("A="+toStr(getFontBytes(bin, "0A00")));
		System.out.println("B="+toStr(getFontBytes(bin, "0b00")));
		System.out.println("C="+toStr(getFontBytes(bin, "0c00")));
		System.out.println("D="+toStr(getFontBytes(bin, "0d00")));
		System.out.println("J="+toStr(getFontBytes(bin, "1300")));
		System.out.println("N="+toStr(getFontBytes(bin, "1700")));
		System.out.println("R="+toStr(getFontBytes(bin, "1b00")));
		System.out.println("S="+toStr(getFontBytes(bin, "1c00")));
		System.out.println("T="+toStr(getFontBytes(bin, "1D00")));
		System.out.println("Z="+toStr(getFontBytes(bin, "2300")));
	}
	
	/**
	 * 抽取ROM中原有的部分字模
	 * @param bin
	 * @param code
	 * @return
	 * @throws IOException
	 */
	public byte[] getFontBytes(File bin, String code) throws IOException {
		FontXy xy = CharDict.toCoord(code).toFontXy();
		RandomAccessFile bi = new RandomAccessFile(bin, "r");
		long start = 0x2b65c;
		ByteBuffer charBytes = ByteBuffer.allocate(12*12/2);//12点宽,12点高,每个点半个字节
		byte[] buf = new byte[12/2];//一次读x轴12个点
		for(int y=xy.y1;y<=xy.y2;y++) {
			int skipPixels = y*1024+xy.x1;
			bi.seek(start+skipPixels/2);
			bi.read(buf);
			charBytes.put(buf);
		}
		bi.close();
		return charBytes.array();
	}
	
	private String toStr(byte[] bs) {
		return Base64.encodeBase64String(bs);
	}

}
