package toy.hack.subtitle;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import toy.hack.ImgPatcher;
import toy.BinSplitPacker;
import toy.hack.ImgPatch;

/**
 * 超自由补丁
 */
public class Chapter_0144692C extends AbstractHandler {
	
	@Override
	void doInHack(BinSplitPacker bin, List<SubtitleImgPos> points, List<String> lines)
			throws IOException {
		SubtitleImgPos pos = points.get(0);
//		RandomAccessFile pfile = new RandomAccessFile(bin.findFile(pos.getPointFileIdStr()), "rw");
//		pfile.seek(pos.startPos);
		List<ImgPatch> patches = new ArrayList<>();
		
		SubtitleRow sub1 = new SubtitleRowGen().generate(lines.get(1));
		patches.add(new ImgPatch().setXY(0, 0).setSource(sub1.datas));
		
		SubtitleRow sub2 = new SubtitleRowGen().generate(lines.get(2));
		patches.add(new ImgPatch().setXY(0, 0x14).setSource(sub2.datas));
		
		SubtitleRow sub3 = new SubtitleRowGen().generate(lines.get(3));
		patches.add(new ImgPatch().setXY(0x78, 0).setSource(sub3.datas));
		
		SubtitleRow sub4 = new SubtitleRowGen().generate(lines.get(4));
		patches.add(new ImgPatch().setXY(0, 0x28).setSource(sub4.datas));
		
		SubtitleRow sub5 = new SubtitleRowGen().generate(lines.get(5));
		patches.add(new ImgPatch().setXY(0, 0x3c).setSource(sub5.datas));
		
		
		ImgPatcher.patch(bin.findFile(pos.getPackIdStr()), pos.imgIndex, patches);
	}
	
}
