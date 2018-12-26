package toy.hack.subtitle;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import toy.BinSplitPacker;

public class Chapter_default extends AbstractHandler {
	
	@Override
	void doInHack(BinSplitPacker bin, List<SubtitleImgPos> points, List<String> lines) throws IOException {
		List<SubtitleRow> subs = toSubs(lines);
		for(SubtitleImgPos s : points){
			RandomAccessFile pfile = new RandomAccessFile(bin.findFile(s.getPointFileIdStr()), "rw");
			pfile.seek(s.startPos);
			for(int i=0;i<subs.size();i++){
				SubtitleRow sub = subs.get(i);
				pfile.writeByte(getX(sub.width));	//left up pointer x in screen
				pfile.read(new byte[7]);	//skip 8bytes
				int x=0;
				pfile.writeByte(x);//left up pointer x in texture page
				pfile.readByte();	//bin.writeByte(Conf.SUBTITLE_CHAR_H*i);//left up pointer y  in texture page
				pfile.writeByte(sub.width&0xff);//row width
				pfile.readByte();//bin.writeByte(Conf.SUBTITLE_CHAR_H);//row height
				pfile.readInt();	//skip 4 bytes
			}
			pfile.close();
			
			writeToBin(s, SubtitleRowPacker.pack(subs), bin);
		}
	}
}
