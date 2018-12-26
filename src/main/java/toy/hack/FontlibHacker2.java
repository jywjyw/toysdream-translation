package toy.hack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import toy.BinSplitPacker;
import toy.Conf;
import toy.dump.FullImagePack;
import toy.dump.ImageReader;
import toy.util.Util;

//扩容字库
public class FontlibHacker2 extends BaseFontlibHacker{
	
	@Override
	public int getPage(){
		return 5;
	}
	
	@Override
	public void hack(BinSplitPacker bin, Map<String,FontData> fonts) {
		super.generateFontModel(fonts);
		byte[] img = buildImgBody(fonts);
		File fontfile = bin.findFile(Conf.FONT_ADDR);
		FullImagePack imgpack=null;
		try {
			imgpack = ImageReader.fullLoad(fontfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		imgpack.replaceImgBody(0, img, 64*getPage(), imgpack.imgHeaders.get(0).h);//64为单个材质页在显存中所占宽度,并非实际宽度
//		imgpack.replaceClut(5, loadPalette("1008-new"));	//将其中一个不易识别的颜色替换
//		imgpack.replaceClut(8, loadPalette("1008-204"));	//将其中一个不易识别的颜色替换
		Util.writeFile(fontfile, imgpack.rebuild());
		
		byte[] pointer = FontPointerTable.rebuild5page(fonts.size());
		Util.writeFile(bin.findFile(Conf.FONT_POINTER_ADDR), pointer);
	}
	
	byte[] loadPalette(String pal){
		try {
			FileInputStream fis = new FileInputStream(Conf.getRawDir()+pal);
			byte[] buf = new byte[32];
			fis.read(buf);
			fis.close();
			return buf;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		for(int i=0;i<8;i++)
			System.out.printf("%02X ",0x1f-4*i);
	}
}
