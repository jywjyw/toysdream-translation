package toy.hack.subtitle;

import java.util.List;

/**
 * 一行字幕
 */
public class SubtitleRow {
	public List<byte[]> datas;
	public int width;	//4bit下宽度
	public SubtitleRow(List<byte[]> datas, int width) {
		this.datas = datas;
		this.width = width;
	}
	
}
