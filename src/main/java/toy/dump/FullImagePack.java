package toy.dump;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 图像文件. 包含一个或多个图像, 包含多个调色板
 */
public class FullImagePack extends ImagePack{
	
	private List<byte[]> 
			imgs = new ArrayList<>(), 
			cluts  = new ArrayList<>();
			
	public FullImagePack(List<TextureMeta> imgHeaders, List<TextureMeta> clutHeaders){
		super(imgHeaders, clutHeaders);
	}
	
	public FullImagePack(List<TextureMeta> imgHeaders, List<TextureMeta> clutHeaders, 
							List<byte[]> imgs, List<byte[]> cluts) {
		super(imgHeaders, clutHeaders);
		this.imgs = imgs;
		this.cluts = cluts;
	}

	//修改图像体,不改变图像宽高
	public void replaceImgBody(int imgIndex, byte[] data){
		imgs.set(imgIndex, data);
	}
	
	//修改图像体大小时, 要修改图像头指针,调色板指针
	public void replaceImgBody(int imgIndex, byte[] data, int w, int h){
		TextureMeta imgHeader = imgHeaders.get(imgIndex);
		imgHeader.w = w;
		imgHeader.h = h;
		
		int diff = data.length - imgs.get(imgIndex).length;
		imgs.set(imgIndex, data);
		for(int i=0;i<imgs.size();i++){
			if(i>imgIndex){
				TextureMeta imgheader = imgHeaders.get(i);
				imgheader.pos += diff;
			}
		}
		for(TextureMeta t : clutHeaders){
			t.pos += diff;
		}
	}
	
	//修改色板体
	public void replaceClut(int clutIndex, byte[] data){
		this.cluts.set(clutIndex, data);
	}
	
	public byte[] rebuild(){
		ByteBuffer buf = ByteBuffer.allocate(256*1024);	//最大图片是224k
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putShort((short) imgs.size());
		buf.putShort((short) cluts.size());
		for(TextureMeta t : imgHeaders){
			buf.put(t.rebuild());
		}
		for(TextureMeta t : clutHeaders){
			buf.put(t.rebuild());
		}
		for(byte[] bs : imgs){
			buf.put(bs);
		}
		for(byte[] bs : cluts){
			buf.put(bs);
		}
		return Arrays.copyOfRange(buf.array(), 0, buf.capacity()-buf.remaining());
	}
	

}
