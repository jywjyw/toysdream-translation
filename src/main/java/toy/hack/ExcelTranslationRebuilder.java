package toy.hack;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import toy.BinSplitPacker;
import toy.Conf;
import toy.Resource;
import toy.common.ExcelParser;
import toy.common.ExcelParser.RowCallback;
import toy.dump.Scripts;
import toy.hack.TranslationScriptReader.Callback;
import toy.util.Util;

public class ExcelTranslationRebuilder {
	
	private String lastTitle;
	private Integer lastPointer;
	private StringBuilder perPoint = new StringBuilder();
	private List<byte[]> perEvent = new ArrayList<>();
	
	public void rebuildSingle(Map<String,FontData> fonts, String rscName, Resource resource, BinSplitPacker bin) throws IOException {
		File rscFile = ExcelFileFinder.getNewestTranslation(rscName);
		InputStream excel = new FileInputStream(rscFile);
		new ExcelParser(excel, rscFile.getName()).parse(1, new RowCallback() {
			
			@Override
			public void doInRow(List<String> strs, int rowNum) {
				if("".equals(strs.get(0))) return;
//				try {
					String thisTitle = strs.get(0);
					int thisPointer = Integer.parseInt(strs.get(1));
					if(lastTitle!=null && !thisTitle.equals(lastTitle)) {
						handle(fonts,resource.getUniqueAddress(lastTitle), bin);
						perPoint = new StringBuilder();
						perEvent = new ArrayList<>();
						
					} else if(lastPointer!=null && lastPointer!=thisPointer) {
						perEvent.add(rebuildAsBytes(perPoint.toString(), fonts));
						perPoint = new StringBuilder();
					} 
					
					String s2="";
					if(strs.size()>=2 && strs.get(2)!=null) {
						s2 = strs.get(2);
					}
					String s4="";
					if(strs.size()>=4 && strs.get(4)!=null) {
						s4 = strs.get(4);
					}
					perPoint.append(s2).append(s4);
					
					lastTitle = thisTitle;
					lastPointer = thisPointer;
//					
//				}catch(Exception e) {
//					throw new RuntimeException(e);
//				}
			}
		});
		handle(fonts, resource.getUniqueAddress(lastTitle), bin);
	}
	
	private void handle(Map<String,FontData> fonts, int address, BinSplitPacker bin){
		perEvent.add(rebuildAsBytes(perPoint.toString(), fonts));
		
		byte[] scriptFile= new Scripts().rebuild(perEvent);
		File target = bin.findFile(String.format("%08X", address));
		int lenLimit = (int) target.length();
		if(scriptFile.length>lenLimit){
			throw new RuntimeException(String.format("%s文本超长,可用字节数%d,实际字节%d", lastTitle, lenLimit, scriptFile.length));
		}
		ByteBuffer buf = ByteBuffer.allocate((int) target.length());	//和原来的文件脚本要相同大小,原因不明
		buf.put(scriptFile);
		Util.writeFile(target, buf.array());
	}
	
	private byte[] rebuildAsBytes(String s, Map<String,FontData> fonts) {
		ByteBuffer ret = ByteBuffer.allocate(50000);
		new TranslationScriptReader().readGBK(s, new Callback() {
			@Override
			public void onReadedUnit(boolean isCtrl, byte[] ctrls, String text, int length) {
				if(isCtrl) {
					ret.put(ctrls);
				} else {
					FontData newfont = fonts.get(text);
					ret.put(newfont.getLittleEndianCode());
				}
			}
		});
		return Arrays.copyOfRange(ret.array(), 0, ret.capacity()-ret.remaining());
	}


}
