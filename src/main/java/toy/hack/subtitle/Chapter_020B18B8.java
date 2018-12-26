package toy.hack.subtitle;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import toy.BinSplitPacker;

public class Chapter_020B18B8 extends AbstractHandler {
	
	@Override
	void doInHack(BinSplitPacker bin, List<SubtitleImgPos> points, List<String> lines)
			throws IOException {
		
		SubtitleImgPos pos1 = points.get(0);
		RandomAccessFile pfile1 = new RandomAccessFile(bin.findFile(pos1.getPointFileIdStr()), "rw");
		pfile1.seek(pos1.startPos);
		for(int i=0;i<5;i++){
			SubtitleRow sub = new SubtitleRowGen().generate(lines.get(i));
			modifyPointer(pfile1, 0, sub.width);
		}
		pfile1.close();
		
		SubtitleImgPos pos2 = points.get(1);
		RandomAccessFile pfile2 = new RandomAccessFile(bin.findFile(pos2.getPointFileIdStr()), "rw");
		pfile2.seek(pos2.startPos);
		for(int i=5;i<8;i++){
			SubtitleRow sub = new SubtitleRowGen().generate(lines.get(i));
			modifyPointer(pfile2, 0, sub.width);
		}
		pfile2.close();
		
		byte[] subtitleBytes = SubtitleRowPacker.pack(toSubs(lines));
		for(SubtitleImgPos s : points){
			writeToBin(s, subtitleBytes, bin);
		}
	}
	
}
