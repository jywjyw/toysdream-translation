package toy.hack;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import toy.BinSplitPacker;
import toy.Conf;
import toy.dump.DumpImg;
import toy.hack.subtitle.SubtitleRowGen;

public class StampStaff {
	//KSS logo位置. 0008DE80的0xec
	
	
	public static void main(String[] args) throws IOException {
		BinSplitPacker bin = BinSplitPacker.newInstance();
		bin.split(Conf.bin);
		new StampStaff().stamp(bin, "1.0", "2018-02-03");
		new DumpImg().dump(bin.findFile("000659C8"), "C:\\Users\\Administrator\\Desktop\\", true);
	}
	
	public void stamp(BinSplitPacker bin, String version, String date) throws IOException{
		File pointerFile = bin.findFile("0008DE80");
		RandomAccessFile pointer = new RandomAccessFile(pointerFile, "rw");
		pointer.seek(0xee);
		pointer.write((byte)0xa3);//y轴相关
		pointer.seek(0xf7);
		pointer.write((byte)0xc3);//高度
		pointer.close();
		
		List<ImgPatch> patches = new ArrayList<>();
		int y=0x70,rowH=0x12;
		patches.add(new ImgPatch().setXY(1075,y).setSource(new StaffRowGen().generate("《玩具之梦》中文版 v"+version).datas));
		patches.add(new ImgPatch().setXY(1060,y+rowH).setSource(new StaffRowGen().generate("破解:草之头").datas));
		patches.add(new ImgPatch().setXY(1176,y+rowH).setSource(new StaffRowGen().generate("翻译:杨雪").datas));
		patches.add(new ImgPatch().setXY(1060,y+rowH*2).setSource(new StaffRowGen().generate("润色:草之头，静").datas));
		patches.add(new ImgPatch().setXY(1176,y+rowH*2).setSource(new StaffRowGen().generate("鸣谢:李海南").datas));
		patches.add(new ImgPatch().setXY(1195,y+rowH*3).setSource(new StaffRowGen().generate(date+"").datas));
		
		File imgFile = bin.findFile("000659C8");
		ImgPatcher.patch(imgFile, 0, patches);
	}
	
	

}
