package toy.hack;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import toy.BinSplitPacker;
import toy.Conf;
import toy.dump.DumpImg;
import toy.dump.ImagePack;
import toy.dump.ImageReader;
import toy.dump.TextureMeta;
import toy.hack.subtitle.SubtitleRow;
import toy.hack.subtitle.SubtitleRowGen;
import toy.hack.subtitle.SubtitleRowPacker;

public class ImgPatcher {
	
	public static void main(String...args) throws IOException{
		SubtitleRow sub0 = new SubtitleRowGen().generate("工工工工工式工工工工工");
		SubtitleRow src = new SubtitleRowGen().generate("有在右脚有在以要要有");
		byte[] subs = SubtitleRowPacker.pack(Arrays.asList(sub0,src));
		int total = src.datas.get(0).length*src.datas.size();
		ByteBuffer buf = ByteBuffer.allocate(total);
		for(byte[] bs:src.datas){
			buf.put(bs);
		}
		ImgPatch p = new ImgPatch().setXY(256, 20).setSource(src.datas);
		List<ImgPatch> ps=new ArrayList<>();
		ps.add(p);
		
		BinSplitPacker binpacker = BinSplitPacker.newInstance();
		binpacker.split(Conf.bin);
		File target = binpacker.findFile("013ECE68");
		patch(target, 1, 256,0,256, subs);
		new DumpImg().dump(new File(binpacker.getSplitDir()+"013ECE68"), "C:\\Users\\Administrator\\Desktop\\", true);
	}
	
	public static void patch(File splitFile, int imgIndex, List<ImgPatch> patches) throws IOException{
		ImagePack imgPack = ImageReader.load(splitFile);
		TextureMeta meta = imgPack.imgHeaders.get(imgIndex);
		int ppb=2;	//pixel per byte
		RandomAccessFile file = new RandomAccessFile(splitFile, "rw");
		for(ImgPatch patch : patches){
			for(int i=0;i<patch.source.size();i++) {
				long offset= ((patch.y+i)*meta.get4bitWidth()+patch.x)/ppb;
				file.seek(meta.pos+offset);
				file.write(patch.source.get(i));
			}
		}
		file.close();
	}
	
	public static void patch(File splitFile,  int imgIndex, int x, int y, int w, byte[] data) throws IOException{
		ImagePack imgPack = ImageReader.load(splitFile);
		TextureMeta meta = imgPack.imgHeaders.get(imgIndex);
		int ppb=2;	//pixel per byte
		int lines=data.length*ppb/w;
		byte[] buf = new byte[w/ppb];
		RandomAccessFile file = new RandomAccessFile(splitFile, "rw");
		for(int i=0;i<lines;i++){
			long offset= ((y+i)*meta.get4bitWidth()+x)/ppb;
			file.seek(meta.pos+offset);
			System.arraycopy(data, buf.length*i, buf, 0, buf.length);
			file.write(buf);
		}
		file.close();
	}

}
