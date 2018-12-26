package toy.hack.subtitle;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import toy.BinSplitPacker;

/**
 * 第1列没用完
 */
public class Chapter_027720F0 extends AbstractHandler {
	
	@Override
	void doInHack(BinSplitPacker bin, List<SubtitleImgPos> points, List<String> lines)
			throws IOException {
		SubtitleImgPos pos = points.get(0);
		RandomAccessFile pfile = new RandomAccessFile(bin.findFile(pos.getPointFileIdStr()), "rw");
		pfile.seek(pos.startPos);
		for(int i=0;i<13;i++){
			String line=null;
			if(i<=8){
				line = lines.get(i);
			}else{	
				line = lines.get(i+3);
			}
			SubtitleRow sub = new SubtitleRowGen().generate(line);
			modifyPointer(pfile, 0, sub.width);
		}
		pfile.close();
		writeToBin(pos, SubtitleRowPacker.pack(toSubs(lines)), bin);
	}
	
}
