package test;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class AntialiasedText extends Applet {
          
    public void paint(Graphics g) {
    	int h = 18;
    	String text = "我们都是一个研究所的人";
    	
    	Font font=null;
		try {
			String ttf = "SonyReader Li.ttf";
			InputStream is = new FileInputStream(System.getProperty("user.dir")+"/raw/"+ttf); 
			font = Font.createFont(Font.TRUETYPE_FONT, is);
			font = font.deriveFont(Font.TRUETYPE_FONT, h);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

        Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.black);	
		g2d.fillRect(0, 0, getWidth(), getHeight());
        
		g2d.setColor(Color.white);
		g2d.setFont(font);
		
//        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
//        g2d.drawString(text, 20, h);
//
//        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//        g2d.drawString(text, 20, 2*h);
//
//        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
//        g2d.drawString(text, 20, 3*h);

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2d.drawString(text, 20, 4*h);
    }

    public static void main(String[] args) {

        Frame f = new Frame("Antialiased Text Sample");
        f.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });

        f.add(new AntialiasedText());
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
//        f.setSize(new Dimension(500, 180));
        f.setBounds(screen.width/3, screen.height/3, 500, 200);
        f.setVisible(true);
    }
}