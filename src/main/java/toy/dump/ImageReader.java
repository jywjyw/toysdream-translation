package toy.dump;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import toy.Palette;
import toy.util.Util;

public class ImageReader {
	
	public static ImagePack load(File splitFile) throws IOException{
		RandomAccessFile in = null;
		try {
			in = new RandomAccessFile(splitFile, "r");
			return load(in, 0, splitFile.length());
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
	}
	
	public static FullImagePack fullLoad(File splitFile)throws IOException{
		RandomAccessFile in = null;
		try {
			in = new RandomAccessFile(splitFile, "r");
			return load(in, 0, splitFile.length(), true);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
			}
		}
	}
	
	public static ImagePack load(RandomAccessFile in, long startPos, Long imgPackSize)throws IOException{
		return load(in, startPos, imgPackSize, false);
	}
	
	public static FullImagePack load(RandomAccessFile in, long startPos, Long imgPackSize, boolean fullLoad)throws IOException{
		in.seek(startPos);
		int imgCount = Util.hiloShort(in.readUnsignedShort());	//1,2,4,6,9,900
		int clutCount = Util.hiloShort(in.readUnsignedShort());
		if(imgCount==0||clutCount==0||imgCount>100) {	//0008DF9C这张图片是地图地块,包含900张子图片,太大,要跳过.
			throw new UnsupportedOperationException("img count or clut count equals 0, not img");
		}
		
		List<TextureMeta> imgHeaders = new ArrayList<>();
		for(int i=0;i<imgCount;i++) {
			TextureMeta img = new TextureMeta();
			img.x = Util.hiloShort(in.readUnsignedShort());
			img.y = Util.hiloShort(in.readUnsignedShort());
			img.w = Util.hiloShort(in.readUnsignedShort());
			img.h = Util.hiloShort(in.readUnsignedShort());
			img.pos = Util.hilo(in.readInt());	//image数据的开始读取位置
			if(imgPackSize!=null){
				img.validate(imgPackSize);
			}
			imgHeaders.add(img);
		}
		
		List<TextureMeta> clutHeaders = new ArrayList<>();
		for(int i=0;i<clutCount;i++) {
			TextureMeta clut = new TextureMeta();
			clut.x = Util.hiloShort(in.readUnsignedShort());	//使用哪个调色板.调色板在显存中的	X坐标
			clut.y = Util.hiloShort(in.readUnsignedShort());  //使用哪个调色板.调色板在显存中的Y坐标
			clut.w = Util.hiloShort(in.readUnsignedShort());  //调色板宽,16=4bitClut,256=8bitclut
			clut.h = Util.hiloShort(in.readUnsignedShort());  //调色板高
			clut.pos = Util.hilo(in.readInt());  //调色板位置
			if(imgPackSize!=null){
				clut.validate(imgPackSize);
			}
			clutHeaders.add(clut);
		}
		
		if(!fullLoad) {
			return new FullImagePack(imgHeaders, clutHeaders);
		}
		
		List<byte[]> imgs = new ArrayList<>();
		for(TextureMeta t : imgHeaders){
			in.seek(t.pos);
			byte[] img = new byte[t.w*t.h*2];
			in.read(img);
			imgs.add(img);
		}
		
		List<byte[]> cluts = new ArrayList<>();
		for(TextureMeta t : clutHeaders){
			in.seek(t.pos);
			byte[] buf = new byte[t.w*t.h*2];
			in.read(buf);
			cluts.add(buf);
		}
		return new FullImagePack(imgHeaders, clutHeaders, imgs, cluts);
	}
	
	public static BufferedImage read(RandomAccessFile in, int imgWidth, int imgHeight, Palette pal) throws IOException {
		if(pal.getColorCount()==16) {
			return readAs4bit(in, imgWidth, imgHeight, pal);
		} else if(pal.getColorCount()==256) {
			return readAs8bit(in, imgWidth, imgHeight, pal);
		} else {
			throw new UnsupportedOperationException("not available clut width:("+pal.getColorCount()+")");
		}
	}
	
	private static BufferedImage readAs4bit(RandomAccessFile in, int imgWidth, int imgHeight, Palette pal) throws IOException {
		int displayW = imgWidth*4;	//4bit下显示宽度*4;
		BufferedImage output = new BufferedImage(displayW, imgHeight, BufferedImage.TYPE_INT_ARGB);	
		WritableRaster raster = output.getRaster();
		
		Set<Integer> indexes = new HashSet<>();
		byte[] buf = new byte[imgWidth*2];
		int x=0,y=0;
		int[][] colorBuf = new int[2][4];
		boolean _break=false;
		while(true) {
			in.read(buf);
			for(byte b : buf) {
				int i1 = b>>>4&0xf, i2 = b&0xf;
				indexes.add(i1);
				indexes.add(i2);
				colorBuf[0]=pal.to32Rgba(i2);
				colorBuf[1]=pal.to32Rgba(i1);
				for(int[] i : colorBuf) {
					raster.setPixel(x++, y, i);	//rgba
					if(x>=displayW) {
						x=0;
						y++;
						if(y>=imgHeight) {
							_break=true;
							break;
						}
					}
				}
			}
			if(_break)break;
		}
		return output;
	}
	
	private static BufferedImage readAs8bit(RandomAccessFile in, int imgWidth, int imgHeight, Palette pal) throws IOException {
		int displayW = imgWidth*2;	//8bit下显示宽度*2;
		final BufferedImage output = new BufferedImage(displayW,imgHeight, BufferedImage.TYPE_INT_ARGB);	
		WritableRaster raster = output.getRaster();
		
		byte[] buf = new byte[imgWidth*2];
		int x=0,y=0;
		boolean _break=false;
		while(true) {
			in.read(buf);
			for(byte b : buf) {
				int[] color =pal.to32Rgba(b&0xff);
				raster.setPixel(x++, y, color);	//rgba
				if(x>=displayW) {
					x=0;
					y++;
					if(y>=imgHeight) {
						_break=true;
						break;
					}
				}
			}
			if(_break)break;
		}
		
		return output;
	}
	
}
