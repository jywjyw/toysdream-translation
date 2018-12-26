package toy;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Conf {
	
	public static String bin,hackbin, export,desktop;
	public static boolean genChinese;
	
	public static final int
			ENTRANCE_COUNT = 0x06f1,	//入口表的指针数
			ENTRANCE_FIRST_ADDR = 0x5358,	//第一个入口指针地址
			BIN_LEN=42534228,	//FILELINK.BIN的长度
			CHAR_LIMIT = 1764,	//字库最大容量
			charW=12,charH=12,	//每个字体宽高
			FONT_IMG_BODY_LEN = 0x20000,	//字库图片的图像体大小
			FONT_IMG_BODY_POS = 0x2b65c,		//字库图片的图像体起始位置,
			SUBTITLE_CHAR_H = 20	//字幕标准字体大小
			;

	public static final String 
			FONT_ADDR = "0002B5E0",	//主字库图的地址
			FONT_POINTER_ADDR = "0004B77C"	//主字库图指针的地址
			;
	
	static {
		InputStream is=null;
		try {
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.prop");
			Properties conf = new Properties();
			conf.load(is);
			bin = conf.getProperty("bin");
			assertNotnull(bin);
			hackbin = conf.getProperty("hackbin");
			export = conf.getProperty("export");
			assertNotnull(export);
			desktop = conf.getProperty("desktop");
			assertNotnull(desktop);
			genChinese = Boolean.parseBoolean(conf.getProperty("genChinese"));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
	}
	
	private static void assertNotnull(Object o) {
		if(o==null)throw new RuntimeException("conf.prop初始化失败..");
	}
	
	public static String getRawDir(){
		return System.getProperty("user.dir")+File.separator+"raw"+File.separator;
	}

}
