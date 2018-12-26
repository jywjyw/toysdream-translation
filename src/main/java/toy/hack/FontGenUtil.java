package toy.hack;

public class FontGenUtil {
	
	/**
	 * 把int型的16色颜色索引值变为ROM图片所需格式: 2个点占1个字节,
	 * @return
	 */
	public static byte[] colorIndexesToImgFormat(int[] indexes) {
		byte[] img = new byte[indexes.length/2];
		int imgI=0;
		for(int i=0;i<indexes.length;i+=2) {
			img[imgI++] = (byte) (indexes[i+1]<<4|indexes[i]&0xf);
		}
		return img;
	}


}
