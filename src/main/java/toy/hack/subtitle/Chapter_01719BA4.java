package toy.hack.subtitle;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import toy.BinSplitPacker;
import toy.hack.ImgPatcher;

/**
 * 第1列没用完,且需要patch
 */
public class Chapter_01719BA4 extends AbstractHandler {
	
	@Override
	void doInHack(BinSplitPacker bin, List<SubtitleImgPos> points, List<String> lines)
			throws IOException {
		SubtitleImgPos pos = points.get(0);
		RandomAccessFile pfile = new RandomAccessFile(bin.findFile(pos.getPointFileIdStr()), "rw");
		pfile.seek(pos.startPos);
		for(int i=0;i<13;i++){
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
		
		byte[] bs = SubtitleRowPacker.pack(toSubs(lines));
		ImgPatcher.patch(bin.findFile(pos.getPackIdStr()), pos.imgIndex, 256*4,0, 256*2, bs);
	}
	
}
