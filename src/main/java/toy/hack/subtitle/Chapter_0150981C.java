package toy.hack.subtitle;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import toy.hack.ImgPatcher;
import toy.BinSplitPacker;
import toy.hack.ImgPatch;

/**
 * patch
 */
public class Chapter_0150981C extends AbstractHandler {
	
	@Override
	void doInHack(BinSplitPacker bin, List<SubtitleImgPos> points, List<String> lines)
			throws IOException {
		SubtitleImgPos pos=points.get(0);
		RandomAccessFile pfile = new RandomAccessFile(bin.findFile(pos.getPointFileIdStr()), "rw");
		pfile.seek(pos.startPos);
		int realIndex=0;
		for(int i=0;i<26;i++){
			if(i==14||i==22) {
				pfile.read(new byte[16]);	//跳过
			} else {
				SubtitleRow sub = new SubtitleRowGen().generate(lines.get(realIndex));
				modifyPointer(pfile, 0, sub.width);
				realIndex++;
			}
		}
		pfile.close();
		
		byte[] img = SubtitleRowPacker.pack(toSubs(lines));
		ImgPatcher.patch(bin.findFile(pos.getPackIdStr()), pos.imgIndex, 256,0,256*2,img);
	}
	
}
