package toy.hack.subtitle;

//字幕图像的指针文件位置
public class SubtitleImgPos {
	
	public int packId, imgIndex, pointerFileId, startPos;
	public SubtitleImgPos(int packId, int imgIndex,  int pointerFileId, int startPos) {
		this.packId = packId;
		this.imgIndex = imgIndex;
		this.pointerFileId = pointerFileId;
		this.startPos = startPos;
	}
	
	public String getPackIdStr(){
		return String.format("%08X", packId);
	}
	
	public String getPointFileIdStr(){
		return String.format("%08X", pointerFileId);
	}
}
