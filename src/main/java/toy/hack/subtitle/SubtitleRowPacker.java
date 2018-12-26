package toy.hack.subtitle;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import toy.Conf;
import toy.dump.DumpImg;
import toy.dump.ImagePack;
import toy.dump.ImageReader;

public class SubtitleRowPacker {
	
	public static void main(String[] args) throws IOException  {
		List<SubtitleRow> subtitlerows = new ArrayList<>();
		for(int i=0;i<30;i++){
			SubtitleRow r = new SubtitleRowGen().generate("我是一个字我是一个字我是一个");
			subtitlerows.add(r);
		}
		RandomAccessFile bin = new RandomAccessFile(Conf.hackbin, "rw");
		long id= 0x01BAD058;
		ImagePack imgPack = ImageReader.load(bin, id, null);
		bin.seek(imgPack.imgHeaders.get(1).pos+id);
		bin.write(pack(subtitlerows));
		bin.close();
		DumpImg.dumpFromBin(Conf.hackbin, "01BAD058");
	}
	
	/**
	 * 字幕打包后,宽度值为256的倍数, 高度固定为256
	 * @param subtitleRows
	 * @return
	 */
	public static byte[] pack(List<SubtitleRow> subtitleRows){
		int rowPerPage = 12,
			scanlineSize = 256/2,	//w/ppb
			rowcount=subtitleRows.size(),
			pagecount=rowcount/rowPerPage;
		if(rowcount%12>0) pagecount++;
		
		ByteBuffer step1 = ByteBuffer.allocate(scanlineSize*256*pagecount);
		for(int i=0;i<subtitleRows.size();i++) {
			SubtitleRow row = subtitleRows.get(i);
			for(byte[] scanline : row.datas){
				step1.put(scanline);
				step1.put(new byte[scanlineSize-scanline.length]);
			}
			if((i+1)%rowPerPage==0){
				int blankScanline = 256-Conf.SUBTITLE_CHAR_H*rowPerPage;
				step1.put(new byte[blankScanline*scanlineSize]);
			}
		}
		byte[] b1 = step1.array();
		if(rowcount<=rowPerPage) {
			return b1;
		} else {
			ByteBuffer step2 = ByteBuffer.allocate(step1.capacity());
			byte[] buf= new byte[256/2];
			for(int i=0;i<256;i++){//256=h
				for(int j=pagecount;j>0;j--){
					int offset= scanlineSize*256*(j-1)+i*scanlineSize;
					System.arraycopy(b1, offset, buf, 0, buf.length);
					step2.put(buf);
				}
			}
			return step2.array();
		}
	}

}
