package toy.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import toy.Conf;

public class AddFontPointer {
	
	public static void main(String[] args) throws IOException {
		byte[] bs = new AddFontPointer().buildNewPointer();
		FileOutputStream fos = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\xxx");
		fos.write(bs);
		fos.close();
	}
	
	//扩充bin,修改入口表地址
	public void extendBin() {
		int breakpoint=0x0004D1B8;
		try {
			File tmp = File.createTempFile("newfilelink", "bin");
			FileOutputStream fos = new FileOutputStream(tmp);
			byte[] bs1 = Util.copyPartFile(Conf.hackbin, 0, breakpoint);
			fos.write(bs1);
			byte[] bs2 = buildNewPointer();
			fos.write(bs2);
			
			File bin = new File(Conf.bin);
			int restLen=(int) (bin.length()-breakpoint);
			byte[] bs3 = Util.copyPartFile(Conf.hackbin, breakpoint, restLen);
			fos.write(bs3);
			fos.close();
			
			new File(Conf.hackbin).delete();
			if(!tmp.renameTo(new File(Conf.hackbin))){
				throw new RuntimeException("无法删除Conf.hackbin");
			}
			
			modifyEntrance(bs2.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void modifyEntrance(int plusLen) throws IOException{
		RandomAccessFile hackbin = new RandomAccessFile(new File(Conf.hackbin), "rw");
		hackbin.seek(0x94);	//定位到子文件fontpointer的容量上
		int len = Util.hilo(hackbin.readInt());
		hackbin.seek(hackbin.getFilePointer()-4);//定位到子文件fontpointer的容量上
		hackbin.writeInt(Util.hilo(len+plusLen));
		hackbin.skipBytes(4);	//定位到下一个指针上
		while(hackbin.getFilePointer()<0x5358) {//TODO fontpointer指针后的所有指针都要改地址,但是entrance中最后一个指针00000000 74007400 00088000含义不明,暂不修改
			int addr = Util.hilo(hackbin.readInt());	
			addr+=plusLen;
			hackbin.seek(hackbin.getFilePointer()-4);	//准备写入新的地址
			hackbin.writeInt(Util.hilo(addr));
			hackbin.skipBytes(8);
		}
		hackbin.close();
	}
	
	public byte[] buildNewPointer(){
		ByteBuffer morepointer = ByteBuffer.allocate(86*4);	//增加86个字
		morepointer.putInt(0x1B00E4C0);
		morepointer.putInt(0x1B00F0C0);
		for(int y : new int[]{0xcc,0xd8,0xe4,0xf0}){
			for(int x=0;x<21;x++){
				morepointer.put(new byte[]{0x1b,0x0,(byte)(x*0xc&0xff),(byte) (y&0xff)});
			}
		}
		return morepointer.array();
	}

}
