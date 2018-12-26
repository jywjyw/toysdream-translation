package toy.hack;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import toy.Conf;
import toy.Palette;
import toy.hack.subtitle.SubtitleRow;
import toy.hack.subtitle.SubtitleRowGen;

public class StaffRowGen {
	

	public static void main(String[] args) throws IOException {
//		Palette pal = new Palette(16, "1008-200");
		StaffRowGen fontg = new StaffRowGen();
		fontg.generate("破解:草之头  翻译:小雪");//０１２
//		fontg.toHorizontalImage(new Palette(16, "992-0"), "期限约束通研究所0123456789".toCharArray());
	}
	
	private static Font FONT;
	private static int FONT_SIZE=12, HEIGHT = 24,ASCENT=16, MAX_WIDTH=0xFE;	//ROM中只用1个字节表示宽度
	static{
		if(FONT_SIZE%2!=0) throw new RuntimeException("fontsize必须为偶数");
//		String ttf = "方正像素18.ttf";
//		String ttf = "方正隶变简体.ttf";
		String ttf = "Zpix.ttf";
		try {
			InputStream is = new FileInputStream(System.getProperty("user.dir")+"/raw/"+ttf); 
			FONT = Font.createFont(Font.TRUETYPE_FONT, is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		FONT=FONT.deriveFont(Font.PLAIN, FONT_SIZE);
	}
	
	/**
	 * 
	 * @param s
	 * @return byte[] is a 1 pixel height scanline.
	 * @throws IOException
	 */
	public SubtitleRow generate(String s) {
		try {
			BufferedImage img = toHorizontalImg(s);
			WritableRaster raster = img.getRaster();
			List<byte[]> ret = new ArrayList<>();
			for(int y=0;y<raster.getHeight();y++) {	
				int[] row = new int[raster.getWidth()*3];	//argb下每个点4字节, rgb下每点3字节
				raster.getPixels(0, y, raster.getWidth(), 1, row);
				int[] indexes=new int[raster.getWidth()];
				int _z=0;
				for(int j=0;j<row.length;j+=3) {
					indexes[_z++]=getColorIndex(row[j], row[j+1], row[j+2]);
				}
				ret.add(FontGenUtil.colorIndexesToImgFormat(indexes));
			}
			return new SubtitleRow(ret, raster.getWidth());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	private BufferedImage toHorizontalImg(String s) throws IOException {
		int imageType = BufferedImage.TYPE_INT_RGB;
		int w = MAX_WIDTH,
			h = HEIGHT;
		if(w%2!=0) {
			throw new RuntimeException("图像宽度必须为偶数");
		}
		BufferedImage img = new BufferedImage(w, h, imageType);
		
		Graphics2D g2d = (Graphics2D) img.getGraphics();
		g2d.setColor(Color.BLACK);	
		g2d.fillRect(0, 0, w, h);
		
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(FONT);
        g2d.setColor(Color.white);
        g2d.drawString(s, 0, ASCENT);
        
        img = crop(img);
		
		g2d.dispose();
	    return img;
	}
	
	private boolean isBgColor(int[] rgb){
		return rgb[0]==0&&rgb[1]==0&&rgb[2]==0;
	}
	
	private int getColorIndex(int r32,int g32, int b32) {
		if(isBgColor(new int[]{r32,g32,b32}))	//这是背景色,透明
			return 0;
		return 11;	//字体颜色设置为960-1色板的第n号颜色
	}
	
	private BufferedImage crop(BufferedImage img){
		WritableRaster raster = img.getRaster();
    	int[] buf = new int[3];
    	int flagX=0;
    	for(int y=0;y<raster.getHeight();y++){
    		for(int x=raster.getWidth()-1;x>=flagX;x--){
    			raster.getPixel(x, y, buf);
    			if(!isBgColor(buf)){
    				flagX=Math.max(flagX, x);
    				break;
    			}
    		}
    	}
    	int cropwidth=flagX+1;
    	if(cropwidth%2!=0) cropwidth++;
    	return img.getSubimage(0, 0, cropwidth, img.getHeight());
	}

}
