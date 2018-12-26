package toy.hack.subtitle;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import toy.BinSplitPacker;

/**
 * 第11行没有指针
 */
public class Chapter_0210DFA0 extends AbstractHandler {
	
	
	@Override
	void doInHack(BinSplitPacker bin, List<SubtitleImgPos> points, List<String> lines)
			throws IOException {
		for(SubtitleImgPos pos : points) {
			RandomAccessFile pfile = new RandomAccessFile(bin.findFile(pos.getPointFileIdStr()), "rw");
			pfile.seek(pos.startPos);
			for(int i=0;i<18;i++){
				String line=null;
				if(i<=10){
					line = lines.get(i);
				}else{	
					line = lines.get(i+1);
				}
				SubtitleRow sub = new SubtitleRowGen().generate(line);
				modifyPointer(pfile, 0, sub.width);
			}
			pfile.close();
			writeToBin(pos, SubtitleRowPacker.pack(toSubs(lines)), bin);
		}
	}
	
}
