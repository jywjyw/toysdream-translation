package toy.hack;

import java.awt.FontFormatException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import toy.BinSplitPacker;
import toy.CharStat;
import toy.Conf;
import toy.FixedCharModel;
import toy.util.Util;

public abstract class BaseFontlibHacker {
	
	protected static final int CHAR_PER_ROW = 21, CHAR_PER_COLUMN = 21;	//每个材质页中, 各行包含多个字, 各列包含多少字  
	protected static final int PIXEL_PER_BYTE = 2;	//每个字节能容纳多个像素点
	
	public Map<String,FontData> validateAndTransformFonts(Map<String,CharStat> char_count) {
		Map<String,FontData> fixedChar = new FixedCharModel().getClone();
		for(String k : fixedChar.keySet()){
			if(char_count.containsKey(k))
				char_count.remove(k);
		}
		
		List<CharStat> rare=new ArrayList<>();
		Set<String> illegalChars=new HashSet<>();
		int rareThreshold=2;
		for(Entry<String,CharStat> e : char_count.entrySet()){
			if(e.getValue().getCount()<=rareThreshold) 
				rare.add(e.getValue().setChar(e.getKey()));
			if(e.getKey().length()>1) 
				illegalChars.add(e.getKey());
		}
		if(illegalChars.size()>0) {
			throw new RuntimeException("不能出现复合字符:"+Util.join(new ArrayList<String>(illegalChars), ","));
		}
		
		int totalSize=fixedChar.size()+char_count.size();
		if(totalSize>getMaxChar()) {
			debugRareChar(rare);
			throw new RuntimeException("字库超出容量了,可用值"+getMaxChar()+",超出"+(totalSize-getMaxChar())+"个,低频字个数="+rare.size());
		} else {
			System.out.printf("字库个数=%d,低频使用字的个数=%d\n",totalSize,rare.size());
		}
		
		List<FontData> fonts = new ArrayList<>(getMaxChar());
		fonts.addAll(fixedChar.values());
		for(Entry<String,CharStat> e : char_count.entrySet()) {
			fonts.add(new FontData(e.getKey(), e.getValue().getCount()));
		}
		
		swapSpecChar(fonts);
		return rebuildCode(fonts);
	}
	
	void swapSpecChar(List<FontData> fonts){
		List<int[]> swaps = new ArrayList<>();
		for(int i=0;i<fonts.size();i++) {
			FontData f = fonts.get(i);
			if(f.position!=null) {
				swaps.add(new int[]{i, f.position});
			}
		}
		for(int[] swap : swaps) {
			Collections.swap(fonts, swap[0], swap[1]);
		}
	}
	
	//重建code
	Map<String,FontData> rebuildCode(List<FontData> fonts){
		Map<String,FontData> ret = new LinkedHashMap<>();
		for(FontData f : fonts) {
			ret.put(f.char_, f);
		}
		
		short code=0;
		for(Entry<String,FontData> e : ret.entrySet()) {
			e.getValue().code=code;
			code++;
		}
		
		try {
			OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(new File(Conf.desktop+"新码表.tbl")), "gbk");
			for(Entry<String,FontData> e : ret.entrySet()) {
				os.write(Util.toHexString(e.getValue().getLittleEndianCode())+"="+e.getKey()+"\n");
			}
			os.flush();
			os.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return ret;
	}
	
	void debugRareChar(List<CharStat> rare){
		Collections.sort(rare,new Comparator<CharStat>() {
			@Override
			public int compare(CharStat o1, CharStat o2) {
				return o1.getCount()-o2.getCount();
			}
		});
		int i=1;
		for(CharStat e : rare){
			System.out.printf("%d - %s(%d次):%s\n",i++, e.getChar(), e.getCount(), e.getLocationsStr());
		}
	}
	
	void generateFontModel(Map<String,FontData> fonts){
		StringBuilder chars = new StringBuilder();
		for(Entry<String,FontData> e:fonts.entrySet()){
			if(e.getValue().imgdata==null)
				chars.append(e.getValue().char_);
		}
		char[] cs = chars.toString().toCharArray();
		try {
			List<byte[]> models = new FontGenerator_zpix().generate(cs);
			for(int i=0;i<cs.length;i++){
				fonts.get(String.valueOf(cs[i])).imgdata = models.get(i);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public abstract void hack(BinSplitPacker bin, Map<String, FontData> fonts) throws IOException, FontFormatException;

	public abstract int getPage();
	
	public int getMaxChar(){
		return getPage()*21*21;
	}
	
	//补足字库总个数,留成空白
	void fill(List<FontData> list) {
		int listSize = list.size();
		for(int i=0;i<getMaxChar()-listSize;i++){
			FontData f = new FontData();
			f.imgdata = new byte[6*Conf.charH];
			Arrays.fill(f.imgdata, (byte)0);
			list.add(f);
		}
	}
	
	/**
	 * 仅构建图像数据体, 不包括色板,宽高等
	 * @param fonts
	 * @return
	 */
	byte[] buildImgBody(Map<String,FontData> fonts)  {
		List<FontData> list = new ArrayList<>(fonts.values());
		fill(list);
		int lineBytes = (Conf.charW*CHAR_PER_ROW+4)/PIXEL_PER_BYTE,	//单个材质页中,每个扫描线占多少字节 
			rowBytes = lineBytes*Conf.charH;	//一个字列占多少字节
		
		//把所有字符排成21列,n行
		ByteBuffer[] rows = new ByteBuffer[CHAR_PER_ROW*getPage()];
		int rowIndex=0;
		for(int i=0;i<list.size();i+=CHAR_PER_ROW) {
			ByteBuffer buf = ByteBuffer.allocate(rowBytes);
			for(int j=0;j<Conf.charH*6;j+=6) {
				for(int k=0;k<CHAR_PER_COLUMN;k++) {
					buf.put(Arrays.copyOfRange(list.get(i+k).imgdata, j, j+6));
				}
				buf.put(new byte[]{0,0});
			}
			rows[rowIndex++]=buf;
		}
		
		ByteBuffer[] pages = new ByteBuffer[getPage()];
		for(int i=0;i<pages.length;i++) {
			pages[i] = ByteBuffer.allocate(rowBytes*CHAR_PER_COLUMN);
			for(int j=21*i;j<21*(i+1);j++) {
				pages[i].put(rows[j].array());
			}
		}
		int pageLen = (Conf.charW*CHAR_PER_ROW+4)*(Conf.charH*CHAR_PER_COLUMN+4)/PIXEL_PER_BYTE;	//每个材质页的字库占多大字节
		ByteBuffer finalImg = ByteBuffer.allocate(pageLen*getPage()); 
		for(int i=0;i<21*Conf.charH;i++) {
			for(int j=pages.length-1;j>=0;j--){
				finalImg.put(Arrays.copyOfRange(pages[j].array(), lineBytes*i, lineBytes*(i+1)));
			}
		}
		return finalImg.array();
	}
	
}
