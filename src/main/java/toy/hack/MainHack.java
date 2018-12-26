package toy.hack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import toy.BinSplitPacker;
import toy.CharStat;
import toy.Conf;
import toy.Resource;
import toy.dump.DumpImg;
import toy.hack.subtitle.SubtitleTranslate;


/**
 * 1.单个子文件的长度不可超过原长度
 * 2.总字数不能超过N个,并展现字数频率统计
 * 3.生成新码表
 * 4.生成新字库图片
 * 5.根据新码表重建指针表和文本区块
 */
public class MainHack {
	
	public static void main(String[] args) throws Exception {
//		copyNewBin();
		Resource resource = new Resource();
		BaseFontlibHacker fontlibHacker = new FontlibHacker2();
		XmlTranslationHandler xmlTranslationHandler = new XmlTranslationHandler();
		String[] xmls = new String[]{"menu_0012D51C","location_0012FFB0","item_00131288"};
		String[] excels = new String[]{"event","npc","mission"};
		
		Map<String,CharStat> char_count = new HashMap<>();
		for(String s : xmls) {
			xmlTranslationHandler.drawAllChars(s.split("_")[0], resource, char_count);
		}
		for(String s : excels) {
			new ExcelTranslationCharDrawer().drawAllChars(s, char_count);
		}
		new MissionExcelValidator().validate();
		
		Map<String,FontData> fonts = fontlibHacker.validateAndTransformFonts(char_count);
		
		BinSplitPacker binSplitPacker = BinSplitPacker.newInstance();
		binSplitPacker.split(Conf.bin);
		
		for(String s : xmls) {
			File file = binSplitPacker.findFile(s.split("_")[1]);
			xmlTranslationHandler.rebuildSingle(fonts, s.split("_")[0], file);
		}
		for(String s : excels) {
			new ExcelTranslationRebuilder().rebuildSingle(fonts, s, resource, binSplitPacker);
		}
//		
		new SubtitleTranslate().importXls(binSplitPacker, System.getProperty("user.dir")+"/translation/subtitle-v5.xlsx");
//new DumpImg().dump(binSplitPacker.findFile("01A8A248"), Conf.desktop, true);
		new StampStaff().stamp(binSplitPacker, "1.1", "2018-09-20");
//new DumpImg().dump(binSplitPacker.findFile("000659C8"), Conf.desktop, true);
		fontlibHacker.hack(binSplitPacker, fonts);
//new DumpImg().dump(binSplitPacker.findFile("0002B5E0"), Conf.desktop, true);
		
		binSplitPacker.pack(Conf.hackbin);
		binSplitPacker.dispose();
		System.out.println("filelink.bin was created successfully, use isopatch overwrite toys-hack.iso next.");
	}
	
	
	public static void copyNewBin(){
		File hackbin = new File(Conf.hackbin);
		if(!hackbin.delete()){
			throw new RuntimeException("无法删除旧的filelink.bin");
		}
		try {
			File bin=new File(Conf.bin);
			FileInputStream fis = new FileInputStream(Conf.bin);
			FileOutputStream fos = new FileOutputStream(hackbin);
			fis.getChannel().transferTo(0, bin.length(), fos.getChannel());
			fis.close();
			fos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
