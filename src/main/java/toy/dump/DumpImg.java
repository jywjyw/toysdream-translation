package toy.dump;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.imageio.ImageIO;

import toy.Conf;
import toy.EntrancePointer;
import toy.EntranceTable;
import toy.Palette;
import toy.Resource;
import toy.util.Util;

public class DumpImg {
	
	public static void main(String[] args) throws IOException {
		batchDump();
//		new DumpImg().dumpWithSpecPal(new File(Conf.export+"split\\027CF610"), "d:\\", new Palette(16, "992-0"));
	}
	
	
	/**
	 * 1. new BinSplitter().split(true); //split bin file first
	 * 2. batchDump(); //dump all available image
	 * @throws IOException
	 */
	public static void batchDump() throws IOException {
		File[] ff = new File(Conf.export+"split\\").listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("0");
			}
		});
		
		for(File f : ff) {
			try {
				new DumpImg().dump(f, Conf.export+"dump\\", true);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
		System.out.println("finished.................");
		
	}
	
	
	//dump字库图片
	public static void dumpFontlibAfterHack() throws IOException {
		EntranceTable table = new EntranceTable(Conf.hackbin);
		EntrancePointer pointer = table.find(0x0002B5E0);
		byte[] buf = Util.copyPartFile(Conf.hackbin, pointer.addr, pointer.size);
		File tmp = File.createTempFile("myfont", "");
		FileOutputStream fos = new FileOutputStream(tmp);
		fos.write(buf);
		fos.close();
		new DumpImg().dump(tmp, Conf.desktop, true);
		tmp.delete();
	}
	
	public static void dumpFromBin(String filelinkBin, String fileId){
		try {
			byte[] buf = Util.copyPartFile(filelinkBin, Long.parseLong(fileId, 16), new Resource().getLengthByAddr(fileId));
			File tmp = File.createTempFile("dump", "");
			FileOutputStream fos = new FileOutputStream(tmp);
			fos.write(buf);
			fos.close();
			new DumpImg().dumpWithSpecPal(tmp, Conf.desktop, new Palette(16, "992-0"));
			tmp.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param file. split files by BinSplitter
	 * @param outDir
	 * @param use1clut  true:只使用一个调色板 	false:使用该图片所有的调色板输出图像
	 * @throws IOException
	 */
	public void dump(File splitFile, String outDir, boolean use1clut) throws IOException {
		ImagePack pack = ImageReader.load(splitFile);
		RandomAccessFile in = null;
		try {
			in = new RandomAccessFile(splitFile, "r");
			
			for(int j=0;j<pack.getImgCount();j++) {
				TextureMeta img = pack.imgHeaders.get(j);
				for(int i=0;i<pack.getClutCount();i++) {
					if(use1clut) {
						if(j==0) {
							if(i>0) continue;	//第1张图片使用第1个调色板
						} else {
							if(i!=pack.getClutCount()-1) continue;	//第2张图片使用最后一个调色板
						}
					}
					TextureMeta clut = pack.clutHeaders.get(i);
					in.seek(clut.pos);
					byte[] palBuf = new byte[clut.w*2];	//clut高度都为1,不用乘
					in.read(palBuf);
					Palette pal = new Palette(clut.w, palBuf);
					
					in.seek(img.pos);
					BufferedImage output=ImageReader.read(in, img.w, img.h, pal);
					
					File out = new File(String.format("%s%s_%d_c%d.png", outDir,splitFile.getName(),j,i));
					if(!out.getParentFile().exists()) {
						out.getParentFile().mkdirs();
					}
					ImageIO.write(output, "png", out);
//					if(i>0)break;
				}
			}
		} finally {
			try {
				in.close();
			} catch (IOException e) {
			}
		}
	}
	
	public void dumpWithSpecPal(File splitFile, String outDir, Palette pal) throws IOException {
		ImagePack pack = ImageReader.load(splitFile);
		RandomAccessFile in = null;
		try {
			in = new RandomAccessFile(splitFile, "r");
			for(int j=0;j<pack.getImgCount();j++) {
				TextureMeta img = pack.imgHeaders.get(j);
				in.seek(img.pos);
				BufferedImage output=ImageReader.read(in, img.w, img.h, pal);
				
				File out = new File(String.format("%s%s_%d.png", outDir,splitFile.getName(),j));
				if(!out.getParentFile().exists()) {
					out.getParentFile().mkdirs();
				}
				ImageIO.write(output, "png", out);
			}
		} finally {
			try {
				in.close();
			} catch (IOException e) {
			}
		}
	}

}
