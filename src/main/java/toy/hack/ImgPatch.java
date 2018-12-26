package toy.hack;

import java.util.List;

public class ImgPatch {
	public int x, y; // 补丁打到哪个位置,即矩形的左上角坐标. x为4bit图像下的横坐标
	public List<byte[]> source; // bytes per line
	
	public ImgPatch setXY(int x, int y){
		this.x=x;
		this.y=y;
		return this;
	}
	
	public ImgPatch setSource(List<byte[]> source) {
		this.source = source;
		return this;
	}
	
}