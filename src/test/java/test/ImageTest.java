package test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import toy.Conf;
import toy.Palette;

public class ImageTest {
	
	public static void main(String[] args) throws Exception{
		g2();
	}
	
	public static IndexColorModel newModel(){
		int colorCount = 7;
		byte[] r = new byte[colorCount], g = new byte[colorCount], b = new byte[colorCount]; 
		r[0] = (byte)(255);
		g[0] = (byte)(255);
		b[0] = (byte)(255);
		
		r[1] = (byte)(255);
		g[1] = (byte)(0);
		b[1] = (byte)(0);

		r[2] = (byte)(0);
		g[2] = (byte)(255);
		b[2] = (byte)(0);
		
		r[2] = (byte)(0);
		g[2] = (byte)(0);
		b[2] = (byte)(255);
		
		r[3] = (byte)(255);
		g[3] = (byte)(255);
		b[3] = (byte)(0);
		
		r[4] = (byte)(255);
		g[4] = (byte)(0);
		b[4] = (byte)(255);
		
		r[5] = (byte)(0);
		g[5] = (byte)(255);
		b[5] = (byte)(255);
		
		r[6] = (byte)(0);
		g[6] = (byte)(0);
		b[6] = (byte)(0);
		
//		return new IndexColorModel(8, colorCount, r, g, b);
//		try {
//			return new Palette(16, "992-0").toIndexColorModel();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			return null;
//		}
		try {
			return toIndexColorModel(new Palette(16, "992-0"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void g3()throws Exception{
		int w=200,h=50;
		BufferedImage img = new BufferedImage(w,h, BufferedImage.TYPE_BYTE_INDEXED, newModel());
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.black);
		g.fillRect(0, 0, w,h);
		
		String ttf = "方正隶变简体.ttf";
		InputStream is = new FileInputStream(System.getProperty("user.dir")+"/raw/"+ttf); 
		Font font = Font.createFont(Font.TRUETYPE_FONT, is);
		is.close();
		font = font.deriveFont(Font.TRUETYPE_FONT, 18);
		g.setFont(font);
		g.setColor(Color.white);
		g.drawString("一二三中载", 0, 30);
		
		ImageIO.write(img, "png", new File(Conf.desktop+"large.png"));
		g.dispose();
	}
	
	public static IndexColorModel toIndexColorModel(Palette pal) {
		List<Integer> indexs =Arrays.asList(0,1,4,7,10,14,15);
		int count= indexs.size();
		byte[] r = new byte[count]; 
		byte[] g = new byte[count]; 
		byte[] b = new byte[count]; 
		
		for(int i=0;i<indexs.size();i++){
			int index=indexs.get(i);
			int[] rgb = pal.rgb32Matrix[index];
			r[i] =(byte) _32to256(rgb[0]);
			g[i] =(byte) _32to256(rgb[1]);
			b[i] =(byte) _32to256(rgb[2]);
		}
		return new IndexColorModel(5, count, r, g, b);
	}
	
	private static int _32to256(int _32) {
		return (int)_32*255/31;
	}
	
	public static void g1()throws Exception{
		int w=200,h=50;
		BufferedImage img = new BufferedImage(w,h, BufferedImage.TYPE_BYTE_INDEXED, newModel());
		String ttf = "SonyReader Li.ttf";
		InputStream is = new FileInputStream(System.getProperty("user.dir")+"/raw/"+ttf); 
		Font font = Font.createFont(Font.TRUETYPE_FONT, is);
		is.close();
		font = font.deriveFont(Font.TRUETYPE_FONT, 24);
		
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setColor(Color.WHITE);	
		g.fillRect(0, 0, w,h);
		
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		g.setColor(Color.BLACK);
		g.setFont(font);
		g.drawString("我们都是", 0,40);
		g.dispose();
		
		ImageIO.write(img, "png", new File(Conf.desktop+"large.png"));
	}
	
	public static void g2()throws Exception{
		int w=200,h=100;
		BufferedImage img = new BufferedImage(w,h, BufferedImage.TYPE_BYTE_GRAY);
//		String ttf = "方正隶变简体.ttf";
		String ttf = "方正像素18.ttf";
		InputStream is = new FileInputStream(System.getProperty("user.dir")+"/raw/"+ttf); 
		Font font = Font.createFont(Font.TRUETYPE_FONT, is);
		is.close();
		float size=18f;
		font = font.deriveFont(Font.BOLD, size);
		Graphics2D g2d = (Graphics2D) img.getGraphics();
		g2d.setColor(Color.white);	
		g2d.fillRect(0, 0, w, h);
		
		g2d.setFont(font);
		int ascent=g2d.getFontMetrics(font).getAscent();
		
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        
        String text="Hello我们都是一个兵器";
        g2d.setColor(Color.black);
//        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
//        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        TextLayout textLayout = new TextLayout(text, font, g2d.getFontRenderContext());
    	Shape outline = textLayout.getOutline(null);
	    g2d.translate(0, size*3);
    	g2d.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
    	g2d.setColor(Color.black);
    	g2d.draw(outline);
    	
//    	g2d.setStroke(new BasicStroke(0));
//    	g2d.setColor(Color.white);
//    	g2d.fill(outline);
    	
    	g2d.translate(0, 0);
	    g2d.setStroke(new BasicStroke(0));
	    g2d.setColor(Color.white);
//        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
//        g2d.drawString(text, 0, size*1);
//        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
//    	g2d.drawString(text, 0, size*2);
//    	g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
    	g2d.drawString(text, 0, 0);
//    	g2d.setColor(new Color(240,240,240));
//    	g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
//    	g2d.drawString(text, 0, 0);
    	
        g2d.dispose();
		ImageIO.write(img, "bmp", new File(Conf.desktop+"large.bmp"));
	}

}
