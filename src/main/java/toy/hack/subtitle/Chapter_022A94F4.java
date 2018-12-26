package toy.hack.subtitle;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import toy.BinSplitPacker;

public class Chapter_022A94F4 extends AbstractHandler {
	
	@Override
	void doInHack(BinSplitPacker bin, List<SubtitleImgPos> points, List<String> lines)
			throws IOException {
		SubtitleImgPos pos = points.get(0);
		RandomAccessFile pfile = new RandomAccessFile(bin.findFile(pos.getPointFileIdStr()), "rw");
		pfile.seek(pos.startPos);
		for(int i=0;i<13;i++){
			SubtitleRow sub = new SubtitleRowGen().generate(lines.get(i));
			if(i==6){
				SubtitleRow sub5 = new SubtitleRowGen().generate(lines.get(5));
				modifyPointer(pfile, 0+sub5.width, sub.width);
			}else{
				modifyPointer(pfile, 0, sub.width);
			}
		}
		pfile.close();
		
		lines.set(5, lines.get(5)+lines.remove(6));	//合并2行
		writeToBin(pos, SubtitleRowPacker.pack(toSubs(lines)), bin);
	}
	
}
