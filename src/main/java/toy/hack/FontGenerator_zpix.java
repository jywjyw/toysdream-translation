package toy.hack;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import toy.Conf;
import toy.Palette;
import toy.dump.DumpImg;

/**
 * 单个字模生成器. 使用小点阵字体.
 * @author me
 *
 */
public class FontGenerator_zpix {
	
	public static void main(String[] args) throws FontFormatException, IOException {
		Palette pal = new Palette(16, "1008-200");
		FontGenerator_zpix fontg = new FontGenerator_zpix();
//		fontg.toVerticalImg(pal, "酪司馆翻箱以评筒螺丝".toCharArray());
		fontg.generate("翻箱以评筒螺丝()0123456789".toCharArray());
		
	}
	
	public List<byte[]> generate(char[] cs) throws FontFormatException, IOException {
		Palette pal = new Palette(16, "1008-200");
		BufferedImage img = toVerticalImg(pal, cs);
		WritableRaster raster = img.getRaster();
		List<byte[]> ret = new ArrayList<>();
		
		int[] buf = new int[Conf.charW*Conf.charH*3];	//rgb下为12*12*3
		for(int i=0;i<cs.length;i++) {
			raster.getPixels(0, i*Conf.charH+2, Conf.charW, Conf.charH, buf);
			
//if(i>=0&&i<5)debugToFile(cs[i]+"", buf);
			int[] indexes = new int[Conf.charW*Conf.charH];
			int _z=0;
			for(int j=0;j<buf.length;j+=3) {
				indexes[_z++]=getColorIndex(pal, buf[j], buf[j+1], buf[j+2]);
			}
			ret.add(FontGenUtil.colorIndexesToImgFormat(indexes));
		}
		
		return ret;
	}
	
	
	private BufferedImage toVerticalImg(Palette pal, char[] cs) throws FontFormatException, IOException {
		String savetype = "bmp";
		int imageType = BufferedImage.TYPE_INT_RGB;
		int w = Conf.charW,h=(cs.length+1)*Conf.charH;
		BufferedImage large = new BufferedImage(w, h, imageType);
		String ttf = "Zpix.ttf";
		InputStream is = new FileInputStream(System.getProperty("user.dir")+"/raw/"+ttf); 
		Font font = Font.createFont(Font.TRUETYPE_FONT, is);
		is.close();
		font=font.deriveFont(Font.PLAIN, 12);
		
		StringBuilder invalidChar = new StringBuilder();
		for(char c : cs) {
			if(!font.canDisplay(c)) invalidChar.append(c);
		}
		if(invalidChar.length()>0)
			throw new RuntimeException("字库无法识别以下字符:" + invalidChar.toString());
		
		Graphics2D g = (Graphics2D) large.getGraphics();
		g.setColor(Color.WHITE);	
		g.fillRect(0, 0, w, h);
		
		
		g.setColor(Color.BLACK);
		g.setFont(font);
		for(int i=0;i<cs.length;i++) {
			int y = (i+1)*(Conf.charH);
			g.drawString(cs[i]+"", 0, y-1);	//向上修正一个像素,因为书写起始位置向上偏了点
		}
		g.dispose();
//ImageIO.write(large, savetype, new File(Conf.desktop+"large."+savetype));
		
	    return large;
	}
	
	private int getColorIndex(Palette pal, int r32,int g32, int b32) {
		if(r32==255&&g32==255&&b32==255)
			return 0;
		return 2;	//只取第1~5个色点
	}
}
