package toy.dump;

import java.util.List;

/**
 * 图像文件. 包含一个或多个图像, 包含多个调色板
 */
public class ImagePack {
	
	public List<TextureMeta> imgHeaders, clutHeaders;
	
	public ImagePack(List<TextureMeta> imgHeaders, List<TextureMeta> clutHeaders) {
		this.imgHeaders = imgHeaders;
		this.clutHeaders = clutHeaders;
	}

	public int getImgCount(){
		return imgHeaders.size();
	}
	
	public int getClutCount(){
		return clutHeaders.size();
	}

}
