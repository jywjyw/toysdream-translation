package toy.hack.subtitle;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import toy.BinSplitPacker;

public class Chapter_01A8A248 extends AbstractHandler {
	
	@Override
	void doInHack(BinSplitPacker bin, List<SubtitleImgPos> points, List<String> lines)
			throws IOException {
		SubtitleImgPos pos = points.get(0);
		RandomAccessFile pfile = new RandomAccessFile(bin.findFile(pos.getPointFileIdStr()), "rw");
		pfile.seek(pos.startPos);
		
		modifyPointer(pfile, 0, new SubtitleRowGen().generate(lines.get(0)).width);
		
		for(int i=2;i<=19;i++){
			SubtitleRow sub = new SubtitleRowGen().generate(lines.get(i));
			modifyPointer(pfile, 0, sub.width);
		}
		writeToBin(pos, SubtitleRowPacker.pack(toSubs(lines)), bin);
	}
	
}
