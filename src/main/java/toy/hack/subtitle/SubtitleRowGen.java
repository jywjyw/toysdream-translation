package toy.hack.subtitle;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import toy.Conf;
import toy.Palette;
import toy.hack.FontGenUtil;

/**
 * 字幕图片生成
 * @author me
 *
 */
public class SubtitleRowGen {
	
	public static void main(String[] args) throws IOException {
//		Palette pal = new Palette(16, "1008-200");
		SubtitleRowGen fontg = new SubtitleRowGen();
		fontg.generate("成为这个国家的机会的我一。");//０１２
//		fontg.toHorizontalImage(new Palette(16, "992-0"), "期限约束通研究所0123456789".toCharArray());
	}
	
	private static Font FONT;
	private static int FONT_SIZE=18, ASCENT=16, MAX_WIDTH=0xFE;	//ROM中只用1个字节表示宽度
	private static Palette PALETTE;
	static{
		if(FONT_SIZE%2!=0) throw new RuntimeException("fontsize必须为偶数");
//		String ttf = "方正像素18.ttf";
		String ttf = "方正隶变简体.ttf";
//		String ttf = "SonyReader Li.ttf";
		try {
			InputStream is = new FileInputStream(System.getProperty("user.dir")+"/raw/"+ttf); 
			FONT = Font.createFont(Font.TRUETYPE_FONT, is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		FONT=FONT.deriveFont(Font.BOLD, FONT_SIZE);
		try {
			PALETTE = new Palette(16, "992-0");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param s
	 * @return byte[] is a 1 pixel height scanline.
	 * @throws IOException
	 */
	public SubtitleRow generate(String s) {
		if("".equals(s)) {	//无字符时,返回一个高度为x的空字幕
			List<byte[]> ret = new ArrayList<>();
			for(int i=0;i<Conf.SUBTITLE_CHAR_H;i++){	
				ret.add(new byte[]{});
			}
			return new SubtitleRow(ret, 0);
		}
		try {
			BufferedImage img = toHorizontalImg(PALETTE, s);
			WritableRaster raster = img.getRaster();
			List<byte[]> ret = new ArrayList<>();
			for(int y=0;y<raster.getHeight();y++) {	//变成色板中最相似的颜色
				int[] row = new int[raster.getWidth()*3];	//argb下每个点4字节, rgb下每点3字节
				raster.getPixels(0, y, raster.getWidth(), 1, row);
				int[] indexes=new int[raster.getWidth()];
				int _z=0;
				for(int j=0;j<row.length;j+=3) {
					indexes[_z++]=getColorIndex(PALETTE, row[j], row[j+1], row[j+2]);
//				indexes[_z++]=0;
				}
				ret.add(FontGenUtil.colorIndexesToImgFormat(indexes));
			}
			return new SubtitleRow(ret, raster.getWidth());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	private BufferedImage toHorizontalImg(Palette pal, String s) throws IOException {
		int imageType = BufferedImage.TYPE_INT_RGB;
		int w = MAX_WIDTH,
			h = Conf.SUBTITLE_CHAR_H;
		if(w%2!=0) {
			throw new RuntimeException("图像宽度必须为偶数");
		}
		BufferedImage img = new BufferedImage(w, h, imageType);
		assertCanDisplay(FONT, s.toCharArray());
		
		Graphics2D g2d = (Graphics2D) img.getGraphics();
		g2d.setColor(Color.blue);	
		g2d.fillRect(0, 0, w, h);
		
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
//        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
//        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        
        TextLayout textLayout = new TextLayout(s, FONT, g2d.getFontRenderContext());
    	Shape outline = textLayout.getOutline(null);
    	g2d.translate(0, ASCENT);
    	
    	g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
    	g2d.setColor(Color.black);
    	g2d.draw(outline);
    	makeDarker(img);
    	if(outline.getBounds().getWidth()>MAX_WIDTH) {
    		throw new RuntimeException("文本超长,最大像素宽度"+MAX_WIDTH+":"+s);
    	}
    	
    	g2d.setStroke(new BasicStroke(0));
    	g2d.setColor(Color.white);
    	g2d.fill(outline);
    	
        
        img = crop(img);
		
//ImageIO.write(img, "bmp", new File(Conf.desktop+"large.bmp"));
		
		g2d.dispose();
	    return img;
	}
	
	private int getColorIndex(Palette pal, int r32,int g32, int b32) {
		if(isBgColor(new int[]{r32,g32,b32}))	//这是背景色,透明
			return 0;
		
		double min = Double.MAX_VALUE;
		Integer index = null;
		for(int i=1;i<=15;i++) {	//第1个颜色全黑,只用于字体背景
			int 
				_r = Math.abs(pal.rgb256Matrix[i][0]-r32), 
				_g=Math.abs(pal.rgb256Matrix[i][1]-g32), 
				_b=Math.abs(pal.rgb256Matrix[i][2]-b32);
			double distance = Math.sqrt(_r*_r+_g*_g+_b*_b);
			if(distance<min) {
				min=distance;
				index = i;
			}
		}
		return index;
	}
	
	private void assertCanDisplay(Font font, char[] cs){
		StringBuilder invalidChar = new StringBuilder();
		for(char c : cs) {
			if(!font.canDisplay(c)) invalidChar.append(c);
		}
		if(invalidChar.length()>0)
			throw new RuntimeException("字库无法识别以下字符:" + invalidChar.toString());
	}
	
	private void makeDarker(BufferedImage img){
		WritableRaster raster = img.getRaster();
    	int[] buf = new int[3];
    	for(int y=0;y<raster.getHeight();y++){
    		for(int x=0;x<raster.getWidth();x++){
    			raster.getPixel(x, y, buf);
    			if(isBgColor(buf)){
    				continue;
    			} else{
    				raster.setPixel(x,y,new int[]{1,1,1});
    			}
    		}
    	}
	}
	
	private boolean isBgColor(int[] rgb){
		return rgb[0]==0&&rgb[1]==0&&rgb[2]==255;
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
