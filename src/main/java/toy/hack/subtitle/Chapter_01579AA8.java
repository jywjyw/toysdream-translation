package toy.hack.subtitle;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import toy.BinSplitPacker;

/**
 * 第6个指针多余，跳过
 */
public class Chapter_01579AA8 extends AbstractHandler {
	
	@Override
	void doInHack(BinSplitPacker bin, List<SubtitleImgPos> points, List<String> lines)
			throws IOException {
		SubtitleImgPos pos = points.get(0);
		RandomAccessFile pfile = new RandomAccessFile(bin.findFile(pos.getPointFileIdStr()), "rw");
		pfile.seek(pos.startPos);
		for(int i=0;i<20;i++){
			SubtitleRow sub = new SubtitleRowGen().generate(lines.get(i));
			if(i==5){
				skipPointers(pfile,1);
			}
			modifyPointer(pfile, 0, sub.width);
		}
		pfile.close();
		writeToBin(pos, SubtitleRowPacker.pack(toSubs(lines)), bin);
	}
	
}
