package toy.hack.subtitle;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import toy.BinSplitPacker;

/**
 * 2行字幕在同一行
 */
public class Chapter_01A1F420 extends AbstractHandler {
	
	@Override
	void doInHack(BinSplitPacker bin, List<SubtitleImgPos> points, List<String> lines)
			throws IOException {
		SubtitleImgPos pos = points.get(0);
		RandomAccessFile pfile = new RandomAccessFile(bin.findFile(pos.getPointFileIdStr()), "rw");
		pfile.seek(pos.startPos);
		for(int i=0;i<13;i++){
			SubtitleRow sub = new SubtitleRowGen().generate(lines.get(i));
			if(i==10){
				SubtitleRow sub8 = new SubtitleRowGen().generate(lines.get(8));
				modifyPointer(pfile, 0+sub8.width, sub.width);
			}else{
				modifyPointer(pfile, 0, sub.width);
			}
		}
		pfile.close();
		
		lines.set(8, lines.get(8)+lines.remove(10));	//合并2行
		writeToBin(pos, SubtitleRowPacker.pack(toSubs(lines)), bin);
	}
	
}
