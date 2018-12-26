package toy.hack.subtitle;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import toy.BinSplitPacker;
import toy.Conf;
import toy.Resource;
import toy.dump.FullImagePack;
import toy.dump.ImagePack;
import toy.dump.ImageReader;
import toy.dump.TextureMeta;
import toy.util.Util;

public abstract class AbstractHandler {
	
	protected void hack(BinSplitPacker bin, List<SubtitleImgPos> points, List<String> lines){
		if(!checkLength(lines)) return;
		try {
			doInHack(bin, points,lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	abstract void doInHack(BinSplitPacker bin, List<SubtitleImgPos> points, List<String> lines) throws IOException;
	
	protected boolean checkLength(List<String> lines){
		int length=0;
		for(String s : lines){length+=s.length();}
		if(length>0) {
			return true;
		} else {
			System.err.println("no translation text, skip");
			return false;
		}
	}
	
	protected void writeToBin(SubtitleImgPos pos, byte[] patch, BinSplitPacker bin) throws IOException{
		File target = bin.findFile(pos.getPackIdStr());
		FullImagePack imgPack = ImageReader.fullLoad(target);
		imgPack.replaceImgBody(pos.imgIndex, patch);
		Util.writeFile(target, imgPack.rebuild());
	}
	
	protected int getX(int w){
		return 0xff-(0xff-0x60)*w/320;
	}
	
	protected List<SubtitleRow> toSubs(List<String> lines){
		List<SubtitleRow> subs = new ArrayList<>();
		for(String s:lines){
			subs.add(new SubtitleRowGen().generate(s));	//生成字幕图像
		}
		return subs;
	}
	
	protected void modifyPointer(RandomAccessFile bin, int textureX, int width) throws IOException{
		bin.writeByte(getX(width));	//left up pointer x in screen
		bin.read(new byte[7]);	//skip n bytes
		bin.writeByte(textureX&0xff);//left up pointer x in texture page
		bin.readByte();	//bin.writeByte(Conf.SUBTITLE_CHAR_H*i);//left up pointer y  in texture page
		bin.writeByte(width&0xff);//row width
		bin.readByte();//bin.writeByte(Conf.SUBTITLE_CHAR_H);//row height
		bin.readInt();	//skip 4 bytes
	}
	
	//跳过n个指针
	protected void skipPointers(RandomAccessFile bin, int skipPointer) throws IOException{
		bin.skipBytes(skipPointer*16);
	}
	
	
	
}
