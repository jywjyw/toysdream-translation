package toy.hack;

import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import toy.BinSplitPacker;
import toy.Conf;
import toy.dump.FullImagePack;
import toy.dump.ImageReader;
import toy.dump.TextureMeta;
import toy.util.Util;

//不扩容字库,仅增加一点字符指针
public class FontlibHacker1 extends BaseFontlibHacker{
	
	@Override
	public int getPage() {
		return 4;
	}
	
	@Override
	public void hack(BinSplitPacker bin, Map<String, FontData> fonts) throws IOException, FontFormatException {
		super.generateFontModel(fonts);
		byte[] img = buildImgBody(fonts);
		
		File fontfile = bin.findFile(Conf.FONT_ADDR);
		FullImagePack imgpack=null;
		try {
			imgpack = ImageReader.fullLoad(fontfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		TextureMeta m = imgpack.imgHeaders.get(0);
		imgpack.replaceImgBody(0, img, m.w, m.h);
		Util.writeFile(fontfile, imgpack.rebuild());
		
		byte[] pointer = FontPointerTable.rebuild4page(fonts.size());
		Util.writeFile(bin.findFile(Conf.FONT_POINTER_ADDR), pointer);
	}
}
