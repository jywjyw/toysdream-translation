package toy.dump;

import java.io.IOException;
import java.io.RandomAccessFile;

import toy.CharTable;

/**
 * 读取ROM中的文本区
 */
public class BinScriptReader {
	
	RandomAccessFile text;
	long startPos;
	CharTable charTable;
	
	public BinScriptReader(RandomAccessFile text, long startPos, CharTable charTable) {
		this.text = text;
		this.startPos = startPos;
		this.charTable = charTable;
	}

	public interface Callback {
		void speakerBegin(String s);	//包围控制符，开始
		void speakerEnd(String s);		//包围控制符，开始
		void isSingleCtrl(String s);	//单个控制符
		void isChar(String s);			//单个字符
		void newIndex();
	}
	
	public interface Callback2{
		void icon(String s);
		void speakerBegin(String s);
		void speakerEnd(String s);
		void colorChange(String s);
		void space(String s);
		void breakLine(String s);
		void begin(String s);
		void wait_(String s);
		void end(String s);
		void isChar(String s);
	}
	
	public void readUntilFF(Callback c) throws IOException {
		text.seek(startPos);
		int prevT=0, prev2T=0, bufT=0;
		while(true) {
			bufT=text.readUnsignedShort();
			if(prev2T==0x08f0 && prevT==0x0000) {
				c.isSingleCtrl(String.format("{icon%04X}", bufT));
			} else if(prevT==0x08f0){
			} else if(prevT==0x03F0){
				if(bufT==0x0000) {
					c.speakerBegin("{03f0B}");
				} else if(bufT==0xffff){
					c.speakerEnd("{03f0E}");
				}
			} else if(prevT==0x06F0){
				c.isSingleCtrl(String.format("{06f0%04x}", bufT));
			} else if(bufT==0x03f0 || bufT==0x06f0 || bufT==0x08f0) {
			} else if(bufT==0x00f0) {
				c.isSingleCtrl("{ }");
			} else if(bufT==0x01f0) {
				c.isSingleCtrl("{br}");
			} else if(bufT==0x02f0){
				c.isSingleCtrl("{02f0}");
			} else if(bufT==0x04f0) {
				c.isSingleCtrl("{wait}");
			} else if(bufT==0xFFFF) {
				c.isSingleCtrl("{end}");
				break;
			} else if(charTable.containChar(bufT)){
				c.isChar(charTable.getChar(bufT));
			} else {
				throw new RuntimeException(String.format("[%04X]", bufT));
			}
			prev2T=prevT;
			prevT=bufT;
		}
	}
	
	public String readTextUntil04ff() throws IOException {
		text.seek(startPos);
		int prevT=0, prev2T=0, bufT=0;
		StringBuilder s = new StringBuilder();
		while(true) {
			bufT=text.readUnsignedShort();
			if(prev2T==0x08f0 && prevT==0x0000) {
				s.append(String.format("{icon%04X}", bufT));
			} else if(prevT==0x08f0){
			} else if(prevT==0x03F0){
				if(bufT==0x0000) {
					s.append("{03f0B}");
				} else if(bufT==0xffff){
					s.append("{03f0E}");
				}
			} else if(prevT==0x06F0){
				s.append(String.format("{06f0%04x}", bufT));
			} else if(bufT==0x03f0 || bufT==0x06f0 || bufT==0x08f0) {
			} else if(bufT==0x00f0) {
				s.append("{ }");
			} else if(bufT==0x01f0) {
				s.append("{br}");
			} else if(bufT==0x02f0){
				s.append("{02f0}");
			} else if(bufT==0x04f0) {
				s.append("{wait}");
			} else if(bufT==0xFFFF) {
				s.append("{end}");
				break;
			} else if(charTable.containChar(bufT)){
				s.append(charTable.getChar(bufT));
			} else {
				throw new RuntimeException(String.format("[%04X]", bufT));
			}
			prev2T=prevT;
			prevT=bufT;
		}
		return s.toString();
	}

}
