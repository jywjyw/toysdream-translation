package toy.hack.subtitle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import toy.BinSplitPacker;
import toy.Conf;
import toy.Resource;
import toy.common.ExcelParser;
import toy.common.ExcelParser.RowCallback;
import toy.dump.DumpImg;

public class SubtitleTranslate {
	
	public static void main(String[] args) throws IOException {
		BinSplitPacker binpacker = BinSplitPacker.newInstance();
		binpacker.split(Conf.bin);
		new SubtitleTranslate().importXls(binpacker, System.getProperty("user.dir")+"/translation/subtitle-v5.xlsx");
		new DumpImg().dump(new File(binpacker.getSplitDir()+"0144692C"), "C:\\Users\\Administrator\\Desktop\\", true);
	}
	
	String lastChapter=null;
	List<SubtitleImgPos> imgPosList = new ArrayList<>();
	List<String> lines = new ArrayList<>();
	Resource rsc=new Resource();
	
	public void importXls(BinSplitPacker bin, String file) {
		new ExcelParser(new File(file)).parse(2, new RowCallback() {
			@Override
			public void doInRow(List<String> strs, int rowNum) {
				String thisChapter = strs.get(0); 
				if(lastChapter!=null && !lastChapter.equals(thisChapter)) {
					getHandler(imgPosList.get(0).packId).hack(bin,imgPosList, lines);
					lastChapter=null;
					imgPosList.clear();
					lines.clear();
				}
				
				String translation = strs.get(7);
				if(translation==null || "".equals(translation))
					translation = " "; //无译文时仍留一个空格
				lines.add(translation);
				
				try {
					String imgPackIdStr=strs.get(1);
					if(imgPackIdStr!=null && imgPackIdStr.length()>0) {
						int imgPackId = Integer.parseInt(strs.get(1), 16);
						int pointFileId = Integer.parseInt(strs.get(3), 16);
						imgPosList.add(new SubtitleImgPos(imgPackId, Integer.parseInt(strs.get(2)), pointFileId, Integer.parseInt(strs.get(4), 16)));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				lastChapter = thisChapter;
			}
		});
		getHandler(imgPosList.get(0).packId).hack(bin,imgPosList, lines);
	}
	
	private AbstractHandler getHandler(int packId){
		String impl=String.format("%s.Chapter_%08X",this.getClass().getPackage().getName(), packId);
		try {
			return (AbstractHandler) Class.forName(impl).newInstance();
		} catch (Exception e) {
			return new Chapter_default();
		}
	}
}
