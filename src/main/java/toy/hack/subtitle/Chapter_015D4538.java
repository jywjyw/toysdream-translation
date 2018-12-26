package toy.hack.subtitle;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import toy.BinSplitPacker;

/**
 * 指针表中只有最后一行
 */
public class Chapter_015D4538 extends AbstractHandler {
	
	@Override
	void doInHack(BinSplitPacker bin, List<SubtitleImgPos> points, List<String> lines)
			throws IOException {
		SubtitleImgPos pos = points.get(0);
		RandomAccessFile pfile = new RandomAccessFile(bin.findFile(pos.getPointFileIdStr()), "rw");
		pfile.seek(pos.startPos);
		
		SubtitleRow only = new SubtitleRowGen().generate(lines.get(11));
		modifyPointer(pfile, 0, only.width);
		pfile.close();
		writeToBin(pos, SubtitleRowPacker.pack(toSubs(lines)), bin);
	}
	
}
