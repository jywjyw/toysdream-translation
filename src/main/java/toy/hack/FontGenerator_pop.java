package toy.hack;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.apache.poi.util.SystemOutLogger;

import toy.Conf;
import toy.Palette;

public class FontGenerator_pop {
	
	public static void main(String[] args) throws FontFormatException, IOException {
		FontGenerator_pop fontg = new FontGenerator_pop();
		fontg.generate("研究所情报记忆卡物品一览".toCharArray());
		
	}
	
	public List<byte[]> generate(char[] cs) throws FontFormatException, IOException {
		BufferedImage img = toVerticalImg(cs);
		WritableRaster raster = img.getRaster();
//debugPixelDist(raster);
		List<byte[]> ret = new ArrayList<>();
		int[] buf = new int[Conf.charW*Conf.charH*1];	//rgb下乘3,arg下乘4,灰度下乘1
		for(int i=0;i<cs.length;i++) {
			raster.getPixels(0, i*Conf.charH+2, Conf.charW, Conf.charH, buf);
			int[] indexes = new int[Conf.charW*Conf.charH];
			int _z=0;
			for(int j=0;j<buf.length;j++) {
				indexes[_z++]=getColorIndex(buf[j]);
			}
			ret.add(FontGenUtil.colorIndexesToImgFormat(indexes));
		}
		
		return ret;
	}
	
	private BufferedImage toVerticalImg(char[] cs) throws FontFormatException, IOException {
		String savetype = "bmp";
		int imageType = BufferedImage.TYPE_BYTE_GRAY;
		int w = Conf.charW,h=(cs.length+1)*Conf.charH;
		BufferedImage large = new BufferedImage(w, h, imageType);
		String ttf = "华康POP1W5.ttc";
		InputStream is = new FileInputStream(System.getProperty("user.dir")+"/raw/"+ttf); 
		Font font = Font.createFont(Font.TRUETYPE_FONT, is);
		is.close();
		font=font.deriveFont(Font.PLAIN,12);
		
		StringBuilder invalidChar = new StringBuilder();
		for(char c : cs) {
			if(!font.canDisplay(c)) invalidChar.append(c);
		}
		if(invalidChar.length()>0)
			throw new RuntimeException("字库无法识别以下字符:" + invalidChar.toString());
		
		Graphics2D g = (Graphics2D) large.getGraphics();
		g.setColor(Color.white);	//背景设为白色,灰度值为255
		g.fillRect(0, 0, w, h);
		
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//		g.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, 160);
		
		g.setColor(Color.black);
		g.setFont(font);
		for(int i=0;i<cs.length;i++) {
			int y = (i+1)*(Conf.charH);
			g.drawString(cs[i]+"", 0, y);
		}
		g.dispose();
		ImageIO.write(large, savetype, new File(Conf.desktop+"large."+savetype));
		
	    return large;
	}
	
	//原始字库图中,字体色彩都分布在第1~8个颜色中. 从第1到第8,颜色越来越亮
	//grey值越小,颜色越暗
	private int getColorIndex(int grey) {
		if	   (grey<=15)	return 1;
		else if(grey<=55)	return 2;
		else if(grey<=95)	return 3;
		else if(grey<=135)	return 4;
		else if(grey<=180)	return 5;
		
		else if(grey<=204)	return 6;
		else if(grey<=228)	return 7;
		else if(grey<=254)	return 8;
		
		else if(grey==255)	return 0;
		else throw new UnsupportedOperationException();
	}
	
	//查看各个像素值的个数
	private void debugPixelDist(WritableRaster raster){
		Map<Integer,Integer> counts = new HashMap<>();
		for(int i=0;i<256;i++){
			counts.put(i, 0);
		}
		int[] buf1 = new int[1];
		for(int y=0;y<raster.getHeight();y++){
//			System.out.print(y+" = ");
			for(int x=0;x<raster.getWidth();x++){
				raster.getPixel(x, y, buf1);
				counts.put(buf1[0], counts.get(buf1[0])+1);
//				System.out.printf("%s,",buf1[0]);
			}
			System.out.println();
		}
		
		int totalPix = raster.getWidth()*raster.getHeight();
		int index=1;
		for(Entry<Integer,Integer> e:counts.entrySet()){
			//gray value = count percent
			if(e.getValue()>0)
			System.out.printf("%d  %d=%.1f%%\n",index++, e.getKey(),e.getValue()/(double)totalPix*100);
		}
	}
}
