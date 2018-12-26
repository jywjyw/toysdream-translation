package toy.hack;

import java.util.Comparator;

public class FontData {
	public String char_;
	public int count;
	public short code;
	public byte[] imgdata;
	public Integer position;	//有些字符必须固定在某个位置
	
	public FontData() {
	}

	public FontData(String char_, int count) {
		this.char_ = char_;
		this.count = count;
	}
	
	public byte[] getLittleEndianCode() {
		byte[] bs = new byte[2];
		bs[0]=(byte)(code&0xff);
		bs[1]=(byte)(code>>8&0xff);
		return bs;
	}
	
	public static Comparator<FontData> countDesc = new Comparator<FontData>() {
		@Override
		public int compare(FontData o1, FontData o2) {
			return o2.count-o1.count;
		}
	};
}
